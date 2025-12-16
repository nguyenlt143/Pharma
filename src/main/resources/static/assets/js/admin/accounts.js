/* accounts.js
   Client-side renderer + API interaction for /admin/accounts
   API base: /api/admin/accounts
*/
(() => {
    const API_BASE = '/api/admin/accounts';
    const tableBody = document.getElementById('accountTableBody');
    const searchInput = document.getElementById('searchInput');
    const btnCreate = document.getElementById('btnCreateAccount');
    const btnToggleDeleted = document.getElementById('btnToggleDeleted');
    const emptyState = document.getElementById('emptyState');

    // Modal elements
    const modal = document.getElementById('accountModal');
    const modalTitle = document.getElementById('modalTitle');
    const modalClose = document.getElementById('modalClose');
    const accountForm = document.getElementById('accountForm');
    const accountIdInput = document.getElementById('accountId');
    const passwordWrapper = document.getElementById('passwordWrapper');
    const branchWrapper = document.getElementById('branchWrapper');
    const roleIdSelect = document.getElementById('roleId');
    const branchIdSelect = document.getElementById('branchId');
    const toastEl = document.getElementById('toast');

    // Role labels and keep a copy of the original role select options so we can restore later
    const roleLabels = {
        '2': 'OWNER',
        '3': 'MANAGER',
        '5': 'WAREHOUSE'
    };
    const originalRoleOptions = roleIdSelect ? roleIdSelect.innerHTML : '';

    let allAccounts = [];
    let filteredAccounts = [];
    let currentPage = 1;
    let recordsPerPage = 25;
    let showDeleted = false;

    // UTIL
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
        // Force reflow to ensure animation works
        void toastEl.offsetWidth;
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

    function updateBranchFieldVisibility() {
        const roleId = roleIdSelect.value;
        // Role ID 2 = OWNER - no branch required (chi nhánh tổng)
        // Role ID 5 = WAREHOUSE - no branch required (kho tổng)
        // Role ID 3 = MANAGER - branch required
        if (roleId === '2' || roleId === '5') {
            // OWNER or WAREHOUSE - hide branch field (thuộc chi nhánh tổng)
            branchWrapper.style.display = 'none';
            branchIdSelect.removeAttribute('required');
            branchIdSelect.value = '';
        } else if (roleId === '3') {
            // MANAGER - show and require branch
            branchWrapper.style.display = '';
            branchIdSelect.setAttribute('required', 'required');
        } else {
            // No role selected - hide
            branchWrapper.style.display = 'none';
            branchIdSelect.removeAttribute('required');
        }
    }

    function openModal(mode = 'create', data = null) {
        modal.classList.remove('hidden');
        modal.setAttribute('aria-hidden', 'false');

        if (mode === 'create') {
            modalTitle.textContent = 'Tạo tài khoản';
            accountIdInput.value = '';
            accountForm.reset();
            // Always show password field on create
            if (passwordWrapper) passwordWrapper.style.display = '';
            // Restore role select to original options and ensure enabled
            try {
                if (roleIdSelect) {
                    roleIdSelect.innerHTML = originalRoleOptions;
                    roleIdSelect.disabled = false;
                }
            } catch (_) {}
            updateBranchFieldVisibility();
        } else {
            modalTitle.textContent = 'Chỉnh sửa tài khoản';
            accountIdInput.value = data.id || '';
            document.getElementById('userName').value = data.userName || '';
            document.getElementById('fullName').value = data.fullName || '';

            // Map roleName to roleId
            const roleMap = {
                'OWNER': '2',
                'MANAGER': '3',
                'WAREHOUSE': '5'
            };
            const roleId = roleMap[data.roleName] || '';
            // If it's OWNER, show a single temporary option and disable select (view-only)
            if (roleId === '2') {
                try {
                    // replace options with a single temporary option for OWNER
                    roleIdSelect.innerHTML = '';
                    const opt = document.createElement('option');
                    opt.value = '2';
                    opt.textContent = roleLabels['2'] || 'OWNER';
                    roleIdSelect.appendChild(opt);
                    roleIdSelect.value = '2';
                    roleIdSelect.disabled = true;
                } catch (_) { /* ignore DOM errors */ }
            } else {
                // restore original options and set the value
                try { roleIdSelect.innerHTML = originalRoleOptions; } catch (_) {}
                roleIdSelect.value = roleId || '';
                roleIdSelect.disabled = false;
            }

            document.getElementById('email').value = data.email || '';
            document.getElementById('phoneNumber').value = data.phoneNumber || '';
            branchIdSelect.value = data.branchId || '';

            // Show password field also on edit (user can change password)
            if (passwordWrapper) passwordWrapper.style.display = '';
            // Do not pre-fill password for security
            const pwdInput = passwordWrapper ? passwordWrapper.querySelector('input') : null;
            if (pwdInput) pwdInput.value = '';

            updateBranchFieldVisibility();
        }
    }

    function closeModal() {
        modal.classList.add('hidden');
        modal.setAttribute('aria-hidden', 'true');
    }

    // API calls
    async function fetchAll() {
        try {
            const url = showDeleted ? `${API_BASE}?showDeleted=true` : API_BASE;
            const res = await fetch(url);
            if (!res.ok) {
                const ct = res.headers.get('content-type') || '';
                if (ct.includes('application/json')) {
                    const data = await res.json();
                    throw new Error(data.message || 'Lỗi khi lấy danh sách');
                }
                const txt = await res.text();
                throw new Error(txt || 'Lỗi khi lấy danh sách');
            }
            allAccounts = await res.json();
            applySearchAndRender();
        } catch (e) {
            console.error(e);
            showToast(e.message || 'Lỗi mạng', 3000, 'error');
        }
    }

    async function fetchById(id) {
        const res = await fetch(`${API_BASE}/${id}`);
        if (!res.ok) {
            const ct = res.headers.get('content-type') || '';
            if (ct.includes('application/json')) {
                const data = await res.json();
                throw new Error(data.message || 'Không tìm thấy tài khoản');
            }
            throw new Error('Không tìm thấy tài khoản');
        }
        return await res.json();
    }

    async function createAccount(payload) {
        const res = await fetch(API_BASE, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload),
        });
        if (!res.ok) {
            const ct = res.headers.get('content-type') || '';
            if (ct.includes('application/json')) {
                const data = await res.json();
                if (data.errors) {
                    // convert field errors to single message for toast
                    const first = Object.values(data.errors)[0];
                    throw new Error(first || data.message || 'Tạo thất bại');
                }
                throw new Error(data.message || 'Tạo thất bại');
            }
            const errorText = await res.text();
            throw new Error(errorText || 'Tạo thất bại');
        }
        return await res.json();
    }

    async function updateAccount(id, payload) {
        const res = await fetch(`${API_BASE}/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload),
        });
        if (!res.ok) {
            const ct = res.headers.get('content-type') || '';
            if (ct.includes('application/json')) {
                const data = await res.json();
                if (data.errors) {
                    const first = Object.values(data.errors)[0];
                    throw new Error(first || data.message || 'Cập nhật thất bại');
                }
                throw new Error(data.message || 'Cập nhật thất bại');
            }
            const errorText = await res.text();
            throw new Error(errorText || 'Cập nhật thất bại');
        }
        return await res.json();
    }

    async function deleteAccount(id) {
        const res = await fetch(`${API_BASE}/${id}`, { method: 'DELETE' });
        if (!res.ok) {
            const ct = res.headers.get('content-type') || '';
            if (ct.includes('application/json')) {
                const data = await res.json();
                throw new Error(data.message || 'Xoá thất bại');
            }
            const errorText = await res.text();
            throw new Error(errorText || 'Xoá thất bại');
        }
        return;
    }

    async function restoreAccount(id) {
        const res = await fetch(`${API_BASE}/${id}/restore`, { method: 'PATCH' });
        if (!res.ok) {
            const contentType = res.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                const errorData = await res.json();
                throw new Error(errorData.message || 'Khôi phục thất bại');
            }
            const errorText = await res.text();
            throw new Error(errorText || 'Khôi phục thất bại');
        }
        return;
    }

    // Render & Pagination
    function updatePaginationControls() {
        const pageInfo = document.getElementById('page-info');
        const prevPageBtn = document.getElementById('prev-page');
        const nextPageBtn = document.getElementById('next-page');
        const recordsPerPageSelect = document.getElementById('records-per-page');

        recordsPerPage = parseInt(recordsPerPageSelect.value, 10);
        const totalRecords = filteredAccounts.length;
        const totalPages = Math.ceil(totalRecords / recordsPerPage) || 1;

        if (currentPage > totalPages) {
            currentPage = totalPages;
        }

        if (pageInfo) pageInfo.textContent = `Trang ${currentPage} / ${totalPages}`;
        if (prevPageBtn) prevPageBtn.disabled = currentPage === 1;
        if (nextPageBtn) nextPageBtn.disabled = currentPage === totalPages;
    }

    function renderTablePage() {
        updatePaginationControls();
        tableBody.innerHTML = '';

        if (!filteredAccounts || filteredAccounts.length === 0) {
            emptyState.classList.remove('hidden');
            return;
        }
        emptyState.classList.add('hidden');

        const startIndex = (currentPage - 1) * recordsPerPage;
        const endIndex = startIndex + recordsPerPage;
        const pageData = filteredAccounts.slice(startIndex, endIndex);

        pageData.forEach((account, idx) => {
            const tr = document.createElement('tr');
            tr.setAttribute('data-id', account.id);

            tr.innerHTML = `
                <td class="text-center">${startIndex + idx + 1}</td>
                <td class="font-medium">${escapeHtml(account.userName || '')}</td>
                <td>${escapeHtml(account.fullName || '')}</td>
                <td>${escapeHtml(account.roleName || '')}</td>
                <td>${escapeHtml(account.branchName || '-')}</td>
                <td>${escapeHtml(account.email || '')}</td>
                <td>${escapeHtml(account.phoneNumber || '')}</td>
                <td>${escapeHtml(account.password || '')}</td>
                <td class="text-center">
                  <span class="badge ${account.deleted ? 'inactive' : 'active'}">${account.deleted ? 'Đã xóa' : 'Hoạt động'}</span>
                </td>
                <td class="text-center">
                  ${account.deleted ? `
                    <button class="btn btn-success restore-btn" data-id="${account.id}" title="Khôi phục">↩ Khôi phục</button>
                  ` : `
                    <button class="btn btn-ghost edit-btn" data-id="${account.id}" title="Sửa">✏️</button>
                    <button class="btn btn-danger del-btn" data-id="${account.id}" title="Xoá">🗑️</button>
                  `}
                </td>
              `;

            tableBody.appendChild(tr);
        });

        attachActionHandlers();
    }

    function applySearchAndRender() {
        const q = searchInput.value.trim().toLowerCase();
        if (!q) {
            filteredAccounts = [...allAccounts];
        } else {
            filteredAccounts = allAccounts.filter(account =>
                (account.userName || '').toLowerCase().includes(q) ||
                (account.fullName || '').toLowerCase().includes(q) ||
                (account.email || '').toLowerCase().includes(q) ||
                (account.roleName || '').toLowerCase().includes(q) ||
                (account.branchName || '').toLowerCase().includes(q)
            );
        }
        currentPage = 1;
        renderTablePage();
    }

    // Render
    function attachActionHandlers() {
        document.querySelectorAll('.edit-btn').forEach(b => {
            b.addEventListener('click', async (ev) => {
                const id = ev.currentTarget.dataset.id;
                try {
                    const data = await fetchById(id);
                    openModal('edit', data);
                } catch (e) {
                    showToast(e.message || 'Lỗi', 3000, 'error');
                }
            });
        });

        document.querySelectorAll('.del-btn').forEach(b => {
            b.addEventListener('click', async (ev) => {
                const id = ev.currentTarget.dataset.id;
                if (!confirm('Bạn chắc chắn muốn xoá tài khoản này?')) return;
                try {
                    await deleteAccount(id);
                    showToast('Xoá thành công', 2500, 'success');
                    await fetchAll();
                } catch (e) {
                    showToast(e.message || 'Lỗi', 4000, 'error');
                }
            });
        });

        document.querySelectorAll('.restore-btn').forEach(b => {
            b.addEventListener('click', async (ev) => {
                const id = ev.currentTarget.dataset.id;
                try {
                    await restoreAccount(id);
                    showToast('Khôi phục thành công', 2500, 'success');
                    await fetchAll();
                } catch (e) {
                    showToast(e.message || 'Lỗi', 3000, 'error');
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

    function setupEventListeners() {
        // Create button
        btnCreate.addEventListener('click', () => openModal('create'));

        // Toggle deleted button
        function updateToggleDeletedBtn() {
            if (!btnToggleDeleted) return;
            if (showDeleted) {
                btnToggleDeleted.textContent = 'Ẩn tài khoản đã xóa';
                btnToggleDeleted.classList.remove('btn-outline');
                btnToggleDeleted.classList.add('btn-primary');
            } else {
                btnToggleDeleted.textContent = 'Hiển thị tài khoản đã xóa';
                btnToggleDeleted.classList.remove('btn-primary');
                btnToggleDeleted.classList.add('btn-outline');
            }
        }
        if (btnToggleDeleted) {
            btnToggleDeleted.addEventListener('click', async () => {
                showDeleted = !showDeleted;
                updateToggleDeletedBtn();
                await fetchAll();
            });
            updateToggleDeletedBtn();
        }

        // Role change handler - update branch field visibility
        roleIdSelect.addEventListener('change', updateBranchFieldVisibility);

        // Modal close
        modalClose.addEventListener('click', () => {
            Array.from(accountForm.elements).forEach(el => el.disabled = false);
            // restore role select enabled and original options when closing
            try {
                if (roleIdSelect) {
                    roleIdSelect.disabled = false;
                    roleIdSelect.innerHTML = originalRoleOptions;
                }
            } catch (_) {}
            document.getElementById('btnSave').style.display = '';
            document.getElementById('btnCancel').textContent = 'Hủy';
            closeModal();
        });
        document.getElementById('btnCancel').addEventListener('click', () => {
            modalClose.click();
        });

        // Form submit
        accountForm.addEventListener('submit', async (ev) => {
            ev.preventDefault();
            const form = new FormData(accountForm);
            const payload = {
                userName: form.get('userName'),
                fullName: form.get('fullName'),
                roleId: Number(form.get('roleId') || null),
                email: form.get('email')?.trim() || null,
                phoneNumber: form.get('phoneNumber')?.trim() || null,
                branchId: form.get('branchId') ? Number(form.get('branchId')) : null,
                password: form.get('password') || undefined
            };

            const id = accountIdInput.value;
            try {
                if (id) {
                    await updateAccount(id, payload);
                    showToast('Cập nhật thành công', 2500, 'success');
                } else {
                    await createAccount(payload);
                    showToast('Tạo thành công', 2500, 'success');
                }
                Array.from(accountForm.elements).forEach(el => el.disabled = false);
                document.getElementById('btnSave').style.display = '';
                document.getElementById('btnCancel').textContent = 'Hủy';
                closeModal();
                await fetchAll();
            } catch (e) {
                console.error(e);
                showToast(e.message || 'Lỗi khi lưu', 3000, 'error');
            }
        });

        // Search
        let searchTimeout = null;
        searchInput.addEventListener('input', () => {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(applySearchAndRender, 200);
        });

        // Pagination controls
        document.getElementById('prev-page').addEventListener('click', () => {
            if (currentPage > 1) {
                currentPage--;
                renderTablePage();
            }
        });

        document.getElementById('next-page').addEventListener('click', () => {
            const totalPages = Math.ceil(filteredAccounts.length / recordsPerPage);
            if (currentPage < totalPages) {
                currentPage++;
                renderTablePage();
            }
        });

        document.getElementById('records-per-page').addEventListener('change', () => {
            currentPage = 1;
            renderTablePage();
        });
    }

    // Initial load
    setupEventListeners();
    fetchAll();
})();
