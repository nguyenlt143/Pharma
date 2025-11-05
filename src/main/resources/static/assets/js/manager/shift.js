document.addEventListener("DOMContentLoaded", () => {
    const btnAddShift = document.getElementById("btnAddShift");
    const shiftModal = document.getElementById("shiftModal");
    const employeeModal = document.getElementById("employeeModal");
    const btnCancel = document.getElementById("btnCancel");
    const closeEmployeeModal = document.getElementById("closeEmployeeModal");
    const shiftForm = document.getElementById("shiftForm");
    const shiftTableBody = document.getElementById("shiftTableBody");

    // Load danh sách ca
    loadShifts();

    btnAddShift.onclick = () => openShiftModal();
    btnCancel.onclick = () => closeModal(shiftModal);
    closeEmployeeModal.onclick = () => closeModal(employeeModal);

    shiftForm.onsubmit = async (e) => {
        e.preventDefault();
        const payload = {
            id: document.getElementById("shiftId").value || null,
            name: document.getElementById("shiftName").value,
            startTime: document.getElementById("startTime").value,
            endTime: document.getElementById("endTime").value,
            note: document.getElementById("note").value,
        };

        const url = payload.id ? `/api/manager/shifts/${payload.id}` : "/api/manager/shifts";
        await fetch(url, {
            method: payload.id ? "PUT" : "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(payload),
        });

        closeModal(shiftModal);
        loadShifts();
    };

    async function loadShifts() {
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
    }

    window.editShift = async (id) => {
        const res = await fetch(`/api/manager/shifts/${id}`);
        const s = await res.json();
        openShiftModal(s);
    };

    window.viewEmployees = async (shiftId) => {
        const res = await fetch(`/api/manager/shifts/${shiftId}/works`);
        let emps = await res.json();
        emps = Array.isArray(emps) ? emps : [];

        document.getElementById("shiftEmployeeTitle").innerText = `Nhân viên trong ca #${shiftId}`;

        const tbody = document.getElementById("employeeTableBody");

        if (emps.length > 0) {
            tbody.innerHTML = emps.map(e => `
            <tr>
                <td>${e.userFullName || ""}</td>
                <td>${e.roleName || ""}</td>
                <td>
                    <span class="badge ${
                e.status === 'Not Started' ? 'not-started' :
                    e.status === 'In Work' ? 'in-work' : 'done'
            }">${e.status}</span>
                </td>
                <td>${e.createdAt || ""}</td>
                <td><span class="action-delete" onclick="removeEmployee(${e.id})">Delete</span></td>
            </tr>
        `).join("");
        } else {
            tbody.innerHTML = `
            <tr>
                <td colspan="5" style="text-align:center; padding: 12px; color: #6b7280;">
                    Chưa có nhân viên nào trong ca
                </td>
            </tr>
        `;
        }

        employeeModal.classList.remove("hidden");
    };


    window.DeleteShift = async (id) => {
        if (!confirm("Bạn có chắc muốn xóa ca làm việc này?")) return;

        const res = await fetch(`/api/manager/shifts/${id}`, {
            method: "DELETE"
        });

        if (res.ok) {
            alert("Đã xóa thành công!");
            loadShifts();
        } else {
            alert("Xóa thất bại!");
        }
    };

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
});
