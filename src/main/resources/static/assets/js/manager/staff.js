/* staff.js
       Client-side renderer + API interaction for /manager/staff
       API base: /api/manager/staff
    */
    (() => {
        const API_BASE = '/api/manager/staffs';
        const tableBody = document.getElementById('staffTableBody');
        const searchInput = document.getElementById('searchInput');
        const btnCreate = document.getElementById('btnCreateStaff');
        const btnToggleDeleted = document.getElementById('btnToggleDeleted');
        const emptyState = document.getElementById('emptyState');

        // Modal elements
        const modal = document.getElementById('staffModal');
        const modalTitle = document.getElementById('modalTitle');
        const modalClose = document.getElementById('modalClose');
        const staffForm = document.getElementById('staffForm');
        const staffIdInput = document.getElementById('staffId');
        const passwordWrapper = document.getElementById('passwordWrapper');
        const toastEl = document.getElementById('toast');

        let allStaff = [];
        let filteredStaff = [];
        let currentPage = 1;
        let recordsPerPage = 25;
        let showDeleted = false;

        // UTIL
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

        function openModal(mode = 'create', data = null) {
            modal.classList.remove('hidden');
            modal.setAttribute('aria-hidden', 'false');

            clearFieldErrors();

            const roleEl = document.getElementById('roleId');
            const roleWrapper = roleEl ? roleEl.closest('label') : null;
            if (mode === 'create') {
                modalTitle.textContent = 'Tạo nhân viên';
                staffIdInput.value = '';
                staffForm.reset();
                // show password and role on create
                passwordWrapper.style.display = '';
                if (roleWrapper) roleWrapper.style.display = '';
            } else {
                modalTitle.textContent = 'Chỉnh sửa nhân viên';
                staffIdInput.value = data.id || '';
                document.getElementById('userName').value = data.userName || '';
                document.getElementById('fullName').value = data.fullName || '';
                document.getElementById('roleId').value = data.roleId || '';
                document.getElementById('email').value = data.email || '';
                document.getElementById('phoneNumber').value = data.phoneNumber || '';
                // show password and role on edit
                passwordWrapper.style.display = '';
                if (roleWrapper) roleWrapper.style.display = '';
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
                if (!res.ok) throw new Error('Lỗi khi lấy danh sách');
                allStaff = await res.json();
                applySearchAndRender();
            } catch (e) {
                console.error(e);
                showToast(e.message || 'Lỗi mạng', 3000, 'error');
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
            if (res.ok) {
                return await res.json();
            }
            let errorData = null;
            try {
                const contentType = res.headers.get('content-type') || '';
                if (contentType.includes('application/json')) {
                    errorData = await res.json();
                } else {
                    const txt = await res.text();
                    errorData = { message: txt };
                }
            } catch (_) {
                errorData = { message: 'Tạo thất bại' };
            }

            // Normalize and try to map generic message to field-level error
            const error = new Error(errorData.message || 'Tạo thất bại');
            error.data = errorData;
            // If backend returned explicit errors map, use it. Otherwise always try to map message -> field errors
            if (errorData && typeof errorData === 'object') {
                if (errorData.errors && typeof errorData.errors === 'object') {
                    error.isValidation = true;
                }
                // Always attempt to map message text into field errors (useful for 409 or other statuses)
                if (errorData.message && typeof errorData.message === 'string') {
                    const m = errorData.message.toLowerCase();
                    const mapped = {};
                    if (m.includes('tên đăng nhập') || m.includes('username') || m.includes('user name')) mapped['userName'] = errorData.message;
                    if (m.includes('email')) mapped['email'] = errorData.message;
                    if (m.includes('số điện thoại') || m.includes('phone')) mapped['phoneNumber'] = errorData.message;
                    if (m.includes('mật khẩu') || m.includes('password')) mapped['password'] = errorData.message;
                    if (m.includes('chi nhánh') || m.includes('manager')) mapped['roleId'] = errorData.message;
                    if (Object.keys(mapped).length > 0) {
                        // merge with any existing errors
                        error.data.errors = Object.assign({}, error.data.errors || {}, mapped);
                        error.isValidation = true;
                    }
                }
            }
            throw error;
        }

        async function updateStaff(id, payload) {
            const res = await fetch(`${API_BASE}/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload),
            });
            if (res.ok) {
                return await res.json();
            }
            const errorData = await res.json().catch(() => ({ message: 'Cập nhật thất bại' }));
            const error = new Error(errorData.message || 'Cập nhật thất bại');
            error.data = errorData;
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
                    if (m.includes('chi nhánh') || m.includes('manager')) mapped['roleId'] = errorData.message;
                    if (Object.keys(mapped).length > 0) {
                        error.data.errors = Object.assign({}, error.data.errors || {}, mapped);
                        error.isValidation = true;
                    }
                }
            }
            throw error;
        }

        async function deleteStaff(id) {
            const res = await fetch(`${API_BASE}/${id}`, { method: 'DELETE' });
            if (!res.ok) {
                const errorText = await res.text();
                throw new Error(errorText || 'Xoá thất bại');
            }
            return;
        }

        async function restoreStaff(id) {
            const res = await fetch(`${API_BASE}/${id}/restore`, { method: 'PATCH' });
            if (!res.ok) throw new Error('Khôi phục thất bại');
            return;
        }

        // Render & Pagination
        // Pagination (Standardized - like Warehouse/Inventory)
        function updatePaginationUI() {
            const recordsPerPageSelect = document.getElementById('recordsPerPage');
            const recordsPerPage = parseInt(recordsPerPageSelect.value, 10);
            const totalRecords = filteredStaff.length;
            const totalPages = Math.ceil(totalRecords / recordsPerPage) || 1;

            if (currentPage > totalPages) {
                currentPage = totalPages > 0 ? totalPages : 1;
            }

            // Update info display
            document.getElementById('totalItems').textContent = totalRecords;
            document.getElementById('showingFrom').textContent = totalRecords > 0 ? (currentPage - 1) * recordsPerPage + 1 : 0;
            document.getElementById('showingTo').textContent = Math.min(currentPage * recordsPerPage, totalRecords);

            // Render pagination buttons
            const paginationButtons = document.getElementById('paginationButtons');
            if (!paginationButtons) return;
            paginationButtons.innerHTML = '';

            if (totalPages <= 1) return;

            // First button
            const firstBtn = document.createElement('button');
            firstBtn.innerHTML = '&laquo;&laquo;';
            firstBtn.className = 'pagination-btn' + (currentPage === 1 ? ' disabled' : '');
            firstBtn.disabled = currentPage === 1;
            firstBtn.title = 'Trang đầu';
            firstBtn.onclick = () => { if (currentPage > 1) { currentPage = 1; renderTablePage(); } };
            paginationButtons.appendChild(firstBtn);

            // Previous button
            const prevBtn = document.createElement('button');
            prevBtn.innerHTML = '&laquo;';
            prevBtn.className = 'pagination-btn' + (currentPage === 1 ? ' disabled' : '');
            prevBtn.disabled = currentPage === 1;
            prevBtn.title = 'Trang trước';
            prevBtn.onclick = () => { if (currentPage > 1) { currentPage--; renderTablePage(); } };
            paginationButtons.appendChild(prevBtn);

            // Page number buttons (max 5 visible)
            const maxButtons = 5;
            let startPage = Math.max(1, currentPage - Math.floor(maxButtons / 2));
            let endPage = Math.min(totalPages, startPage + maxButtons - 1);

            if (endPage - startPage < maxButtons - 1) {
                startPage = Math.max(1, endPage - maxButtons + 1);
            }

            for (let i = startPage; i <= endPage; i++) {
                const pageBtn = document.createElement('button');
                pageBtn.textContent = i;
                pageBtn.className = 'pagination-btn' + (i === currentPage ? ' active' : '');
                pageBtn.onclick = () => { currentPage = i; renderTablePage(); };
                paginationButtons.appendChild(pageBtn);
            }

            // Next button
            const nextBtn = document.createElement('button');
            nextBtn.innerHTML = '&raquo;';
            nextBtn.className = 'pagination-btn' + (currentPage === totalPages ? ' disabled' : '');
            nextBtn.disabled = currentPage === totalPages;
            nextBtn.title = 'Trang sau';
            nextBtn.onclick = () => { if (currentPage < totalPages) { currentPage++; renderTablePage(); } };
            paginationButtons.appendChild(nextBtn);

            // Last button
            const lastBtn = document.createElement('button');
            lastBtn.innerHTML = '&raquo;&raquo;';
            lastBtn.className = 'pagination-btn' + (currentPage === totalPages ? ' disabled' : '');
            lastBtn.disabled = currentPage === totalPages;
            lastBtn.title = 'Trang cuối';
            lastBtn.onclick = () => { if (currentPage < totalPages) { currentPage = totalPages; renderTablePage(); } };
            paginationButtons.appendChild(lastBtn);
        }

        function renderTablePage() {
            const recordsPerPageSelect = document.getElementById('recordsPerPage');
            const recordsPerPage = parseInt(recordsPerPageSelect.value, 10);

            tableBody.innerHTML = '';

            if (!filteredStaff || filteredStaff.length === 0) {
                emptyState.classList.remove('hidden');
                updatePaginationUI();
                return;
            }
            emptyState.classList.add('hidden');

            const startIndex = (currentPage - 1) * recordsPerPage;
            const endIndex = startIndex + recordsPerPage;
            const pageData = filteredStaff.slice(startIndex, endIndex);


            pageData.forEach((s, idx) => {
                const tr = document.createElement('tr');
                tr.setAttribute('data-id', s.id);

                tr.innerHTML = `
                    <td class="text-center">${startIndex + idx + 1}</td>
                    <td class="font-medium">${escapeHtml(s.userName || '')}</td>
                    <td>${escapeHtml(s.fullName || '')}</td>
                    <td>${escapeHtml(s.roleName || '')}</td>
                    <td>${escapeHtml(s.email || '')}</td>
                    <td>${escapeHtml(s.phoneNumber || '')}</td>
                    <td>${escapeHtml(s.password || '')}</td>
                    <td class="text-center">
                      <span class="badge ${s.deleted ? 'inactive' : 'active'}">${s.deleted ? 'Đã xóa' : 'Hoạt động'}</span>
                    </td>
                    <td class="text-center">
                      ${s.deleted ? `
                        <button class="btn btn-success restore-btn" data-id="${s.id}" title="Khôi phục">↩ Khôi phục</button>
                      ` : `
                        <button class="btn btn-ghost edit-btn" data-id="${s.id}" title="Sửa">✏️</button>
                        <button class="btn btn-danger del-btn" data-id="${s.id}" title="Xoá">🗑️</button>
                      `}
                    </td>
                  `;

                tableBody.appendChild(tr);
            });

            updatePaginationUI();
            attachActionHandlers();
        }

        function applySearchAndRender() {
            const q = searchInput.value.trim().toLowerCase();
            if (!q) {
                filteredStaff = [...allStaff];
            } else {
                filteredStaff = allStaff.filter(s =>
                    (s.userName || '').toLowerCase().includes(q) ||
                    (s.fullName || '').toLowerCase().includes(q) ||
                    (s.email || '').toLowerCase().includes(q) ||
                    (s.roleName || '').toLowerCase().includes(q)
                );
            }
            currentPage = 1;
            renderTablePage();
        }


        // Render
        function attachActionHandlers() {
            // attach handlers
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
                    if (!confirm('Bạn chắc chắn muốn xoá nhân viên này?')) return;
                    try {
                        await deleteStaff(id);
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
                        await restoreStaff(id);
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
            console.log('displayFieldErrors called with:', errors);
            for (const [field, message] of Object.entries(errors)) {
                console.log(`Processing field: ${field}, message: ${message}`);
                const input = document.getElementById(field);
                if (input) {
                    console.log(`Adding .is-invalid to input: ${field}`);
                    input.classList.add('is-invalid');
                } else {
                    console.warn(`Input not found: ${field}`);
                }

                const errorDiv = document.getElementById(`${field}-error`);
                if (errorDiv) {
                    console.log(`Displaying error message for ${field}`);
                    errorDiv.textContent = message;
                    try {
                        errorDiv.style.setProperty('display', 'block', 'important');
                    } catch (_) {
                        errorDiv.style.display = 'block';
                    }
                    errorDiv.setAttribute('role', 'alert');
                    errorDiv.setAttribute('aria-live', 'assertive');
                } else {
                    console.warn(`Error div not found: ${field}-error`);
                }
            }
        }

        function validateForm() {
            let isValid = true;
            const errors = {};
            
            // Basic required check
            ['userName', 'fullName', 'roleId'].forEach(id => {
                const input = document.getElementById(id);
                if (!input.value) {
                    isValid = false;
                    errors[id] = 'Trường này là bắt buộc.';
                }
            });

            // Password validation (only if creating or password has value)
            const password = document.getElementById('password');
            // If creating a new staff (no id), password is required -> show same field-level error
            if (!staffIdInput.value) {
                if (!password.value || password.value.trim() === '') {
                    isValid = false;
                    errors['password'] = 'Trường này là bắt buộc.';
                } else if (!password.checkValidity()) {
                    isValid = false;
                    errors['password'] = password.title;
                }
            } else {
                // On update, only validate password if user entered a value
                if (password.value && !password.checkValidity()) {
                    isValid = false;
                    errors['password'] = password.title;
                }
            }

            // Email format
            const email = document.getElementById('email');
            if (email.value && !email.checkValidity()) {
                isValid = false;
                errors['email'] = 'Email không hợp lệ.';
            }

            // Phone number format
            const phone = document.getElementById('phoneNumber');
            if (phone.value && !phone.checkValidity()) {
                isValid = false;
                errors['phoneNumber'] = phone.title; // Use the title attribute for the error message
            }

            clearFieldErrors();
            if (!isValid) {
                displayFieldErrors(errors);
            }
            return isValid;
        }

        function setupEventListeners() {
            // events
            btnCreate.addEventListener('click', () => openModal('create'));

            function updateToggleDeletedBtn() {
                if (!btnToggleDeleted) return;
                if (showDeleted) {
                    btnToggleDeleted.textContent = 'Ẩn nhân viên đã xóa';
                    btnToggleDeleted.classList.remove('btn-outline');
                    btnToggleDeleted.classList.add('btn-primary');
                } else {
                    btnToggleDeleted.textContent = 'Hiển thị nhân viên đã xóa';
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
                clearFieldErrors();

                if (!validateForm()) {
                    focusFirstInvalidField();
                    return;
                }

                const form = new FormData(staffForm);
                const payload = {
                    id: form.get('id'),
                    userName: form.get('userName'),
                    fullName: form.get('fullName'),
                    roleId: Number(form.get('roleId') || null),
                    email: form.get('email')?.trim() || null,
                    phoneNumber: form.get('phoneNumber')?.trim() || null,
                    password: form.get('password') || undefined
                };

                const id = staffIdInput.value;
                try {
                    if (id) {
                        await updateStaff(id, payload);
                        showToast('Cập nhật thành công', 2500, 'success');
                    } else {
                        await createStaff(payload);
                        showToast('Tạo thành công', 2500, 'success');
                    }
                    // restore buttons and fields
                    Array.from(staffForm.elements).forEach(el => el.disabled = false);
                    document.getElementById('btnSave').style.display = '';
                    document.getElementById('btnCancel').textContent = 'Hủy';
                    closeModal();
                    await fetchAll();
                } catch (e) {
                    console.log('Submit error:', e);
                    console.log('Error data:', e.data);
                    console.log('Error data.errors:', e.data?.errors);

                    // Check if error has field-level errors
                    if (e.data && e.data.errors && typeof e.data.errors === 'object' && Object.keys(e.data.errors).length > 0) {
                        console.log('Displaying field errors:', e.data.errors);
                        displayFieldErrors(e.data.errors);
                        focusFirstInvalidField();
                    } else if (e.isValidation && e.data?.errors) {
                        console.log('Displaying validation errors:', e.data.errors);
                        displayFieldErrors(e.data.errors);
                        focusFirstInvalidField();
                    } else {
                        // Generic error - show toast
                        console.log('Displaying toast for generic error');
                        showToast(e.message || e.data?.message || 'Lỗi khi lưu', 3000, 'error');
                    }
                }
            });

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

            // search
            let searchTimeout = null;
            searchInput.addEventListener('input', () => {
                clearTimeout(searchTimeout);
                searchTimeout = setTimeout(applySearchAndRender, 200);
            });

            // Page length selector
            const recordsPerPageSelect = document.getElementById('recordsPerPage');
            if (recordsPerPageSelect) {
                recordsPerPageSelect.addEventListener('change', () => {
                    currentPage = 1; // Reset to first page
                    renderTablePage();
                });
            }

            // Pagination now handled by dynamic buttons in updatePaginationUI()
        }


        // initial load
        setupEventListeners();
        fetchAll();
    })();
