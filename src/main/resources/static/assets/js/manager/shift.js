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

        await fetch("/api/manager/shifts", {
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
        const res = await fetch(`/api/manager/shifts/${shiftId}/employees`);
        const emps = await res.json();
        const tbody = document.getElementById("employeeTableBody");
        tbody.innerHTML = emps.map(e => `
      <tr>
        <td>${e.fullName}</td>
        <td>${e.phoneNumber}</td>
        <td>${e.workType}</td>
      </tr>
    `).join("");
        employeeModal.classList.remove("hidden");
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
