// Price Management JavaScript for Warehouse
let priceTable;
let currentVariantId = null;
let currentBranchId = null;
let variants = [];
let branches = [];

$(document).ready(function() {
    loadVariants();
    loadBranches();
    initDataTable();
});

// Toast Notification Utility
function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.className = `toast ${type} show`;

    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

function initDataTable() {
    priceTable = $('#priceTable').DataTable({
        processing: true,
        serverSide: true,
        ajax: {
            url: '/api/warehouse/price',
            type: 'GET',
            data: function(d) {
                const params = {
                    draw: d.draw,
                    start: d.start,
                    length: d.length,
                    'search[value]': d.search.value,
                    'order[0][column]': d.order[0].column,
                    'order[0][dir]': d.order[0].dir
                };
                if (currentVariantId) params.variantId = currentVariantId;
                if (currentBranchId) params.branchId = currentBranchId;
                return params;
            },
            dataSrc: function(json) {
                return json.data;
            }
        },
        columns: [
            { data: 'id' },
            {
                data: 'variantName',
                defaultContent: '-',
                render: function(data, type, row) {
                    return data || (row.variantId ? `Biến thể #${row.variantId}` : '-');
                }
            },
            {
                data: 'salePrice',
                render: function(data) {
                    return data ? new Intl.NumberFormat('vi-VN').format(data) + ' đ' : '-';
                }
            },
            {
                data: 'branchPrice',
                render: function(data) {
                    return data ? new Intl.NumberFormat('vi-VN').format(data) + ' đ' : '-';
                }
            },
            {
                data: 'startDate',
                render: function(data) {
                    return data ? new Date(data).toLocaleString('vi-VN') : '-';
                }
            },
            {
                data: 'endDate',
                render: function(data) {
                    return data ? new Date(data).toLocaleString('vi-VN') : '-';
                }
            },
            {
                data: null,
                orderable: false,
                render: function(data, type, row) {
                    return `
                        <button onclick="openEditModal(${row.id})" class="btn-link" style="color: #2563EB; margin-right: 12px;">Sửa</button>
                        <button onclick="viewDetails(${row.id})" class="btn-link" style="color: #7C3AED; margin-right: 12px;">Chi tiết</button>
                        <button onclick="confirmDelete(${row.id})" class="btn-link delete">Xóa</button>
                    `;
                }
            }
        ],
        order: [[0, 'desc']],
        language: {
            url: '//cdn.datatables.net/plug-ins/1.13.7/i18n/vi.json'
        }
    });
}

function loadVariants() {
    fetch('/api/warehouse/variants')
        .then(res => res.json())
        .then(data => {
            variants = data;
            const filterSelect = document.getElementById('filterVariantId');
            const modalSelect = document.getElementById('variantId');

            // Populate filter dropdown
            data.forEach(variant => {
                const option = document.createElement('option');
                option.value = variant.id;
                option.textContent = variant.displayName || `Biến thể #${variant.id}`;
                filterSelect.appendChild(option);
            });

            // Populate modal dropdown
            data.forEach(variant => {
                const option = document.createElement('option');
                option.value = variant.id;
                option.textContent = variant.displayName || `Biến thể #${variant.id}`;
                modalSelect.appendChild(option);
            });
        })
        .catch(err => {
            console.error('Error loading variants:', err);
        });
}

function loadBranches() {
    fetch('/api/warehouse/branches')
        .then(res => res.json())
        .then(data => {
            branches = data;
            const filterSelect = document.getElementById('filterBranchId');
            const modalSelect = document.getElementById('branchId');

            // Populate filter dropdown
            data.forEach(branch => {
                const option = document.createElement('option');
                option.value = branch.id;
                option.textContent = branch.name || `Chi nhánh #${branch.id}`;
                filterSelect.appendChild(option);
            });

            // Populate modal dropdown
            data.forEach(branch => {
                const option = document.createElement('option');
                option.value = branch.id;
                option.textContent = branch.name || `Chi nhánh #${branch.id}`;
                modalSelect.appendChild(option);
            });
        })
        .catch(err => {
            console.error('Error loading branches:', err);
        });
}

function applyFilter() {
    const variantSelect = document.getElementById('filterVariantId');
    const branchSelect = document.getElementById('filterBranchId');
    currentVariantId = variantSelect.value || null;
    currentBranchId = branchSelect.value || null;
    priceTable.ajax.reload();
}

function clearFilter() {
    document.getElementById('filterVariantId').value = '';
    document.getElementById('filterBranchId').value = '';
    currentVariantId = null;
    currentBranchId = null;
    priceTable.ajax.reload();
}

function openCreateModal() {
    document.getElementById('modalTitle').textContent = 'Điều chỉnh giá';
    document.getElementById('priceForm').reset();
    document.getElementById('priceId').value = '';
    // Reset dropdowns to default option
    document.getElementById('variantId').value = '';
    document.getElementById('branchId').value = '';
    document.getElementById('priceModal').style.display = 'block';
}

function openEditModal(id) {
    fetch(`/api/warehouse/price/${id}`)
        .then(res => res.json())
        .then(data => {
            document.getElementById('modalTitle').textContent = 'Cập nhật giá';
            document.getElementById('priceId').value = data.id;
            document.getElementById('variantId').value = data.variantId || '';
            document.getElementById('branchId').value = data.branchId || '';
            document.getElementById('salePrice').value = data.salePrice || '';
            document.getElementById('branchPrice').value = data.branchPrice || '';
            if (data.startDate) {
                const startDate = new Date(data.startDate);
                document.getElementById('startDate').value = startDate.toISOString().slice(0, 16);
            }
            if (data.endDate) {
                const endDate = new Date(data.endDate);
                document.getElementById('endDate').value = endDate.toISOString().slice(0, 16);
            }
            document.getElementById('priceModal').style.display = 'block';
        })
        .catch(err => {
            showToast('Không thể tải thông tin giá', 'error');
        });
}

function closePriceModal() {
    document.getElementById('priceModal').style.display = 'none';
}

function viewDetails(id) {
    fetch(`/api/warehouse/price/${id}`)
        .then(res => res.json())
        .then(data => {
            const detailContent = document.getElementById('detailContent');
            detailContent.innerHTML = `
                <div class="detail-item">
                    <div class="detail-label">ID</div>
                    <div class="detail-value">${data.id}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Biến thể thuốc</div>
                    <div class="detail-value">${data.variantName || (data.variantId ? `Biến thể #${data.variantId}` : '-')}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Giá bán</div>
                    <div class="detail-value">${data.salePrice ? new Intl.NumberFormat('vi-VN').format(data.salePrice) + ' đ' : '-'}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Giá chi nhánh</div>
                    <div class="detail-value">${data.branchPrice ? new Intl.NumberFormat('vi-VN').format(data.branchPrice) + ' đ' : '-'}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Ngày bắt đầu</div>
                    <div class="detail-value">${data.startDate ? new Date(data.startDate).toLocaleString('vi-VN') : '-'}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Ngày kết thúc</div>
                    <div class="detail-value">${data.endDate ? new Date(data.endDate).toLocaleString('vi-VN') : '-'}</div>
                </div>
            `;
            document.getElementById('detailModal').style.display = 'block';
        })
        .catch(err => {
            showToast('Không thể tải chi tiết giá', 'error');
        });
}

function closeDetailModal() {
    document.getElementById('detailModal').style.display = 'none';
}

function confirmDelete(id) {
    if (confirm('Bạn có chắc chắn muốn xóa giá này không?')) {
        deletePrice(id);
    }
}

function deletePrice(id) {
    fetch(`/api/warehouse/price/${id}`, {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' }
    })
    .then(res => {
        if (res.ok) {
            showToast('Xóa giá thành công!', 'success');
            priceTable.ajax.reload();
        } else {
            return res.json().then(data => {
                const errorMessage = data.message || 'Không thể xóa giá';
                showToast(errorMessage, 'error');
            });
        }
    })
    .catch(err => {
        showToast('Có lỗi xảy ra: ' + err.message, 'error');
    });
}

document.addEventListener('DOMContentLoaded', function() {
    const priceForm = document.getElementById('priceForm');
    if (priceForm) {
        priceForm.addEventListener('submit', function(e) {
            e.preventDefault();

            const id = document.getElementById('priceId').value;
            const data = {
                variantId: parseInt(document.getElementById('variantId').value),
                branchId: document.getElementById('branchId').value ? parseInt(document.getElementById('branchId').value) : null,
                salePrice: parseFloat(document.getElementById('salePrice').value),
                branchPrice: document.getElementById('branchPrice').value ? parseFloat(document.getElementById('branchPrice').value) : null,
                startDate: document.getElementById('startDate').value ? new Date(document.getElementById('startDate').value).toISOString() : null,
                endDate: document.getElementById('endDate').value ? new Date(document.getElementById('endDate').value).toISOString() : null
            };

            const url = id ? `/api/warehouse/price/${id}` : '/api/warehouse/price';
            const method = id ? 'PUT' : 'POST';

            fetch(url, {
                method: method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            })
            .then(res => {
                if (res.ok) {
                    return res.json();
                } else {
                    throw new Error('Có lỗi xảy ra khi lưu giá');
                }
            })
            .then(data => {
                closePriceModal();
                priceTable.ajax.reload();
                showToast(id ? 'Cập nhật giá thành công!' : 'Thêm giá mới thành công!', 'success');
            })
            .catch(err => {
                showToast('Có lỗi xảy ra: ' + err.message, 'error');
            });
        });
    }

    window.onclick = function(event) {
        const priceModal = document.getElementById('priceModal');
        const detailModal = document.getElementById('detailModal');
        if (event.target == priceModal) {
            closePriceModal();
        }
        if (event.target == detailModal) {
            closeDetailModal();
        }
    }
});

