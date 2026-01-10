// Category Management JavaScript for Warehouse
let categoryTable;

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
    categoryTable = $('#categoryTable').DataTable({
        processing: true,
        serverSide: true,
        ajax: {
            url: '/api/warehouse/category',
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
            { data: 'categoryName' },
            { data: 'description', defaultContent: '-' },
            {
                data: null,
                orderable: false,
                render: function(data, type, row) {
                    return `
                        <div class="action-buttons">
                            <button onclick="viewDetails(${row.id})" class="btn-action view" title="Xem chi tiết">
                                Chi tiết
                            </button>
                            <button onclick="openEditModal(${row.id})" class="btn-action edit" title="Chỉnh sửa">
                                Sửa
                            </button>
                            <button onclick="confirmDelete(${row.id})" class="btn-action delete" title="Xóa">
                                Xóa
                            </button>
                        </div>
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
    document.getElementById('modalTitle').textContent = 'Thêm danh mục mới';
    document.getElementById('categoryForm').reset();
    document.getElementById('categoryId').value = '';
    document.getElementById('categoryModal').style.display = 'block';
}

function openEditModal(id) {
    fetch(`/api/warehouse/category/${id}`)
        .then(res => res.json())
        .then(data => {
            document.getElementById('modalTitle').textContent = 'Cập nhật danh mục';
            document.getElementById('categoryId').value = data.id;
            document.getElementById('categoryName').value = data.categoryName || '';
            document.getElementById('description').value = data.description || '';
            document.getElementById('categoryModal').style.display = 'block';
        });
}

function closeCategoryModal() {
    document.getElementById('categoryModal').style.display = 'none';
}

function viewDetails(id) {
    fetch(`/api/warehouse/category/${id}`)
        .then(res => res.json())
        .then(data => {
            const detailContent = document.getElementById('detailContent');
            detailContent.innerHTML = `
                <div class="detail-item">
                    <div class="detail-label">ID</div>
                    <div class="detail-value">${data.id}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Tên danh mục</div>
                    <div class="detail-value">${data.categoryName || '-'}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Mô tả</div>
                    <div class="detail-value">${data.description || '-'}</div>
                </div>
            `;
            document.getElementById('detailModal').style.display = 'block';
        })
        .catch(err => {
            showToast('Không thể tải chi tiết danh mục', 'error');
        });
}

function closeDetailModal() {
    document.getElementById('detailModal').style.display = 'none';
}

function confirmDelete(id) {
    if (confirm('Bạn có chắc chắn muốn xóa danh mục này không?')) {
        deleteCategory(id);
    }
}

function deleteCategory(id) {
    fetch(`/api/warehouse/category/${id}`, {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' }
    })
    .then(res => {
        if (res.ok) {
            showToast('Xóa danh mục thành công!', 'success');
            categoryTable.ajax.reload();
        } else {
            return res.json().then(data => {
                const errorMessage = data.message || 'Không thể xóa danh mục';
                showToast(errorMessage, 'error');
            });
        }
    })
    .catch(err => {
        showToast('Có lỗi xảy ra: ' + err.message, 'error');
    });
}

document.addEventListener('DOMContentLoaded', function() {
    const categoryForm = document.getElementById('categoryForm');
    if (categoryForm) {
        categoryForm.addEventListener('submit', function(e) {
            e.preventDefault();

            const id = document.getElementById('categoryId').value;
            const data = {
                categoryName: document.getElementById('categoryName').value,
                description: document.getElementById('description').value
            };

            const url = id ? `/api/warehouse/category/${id}` : '/api/warehouse/category';
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
                    throw new Error('Có lỗi xảy ra khi lưu danh mục');
                }
            })
            .then(data => {
                closeCategoryModal();
                categoryTable.ajax.reload();
                showToast(id ? 'Cập nhật danh mục thành công!' : 'Thêm danh mục mới thành công!', 'success');
            })
            .catch(err => {
                showToast('Có lỗi xảy ra: ' + err.message, 'error');
            });
        });
    }

    window.onclick = function(event) {
        const categoryModal = document.getElementById('categoryModal');
        const detailModal = document.getElementById('detailModal');
        if (event.target == categoryModal) {
            closeCategoryModal();
        }
        if (event.target == detailModal) {
            closeDetailModal();
        }
    }
});

