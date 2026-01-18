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
                        <button onclick="openEditModal(${row.id})" class="btn-action" style="margin-right: 8px;">Sửa</button>
                        <button onclick="viewDetails(${row.id})" class="btn-action" style="margin-right: 8px;">Chi tiết</button>
                        <button onclick="confirmDelete(${row.id})" class="btn-action delete">Xóa</button>
                    `;
                }
            }
        ],
        order: [[0, 'desc']],
        language: {
            url: '/assets/datatable_vi.json'
        }
    });
}

function openCreateModal() {
    document.getElementById('modalTitle').textContent = 'Thêm nhà cung cấp mới';
    document.getElementById('supplierForm').reset();
    document.getElementById('supplierId').value = '';

    // Clear validation errors
    const phoneInput = document.getElementById('phone');
    const phoneError = document.getElementById('phoneError');
    if (phoneInput) {
        phoneInput.classList.remove('error');
    }
    if (phoneError) {
        phoneError.classList.remove('show');
    }

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

            // Clear validation errors
            const phoneInput = document.getElementById('phone');
            const phoneError = document.getElementById('phoneError');
            if (phoneInput) {
                phoneInput.classList.remove('error');
            }
            if (phoneError) {
                phoneError.classList.remove('show');
            }

            document.getElementById('supplierModal').style.display = 'block';
        });
}

function closeSupplierModal() {
    document.getElementById('supplierModal').style.display = 'none';
    // Clear validation errors
    const phoneInput = document.getElementById('phone');
    const phoneError = document.getElementById('phoneError');
    if (phoneInput) {
        phoneInput.classList.remove('error');
    }
    if (phoneError) {
        phoneError.classList.remove('show');
    }
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
    const phoneInput = document.getElementById('phone');
    const phoneError = document.getElementById('phoneError');

    // Real-time phone validation
    if (phoneInput) {
        phoneInput.addEventListener('input', function(e) {
            // Only allow numbers
            this.value = this.value.replace(/[^0-9]/g, '');

            // Validate phone number
            validatePhone();
        });

        phoneInput.addEventListener('blur', function() {
            validatePhone();
        });
    }

    function validatePhone() {
        const phoneValue = phoneInput.value.trim();

        if (phoneValue === '') {
            // Phone is optional, so empty is valid
            phoneInput.classList.remove('error');
            phoneError.classList.remove('show');
            return true;
        }

        if (phoneValue.length < 10 || phoneValue.length > 11) {
            phoneInput.classList.add('error');
            phoneError.classList.add('show');
            return false;
        }

        phoneInput.classList.remove('error');
        phoneError.classList.remove('show');
        return true;
    }

    if (supplierForm) {
        supplierForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            // Validate phone before submit
            if (!validatePhone()) {
                showToast('Vui lòng kiểm tra lại số điện thoại', 'error');
                return;
            }

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
                    }
                    // Try to parse error body for detailed message
                    return res.json()
                        .then(err => {
                            const msg = err.message || err.error || 'Có lỗi xảy ra khi lưu nhà cung cấp';
                            throw new Error(msg);
                        })
                        .catch(() => {
                            throw new Error('Có lỗi xảy ra khi lưu nhà cung cấp');
                        });
                })
                .then(data => {
                    closeSupplierModal();
                    supplierTable.ajax.reload();
                    showToast(id ? 'Cập nhật nhà cung cấp thành công!' : 'Thêm nhà cung cấp mới thành công!', 'success');
                })
                .catch(err => {
                    showToast(err.message, 'error');
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

