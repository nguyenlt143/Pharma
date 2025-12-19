document.addEventListener("DOMContentLoaded", () => {
    const btnAddShift = document.getElementById("btnAddShift");
    const shiftModal = document.getElementById("shiftModal");
    const employeeModal = document.getElementById("employeeModal");
    const modalClose = document.getElementById("modalClose");
    const btnCancel = document.getElementById("btnCancel");
    const closeEmployeeModal = document.getElementById("closeEmployeeModal");
    const shiftForm = document.getElementById("shiftForm");
    const shiftTableBody = document.getElementById("shiftTableBody");
    const employeeTableBody = document.getElementById("employeeTableBody");
    const employeeSelect = document.getElementById("employeeSelect");
    const assignBtn = document.getElementById("assignBtn");
    const toastEl = document.getElementById("toast");

    // ====================== PAGINATION STATE ======================
    let allShifts = [];
    let currentPage = 1;
    let recordsPerPage = 25;

    // ====================== TOAST UTILITY ======================
    function showToast(msg, timeout = 2500, type = 'info') {
        console.log('showToast called:', msg, type);
        if (!toastEl) {
            console.error('Toast element not found - using global toast');
            // Use global toast system as fallback
            if (window.showToast) {
                window.showToast(msg, type, timeout);
            }
            return;
        }
        toastEl.textContent = msg;
        toastEl.classList.remove('hidden', 'success', 'error');
        toastEl.style.display = 'block';
        void toastEl.offsetWidth; // Force reflow
        toastEl.classList.add('show');
        if (type === 'success') {
            toastEl.classList.add('success');
        } else if (type === 'error') {
            toastEl.classList.add('error');
        }
        setTimeout(() => {
            toastEl.classList.remove('show');
            setTimeout(() => {
                toastEl.classList.add('hidden');
                toastEl.style.display = 'none';
            }, 250);
        }, timeout);
    }

    // ====================== MODAL OPEN/CLOSE ======================
    btnAddShift.onclick = () => openShiftModal();
    modalClose.onclick = () => closeModal(shiftModal);
    btnCancel.onclick = () => closeModal(shiftModal);
    closeEmployeeModal.onclick = () => closeModal(employeeModal);

    // Close modal on overlay click
    [shiftModal, employeeModal].forEach(modal => {
        modal.addEventListener("click", e => {
            if (e.target === modal) closeModal(modal);
        });
    });

    function openShiftModal(s = {}) {
        shiftModal.classList.remove("hidden");
        document.getElementById("shiftId").value = s.id || "";
        document.getElementById("shiftName").value = s.name || "";
        // Populate hour/minute inputs from shift time if available
        if (s.startTime) {
            const st = formatToHHMM(s.startTime).split(":");
            document.getElementById("startHour").value = st[0];
            document.getElementById("startMinute").value = st[1];
        } else {
            document.getElementById("startHour").value = "";
            document.getElementById("startMinute").value = "00";
        }
        if (s.endTime) {
            const et = formatToHHMM(s.endTime).split(":");
            document.getElementById("endHour").value = et[0];
            document.getElementById("endMinute").value = et[1];
        } else {
            document.getElementById("endHour").value = "";
            document.getElementById("endMinute").value = "00";
        }
        document.getElementById("note").value = s.note || "";
        document.getElementById("modalTitle").textContent = s.id ? "Chỉnh sửa ca làm việc" : "Thêm ca làm việc mới";
    }

    function closeModal(modal) {
        modal.classList.add("hidden");
    }

    let showDeleted = false;

    // ====================== PAGINATION & RENDERING ======================
    function updatePaginationControls() {
        const pageInfo = document.getElementById('page-info');
        const prevPageBtn = document.getElementById('prev-page');
        const nextPageBtn = document.getElementById('next-page');
        const recordsPerPageSelect = document.getElementById('records-per-page');

        recordsPerPage = parseInt(recordsPerPageSelect.value, 10);
        const totalRecords = allShifts.length;
        const totalPages = Math.ceil(totalRecords / recordsPerPage) || 1;

        if (currentPage > totalPages) {
            currentPage = totalPages;
        }

        pageInfo.textContent = `Trang ${currentPage} / ${totalPages}`;
        prevPageBtn.disabled = currentPage === 1;
        nextPageBtn.disabled = currentPage === totalPages;
    }

    function renderTablePage() {
        updatePaginationControls();

        const startIndex = (currentPage - 1) * recordsPerPage;
        const endIndex = startIndex + recordsPerPage;
        const pageData = allShifts.slice(startIndex, endIndex);

        shiftTableBody.innerHTML = pageData.map(s => {
            const statusBadge = s.deleted
                ? '<span class="badge inactive">Đã xóa</span>'
                : '<span class="badge active">Hoạt động</span>';

            const actionButtons = s.deleted
                ? `<button class="btn btn-success restore-btn" onclick="restoreShift(${s.id})">↩️ Khôi phục</button>`
                : `
                    <button class="btn btn-ghost" onclick="editShift(${s.id})">✏️ Sửa</button>
                    <button class="btn btn-danger" onclick="deleteShift(${s.id})">🗑️ Xóa</button>
                    <button class="btn btn-info" onclick="viewEmployees(${s.id})">👥 Xem nhân viên</button>
                `;

            const dispStart = s.startTime ? formatToHHMM(s.startTime) : "";
            const dispEnd = s.endTime ? formatToHHMM(s.endTime) : "";

            return `
            <tr>
                <td>${s.name}</td>
                <td>${dispStart}</td>
                <td>${dispEnd}</td>
                <td>${s.note || ""}</td>
                <td class="text-center">${statusBadge}</td>
                <td class="text-center action-buttons">${actionButtons}</td>
            </tr>
        `;
        }).join("");
    }


    // ====================== LOAD SHIFTS ======================
    async function loadShifts() {
        try {
            const url = showDeleted ? "/api/manager/shifts?includeDeleted=true" : "/api/manager/shifts";
            const res = await fetch(url);
            allShifts = await res.json();
            currentPage = 1;
            renderTablePage();
        } catch (err) {
            console.error("❌ Lỗi load shifts:", err);
        }
    }

    // ====================== ADD / EDIT SHIFT ======================
    shiftForm.onsubmit = async (e) => {
        e.preventDefault();
        clearFieldErrors();

        const idVal = document.getElementById("shiftId").value || null;

        // Read hour/minute inputs
        const startHourRaw = document.getElementById("startHour").value;
        const startMinuteRaw = document.getElementById("startMinute").value;
        const endHourRaw = document.getElementById("endHour").value;
        const endMinuteRaw = document.getElementById("endMinute").value;

        // Basic validation for hour fields
        const shNum = Number(startHourRaw);
        const ehNum = Number(endHourRaw);
        const smNum = Number(startMinuteRaw);
        const emNum = Number(endMinuteRaw);
        if (!Number.isInteger(shNum) || shNum < 0 || shNum > 23) {
            displayFieldErrors({ 'startTime': 'Giờ bắt đầu phải là số nguyên từ 0 đến 23' });
            focusFirstInvalidField();
            return;
        }
        if (!Number.isInteger(ehNum) || ehNum < 0 || ehNum > 23) {
            displayFieldErrors({ 'endTime': 'Giờ kết thúc phải là số nguyên từ 0 đến 23' });
            focusFirstInvalidField();
            return;
        }
        // Validate minutes
        if (!Number.isInteger(smNum) || smNum < 0 || smNum > 59) {
            displayFieldErrors({ 'startTime': 'Phút bắt đầu phải là số từ 0 đến 59' });
            focusFirstInvalidField();
            return;
        }
        if (!Number.isInteger(emNum) || emNum < 0 || emNum > 59) {
            displayFieldErrors({ 'endTime': 'Phút kết thúc phải là số từ 0 đến 59' });
            focusFirstInvalidField();
            return;
        }

        // Build HH:mm strings
        const startTime = (shNum < 10 ? '0' + shNum : '' + shNum) + ':' + (smNum < 10 ? '0' + smNum : '' + smNum);
        const endTime = (ehNum < 10 ? '0' + ehNum : '' + ehNum) + ':' + (emNum < 10 ? '0' + emNum : '' + emNum);

        // Frontend validation: end time must be after start time
        const startMinutes = shNum * 60 + smNum;
        const endMinutes = ehNum * 60 + emNum;
        if (endMinutes <= startMinutes) {
            // Use the existing displayFieldErrors to show the error on the end-time line
            displayFieldErrors({ 'endTime': 'Giờ kết thúc phải lớn hơn giờ bắt đầu' });
            focusFirstInvalidField();
            return;
        }

        const payload = {
            id: idVal,
            name: document.getElementById("shiftName").value.trim(),
            startTime: startTime,
            endTime: endTime,
            note: document.getElementById("note").value.trim()
        };

        try {
            const url = payload.id ? `/api/manager/shifts/${payload.id}` : "/api/manager/shifts";
            const res = await fetch(url, {
                method: payload.id ? "PUT" : "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload),
            });

            if (res.ok) {
                closeModal(shiftModal);
                loadShifts();
                showToast(payload.id ? "Cập nhật ca thành công!" : "Thêm ca mới thành công!", 2500, 'success');
            } else {
                const contentType = res.headers.get('content-type');
                if (contentType && contentType.includes('application/json')) {
                    const errorData = await res.json();

                    if (errorData.errors) {
                        // Validation errors - display field-level feedback
                        displayFieldErrors(errorData.errors);
                        focusFirstInvalidField();
                    } else if (errorData.message) {
                        // Business logic error - show toast only
                        showToast(errorData.message, 4000, 'error');
                    } else {
                        showToast("Lỗi khi lưu ca làm việc", 4000, 'error');
                    }
                } else {
                    const error = await res.text();
                    showToast(error || "Lỗi khi lưu ca làm việc", 4000, 'error');
                }
            }
        } catch (err) {
            console.error("❌ Lỗi lưu shift:", err);
            showToast("Có lỗi xảy ra khi lưu ca làm việc!", 3000, 'error');
        }
    };

    function clearFieldErrors() {
        document.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));
        document.querySelectorAll('.invalid-feedback').forEach(el => { el.textContent = ''; el.style.setProperty('display', 'none', 'important'); });
    }

    function displayFieldErrors(errors) {
        for (const [field, message] of Object.entries(errors)) {
            // Map logical field names to actual input elements
            let inputs = [];
            if (field === 'startTime') {
                inputs = [document.getElementById('startHour'), document.getElementById('startMinute')];
            } else if (field === 'endTime') {
                inputs = [document.getElementById('endHour'), document.getElementById('endMinute')];
            } else {
                const single = document.getElementById(field);
                if (single) inputs = [single];
            }

            // Add invalid class to all related inputs (same behavior as other validations)
            inputs.forEach(input => {
                if (input) input.classList.add('is-invalid');
            });

            // Populate the error div so it displays like other field errors
            const errorDiv = document.getElementById(`${field}-error`);
            if (errorDiv) {
                errorDiv.textContent = message;
                // show the error div; leave visual styling to CSS so it matches other errors
                try { errorDiv.style.setProperty('display', 'block', 'important'); } catch (_) { errorDiv.style.display = 'block'; }
                errorDiv.setAttribute('role', 'alert');
                errorDiv.setAttribute('aria-live', 'assertive');
            }
        }
    }

    // helper: focus first invalid field after showing errors
    function focusFirstInvalidField() {
        const first = document.querySelector('.is-invalid');
        if (first) {
            try {
                if (typeof first.scrollIntoView === 'function') {
                    first.scrollIntoView({ behavior: 'smooth', block: 'center' });
                }
                if (typeof first.focus === 'function') {
                    first.focus();
                }
            } catch (err) {
                // ignore if focusing fails
                console.error('Could not focus invalid field', err);
            }
        }
    }

    window.editShift = async (id) => {
        try {
            const res = await fetch(`/api/manager/shifts/${id}`);
            const s = await res.json();
            openShiftModal(s);
        } catch (err) {
            console.error("❌ Lỗi edit shift:", err);
        }
    };

    window.deleteShift = async (id) => {
        if (!confirm("Bạn có chắc muốn xóa ca làm việc này?")) return;
        try {
            const res = await fetch(`/api/manager/shifts/${id}`, { method: "DELETE" });
            if (res.ok) {
                showToast("Đã xóa thành công!", 2500, 'success');
                loadShifts();
            } else {
                const error = await res.text();
                showToast(error || "Xóa thất bại!", 3000, 'error');
            }
        } catch (err) {
            console.error("❌ Lỗi xóa shift:", err);
            showToast("Có lỗi xảy ra khi xóa!", 3000, 'error');
        }
    };

    window.restoreShift = async (id) => {
        if (!confirm("Bạn có chắc muốn khôi phục ca làm việc này?")) return;
        try {
            const res = await fetch(`/api/manager/shifts/${id}/restore`, { method: "PATCH" });
            if (res.ok) {
                showToast("Đã khôi phục thành công!", 2500, 'success');
                loadShifts();
            } else {
                const error = await res.text();
                showToast(error || "Khôi phục thất bại!", 3000, 'error');
            }
        } catch (err) {
            console.error("❌ Lỗi khôi phục shift:", err);
            showToast("Có lỗi xảy ra khi khôi phục!", 3000, 'error');
        }
    };

    // ====================== EMPLOYEE MODAL ======================
    window.viewEmployees = async (shiftId) => {
        try {
            employeeModal.classList.remove("hidden");

            // Fetch shift details to show title as: "<name> (<start> - <end>)"
            try {
                const shiftRes = await fetch(`/api/manager/shifts/${shiftId}`);
                if (shiftRes.ok) {
                    const s = await shiftRes.json();
                    const st = s.startTime ? formatToHHMM(s.startTime) : "";
                    const et = s.endTime ? formatToHHMM(s.endTime) : "";
                    const titleText = `${s.name || "Ca"} ${st || et ? `(${st} - ${et})` : ""}`.trim();
                    const titleEl = document.getElementById("shiftEmployeeTitle");
                    if (titleEl) titleEl.innerText = titleText;
                } else {
                    const titleEl = document.getElementById("shiftEmployeeTitle");
                    if (titleEl) titleEl.innerText = `Nhân viên trong ca #${shiftId}`;
                }
            } catch (_) {
                const titleEl = document.getElementById("shiftEmployeeTitle");
                if (titleEl) titleEl.innerText = `Nhân viên trong ca #${shiftId}`;
            }

            const res = await fetch(`/api/manager/shifts/${shiftId}/assignments`);
            let emps = await res.json();
            emps = Array.isArray(emps) ? emps : [];

            // Align with table headers: Name | Role | Remaining Days | Last Work Date | Actions
            employeeTableBody.innerHTML = emps.length > 0
                ? emps.map(e => {
                    const remainingDays = e.remainingDays !== null && e.remainingDays !== undefined ? e.remainingDays : 0;
                    const lastWorkDate = e.lastWorkDate ? new Date(e.lastWorkDate).toLocaleDateString("vi-VN") : "Chưa có";
                    const remainingDaysClass = remainingDays < 7 ? 'style="color: red; font-weight: bold;"' : '';

                    return `
                    <tr>
                        <td>${e.userFullName || ""}</td>
                        <td>${e.roleName || ""}</td>
                        <td ${remainingDaysClass}>${remainingDays} ngày</td>
                        <td>${lastWorkDate}</td>
                        <td class="text-center">
                            <button class="btn btn-primary btn-sm" onclick="extendSchedule(${e.userId}, ${shiftId})" title="Thêm 30 ngày">
                                ➕ 30 ngày
                            </button>
                            <button class="btn btn-danger btn-icon" onclick="removeEmployee(${e.userId}, ${shiftId})" title="Xóa khỏi ca">
                                🗑️
                            </button>
                        </td>
                    </tr>
                `;
                }).join("")
                : `<tr>
                        <td colspan="5" style="text-align:center; padding: 20px; color: #6b7280; font-style: italic;">
                            Chưa có nhân viên nào trong ca này
                        </td>
                   </tr>`;

            // Load employee select options
            await loadEmployeeOptions(shiftId);
            assignBtn.onclick = () => assignEmployee(shiftId);

        } catch (err) {
            console.error("❌ Lỗi view employees:", err);
        }
    };

    async function loadEmployeeOptions(shiftId) {
        try {
            const res = await fetch(`/api/manager/shifts/${shiftId}/assign`);
            const employees = await res.json();
            employeeSelect.innerHTML = `<option value="">-- Chọn nhân viên thêm vào ca --</option>` +
                employees.map(e => `<option value="${e.id}">${e.fullName} (${e.roleName})</option>`).join("");
        } catch (err) {
            console.error("❌ Lỗi load employee options:", err);
        }
    }

    async function assignEmployee(shiftId) {
        const empId = employeeSelect.value;
        if (!empId) {
            showToast("Vui lòng chọn nhân viên", 2500, 'error');
            return;
        }

        try {
            const res = await fetch(`/api/manager/shifts/${shiftId}/assign`, {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({ userId: empId })
            });
            if (res.ok) {
                showToast("Thêm nhân viên vào ca thành công!", 2500, 'success');
                viewEmployees(shiftId);
            } else {
                const error = await res.text();
                showToast(error || "Thêm nhân viên thất bại!", 3000, 'error');
            }
        } catch (err) {
            console.error("❌ Lỗi assign employee:", err);
            showToast("Có lỗi xảy ra!", 3000, 'error');
        }
    }

    window.removeEmployee = async (userId, shiftId) => {
        if (!confirm("Bạn có chắc muốn gỡ nhân viên này khỏi ca?")) return;

        try {
            const res = await fetch(`/api/manager/shifts/${shiftId}/remove/${userId}`, { method: "DELETE" });
            if (res.ok) {
                showToast("Gỡ nhân viên khỏi ca thành công!", 2500, 'success');
                viewEmployees(shiftId);
            } else {
                const error = await res.text();
                showToast(error || "Gỡ nhân viên thất bại!", 3000, 'error');
            }
        } catch (err) {
            console.error("❌ Lỗi remove employee:", err);
            showToast("Có lỗi xảy ra!", 3000, 'error');
        }
    };

    window.extendSchedule = async (userId, shiftId) => {
        if (!confirm("Bạn có chắc muốn thêm 30 ngày làm việc cho nhân viên này?")) return;

        try {
            const res = await fetch(`/api/manager/shifts/${shiftId}/extend/${userId}`, { method: "POST" });
            if (res.ok) {
                showToast("Đã thêm 30 ngày làm việc thành công!", 2500, 'success');
                viewEmployees(shiftId);
            } else {
                const error = await res.text();
                showToast(error || "Thêm ngày làm việc thất bại!", 3000, 'error');
            }
        } catch (err) {
            console.error("❌ Lỗi extend schedule:", err);
            showToast("Có lỗi xảy ra!", 3000, 'error');
        }
    };

    // ====================== INIT ======================
    loadShifts();

    // Toggle deleted shifts button
    const btnToggleDeleted = document.getElementById("btnToggleDeleted");
    btnToggleDeleted.addEventListener("click", () => {
        showDeleted = !showDeleted;
        btnToggleDeleted.textContent = showDeleted ? "Ẩn ca đã xóa" : "Hiển thị ca đã xóa";
        loadShifts();
    });

    // Pagination controls
    document.getElementById('prev-page').addEventListener('click', () => {
        if (currentPage > 1) {
            currentPage--;
            renderTablePage();
        }
    });

    document.getElementById('next-page').addEventListener('click', () => {
        const totalPages = Math.ceil(allShifts.length / recordsPerPage);
        if (currentPage < totalPages) {
            currentPage++;
            renderTablePage();
        }
    });

    document.getElementById('records-per-page').addEventListener('change', () => {
        currentPage = 1;
        renderTablePage();
    });


    // Load shifts when page loads
    loadShifts();
});

// Helper: normalize various time inputs to 24-hour HH:mm format
function normalizeTimeTo24(input) {
    if (!input) return "";
    let s = input.trim();
    // If already in HH:mm or H:mm(:ss), return HH:mm
    const hhmm = s.match(/^(\d{1,2}):(\d{2})(?::\d{2})?$/);
    if (hhmm) {
        let h = parseInt(hhmm[1], 10);
        let m = hhmm[2];
        return (h < 10 ? '0' + h : '' + h) + ':' + m;
    }
    // Match 12h formats like '12:00 AM', '12 PM', '9:30pm', '9 PM'
    const ampm = s.match(/^(\d{1,2})(?::(\d{2}))?\s*(AM|PM|am|pm|Am|Pm|aM|pM)?$/);
    if (ampm) {
        let hour = parseInt(ampm[1], 10);
        let minute = ampm[2] ? ampm[2] : '00';
        const meridiem = (ampm[3] || '').toUpperCase();
        if (meridiem === 'AM') {
            if (hour === 12) hour = 0;
        } else if (meridiem === 'PM') {
            if (hour !== 12) hour += 12;
        }
        if (hour < 0) hour = 0;
        if (hour > 23) hour = hour % 24;
        const hh = hour < 10 ? '0' + hour : '' + hour;
        return hh + ':' + minute;
    }
    // Fallback: try Date parsing
    try {
        const d = new Date('1970-01-01T' + s);
        if (!isNaN(d.getTime())) {
            const hh = d.getHours();
            const mm = d.getMinutes();
            return (hh < 10 ? '0' + hh : '' + hh) + ':' + (mm < 10 ? '0' + mm : '' + mm);
        }
    } catch (_) {}
    return s; // return as-is
}

// Helper: format any time-like string to HH:mm for display
function formatToHHMM(input) {
    if (!input) return "";
    const normalized = normalizeTimeTo24(input);
    // Ensure HH:mm (pad if necessary)
    const m = normalized.match(/^(\d{1,2}):(\d{2})$/);
    if (m) {
        const h = parseInt(m[1], 10);
        const mm = m[2];
        return (h < 10 ? '0' + h : '' + h) + ':' + mm;
    }
    return normalized;
}
