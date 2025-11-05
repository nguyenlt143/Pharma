/* staff.js
   Client-side renderer + API interaction for /manager/staff
   API base: /api/manager/staff
*/
(() => {
    const API_BASE = '/api/manager/staffs';
    const tableBody = document.getElementById('staffTableBody');
    const searchInput = document.getElementById('searchInput');
    const btnCreate = document.getElementById('btnCreateStaff');
    const btnRefresh = document.getElementById('btnRefresh');
    const emptyState = document.getElementById('emptyState');

    // Modal elements
    const modal = document.getElementById('staffModal');
    const modalTitle = document.getElementById('modalTitle');
    const modalClose = document.getElementById('modalClose');
    const staffForm = document.getElementById('staffForm');
    const staffIdInput = document.getElementById('staffId');
    const passwordWrapper = document.getElementById('passwordWrapper');
    const toastEl = document.getElementById('toast');

    let staffList = [];

    // UTIL
    function showToast(msg, timeout = 2500) {
        toastEl.textContent = msg;
        toastEl.classList.remove('hidden');
        setTimeout(() => toastEl.classList.add('hidden'), timeout);
    }

    function openModal(mode = 'create', data = null) {
        modal.classList.remove('hidden');
        modal.setAttribute('aria-hidden', 'false');
        if (mode === 'create') {
            modalTitle.textContent = 'Tạo nhân viên';
            staffIdInput.value = '';
            staffForm.reset();
            passwordWrapper.style.display = '';
        } else {
            modalTitle.textContent = 'Chỉnh sửa nhân viên';
            staffIdInput.value = data.id || '';
            document.getElementById('userName').value = data.userName || '';
            document.getElementById('fullName').value = data.fullName || '';
            document.getElementById('roleId').value = data.roleId || '';
            document.getElementById('email').value = data.email || '';
            document.getElementById('phoneNumber').value = data.phoneNumber || '';
            document.getElementById('branchId').value = data.branchId || '';
            document.getElementById('imageUrl').value = data.imageUrl || '';
            // hide password on edit
            passwordWrapper.style.display = 'none';
        }
    }

    function closeModal() {
        modal.classList.add('hidden');
        modal.setAttribute('aria-hidden', 'true');
    }

    // API calls
    async function fetchAll() {
        try {
            const res = await fetch(API_BASE);
            if (!res.ok) throw new Error('Lỗi khi lấy danh sách');
            staffList = await res.json();
            renderTable(staffList);
        } catch (e) {
            console.error(e);
            showToast(e.message || 'Lỗi mạng');
        }
    }

    async function fetchById(id) {
        const res = await fetch(`${API_BASE}/${id}`);
        if (!res.ok) throw new Error('Không tìm thấy nhân viên');
        return await res.json();
    }

    async function createStaff(payload) {
        const res = await fetch(API_BASE, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload),
        });
        if (!res.ok) throw new Error('Tạo thất bại');
        return await res.json();
    }

    async function updateStaff(id, payload) {
        const res = await fetch(`${API_BASE}/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload),
        });
        if (!res.ok) throw new Error('Cập nhật thất bại');
        return await res.json();
    }

    async function deleteStaff(id) {
        const res = await fetch(`${API_BASE}/${id}`, { method: 'DELETE' });
        if (!res.ok) throw new Error('Xoá thất bại');
        return;
    }

    // Render
    function renderTable(list) {
        tableBody.innerHTML = '';
        if (!list || list.length === 0) {
            emptyState.classList.remove('hidden');
            return;
        }
        emptyState.classList.add('hidden');

        list.forEach((s, idx) => {
            const tr = document.createElement('tr');
            tr.setAttribute('data-id', s.id);

            tr.innerHTML = `
        <td class="text-center">${idx + 1}</td>
        <td class="font-medium">${escapeHtml(s.userName || '')}</td>
        <td>${escapeHtml(s.fullName || '')}</td>
        <td>${escapeHtml(s.roleName || '')}</td>
        <td>${escapeHtml(s.email || '')}</td>
        <td>${escapeHtml(s.phoneNumber || '')}</td>
        <td class="text-center"><span class="badge">${escapeHtml(s.accountStatus || 'active')}</span></td>
        <td class="text-center">
          <button class="btn btn-ghost view-btn" data-id="${s.id}" title="Xem">👁️</button>
          <button class="btn btn-ghost edit-btn" data-id="${s.id}" title="Sửa">✏️</button>
          <button class="btn btn-danger del-btn" data-id="${s.id}" title="Xoá">🗑️</button>
        </td>
      `;

            tableBody.appendChild(tr);
        });

        // attach handlers
        document.querySelectorAll('.edit-btn').forEach(b => {
            b.addEventListener('click', async (ev) => {
                const id = ev.currentTarget.dataset.id;
                try {
                    const data = await fetchById(id);
                    openModal('edit', data);
                } catch (e) {
                    showToast(e.message || 'Lỗi');
                }
            });
        });

        document.querySelectorAll('.del-btn').forEach(b => {
            b.addEventListener('click', async (ev) => {
                const id = ev.currentTarget.dataset.id;
                if (!confirm('Bạn chắc chắn muốn xoá nhân viên này?')) return;
                try {
                    await deleteStaff(id);
                    showToast('Xoá thành công');
                    await fetchAll();
                } catch (e) {
                    showToast(e.message || 'Lỗi');
                }
            });
        });

        document.querySelectorAll('.view-btn').forEach(b => {
            b.addEventListener('click', async (ev) => {
                const id = ev.currentTarget.dataset.id;
                try {
                    const data = await fetchById(id);
                    // quick view: use modal but show fields readonly
                    openModal('edit', data);
                    // make fields readonly
                    Array.from(staffForm.elements).forEach(el => el.disabled = true);
                    document.getElementById('btnSave').style.display = 'none';
                    document.getElementById('btnCancel').textContent = 'Đóng';
                    // restore on close
                } catch (e) {
                    showToast(e.message || 'Lỗi');
                }
            });
        });
    }

    // helpers
    function escapeHtml(s) {
        if (!s) return '';
        return String(s)
            .replaceAll('&', '&amp;')
            .replaceAll('<', '&lt;')
            .replaceAll('>', '&gt;')
            .replaceAll('"', '&quot;')
            .replaceAll("'", '&#39;');
    }

    // events
    btnCreate.addEventListener('click', () => openModal('create'));
    btnRefresh.addEventListener('click', () => fetchAll());
    modalClose.addEventListener('click', () => {
        // re-enable form if was readonly
        Array.from(staffForm.elements).forEach(el => el.disabled = false);
        document.getElementById('btnSave').style.display = '';
        document.getElementById('btnCancel').textContent = 'Hủy';
        closeModal();
    });
    document.getElementById('btnCancel').addEventListener('click', () => {
        modalClose.click();
    });

    // submit
    staffForm.addEventListener('submit', async (ev) => {
        ev.preventDefault();
        const form = new FormData(staffForm);
        const payload = {
            userName: form.get('userName'),
            fullName: form.get('fullName'),
            roleId: Number(form.get('roleId') || null),
            email: form.get('email'),
            phoneNumber: form.get('phoneNumber'),
            branchId: form.get('branchId') ? Number(form.get('branchId')) : null,
            imageUrl: form.get('imageUrl'),
            password: form.get('password') || undefined
        };

        const id = staffIdInput.value;
        try {
            if (id) {
                await updateStaff(id, payload);
                showToast('Cập nhật thành công');
            } else {
                await createStaff(payload);
                showToast('Tạo thành công');
            }
            // restore buttons and fields
            Array.from(staffForm.elements).forEach(el => el.disabled = false);
            document.getElementById('btnSave').style.display = '';
            document.getElementById('btnCancel').textContent = 'Hủy';
            closeModal();
            await fetchAll();
        } catch (e) {
            console.error(e);
            showToast(e.message || 'Lỗi khi lưu');
        }
    });

    // search
    let searchTimeout = null;
    searchInput.addEventListener('input', (e) => {
        clearTimeout(searchTimeout);
        const q = e.target.value.trim().toLowerCase();
        searchTimeout = setTimeout(() => {
            if (!q) {
                renderTable(staffList);
                return;
            }
            const filtered = staffList.filter(s =>
                (s.userName || '').toLowerCase().includes(q) ||
                (s.fullName || '').toLowerCase().includes(q) ||
                (s.email || '').toLowerCase().includes(q) ||
                (s.roleName || '').toLowerCase().includes(q)
            );
            renderTable(filtered);
        }, 200);
    });

    // initial load
    fetchAll();
})();
