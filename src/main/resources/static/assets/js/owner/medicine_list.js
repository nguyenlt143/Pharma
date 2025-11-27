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
                    const columnMap = ['id', 'name', 'category.name', 'brandName', 'manufacturer', 'activeIngredient'];
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
                name: 'name',
                render: function(data, type, row) {
                    return data || row.name || '-';
                }
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
                data: null,
                orderable: false,
                render: function(data, type, row) {
                    const medicineName = ((row.medicineName || row.name || '')).replace(/'/g, "\\'");
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
            // Store units in global variable for unit conversion rows
            if (Array.isArray(data)) {
                units = data;
            }

            // Load into variant modal dropdowns
            const baseUnitSelect = document.getElementById('baseUnitId');
            const packageUnitSelect = document.getElementById('packageUnitId');
            
            if (baseUnitSelect) {
                baseUnitSelect.innerHTML = '<option value="">Chọn đơn vị</option>';
                if (Array.isArray(data)) {
                    data.forEach(unit => {
                        const option = document.createElement('option');
                        option.value = unit.id;
                        option.textContent = unit.name || '';
                        baseUnitSelect.appendChild(option);
                    });
                }
            }
            
            if (packageUnitSelect) {
                packageUnitSelect.innerHTML = '<option value="">Chọn đơn vị</option>';
                if (Array.isArray(data)) {
                    data.forEach(unit => {
                        const option = document.createElement('option');
                        option.value = unit.id;
                        option.textContent = unit.name || '';
                        packageUnitSelect.appendChild(option);
                    });
                }
            }
        })
        .catch(err => {
            console.error('Error loading units:', err);
            units = [];
            const baseUnitSelect = document.getElementById('baseUnitId');
            const packageUnitSelect = document.getElementById('packageUnitId');
            if (baseUnitSelect) {
                baseUnitSelect.innerHTML = '<option value="">Chọn đơn vị</option>';
            }
            if (packageUnitSelect) {
                packageUnitSelect.innerHTML = '<option value="">Chọn đơn vị</option>';
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
            document.getElementById('medicineId').value = data.id || '';
            
            // Only set fields that exist in Medicine entity: name, category, activeIngredient, brandName, manufacturer, country
            const medicineNameEl = document.getElementById('medicineName');
            if (medicineNameEl) {
                medicineNameEl.value = data.name || data.medicineName || '';
            }
            
            const categoryIdEl = document.getElementById('categoryId');
            if (categoryIdEl) {
                categoryIdEl.value = data.categoryId || (data.category ? data.category.id : '') || '';
            }
            
            const brandNameEl = document.getElementById('brandName');
            if (brandNameEl) {
                brandNameEl.value = data.brandName || '';
            }
            
            const manufacturerEl = document.getElementById('manufacturer');
            if (manufacturerEl) {
                manufacturerEl.value = data.manufacturer || '';
            }
            
            const countryEl = document.getElementById('countryOfOrigin');
            if (countryEl) {
                countryEl.value = data.country || data.countryOfOrigin || '';
            }
            
            const activeIngredientEl = document.getElementById('activeIngredient');
            if (activeIngredientEl) {
                activeIngredientEl.value = data.activeIngredient || '';
            }
            
            document.getElementById('medicineModal').style.display = 'block';
        })
        .catch(err => {
            console.error('Error loading medicine:', err);
            showToast('Không thể tải thông tin thuốc', 'error');
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
                    <td style="padding: 12px;">${variant.dosageForm || variant.dosage_form || '-'}</td>
                    <td style="padding: 12px;">${variant.dosage || '-'}</td>
                    <td style="padding: 12px;">${variant.strength || '-'}</td>
                    <td style="padding: 12px;">${variant.baseUnitName || (variant.baseUnitId && variant.baseUnitId.name) || '-'}</td>
                    <td style="padding: 12px;">${variant.barcode || variant.Barcode || '-'}</td>
                    <td style="padding: 12px; text-align: center;">
                        <button onclick="viewVariantDetail(${variant.id})" class="btn-link" style="color: #7C3AED; margin: 0 4px;">Xem chi tiết</button>
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
    const form = document.getElementById('variantForm');
    if (form) {
        form.reset();
    }
    
    // Reset all variant form fields safely
    const fields = [
        'variantId', 'dosageForm', 'dosage', 'strength', 
        'packageUnitId', 'baseUnitId', 'quantityPerPackage', 
        'barcode', 'registrationNumber', 'storageConditions', 
        'indications', 'contraindications', 'sideEffects', 
        'instructions', 'prescriptionRequired', 'uses'
    ];
    
    fields.forEach(fieldId => {
        const field = document.getElementById(fieldId);
        if (field) {
            if (field.type === 'select-one') {
                field.value = '';
            } else {
                field.value = '';
            }
        }
    });
    
    // Set default for prescriptionRequired
    const prescriptionField = document.getElementById('prescriptionRequired');
    if (prescriptionField) {
        prescriptionField.value = 'false';
    }
    
    document.getElementById('variantFormContainer').style.display = 'block';
    document.getElementById('variantFormContainer').scrollIntoView({ behavior: 'smooth', block: 'nearest' });
}

function openEditVariantForm(variantId) {
    fetch(`/api/owner/medicine/variant/${variantId}`)
        .then(res => res.json())
        .then(data => {
            document.getElementById('variantModalTitle').textContent = 'Cập nhật biến thể thuốc';
            
            // Set values safely with null checks
            const setValue = (id, value) => {
                const el = document.getElementById(id);
                if (el) el.value = value || '';
            };
            
            setValue('variantId', data.id);
            setValue('dosageForm', data.dosageForm || data.dosage_form);
            setValue('dosage', data.dosage);
            setValue('strength', data.strength);
            setValue('packageUnitId', data.packageUnitId ? (typeof data.packageUnitId === 'object' ? data.packageUnitId.id : data.packageUnitId) : '');
            setValue('baseUnitId', data.baseUnitId ? (typeof data.baseUnitId === 'object' ? data.baseUnitId.id : data.baseUnitId) : '');
            setValue('quantityPerPackage', data.quantityPerPackage);
            setValue('barcode', data.barcode || data.Barcode);
            setValue('registrationNumber', data.registrationNumber);
            setValue('storageConditions', data.storageConditions);
            setValue('indications', data.indications);
            setValue('contraindications', data.contraindications);
            setValue('sideEffects', data.sideEffects);
            setValue('instructions', data.instructions);
            setValue('uses', data.uses);
            
            const prescriptionField = document.getElementById('prescriptionRequired');
            if (prescriptionField) {
                prescriptionField.value = (data.prescription_require !== undefined ? data.prescription_require : data.prescriptionRequired) ? 'true' : 'false';
            }
            
            // Load unit conversions for this variant
            loadUnitConversions(variantId);

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

function viewVariantDetail(variantId) {
    fetch(`/api/owner/medicine/variant/${variantId}`)
        .then(res => res.json())
        .then(data => {
            document.getElementById('variantModalTitle').textContent = 'Chi tiết biến thể thuốc';

            // Set values safely with null checks
            const setValue = (id, value) => {
                const el = document.getElementById(id);
                if (el) {
                    el.value = value || '';
                    el.disabled = true; // Disable all fields for read-only mode
                }
            };

            setValue('variantId', data.id);
            setValue('dosageForm', data.dosageForm || data.dosage_form);
            setValue('dosage', data.dosage);
            setValue('strength', data.strength);
            setValue('packageUnitId', data.packageUnitId ? (typeof data.packageUnitId === 'object' ? data.packageUnitId.id : data.packageUnitId) : '');
            setValue('baseUnitId', data.baseUnitId ? (typeof data.baseUnitId === 'object' ? data.baseUnitId.id : data.baseUnitId) : '');
            setValue('quantityPerPackage', data.quantityPerPackage);
            setValue('barcode', data.barcode || data.Barcode);
            setValue('registrationNumber', data.registrationNumber);
            setValue('storageConditions', data.storageConditions);
            setValue('indications', data.indications);
            setValue('contraindications', data.contraindications);
            setValue('sideEffects', data.sideEffects);
            setValue('instructions', data.instructions);
            setValue('uses', data.uses);

            const prescriptionField = document.getElementById('prescriptionRequired');
            if (prescriptionField) {
                prescriptionField.value = (data.prescription_require !== undefined ? data.prescription_require : data.prescriptionRequired) ? 'true' : 'false';
                prescriptionField.disabled = true;
            }

            // Load unit conversions for this variant (read-only)
            loadUnitConversions(variantId);

            // Disable unit conversion controls
            setTimeout(() => {
                // Disable all unit conversion inputs and selects
                document.querySelectorAll('#unitConversionTableBody .unit-select').forEach(el => el.disabled = true);
                document.querySelectorAll('#unitConversionTableBody .multiplier-input').forEach(el => el.disabled = true);
                document.querySelectorAll('#unitConversionTableBody .btn-link.delete').forEach(el => el.style.display = 'none');

                // Hide add button
                const addButtons = document.querySelectorAll('button[onclick="addUnitConversionRow()"]');
                addButtons.forEach(btn => btn.style.display = 'none');
            }, 500);

            // Show form container
            document.getElementById('variantFormContainer').style.display = 'block';

            // Hide submit button, change cancel button text
            const variantForm = document.getElementById('variantForm');
            const btnGroup = variantForm.querySelector('.btn-group');
            if (btnGroup) {
                btnGroup.innerHTML = `
                    <button type="button" class="btn-secondary" onclick="cancelViewVariant()">Đóng</button>
                `;
            }

            document.getElementById('variantFormContainer').scrollIntoView({ behavior: 'smooth', block: 'nearest' });
        })
        .catch(err => {
            showToast('Không thể tải thông tin biến thể', 'error');
        });
}

function cancelViewVariant() {
    // Re-enable all fields
    document.querySelectorAll('#variantForm input, #variantForm select, #variantForm textarea').forEach(el => {
        el.disabled = false;
    });

    // Re-enable unit conversion controls
    document.querySelectorAll('#unitConversionTableBody .unit-select').forEach(el => el.disabled = false);
    document.querySelectorAll('#unitConversionTableBody .multiplier-input').forEach(el => el.disabled = false);
    document.querySelectorAll('#unitConversionTableBody .btn-link.delete').forEach(el => el.style.display = '');

    // Show add button
    const addButtons = document.querySelectorAll('button[onclick="addUnitConversionRow()"]');
    addButtons.forEach(btn => btn.style.display = '');

    // Restore original button group
    const variantForm = document.getElementById('variantForm');
    const btnGroup = variantForm.querySelector('.btn-group');
    if (btnGroup) {
        btnGroup.innerHTML = `
            <button type="button" class="btn-secondary" onclick="cancelVariantForm()">Hủy</button>
            <button type="submit" class="btn-primary">Lưu</button>
        `;
    }

    // Hide form and reset title
    cancelVariantForm();
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
                    <div class="detail-value">${data.name || data.medicineName || '-'}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Danh mục</div>
                    <div class="detail-value">${data.categoryName || (data.category ? data.category.categoryName : '-') || '-'}</div>
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
                    <div class="detail-value">${data.country || data.countryOfOrigin || '-'}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Hoạt chất</div>
                    <div class="detail-value">${data.activeIngredient || '-'}</div>
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
            const categoryIdValue = document.getElementById('categoryId').value;
            
            // Only send fields that exist in Medicine entity
            const data = {
                medicineName: document.getElementById('medicineName').value, // DTO expects medicineName, entity has name
                categoryId: categoryIdValue ? parseInt(categoryIdValue) : null,
                brandName: document.getElementById('brandName').value || null,
                manufacturer: document.getElementById('manufacturer').value || null,
                countryOfOrigin: document.getElementById('countryOfOrigin').value || null, // DTO expects countryOfOrigin, entity has country
                activeIngredient: document.getElementById('activeIngredient').value || null
            };
            
            // Remove null/empty values to match entity structure
            Object.keys(data).forEach(key => {
                if (data[key] === null || data[key] === '') {
                    delete data[key];
                }
            });

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

            // Get unit conversions from form
            const unitConversionsData = getUnitConversionsFromForm();

            const data = {
                medicineId: medicineId,
                dosageForm: document.getElementById('dosageForm').value,
                dosage: document.getElementById('dosage').value,
                strength: document.getElementById('strength').value,
                packageUnitId: document.getElementById('packageUnitId').value ? parseInt(document.getElementById('packageUnitId').value) : null,
                baseUnitId: document.getElementById('baseUnitId').value ? parseInt(document.getElementById('baseUnitId').value) : null,
                quantityPerPackage: document.getElementById('quantityPerPackage').value ? parseFloat(document.getElementById('quantityPerPackage').value) : null,
                barcode: document.getElementById('barcode').value,
                registrationNumber: document.getElementById('registrationNumber').value,
                storageConditions: document.getElementById('storageConditions').value,
                indications: document.getElementById('indications').value,
                contraindications: document.getElementById('contraindications').value,
                sideEffects: document.getElementById('sideEffects').value,
                instructions: document.getElementById('instructions').value,
                prescription_require: document.getElementById('prescriptionRequired').value === 'true',
                uses: document.getElementById('uses').value,
                unitConversions: unitConversionsData
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

// ========== Unit Conversion Management ==========
let unitConversions = []; // Store unit conversions for the current variant

function addUnitConversionRow() {
    const tbody = document.getElementById('unitConversionTableBody');
    const rowIndex = tbody.children.length;

    const row = document.createElement('tr');
    row.style.borderBottom = '1px solid #E5E7EB';
    row.innerHTML = `
        <td style="padding: 12px;">
            <select class="form-select unit-select" data-index="${rowIndex}" onchange="updateTotalUnits()">
                <option value="">Chọn đơn vị</option>
                ${units.map(unit => `<option value="${unit.id}">${unit.name}</option>`).join('')}
            </select>
        </td>
        <td style="padding: 12px;">
            <input type="number" class="form-input multiplier-input" data-index="${rowIndex}" 
                   placeholder="VD: 1, 10, 100" step="0.01" min="0.01" required
                   onchange="updateTotalUnits()" style="width: 100%;">
        </td>
        <td style="padding: 12px; text-align: center;">
            <button type="button" onclick="removeUnitConversionRow(this)" class="btn-link delete">Xóa</button>
        </td>
    `;

    tbody.appendChild(row);
    updateTotalUnits();
}

function removeUnitConversionRow(button) {
    const row = button.closest('tr');
    row.remove();
    updateTotalUnits();

    // Re-index remaining rows
    const tbody = document.getElementById('unitConversionTableBody');
    Array.from(tbody.children).forEach((row, index) => {
        const select = row.querySelector('.unit-select');
        const input = row.querySelector('.multiplier-input');
        if (select) select.setAttribute('data-index', index);
        if (input) input.setAttribute('data-index', index);
    });
}

function updateTotalUnits() {
    const tbody = document.getElementById('unitConversionTableBody');
    const rows = tbody.querySelectorAll('tr');
    let total = 0;
    let hasError = false;

    rows.forEach(row => {
        const multiplierInput = row.querySelector('.multiplier-input');
        if (multiplierInput) {
            const value = parseFloat(multiplierInput.value);
            if (!isNaN(value) && value > 0) {
                total += value;
            } else if (multiplierInput.value) {
                hasError = true;
            }
        }
    });

    const displayEl = document.getElementById('totalUnitsDisplay');
    if (displayEl) {
        displayEl.textContent = hasError ? 'Lỗi nhập liệu' : total.toFixed(2);
        displayEl.style.color = hasError ? '#DC2626' : '#1E40AF';
    }
}

function getUnitConversionsFromForm() {
    const tbody = document.getElementById('unitConversionTableBody');
    const rows = tbody.querySelectorAll('tr');
    const conversions = [];

    rows.forEach(row => {
        const unitSelect = row.querySelector('.unit-select');
        const multiplierInput = row.querySelector('.multiplier-input');

        if (unitSelect && multiplierInput) {
            const unitId = parseInt(unitSelect.value);
            const multiplier = parseFloat(multiplierInput.value);

            if (unitId && !isNaN(multiplier) && multiplier > 0) {
                conversions.push({
                    unitId: unitId,
                    multiplier: multiplier
                });
            }
        }
    });

    return conversions;
}

function loadUnitConversions(variantId) {
    fetch(`/api/owner/medicine/variant/${variantId}/unit-conversions`)
        .then(res => res.json())
        .then(data => {
            unitConversions = data || [];
            renderUnitConversions(unitConversions);
        })
        .catch(err => {
            console.error('Error loading unit conversions:', err);
            unitConversions = [];
            renderUnitConversions([]);
        });
}

function renderUnitConversions(conversions) {
    const tbody = document.getElementById('unitConversionTableBody');
    tbody.innerHTML = '';

    if (conversions && conversions.length > 0) {
        conversions.forEach((conversion, index) => {
            const row = document.createElement('tr');
            row.style.borderBottom = '1px solid #E5E7EB';

            const unitId = conversion.unitId ? (typeof conversion.unitId === 'object' ? conversion.unitId.id : conversion.unitId) : '';
            const unitName = conversion.unitId ? (typeof conversion.unitId === 'object' ? conversion.unitId.name : '') : '';

            row.innerHTML = `
                <td style="padding: 12px;">
                    <select class="form-select unit-select" data-index="${index}" onchange="updateTotalUnits()">
                        <option value="">Chọn đơn vị</option>
                        ${units.map(unit => {
                            const selected = unit.id == unitId ? 'selected' : '';
                            return `<option value="${unit.id}" ${selected}>${unit.name}</option>`;
                        }).join('')}
                    </select>
                </td>
                <td style="padding: 12px;">
                    <input type="number" class="form-input multiplier-input" data-index="${index}" 
                           value="${conversion.multiplier || ''}" 
                           placeholder="VD: 1, 10, 100" step="0.01" min="0.01" required
                           onchange="updateTotalUnits()" style="width: 100%;">
                </td>
                <td style="padding: 12px; text-align: center;">
                    <button type="button" onclick="removeUnitConversionRow(this)" class="btn-link delete">Xóa</button>
                </td>
            `;

            tbody.appendChild(row);
        });
    }

    updateTotalUnits();
}

function clearUnitConversions() {
    const tbody = document.getElementById('unitConversionTableBody');
    tbody.innerHTML = '';
    unitConversions = [];
    updateTotalUnits();
}

