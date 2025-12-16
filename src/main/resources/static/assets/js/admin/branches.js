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

    // UTIL
    function showToast(msg, timeout = 2500, type = 'info') {
        if (!toastEl) {
            alert(msg);
            return;
        }
        toastEl.textContent = msg;
        toastEl.classList.remove('hidden', 'success', 'error');
        toastEl.style.display = 'block';
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

        populateBranchTypeOptions();
        const branchTypeSelect = document.getElementById('branchType');

        if (mode === 'create') {
            modalTitle.textContent = 'Tạo chi nhánh';
            branchIdInput.value = '';
            branchForm.reset();
            branchTypeSelect.disabled = false;
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
        modal.classList.add('hidden');
        modal.setAttribute('aria-hidden', 'true');
    }

    // API calls
    async function fetchAll() {
        try {
            const url = showDeleted ? `${API_BASE}?showDeleted=true` : API_BASE;
            const res = await fetch(url);
            if (!res.ok) throw new Error('Lỗi khi lấy danh sách');
            allBranches = await res.json();
            applySearchAndRender();
        } catch (e) {
            console.error(e);
            showToast(e.message || 'Lỗi mạng', 3000, 'error');
        }
    }

    async function createBranch(payload) {
        const res = await fetch(API_BASE, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload),
        });
        if (!res.ok) {
            const errorText = await res.text();
            throw new Error(errorText || 'Tạo thất bại');
        }
        return await res.json();
    }

    async function updateBranch(id, payload) {
        const res = await fetch(`${API_BASE}/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload),
        });
        if (!res.ok) {
            const errorText = await res.text();
            throw new Error(errorText || 'Cập nhật thất bại');
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
                    showToast(e.message || 'Lỗi', 3000, 'error');
                }
            });
        });

        document.querySelectorAll('.del-btn').forEach(b => {
            b.addEventListener('click', async (ev) => {
                const id = ev.currentTarget.dataset.id;
                if (!confirm('Bạn chắc chắn muốn xoá chi nhánh này?')) return;
                try {
                    await deleteBranch(id);
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
                if (!confirm('Bạn chắc chắn muốn khôi phục chi nhánh này?')) return;
                try {
                    await restoreBranch(id);
                    showToast('Khôi phục thành công', 2500, 'success');
                    await fetchAll();
                } catch (e) {
                    showToast(e.message || 'Lỗi', 4000, 'error');
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

        // Form submit
        branchForm.addEventListener('submit', async (ev) => {
            ev.preventDefault();
            const form = new FormData(branchForm);
            const payload = {
                name: form.get('name'),
                branchType: form.get('branchType'),
                address: form.get('address')
            };

            const id = branchIdInput.value;
            try {
                if (id) {
                    await updateBranch(id, payload);
                    showToast('Cập nhật thành công', 2500, 'success');
                } else {
                    await createBranch(payload);
                    showToast('Tạo thành công', 2500, 'success');
                }
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
