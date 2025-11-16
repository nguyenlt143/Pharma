document.addEventListener("DOMContentLoaded", () => {
    const btnAddShift = document.getElementById("btnAddShift");
    const shiftModal = document.getElementById("shiftModal");
    const employeeModal = document.getElementById("employeeModal");
    const btnCancel = document.getElementById("btnCancel");
    const closeEmployeeModal = document.getElementById("closeEmployeeModal");
    const shiftForm = document.getElementById("shiftForm");
    const shiftTableBody = document.getElementById("shiftTableBody");
    const employeeTableBody = document.getElementById("employeeTableBody");
    const employeeSelect = document.getElementById("employeeSelect");
    const assignBtn = document.getElementById("assignBtn");

    // ====================== MODAL OPEN/CLOSE ======================
    btnAddShift.onclick = () => openShiftModal();
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
        document.getElementById("modalTitle").textContent = s.id ? "Chỉnh sửa ca" : "Thêm ca mới";
    }

    function closeModal(modal) {
        modal.classList.add("hidden");
    }

    // ====================== LOAD SHIFTS ======================
    async function loadShifts() {
        try {
            const res = await fetch("/api/manager/shifts");
            const shifts = await res.json();
            shiftTableBody.innerHTML = shifts.map(s => `
                <tr>
                    <td>${s.name}</td>
                    <td>${s.startTime}</td>
                    <td>${s.endTime}</td>
                    <td>${s.note || ""}</td>
                    <td>
                        <button onclick="editShift(${s.id})">Sửa</button>
                        <button onclick="DeleteShift(${s.id})">Xóa</button>
                        <button onclick="viewEmployees(${s.id})">Nhân viên</button>
                    </td>
                </tr>
            `).join("");
        } catch (err) {
            console.error("❌ Lỗi load shifts:", err);
        }
    }

    // ====================== ADD / EDIT SHIFT ======================
    shiftForm.onsubmit = async (e) => {
        e.preventDefault();
        const payload = {
            id: document.getElementById("shiftId").value || null,
            name: document.getElementById("shiftName").value,
            startTime: document.getElementById("startTime").value,
            endTime: document.getElementById("endTime").value,
            note: document.getElementById("note").value,
        };

        try {
            const url = payload.id ? `/api/manager/shifts/${payload.id}` : "/api/manager/shifts";
            await fetch(url, {
                method: payload.id ? "PUT" : "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(payload),
            });
            closeModal(shiftModal);
            loadShifts();
        } catch (err) {
            console.error("❌ Lỗi lưu shift:", err);
        }
    };

    window.editShift = async (id) => {
        try {
            const res = await fetch(`/api/manager/shifts/${id}`);
            const s = await res.json();
            openShiftModal(s);
        } catch (err) {
            console.error("❌ Lỗi edit shift:", err);
        }
    };

    window.DeleteShift = async (id) => {
        if (!confirm("Bạn có chắc muốn xóa ca làm việc này?")) return;
        try {
            const res = await fetch(`/api/manager/shifts/${id}`, { method: "DELETE" });
            if (res.ok) {
                alert("Đã xóa thành công!");
                loadShifts();
            } else {
                alert("Xóa thất bại!");
            }
        } catch (err) {
            console.error("❌ Lỗi xóa shift:", err);
        }
    };

    // ====================== EMPLOYEE MODAL ======================
    window.viewEmployees = async (shiftId) => {
        try {
            employeeModal.classList.remove("hidden");

            const titleEl = document.getElementById("shiftEmployeeTitle");
            if (titleEl) titleEl.innerText = `Nhân viên trong ca #${shiftId}`;

            const res = await fetch(`/api/manager/shifts/${shiftId}/works`);
            let emps = await res.json();
            emps = Array.isArray(emps) ? emps : [];

            employeeTableBody.innerHTML = emps.length > 0
                ? emps.map(e => `
                    <tr>
                        <td>${e.userFullName || ""}</td>
                        <td>${e.roleName || ""}</td>
                        <td>
                            <span class="badge ${
                    e.status === 'Not Started' ? 'not-started' :
                        e.status === 'In Work' ? 'in-work' : 'done'
                }">${e.status}</span>
                        </td>
                        <td>${e.createdAt ? new Date(e.createdAt).toLocaleString("vi-VN") : ""}</td>
                        <td>
                            <span class="action-delete" onclick="removeEmployee(${e.id}, ${shiftId})">Delete</span>
                        </td>
                    </tr>
                `).join("")
                : `<tr>
                        <td colspan="5" style="text-align:center; padding: 12px; color: #6b7280;">
                            Chưa có nhân viên nào trong ca
                        </td>
                   </tr>`;

            // Load employee select options
            await loadEmployeeOptions();
            assignBtn.onclick = () => assignEmployee(shiftId);

        } catch (err) {
            console.error("❌ Lỗi view employees:", err);
        }
    };

    async function loadEmployeeOptions() {
        try {
            const res = await fetch(`/api/manager/shifts/${shiftId}/assign`);
            const employees = await res.json();
            employeeSelect.innerHTML = `<option value="">Select an employee to assign...</option>` +
                employees.map(e => `<option value="${e.id}">${e.fullName} (${e.roleName})</option>`).join("");
        } catch (err) {
            console.error("❌ Lỗi load employee options:", err);
        }
    }

    async function assignEmployee(shiftId) {
        const empId = employeeSelect.value;
        if (!empId) return alert("Vui lòng chọn nhân viên");

        try {
            await fetch(`/api/manager/shifts/${shiftId}/assign`, {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({ userId: empId })
            });
            viewEmployees(shiftId);
        } catch (err) {
            console.error("❌ Lỗi assign employee:", err);
        }
    }

    window.removeEmployee = async (empId, shiftId) => {
        if (!confirm("Bạn có chắc muốn gỡ nhân viên này khỏi ca?")) return;

        try {
            await fetch(`/api/manager/shifts/${shiftId}/remove/${empId}`, { method: "DELETE" });
            viewEmployees(shiftId);
        } catch (err) {
            console.error("❌ Lỗi remove employee:", err);
        }
    };

    // ====================== INIT ======================
    loadShifts();
});
