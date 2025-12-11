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
            console.error('Toast element not found');
            alert(msg);
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
        document.getElementById("startTime").value = s.startTime || "";
        document.getElementById("endTime").value = s.endTime || "";
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

            return `
            <tr>
                <td>${s.name}</td>
                <td>${s.startTime}</td>
                <td>${s.endTime}</td>
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

        const startTime = document.getElementById("startTime").value;
        const endTime = document.getElementById("endTime").value;

        // Frontend validation: end time must be after start time
        if (startTime && endTime && endTime <= startTime) {
            displayFieldErrors({
                'endTime': 'Giờ kết thúc phải lớn hơn giờ bắt đầu'
            });
            focusFirstInvalidField();
            return;
        }

        const payload = {
            id: idVal,
            name: document.getElementById("shiftName").value,
            startTime: startTime,
            endTime: endTime,
            note: document.getElementById("note").value
        };

        try {
            const url = payload.id ? `/api/manager/shifts/${payload.id}` : "/api/manager/shifts";
            const res = await fetch(url, {
                method: payload.id ? "PUT" : "POST",
                headers: {"Content-Type": "application/json"},
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
                        // Do NOT show toast for field-level validation errors
                        if (!errorData.errors || Object.keys(errorData.errors).length === 0) {
                            if (errorData.message) showToast(errorData.message, 4000, 'error');
                        }
                    } else {
                        // Business logic error - show toast only
                        showToast(errorData.message || "Lỗi khi lưu ca làm việc", 4000, 'error');
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
                    const st = s.startTime ? s.startTime : "";
                    const et = s.endTime ? s.endTime : "";
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
