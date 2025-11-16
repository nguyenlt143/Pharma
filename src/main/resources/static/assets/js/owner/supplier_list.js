// Supplier Management JavaScript
let supplierTable;

$(document).ready(function() {
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
    supplierTable = $('#supplierTable').DataTable({
        processing: true,
        serverSide: true,
        ajax: {
            url: '/api/owner/supplier',
            type: 'GET',
            data: function(d) {
                return {
                    draw: d.draw,
                    start: d.start,
                    length: d.length,
                    'search[value]': d.search.value,
                    'order[0][column]': d.order[0].column,
                    'order[0][dir]': d.order[0].dir
                };
            },
            dataSrc: function(json) {
                return json.data;
            }
        },
        columns: [
            { data: 'id' },
            { data: 'supplierName' },
            { data: 'phone', defaultContent: '-' },
            { data: 'address', defaultContent: '-' },
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

function openCreateModal() {
    document.getElementById('modalTitle').textContent = 'Thêm nhà cung cấp mới';
    document.getElementById('supplierForm').reset();
    document.getElementById('supplierId').value = '';
    document.getElementById('supplierModal').style.display = 'block';
}

function openEditModal(id) {
    fetch(`/api/owner/supplier/${id}`)
        .then(res => res.json())
        .then(data => {
            document.getElementById('modalTitle').textContent = 'Cập nhật nhà cung cấp';
            document.getElementById('supplierId').value = data.id;
            document.getElementById('supplierName').value = data.supplierName || '';
            document.getElementById('phone').value = data.phone || '';
            document.getElementById('address').value = data.address || '';
            document.getElementById('supplierModal').style.display = 'block';
        });
}

function closeSupplierModal() {
    document.getElementById('supplierModal').style.display = 'none';
}

function viewDetails(id) {
    fetch(`/api/owner/supplier/${id}`)
        .then(res => res.json())
        .then(data => {
            const detailContent = document.getElementById('detailContent');
            detailContent.innerHTML = `
                <div class="detail-item">
                    <div class="detail-label">ID</div>
                    <div class="detail-value">${data.id}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Tên nhà cung cấp</div>
                    <div class="detail-value">${data.supplierName || '-'}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Số điện thoại</div>
                    <div class="detail-value">${data.phone || '-'}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Địa chỉ</div>
                    <div class="detail-value">${data.address || '-'}</div>
                </div>
            `;
            document.getElementById('detailModal').style.display = 'block';
        })
        .catch(err => {
            showToast('Không thể tải chi tiết nhà cung cấp', 'error');
        });
}

function closeDetailModal() {
    document.getElementById('detailModal').style.display = 'none';
}

function confirmDelete(id) {
    if (confirm('Bạn có chắc chắn muốn xóa nhà cung cấp này không?')) {
        deleteSupplier(id);
    }
}

function deleteSupplier(id) {
    fetch(`/api/owner/supplier/${id}`, {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' }
    })
    .then(res => {
        if (res.ok) {
            showToast('Xóa nhà cung cấp thành công!', 'success');
            supplierTable.ajax.reload();
        } else {
            return res.json().then(data => {
                const errorMessage = data.message || 'Không thể xóa nhà cung cấp';
                showToast(errorMessage, 'error');
            });
        }
    })
    .catch(err => {
        showToast('Có lỗi xảy ra: ' + err.message, 'error');
    });
}

document.addEventListener('DOMContentLoaded', function() {
    const supplierForm = document.getElementById('supplierForm');
    if (supplierForm) {
        supplierForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const id = document.getElementById('supplierId').value;
            const data = {
                supplierName: document.getElementById('supplierName').value,
                phone: document.getElementById('phone').value,
                address: document.getElementById('address').value
            };

            const url = id ? `/api/owner/supplier/${id}` : '/api/owner/supplier';
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
                    throw new Error('Có lỗi xảy ra khi lưu nhà cung cấp');
                }
            })
            .then(data => {
                closeSupplierModal();
                supplierTable.ajax.reload();
                showToast(id ? 'Cập nhật nhà cung cấp thành công!' : 'Thêm nhà cung cấp mới thành công!', 'success');
            })
            .catch(err => {
                showToast('Có lỗi xảy ra: ' + err.message, 'error');
            });
        });
    }

    window.onclick = function(event) {
        const supplierModal = document.getElementById('supplierModal');
        const detailModal = document.getElementById('detailModal');
        if (event.target == supplierModal) {
            closeSupplierModal();
        }
        if (event.target == detailModal) {
            closeDetailModal();
        }
    }
});

