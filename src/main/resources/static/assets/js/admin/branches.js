/* branches.js
   Client-side renderer + API interaction for /admin/branches
   API base: /api/admin/accounts/branches
*/
(() => {
    const API_BASE = '/api/admin/branches';
    const tableBody = document.getElementById('branchTableBody');
    const searchInput = document.getElementById('searchInput');
    const btnCreate = document.getElementById('btnCreateBranch');
    const btnToggleDeleted = document.getElementById('btnToggleDeleted');
    const emptyState = document.getElementById('emptyState');

    // Modal elements
    const modal = document.getElementById('branchModal');
    const modalTitle = document.getElementById('modalTitle');
    const modalClose = document.getElementById('modalClose');
    const branchForm = document.getElementById('branchForm');
    const branchIdInput = document.getElementById('branchId');
    const toastEl = document.getElementById('toast');

    let allBranches = [];
    let filteredBranches = [];
    let currentPage = 1;
    let recordsPerPage = 25;
    let showDeleted = false;
    let isSubmitting = false; // Prevent double submission
    let submitListenerCount = 0; // Track how many times submit listener is attached

    // Branch type labels
    const branchTypeLabels = {
        'HEAD_QUARTER': 'Tổng công ty',
        'BRANCH': 'Chi nhánh',
        'DISPOSAL_AREA': 'Khu vực hủy'
    };

    // Allowed types to display in UI (exclude HEAD_QUARTER)
    const allowedBranchTypes = ['BRANCH', 'DISPOSAL_AREA'];

    // Populate the branchType <select> with only allowed types
    function populateBranchTypeOptions() {
        const select = document.getElementById('branchType');
        if (!select) return;
        select.innerHTML = '';
        const defaultOpt = document.createElement('option');
        defaultOpt.value = '';
        defaultOpt.textContent = '-- Chọn loại chi nhánh --';
        select.appendChild(defaultOpt);
        allowedBranchTypes.forEach(t => {
            const opt = document.createElement('option');
            opt.value = t;
            opt.textContent = branchTypeLabels[t] || t;
            select.appendChild(opt);
        });
    }

    // Use global toast system from toast.js
    // showToast(message, type, duration) is available globally

    function openModal(mode = 'create', data = null) {
        modal.classList.remove('hidden');
        modal.setAttribute('aria-hidden', 'false');

        clearFieldErrors();
        populateBranchTypeOptions();
        const branchTypeSelect = document.getElementById('branchType');

        if (mode === 'create') {
            modalTitle.textContent = 'Tạo chi nhánh';
            branchIdInput.value = '';

            // ✅ Reset form completely
            branchForm.reset();

            // ✅ Manually clear all input values to prevent browser cache
            document.getElementById('name').value = '';
            document.getElementById('address').value = '';

            branchTypeSelect.disabled = false;
            branchTypeSelect.value = ''; // ✅ Reset to no selection
            return;
        }

        // edit
        modalTitle.textContent = 'Chỉnh sửa chi nhánh';
        branchIdInput.value = data.id || '';
        document.getElementById('name').value = data.name || '';
        document.getElementById('address').value = data.address || '';
        if (data.branchType === 'HEAD_QUARTER') {
            branchTypeSelect.innerHTML = '';

            const opt = document.createElement('option');
            opt.value = 'HEAD_QUARTER';
            opt.textContent = branchTypeLabels['HEAD_QUARTER']; // "Tổng công ty"
            branchTypeSelect.appendChild(opt);

            branchTypeSelect.value = 'HEAD_QUARTER';
            branchTypeSelect.disabled = true;
        } else {
            branchTypeSelect.disabled = false;
            branchTypeSelect.value = data.branchType || '';
        }
    }


    function closeModal() {
        clearFieldErrors(); // Clear any validation errors
        branchForm.reset(); // Reset form values
        branchIdInput.value = ''; // Clear ID field
        modal.classList.add('hidden');
        modal.setAttribute('aria-hidden', 'true');
    }

    // API calls
    async function fetchAll() {
        try {
            const url = showDeleted ? `${API_BASE}?showDeleted=true` : API_BASE;
            console.log('[Branch] Fetching branches from:', url);
            const res = await fetch(url);
            if (!res.ok) {
                console.error('[Branch] fetchAll failed with status:', res.status);
                throw new Error('Lỗi khi lấy danh sách');
            }
            allBranches = await res.json();
            console.log('[Branch] Fetched', allBranches.length, 'branches');
            applySearchAndRender();
        } catch (e) {
            console.error('[Branch] fetchAll error:', e);
            showToast(e.message || 'Lỗi mạng', 'error', 3000);
        }
    }

    async function createBranch(payload) {
        console.log('[Branch] createBranch called with payload:', payload);
        const res = await fetch(API_BASE, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload),
        });
        console.log('[Branch] createBranch response status:', res.status, res.ok);
        if (!res.ok) {
            const contentType = res.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                const errorData = await res.json();
                console.error('[Branch] createBranch error response:', errorData);
                // Check if it's field-level errors (object with field names)
                if (typeof errorData === 'object' && !errorData.message) {
                    const err = new Error('Validation failed');
                    err.errors = errorData;
                    throw err;
                } else {
                    throw new Error(errorData.message || 'Tạo thất bại');
                }
            } else {
                const errorText = await res.text();
                console.error('[Branch] createBranch error text:', errorText);
                throw new Error(errorText || 'Tạo thất bại');
            }
        }
        const result = await res.json();
        console.log('[Branch] createBranch success response:', result);
        return result;
    }

    async function updateBranch(id, payload) {
        const res = await fetch(`${API_BASE}/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload),
        });
        if (!res.ok) {
            const contentType = res.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                const errorData = await res.json();
                // Check if it's field-level errors (object with field names)
                if (typeof errorData === 'object' && !errorData.message) {
                    const err = new Error('Validation failed');
                    err.errors = errorData;
                    throw err;
                } else {
                    throw new Error(errorData.message || 'Cập nhật thất bại');
                }
            } else {
                const errorText = await res.text();
                throw new Error(errorText || 'Cập nhật thất bại');
            }
        }
        return await res.json();
    }

    async function deleteBranch(id) {
        const res = await fetch(`${API_BASE}/${id}`, { method: 'DELETE' });
        if (!res.ok) {
            const contentType = res.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                const errorData = await res.json();
                throw new Error(errorData.message || 'Xoá thất bại');
            }
            const errorText = await res.text();
            throw new Error(errorText || 'Xoá thất bại');
        }
        return;
    }

    async function restoreBranch(id) {
        const res = await fetch(`${API_BASE}/${id}/restore`, { method: 'PATCH' });
        if (!res.ok) {
            const errorText = await res.text();
            throw new Error(errorText || 'Khôi phục thất bại');
        }
    }

    // Render & Pagination
    function updatePaginationControls() {
        const pageInfo = document.getElementById('page-info');
        const prevPageBtn = document.getElementById('prev-page');
        const nextPageBtn = document.getElementById('next-page');
        const recordsPerPageSelect = document.getElementById('records-per-page');

        recordsPerPage = parseInt(recordsPerPageSelect.value, 10);
        const totalRecords = filteredBranches.length;
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

        if (!filteredBranches || filteredBranches.length === 0) {
            emptyState.classList.remove('hidden');
            return;
        }
        emptyState.classList.add('hidden');

        const startIndex = (currentPage - 1) * recordsPerPage;
        const endIndex = startIndex + recordsPerPage;
        const pageData = filteredBranches.slice(startIndex, endIndex);

        pageData.forEach((branch, idx) => {
            const tr = document.createElement('tr');
            tr.setAttribute('data-id', branch.id);

            const branchTypeLabel = branchTypeLabels[branch.branchType] || branch.branchType;
            const isDeleted = branch.deleted || false;

            tr.innerHTML = `
                <td class="text-center">${startIndex + idx + 1}</td>
                <td class="font-medium">${escapeHtml(branch.name || '')}</td>
                <td>${escapeHtml(branchTypeLabel)}</td>
                <td>${escapeHtml(branch.address || '')}</td>
                <td class="text-center">
                  <span class="badge ${isDeleted ? 'inactive' : 'active'}">${isDeleted ? 'Đã xóa' : 'Hoạt động'}</span>
                </td>
                <td class="text-center">
                  ${isDeleted ? `
                    <button class="btn btn-success restore-btn" data-id="${branch.id}" title="Khôi phục">↩ Khôi phục</button>
                  ` : `
                    <button class="btn btn-ghost edit-btn" data-id="${branch.id}" title="Sửa">✏️</button>
                    <button class="btn btn-danger del-btn" data-id="${branch.id}" title="Xoá">🗑️</button>
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
            filteredBranches = [...allBranches];
        } else {
            filteredBranches = allBranches.filter(branch =>
                (branch.name || '').toLowerCase().includes(q) ||
                (branch.address || '').toLowerCase().includes(q)
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
                    const data = allBranches.find(br => br.id == id);
                    if (data) {
                        openModal('edit', data);
                    }
                } catch (e) {
                    showToast(e.message || 'Lỗi', 'error', 3000);
                }
            });
        });

        document.querySelectorAll('.del-btn').forEach(b => {
            b.addEventListener('click', async (ev) => {
                const id = ev.currentTarget.dataset.id;
                if (!confirm('Bạn chắc chắn muốn xoá chi nhánh này?')) return;
                try {
                    await deleteBranch(id);
                    showToast('Xoá thành công', 'success', 3000);
                    await fetchAll();
                } catch (e) {
                    showToast(e.message || 'Lỗi xóa chi nhánh', 'error', 5000);
                }
            });
        });

        document.querySelectorAll('.restore-btn').forEach(b => {
            b.addEventListener('click', async (ev) => {
                const id = ev.currentTarget.dataset.id;
                if (!confirm('Bạn chắc chắn muốn khôi phục chi nhánh này?')) return;
                try {
                    await restoreBranch(id);
                    showToast('Khôi phục thành công', 'success', 3000);
                    await fetchAll();
                } catch (e) {
                    showToast(e.message || 'Lỗi khôi phục', 'error', 5000);
                }
            });
        });
    }

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
                    first.scrollIntoView({ behavior: 'smooth', block: 'center' });
                }
                first.focus();
            } catch (_) {
                // Fallback if scrollIntoView fails
                first.focus();
            }
        }
    }

    // Global flag to prevent duplicate event listener attachment
    let isEventListenersAttached = false;

    function setupEventListeners() {
        // Prevent attaching listeners multiple times
        if (isEventListenersAttached) {
            console.warn('[Branch] Event listeners already attached, skipping...');
            return;
        }
        isEventListenersAttached = true;
        console.log('[Branch] Attaching event listeners...');

        // Create button
        btnCreate.addEventListener('click', () => openModal('create'));

        // Toggle deleted button
        function updateToggleDeletedBtn() {
            if (!btnToggleDeleted) return;
            if (showDeleted) {
                btnToggleDeleted.textContent = 'Ẩn chi nhánh đã xóa';
                btnToggleDeleted.classList.remove('btn-outline');
                btnToggleDeleted.classList.add('btn-primary');
            } else {
                btnToggleDeleted.textContent = 'Hiển thị chi nhánh đã xóa';
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

        // Modal close
        modalClose.addEventListener('click', () => {
            closeModal();
        });
        document.getElementById('btnCancel').addEventListener('click', () => {
            modalClose.click();
        });

        // Form submit - Use { once: false } but with isSubmitting flag
        submitListenerCount++;
        console.log('[Branch] 📌 Attaching submit listener #' + submitListenerCount);
        branchForm.addEventListener('submit', async (ev) => {
            ev.preventDefault();

            console.log('[Branch] ⚠️ FORM SUBMIT EVENT FIRED (listener #' + submitListenerCount + ') - isSubmitting:', isSubmitting);

            // Prevent double submission
            if (isSubmitting) {
                console.log('[Branch] ❌ Form submission already in progress, IGNORING this submission...');
                return;
            }

            console.log('[Branch] ✅ Starting form submission...');
            isSubmitting = true;
            clearFieldErrors();

            // ⚠️ IMPORTANT: Collect form data BEFORE disabling elements
            // Disabled inputs are NOT included in FormData!
            const form = new FormData(branchForm);
            const payload = {
                name: form.get('name'),
                branchType: form.get('branchType'),
                address: form.get('address')
            };

            console.log('[Branch] 📋 Form data collected:', payload);
            console.log('[Branch] 📋 Raw values:', {
                name: document.getElementById('name').value,
                branchType: document.getElementById('branchType').value,
                address: document.getElementById('address').value
            });

            // Disable form to prevent double-clicks AFTER collecting data
            const formElements = branchForm.querySelectorAll('input, select, button');
            formElements.forEach(el => el.disabled = true);

            const id = branchIdInput.value;
            try {
                if (id) {
                    console.log('[Branch] Updating branch:', id, payload);
                    await updateBranch(id, payload);
                    console.log('[Branch] Update successful');
                    showToast('Cập nhật thành công', 'success', 3000);
                } else {
                    console.log('[Branch] Creating branch:', payload);
                    const result = await createBranch(payload);
                    console.log('[Branch] Create successful:', result);
                    showToast('Tạo thành công', 'success', 3000);
                }

                // ✅ Close modal (this will reset form and clear errors)
                closeModal();

                // ✅ Add small delay to ensure database transaction is committed
                await new Promise(resolve => setTimeout(resolve, 100));

                console.log('[Branch] Fetching updated branch list...');
                await fetchAll();
                console.log('[Branch] Branch list refreshed successfully');
            } catch (e) {
                console.error(e);
                // Check if error has field-level errors
                if (e.errors && typeof e.errors === 'object') {
                    displayFieldErrors(e.errors);
                    focusFirstInvalidField();
                } else {
                    // Generic error - show toast with better message formatting
                    let errorMessage = e.message || 'Lỗi khi lưu';

                    // Check for duplicate entry error
                    if (errorMessage.includes('Duplicate entry')) {
                        const branchName = form.get('name');
                        errorMessage = `Chi nhánh "${branchName}" đã tồn tại trong hệ thống. Vui lòng chọn tên khác.`;
                    } else if (errorMessage.includes('constraint')) {
                        errorMessage = 'Dữ liệu bị trùng lặp. Vui lòng kiểm tra lại thông tin.';
                    }

                    showToast(errorMessage, 'error', 5000); // 5 seconds for error messages
                }
            } finally {
                // Always reset the submission flag
                isSubmitting = false;

                // Re-enable form elements
                const formElements = branchForm.querySelectorAll('input, select, button');
                formElements.forEach(el => el.disabled = false);
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
            const totalPages = Math.ceil(filteredBranches.length / recordsPerPage);
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
    // pre-populate branch type options for the page
    populateBranchTypeOptions();
    fetchAll();
})();
