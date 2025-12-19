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

    // Use global toast system from toast.js
    // showToast(message, type, duration) is available globally

    function updateBranchFieldVisibility() {
        const roleId = roleIdSelect.value;
        // Role ID 2 = OWNER - no branch required (chi nhánh tổng)
        // Role ID 5 = WAREHOUSE - no branch required (kho tổng, auto set branchId=1)
        // Role ID 3 = MANAGER - branch required
        if (roleId === '2') {
            // OWNER - hide branch field (thuộc chi nhánh tổng)
            branchWrapper.style.display = 'none';
            branchIdSelect.removeAttribute('required');
            branchIdSelect.value = '';
        } else if (roleId === '5') {
            // WAREHOUSE - hide branch field and auto set to branch 1 (kho tổng)
            branchWrapper.style.display = 'none';
            branchIdSelect.removeAttribute('required');
            branchIdSelect.value = '1';
        } else if (roleId === '3') {
            // MANAGER - show and require branch
            branchWrapper.style.display = '';
            branchIdSelect.setAttribute('required', 'required');
            loadBranches();
        } else {
            // No role selected - hide
            branchWrapper.style.display = 'none';
            branchIdSelect.removeAttribute('required');
        }
    }

    function openModal(mode = 'create', data = null) {
        modal.classList.remove('hidden');
        modal.setAttribute('aria-hidden', 'false');

        clearFieldErrors();

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
        if (res.ok) {
            return await res.json();
        }

        let errorData = null;
        try {
            const ct = res.headers.get('content-type') || '';
            if (ct.includes('application/json')) {
                errorData = await res.json();
            } else {
                const txt = await res.text();
                errorData = { message: txt };
            }
        } catch (_) {
            errorData = { message: 'Tạo thất bại' };
        }

        // Create error and map message to field errors
        const error = new Error(errorData.message || 'Tạo thất bại');
        error.data = errorData;

        // ✅ Handle duplicate entry errors specially
        if (errorData.message && errorData.message.includes('Duplicate entry')) {
            const msg = errorData.message;
            const mapped = {};

            // Extract the duplicate value from error message
            const duplicateMatch = msg.match(/Duplicate entry '([^']+)'/);
            const duplicateValue = duplicateMatch ? duplicateMatch[1] : '';

            // Check which field constraint was violated
            if (msg.includes('UKk8d0f2n7n88w1a16yhua64onx') || msg.includes('userName')) {
                // Username is always required, so show error
                mapped['userName'] = `Tên đăng nhập '${duplicateValue || payload.userName}' đã tồn tại. Vui lòng chọn tên khác.`;
            } else if (msg.toLowerCase().includes('email')) {
                // Only show error if email is not null/empty (skip multiple NULL values)
                if (duplicateValue && duplicateValue.trim() !== '' && duplicateValue.toLowerCase() !== 'null') {
                    mapped['email'] = `Email '${duplicateValue}' đã được sử dụng.`;
                }
            } else if (msg.toLowerCase().includes('phone')) {
                // Only show error if phone is not null/empty (skip multiple NULL values)
                if (duplicateValue && duplicateValue.trim() !== '' && duplicateValue.toLowerCase() !== 'null') {
                    mapped['phoneNumber'] = `Số điện thoại '${duplicateValue}' đã được sử dụng.`;
                }
            }

            // If we identified specific fields with errors, set them
            if (Object.keys(mapped).length > 0) {
                error.data.errors = Object.assign({}, error.data.errors || {}, mapped);
                error.isValidation = true;
                throw error;
            }
            // If no specific field identified but is duplicate, just continue to normal handling
        }

        if (errorData && typeof errorData === 'object') {
            if (errorData.errors && typeof errorData.errors === 'object') {
                error.isValidation = true;
            }
            // Map message text to field errors
            if (errorData.message && typeof errorData.message === 'string') {
                const m = errorData.message.toLowerCase();
                const mapped = {};
                if (m.includes('tên đăng nhập') || m.includes('username') || m.includes('user name')) mapped['userName'] = errorData.message;
                if (m.includes('email')) mapped['email'] = errorData.message;
                if (m.includes('số điện thoại') || m.includes('phone')) mapped['phoneNumber'] = errorData.message;
                if (m.includes('mật khẩu') || m.includes('password')) mapped['password'] = errorData.message;
                if (m.includes('chi nhánh') || m.includes('manager')) mapped['branchId'] = errorData.message;
                if (Object.keys(mapped).length > 0) {
                    error.data.errors = Object.assign({}, error.data.errors || {}, mapped);
                    error.isValidation = true;
                }
            }
        }
        throw error;
    }

    async function updateAccount(id, payload) {
        const res = await fetch(`${API_BASE}/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload),
        });
        if (res.ok) {
            return await res.json();
        }

        let errorData = null;
        try {
            const ct = res.headers.get('content-type') || '';
            if (ct.includes('application/json')) {
                errorData = await res.json();
            } else {
                const txt = await res.text();
                errorData = { message: txt };
            }
        } catch (_) {
            errorData = { message: 'Cập nhật thất bại' };
        }

        const error = new Error(errorData.message || 'Cập nhật thất bại');
        error.data = errorData;

        // ✅ Handle duplicate entry errors specially
        if (errorData.message && errorData.message.includes('Duplicate entry')) {
            const msg = errorData.message;
            const mapped = {};

            // Extract the duplicate value from error message
            const duplicateMatch = msg.match(/Duplicate entry '([^']+)'/);
            const duplicateValue = duplicateMatch ? duplicateMatch[1] : '';

            // Check which field constraint was violated
            if (msg.includes('UKk8d0f2n7n88w1a16yhua64onx') || msg.includes('userName')) {
                // Username is always required, so show error
                mapped['userName'] = `Tên đăng nhập '${duplicateValue || payload.userName}' đã tồn tại. Vui lòng chọn tên khác.`;
            } else if (msg.toLowerCase().includes('email')) {
                // Only show error if email is not null/empty (skip multiple NULL values)
                if (duplicateValue && duplicateValue.trim() !== '' && duplicateValue.toLowerCase() !== 'null') {
                    mapped['email'] = `Email '${duplicateValue}' đã được sử dụng.`;
                }
            } else if (msg.toLowerCase().includes('phone')) {
                // Only show error if phone is not null/empty (skip multiple NULL values)
                if (duplicateValue && duplicateValue.trim() !== '' && duplicateValue.toLowerCase() !== 'null') {
                    mapped['phoneNumber'] = `Số điện thoại '${duplicateValue}' đã được sử dụng.`;
                }
            }

            // If we identified specific fields with errors, set them
            if (Object.keys(mapped).length > 0) {
                error.data.errors = Object.assign({}, error.data.errors || {}, mapped);
                error.isValidation = true;
                throw error;
            }
            // If no specific field identified but is duplicate, just continue to normal handling
        }

        if (errorData && typeof errorData === 'object') {
            if (errorData.errors && typeof errorData.errors === 'object') {
                error.isValidation = true;
            }
            if (errorData.message && typeof errorData.message === 'string') {
                const m = errorData.message.toLowerCase();
                const mapped = {};
                if (m.includes('tên đăng nhập') || m.includes('username') || m.includes('user name')) mapped['userName'] = errorData.message;
                if (m.includes('email')) mapped['email'] = errorData.message;
                if (m.includes('số điện thoại') || m.includes('phone')) mapped['phoneNumber'] = errorData.message;
                if (m.includes('mật khẩu') || m.includes('password')) mapped['password'] = errorData.message;
                if (m.includes('chi nhánh') || m.includes('manager')) mapped['branchId'] = errorData.message;
                if (Object.keys(mapped).length > 0) {
                    error.data.errors = Object.assign({}, error.data.errors || {}, mapped);
                    error.isValidation = true;
                }
            }
        }
        throw error;
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

    async function loadBranches() {
        try {
            const res = await fetch('/api/admin/branches');
            if (!res.ok) throw new Error('Không thể tải danh sách chi nhánh');
            const branches = await res.json();

            // Clear existing options except the first placeholder
            branchIdSelect.innerHTML = '<option value="">-- Chọn chi nhánh --</option>';

            // Add branch options
            branches.forEach(branch => {
                if (!branch.deleted) {
                    const option = document.createElement('option');
                    option.value = branch.id;
                    option.textContent = branch.name;
                    branchIdSelect.appendChild(option);
                }
            });
        } catch (e) {
            console.error('Error loading branches:', e);
            showToast('Không thể tải danh sách chi nhánh', 3000, 'error');
        }
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

    function clearFieldErrors() {
        document.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));
        document.querySelectorAll('.invalid-feedback').forEach(el => {
            el.textContent = '';
            el.style.setProperty('display', 'none', 'important');
        });
    }

    function displayFieldErrors(errors) {
        for (const [field, message] of Object.entries(errors)) {
            const input = document.getElementById(field);
            if (input) {
                input.classList.add('is-invalid');
            }

            const errorDiv = document.getElementById(`${field}-error`);
            if (errorDiv) {
                errorDiv.textContent = message;
                try {
                    errorDiv.style.setProperty('display', 'block', 'important');
                } catch (_) {
                    errorDiv.style.display = 'block';
                }
                errorDiv.setAttribute('role', 'alert');
                errorDiv.setAttribute('aria-live', 'assertive');
            }
        }
    }

    function focusFirstInvalidField() {
        const first = document.querySelector('.is-invalid');
        if (first) {
            try {
                if (typeof first.scrollIntoView === 'function') {
                    first.scrollIntoView({ behavior: 'smooth', block: 'center' });s
                }
                first.focus();
            } catch (_) {
                // Fallback if scrollIntoView fails
                first.focus();
            }
        }
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
            clearFieldErrors();

            const form = new FormData(accountForm);
            const roleId = Number(form.get('roleId') || null);

            const payload = {
                userName: form.get('userName'),
                fullName: form.get('fullName'),
                roleId: roleId,
                email: form.get('email')?.trim() || null,
                phoneNumber: form.get('phoneNumber')?.trim() || null,
                branchId: form.get('branchId') ? Number(form.get('branchId')) : null,
                password: form.get('password') || undefined
            };

            // Auto set branchId=1 for Warehouse role
            if (roleId === 5) {
                payload.branchId = 1;
            }

            const id = accountIdInput.value;
            try {
                if (id) {
                    await updateAccount(id, payload);
                    showToast('Cập nhật thành công', 'success', 3000);
                } else {
                    await createAccount(payload);
                    showToast('Tạo thành công', 'success', 3000);
                }
                Array.from(accountForm.elements).forEach(el => el.disabled = false);
                document.getElementById('btnSave').style.display = '';
                document.getElementById('btnCancel').textContent = 'Hủy';

                // ✅ Reset form completely before closing
                accountForm.reset();
                accountIdInput.value = '';

                closeModal();
                await fetchAll();
            } catch (e) {
                console.error(e);
                // Check if error has field-level errors
                if (e.data && e.data.errors && typeof e.data.errors === 'object') {
                    displayFieldErrors(e.data.errors);
                    focusFirstInvalidField();
                } else {
                    // Generic error - show toast
                    showToast(e.message || e.data?.message || 'Lỗi khi lưu', 3000, 'error');
                }
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
