// Medicine Management JavaScript
let medicineTable;
let categories = [];
let units = [];

$(document).ready(function() {
    loadCategories();
    loadUnits();
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
    medicineTable = $('#medicineTable').DataTable({
        processing: true,
        serverSide: true,
        ajax: {
            url: '/api/owner/medicine',
            type: 'GET',
            data: function(d) {
                const params = {
                    draw: d.draw,
                    start: d.start,
                    length: d.length,
                    'search[value]': d.search.value || ''
                };
                
                // Add sorting
                if (d.order && d.order.length > 0) {
                    const orderCol = d.order[0].column;
                    const orderDir = d.order[0].dir;
                    params['order[0][column]'] = orderCol;
                    params['order[0][dir]'] = orderDir;
                    
                    // Map column index to field name for sorting
                    const columnMap = ['id', 'medicineName', 'category.name', 'brandName', 'manufacturer', 'activeIngredient', 'status'];
                    if (columnMap[orderCol]) {
                        params['columns[' + orderCol + '][data]'] = columnMap[orderCol];
                    }
                }
                
                return params;
            },
            dataSrc: function(json) {
                json.recordsTotal = json.recordsTotal;
                json.recordsFiltered = json.recordsFiltered;
                return json.data;
            }
        },
        columns: [
            { 
                data: 'id',
                name: 'id'
            },
            { 
                data: 'medicineName',
                name: 'medicineName'
            },
            { 
                data: 'categoryName',
                name: 'category.name',
                defaultContent: '-'
            },
            { 
                data: 'brandName',
                name: 'brandName',
                defaultContent: '-'
            },
            { 
                data: 'manufacturer',
                name: 'manufacturer',
                defaultContent: '-'
            },
            { 
                data: 'activeIngredient',
                name: 'activeIngredient',
                defaultContent: '-'
            },
            { 
                data: 'status',
                name: 'status',
                render: function(data) {
                    return data === 1 
                        ? '<span class="status-badge status-active">Hoạt động</span>'
                        : '<span class="status-badge status-inactive">Ngừng</span>';
                }
            },
            {
                data: null,
                orderable: false,
                render: function(data, type, row) {
                    const medicineName = (row.medicineName || '').replace(/'/g, "\\'");
                    return `
                        <div class="action-buttons">
                            <button onclick="openEditModal(${row.id})" class="btn-link" style="color: #2563EB;">Sửa</button>
                            <button onclick="openVariantModal(${row.id}, '${medicineName}')" class="btn-link" style="color: #059669;">Thêm biến thể</button>
                            <button onclick="viewDetails(${row.id})" class="btn-link" style="color: #7C3AED;">Chi tiết</button>
                            <button onclick="confirmDelete(${row.id})" class="btn-link delete">Xóa</button>
                        </div>
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

function loadCategories() {
    fetch('/api/owner/category?draw=1&start=0&length=1000')
        .then(res => res.json())
        .then(data => {
            categories = data.data;
            const select = document.getElementById('categoryId');
            select.innerHTML = '<option value="">Chọn danh mục</option>';
            categories.forEach(cat => {
                const option = document.createElement('option');
                option.value = cat.id;
                option.textContent = cat.categoryName;
                select.appendChild(option);
            });
        });
}

function loadUnits() {
    // Load units from API
    fetch('/api/owner/units')
        .then(res => res.json())
        .then(data => {
            // Load into variant modal dropdown
            const variantSelect = document.getElementById('baseUnitId');
            if (variantSelect) {
                variantSelect.innerHTML = '<option value="">Chọn đơn vị</option>';
                if (Array.isArray(data)) {
                    data.forEach(unit => {
                        const option = document.createElement('option');
                        option.value = unit.id;
                        option.textContent = unit.name || '';
                        variantSelect.appendChild(option);
                    });
                }
            }
        })
        .catch(err => {
            console.error('Error loading units:', err);
            const variantSelect = document.getElementById('baseUnitId');
            if (variantSelect) {
                variantSelect.innerHTML = '<option value="">Chọn đơn vị</option>';
            }
        });
}

function openCreateModal() {
    document.getElementById('modalTitle').textContent = 'Thêm thuốc mới';
    document.getElementById('medicineForm').reset();
    document.getElementById('medicineId').value = '';
    document.getElementById('medicineModal').style.display = 'block';
}

function openEditModal(id) {
    fetch(`/api/owner/medicine/${id}`)
        .then(res => res.json())
        .then(data => {
            document.getElementById('modalTitle').textContent = 'Cập nhật thuốc';
            document.getElementById('medicineId').value = data.id;
            document.getElementById('medicineName').value = data.medicineName || '';
            document.getElementById('categoryId').value = data.categoryId || '';
            document.getElementById('brandName').value = data.brandName || '';
            document.getElementById('manufacturer').value = data.manufacturer || '';
            document.getElementById('countryOfOrigin').value = data.countryOfOrigin || '';
            document.getElementById('activeIngredient').value = data.activeIngredient || '';
            document.getElementById('registrationNumber').value = data.registrationNumber || '';
            document.getElementById('storageConditions').value = data.storageConditions || '';
            document.getElementById('indications').value = data.indications || '';
            document.getElementById('contraindications').value = data.contraindications || '';
            document.getElementById('sideEffects').value = data.sideEffects || '';
            document.getElementById('instructions').value = data.instructions || '';
            document.getElementById('prescriptionRequired').value = data.prescriptionRequired ? 'true' : 'false';
            document.getElementById('status').value = data.status || 1;
            document.getElementById('medicineModal').style.display = 'block';
        });
}

function closeMedicineModal() {
    document.getElementById('medicineModal').style.display = 'none';
}

function openVariantModal(medicineId, medicineName) {
    document.getElementById('variantMedicineId').value = medicineId;
    document.getElementById('variantMedicineName').textContent = medicineName;
    document.getElementById('variantFormContainer').style.display = 'none';
    document.getElementById('variantTableBody').innerHTML = '<tr><td colspan="6" style="padding: 24px; text-align: center; color: #6B7280;">Đang tải...</td></tr>';
    document.getElementById('variantModal').style.display = 'block';
    loadVariants(medicineId);
}

function closeVariantModal() {
    document.getElementById('variantModal').style.display = 'none';
    document.getElementById('variantFormContainer').style.display = 'none';
    document.getElementById('variantForm').reset();
}

function loadVariants(medicineId) {
    fetch(`/api/owner/medicine/${medicineId}/variants`)
        .then(res => res.json())
        .then(data => {
            const tbody = document.getElementById('variantTableBody');
            if (data.length === 0) {
                tbody.innerHTML = '<tr><td colspan="6" style="padding: 24px; text-align: center; color: #6B7280;">Chưa có biến thể nào</td></tr>';
                return;
            }
            
            tbody.innerHTML = data.map(variant => `
                <tr style="border-bottom: 1px solid #E5E7EB;">
                    <td style="padding: 12px;">${variant.dosageForm || '-'}</td>
                    <td style="padding: 12px;">${variant.dosage || '-'}</td>
                    <td style="padding: 12px;">${variant.strength || '-'}</td>
                    <td style="padding: 12px;">${variant.baseUnitName || '-'}</td>
                    <td style="padding: 12px;">${variant.barcode || '-'}</td>
                    <td style="padding: 12px; text-align: center;">
                        <button onclick="openEditVariantForm(${variant.id})" class="btn-link" style="color: #2563EB; margin: 0 4px;">Sửa</button>
                        <button onclick="confirmDeleteVariant(${variant.id})" class="btn-link delete" style="margin: 0 4px;">Xóa</button>
                    </td>
                </tr>
            `).join('');
        })
        .catch(err => {
            console.error('Error loading variants:', err);
            document.getElementById('variantTableBody').innerHTML = '<tr><td colspan="6" style="padding: 24px; text-align: center; color: #DC2626;">Lỗi khi tải danh sách biến thể</td></tr>';
        });
}

function openCreateVariantForm() {
    document.getElementById('variantModalTitle').textContent = 'Thêm biến thể thuốc';
    document.getElementById('variantForm').reset();
    document.getElementById('variantId').value = '';
    document.getElementById('variantFormContainer').style.display = 'block';
    document.getElementById('variantFormContainer').scrollIntoView({ behavior: 'smooth', block: 'nearest' });
}

function openEditVariantForm(variantId) {
    fetch(`/api/owner/medicine/variant/${variantId}`)
        .then(res => res.json())
        .then(data => {
            document.getElementById('variantModalTitle').textContent = 'Cập nhật biến thể thuốc';
            document.getElementById('variantId').value = data.id;
            document.getElementById('dosageForm').value = data.dosageForm || '';
            document.getElementById('dosage').value = data.dosage || '';
            document.getElementById('strength').value = data.strength || '';
            document.getElementById('baseUnitId').value = data.baseUnitId || '';
            document.getElementById('barcode').value = data.barcode || '';
            document.getElementById('variantFormContainer').style.display = 'block';
            document.getElementById('variantFormContainer').scrollIntoView({ behavior: 'smooth', block: 'nearest' });
        })
        .catch(err => {
            showToast('Không thể tải thông tin biến thể', 'error');
        });
}

function cancelVariantForm() {
    document.getElementById('variantFormContainer').style.display = 'none';
    document.getElementById('variantForm').reset();
    document.getElementById('variantModalTitle').textContent = 'Quản lý biến thể thuốc';
}

function confirmDeleteVariant(variantId) {
    if (confirm('Bạn có chắc chắn muốn xóa biến thể này không?')) {
        deleteVariant(variantId);
    }
}

function deleteVariant(variantId) {
    fetch(`/api/owner/medicine/variant/${variantId}`, {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' }
    })
    .then(res => {
        if (res.ok) {
            showToast('Xóa biến thể thành công!', 'success');
            const medicineId = document.getElementById('variantMedicineId').value;
            loadVariants(medicineId);
        } else {
            return res.json().then(data => {
                const errorMessage = data.message || 'Không thể xóa biến thể';
                showToast(errorMessage, 'error');
            });
        }
    })
    .catch(err => {
        showToast('Có lỗi xảy ra: ' + err.message, 'error');
    });
}

function viewDetails(id) {
    fetch(`/api/owner/medicine/${id}`)
        .then(res => res.json())
        .then(data => {
            const detailContent = document.getElementById('detailContent');
            detailContent.innerHTML = `
                <div class="detail-item">
                    <div class="detail-label">ID</div>
                    <div class="detail-value">${data.id}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Tên thuốc</div>
                    <div class="detail-value">${data.medicineName || '-'}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Danh mục</div>
                    <div class="detail-value">${data.categoryName || '-'}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Thương hiệu</div>
                    <div class="detail-value">${data.brandName || '-'}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Nhà sản xuất</div>
                    <div class="detail-value">${data.manufacturer || '-'}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Xuất xứ</div>
                    <div class="detail-value">${data.countryOfOrigin || '-'}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Hoạt chất</div>
                    <div class="detail-value">${data.activeIngredient || '-'}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Số đăng ký</div>
                    <div class="detail-value">${data.registrationNumber || '-'}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Điều kiện bảo quản</div>
                    <div class="detail-value">${data.storageConditions || '-'}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Chỉ định</div>
                    <div class="detail-value">${data.indications || '-'}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Chống chỉ định</div>
                    <div class="detail-value">${data.contraindications || '-'}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Tác dụng phụ</div>
                    <div class="detail-value">${data.sideEffects || '-'}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Hướng dẫn sử dụng</div>
                    <div class="detail-value">${data.instructions || '-'}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Cần kê đơn</div>
                    <div class="detail-value">${data.prescriptionRequired ? 'Có' : 'Không'}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Trạng thái</div>
                    <div class="detail-value">${data.status === 1 ? 'Hoạt động' : 'Ngừng'}</div>
                </div>
            `;
            document.getElementById('detailModal').style.display = 'block';
        })
        .catch(err => {
            showToast('Không thể tải chi tiết thuốc', 'error');
        });
}

function closeDetailModal() {
    document.getElementById('detailModal').style.display = 'none';
}

function confirmDelete(id) {
    if (confirm('Bạn có chắc chắn muốn xóa thuốc này không?')) {
        deleteMedicine(id);
    }
}

function deleteMedicine(id) {
    fetch(`/api/owner/medicine/${id}`, {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' }
    })
    .then(res => {
        if (res.ok) {
            showToast('Xóa thuốc thành công!', 'success');
            medicineTable.ajax.reload();
        } else {
            return res.json().then(data => {
                const errorMessage = data.message || 'Không thể xóa thuốc';
                showToast(errorMessage, 'error');
            });
        }
    })
    .catch(err => {
        showToast('Có lỗi xảy ra: ' + err.message, 'error');
    });
}

// Form submissions
document.addEventListener('DOMContentLoaded', function() {
    const medicineForm = document.getElementById('medicineForm');
    const variantForm = document.getElementById('variantForm');
    
    if (medicineForm) {
        medicineForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const id = document.getElementById('medicineId').value;
            const data = {
                medicineName: document.getElementById('medicineName').value,
                categoryId: parseInt(document.getElementById('categoryId').value),
                brandName: document.getElementById('brandName').value,
                manufacturer: document.getElementById('manufacturer').value,
                countryOfOrigin: document.getElementById('countryOfOrigin').value,
                activeIngredient: document.getElementById('activeIngredient').value,
                registrationNumber: document.getElementById('registrationNumber').value,
                storageConditions: document.getElementById('storageConditions').value,
                indications: document.getElementById('indications').value,
                contraindications: document.getElementById('contraindications').value,
                sideEffects: document.getElementById('sideEffects').value,
                instructions: document.getElementById('instructions').value,
                prescriptionRequired: document.getElementById('prescriptionRequired').value === 'true',
                status: parseInt(document.getElementById('status').value)
            };

            const url = id ? `/api/owner/medicine/${id}` : '/api/owner/medicine';
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
                    throw new Error('Có lỗi xảy ra khi lưu thuốc');
                }
            })
            .then(data => {
                closeMedicineModal();
                medicineTable.ajax.reload();
                showToast(id ? 'Cập nhật thuốc thành công!' : 'Thêm thuốc mới thành công!', 'success');
            })
            .catch(err => {
                showToast('Có lỗi xảy ra: ' + err.message, 'error');
            });
        });
    }

    if (variantForm) {
        variantForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const variantId = document.getElementById('variantId').value;
            const medicineId = parseInt(document.getElementById('variantMedicineId').value);
            const data = {
                medicineId: medicineId,
                dosageForm: document.getElementById('dosageForm').value,
                dosage: document.getElementById('dosage').value,
                strength: document.getElementById('strength').value,
                baseUnitId: document.getElementById('baseUnitId').value ? parseInt(document.getElementById('baseUnitId').value) : null,
                barcode: document.getElementById('barcode').value
            };

            const url = variantId ? `/api/owner/medicine/variant/${variantId}` : '/api/owner/medicine/variant';
            const method = variantId ? 'PUT' : 'POST';

            fetch(url, {
                method: method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            })
            .then(res => {
                if (res.ok) {
                    return res.json();
                } else {
                    throw new Error('Có lỗi xảy ra khi lưu biến thể');
                }
            })
            .then(data => {
                cancelVariantForm();
                loadVariants(medicineId);
                showToast(variantId ? 'Cập nhật biến thể thành công!' : 'Thêm biến thể thành công!', 'success');
            })
            .catch(err => {
                showToast('Có lỗi xảy ra: ' + err.message, 'error');
            });
        });
    }

    // Close modal when clicking outside
    const medicineModal = document.getElementById('medicineModal');
    const variantModal = document.getElementById('variantModal');
    const detailModal = document.getElementById('detailModal');
    
    window.onclick = function(event) {
        if (event.target == medicineModal) {
            closeMedicineModal();
        }
        if (event.target == variantModal) {
            closeVariantModal();
        }
        if (event.target == detailModal) {
            closeDetailModal();
        }
    }
});

