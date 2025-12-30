// Medicine Management JavaScript for Warehouse
let medicineTable;
let categories = [];
let units = [];
let dosageForms = [];

$(document).ready(function() {
    loadCategories();
    loadUnits();
    loadDosageForms();
    initDataTable();

    // Add event listener for dosage form selection
    $(document).on('change', '#dosageForm', function() {
        const dosageFormId = $(this).val();
        const previousValue = $(this).data('previous-value');

        // Check if there are existing unit conversions
        const tbody = document.getElementById('unitConversionTableBody');
        const hasExistingUnits = tbody && tbody.children.length > 0;

        if (hasExistingUnits && previousValue && previousValue !== dosageFormId) {
            // Ask for confirmation before changing
            if (!confirm('Thay đổi dạng bào chế sẽ xóa tất cả các đơn vị quy đổi hiện tại. Bạn có chắc chắn muốn tiếp tục?')) {
                // Revert to previous value
                $(this).val(previousValue);
                return;
            }
        }

        // Store current value as previous for next change
        $(this).data('previous-value', dosageFormId);

        if (dosageFormId) {
            loadAvailableUnitsForDosageForm(dosageFormId);
        } else {
            clearUnitConversions();
            units = [];
        }
    });
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
            url: '/api/warehouse/medicine',
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
                            <button onclick="openVariantModal(${row.id}, '${medicineName}')" class="btn-link" style="color: #059669; font-weight: 600;">📋 Xem Dạng thuốc</button>
                            <button onclick="viewDetails(${row.id})" class="btn-link" style="color: #7C3AED; font-weight: 600;">👁 Chi tiết</button>
                            <button onclick="openEditModal(${row.id})" class="btn-link" style="color: #2563EB;">✏ Sửa</button>
                            <button onclick="confirmDelete(${row.id})" class="btn-link delete" style="font-weight: 600;">🗑 Xóa</button>
                        </div>
                    `;
                }
            }
        ],
        // Mặc định sắp xếp ID tăng dần (1 -> mới nhất)
        order: [[0, 'asc']],
        language: {
            url: '/assets/datatable_vi.json'
        }
    });
}

function loadCategories() {
    fetch('/api/warehouse/category?draw=1&start=0&length=1000')
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
    fetch('/api/warehouse/units')
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

function loadDosageForms() {
    // Load dosage forms from API
    fetch('/api/warehouse/dosage-form')
        .then(res => res.json())
        .then(data => {
            // Store dosage forms in global variable
            if (Array.isArray(data)) {
                dosageForms = data;
            }

            // Load into dosage form dropdown
            const dosageFormSelect = document.getElementById('dosageForm');
            if (dosageFormSelect) {
                dosageFormSelect.innerHTML = '<option value="">Chọn dạng bào chế</option>';
                if (Array.isArray(data)) {
                    data.forEach(form => {
                        const option = document.createElement('option');
                        option.value = form.id;
                        // Display format: "Tên dạng bào chế (đơn vị cơ bản)"
                        const displayText = form.displayName + (form.baseUnitName ? ` (${form.baseUnitName})` : '');
                        option.textContent = displayText;
                        dosageFormSelect.appendChild(option);
                    });
                }
            }
        })
        .catch(err => {
            console.error('Error loading dosage forms:', err);
            dosageForms = [];
            const dosageFormSelect = document.getElementById('dosageForm');
            if (dosageFormSelect) {
                dosageFormSelect.innerHTML = '<option value="">Chọn dạng bào chế</option>';
            }
        });
}

function loadAvailableUnitsForDosageForm(dosageFormId) {
    // Load available units for the selected dosage form
    fetch(`/api/warehouse/unit/available?dosageFormId=${dosageFormId}`)
        .then(res => res.json())
        .then(data => {
            // Update global units variable with available units
            if (Array.isArray(data)) {
                units = data;
            } else {
                units = [];
            }

            // Clear existing unit conversion rows
            clearUnitConversions();

            // Load dosage form info to get base unit
            return fetch(`/api/warehouse/dosage-form/${dosageFormId}`);
        })
        .then(res => res.json())
        .then(dosageFormData => {
            // Add base unit automatically with multiplier = 1 (locked)
            if (dosageFormData && dosageFormData.baseUnitId) {
                addBaseUnitConversion(dosageFormData.baseUnitId, dosageFormData.baseUnitName);
            }

            // Show message if no available units
            if (units.length === 0) {
                showToast('Không có đơn vị khả dụng cho dạng bào chế này', 'info');
            }
        })
        .catch(err => {
            console.error('Error loading available units or dosage form:', err);
            units = [];
            showToast('Không thể tải danh sách đơn vị', 'error');
        });
}

function openCreateModal() {
    // Close other modals if they're open
    closeVariantModal();
    closeDetailModal();

    document.getElementById('modalTitle').textContent = 'Thêm thuốc mới';
    document.getElementById('medicineForm').reset();
    document.getElementById('medicineId').value = '';
    document.getElementById('medicineModal').style.display = 'block';
}

function openEditModal(id) {
    // Close other modals if they're open
    closeVariantModal();
    closeDetailModal();

    fetch(`/api/warehouse/medicine/${id}`)
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
    // Close other modals if they're open
    closeMedicineModal();
    closeDetailModal();

    document.getElementById('variantMedicineId').value = medicineId;
    document.getElementById('variantMedicineName').textContent = medicineName;
    document.getElementById('variantFormContainer').style.display = 'none';
    document.getElementById('variantTableBody').innerHTML = '<tr><td colspan="6" style="padding: 24px; text-align: center; color: #6B7280;">Đang tải...</td></tr>';

    // Ensure variant list is visible when opening modal
    const variantListContainer = document.getElementById('variantList').parentElement;
    if (variantListContainer) {
        variantListContainer.style.display = 'block';
    }

    document.getElementById('variantModal').style.display = 'block';
    loadVariants(medicineId);
}

function closeVariantModal() {
    document.getElementById('variantModal').style.display = 'none';
    document.getElementById('variantFormContainer').style.display = 'none';
    document.getElementById('variantForm').reset();

    // Show variant list again when closing modal
    const variantListContainer = document.getElementById('variantList').parentElement;
    if (variantListContainer) {
        variantListContainer.style.display = 'block';
    }
}

function loadVariants(medicineId) {
    fetch(`/api/warehouse/medicine/${medicineId}/variants`)
        .then(res => res.json())
        .then(data => {
            const tbody = document.getElementById('variantTableBody');
            if (data.length === 0) {
                tbody.innerHTML = '<tr><td colspan="5" style="padding: 24px; text-align: center; color: #6B7280;">Chưa có dạng thuốc nào</td></tr>';
                return;
            }

            tbody.innerHTML = data.map(variant => `
                <tr style="border-bottom: 1px solid #E5E7EB;">
                    <td style="padding: 12px;">${variant.dosageForm || variant.dosage_form || '-'}</td>
                    <td style="padding: 12px;">${variant.dosage || '-'}</td>
                    <td style="padding: 12px;">${variant.strength || '-'}</td>
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
            document.getElementById('variantTableBody').innerHTML = '<tr><td colspan="5" style="padding: 24px; text-align: center; color: #DC2626;">Lỗi khi tải danh sách dạng thuốc</td></tr>';
        });
}

function openCreateVariantForm() {
    document.getElementById('variantModalTitle').textContent = 'Thêm dạng thuốc';
    const form = document.getElementById('variantForm');
    if (form) {
        form.reset();
    }

    // Reset all variant form fields safely
    const fields = [
        'variantId', 'dosageForm', 'dosage', 'strength', 'packaging',
        'packageUnitId', 'baseUnitId', 'quantityPerPackage',
        'barcode', 'registrationNumber', 'storageConditions',
        'indications', 'contraindications', 'sideEffects',
        'instructions', 'prescriptionRequired', 'uses', 'note'
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

    // Clear unit conversions
    clearUnitConversions();

    // Reset previous-value data attribute for dosageForm
    const dosageFormSelect = document.getElementById('dosageForm');
    if (dosageFormSelect) {
        $(dosageFormSelect).data('previous-value', '');
    }

    // Hide variant list and show form
    const variantListContainer = document.getElementById('variantList').parentElement;
    if (variantListContainer) {
        variantListContainer.style.display = 'none';
    }

    document.getElementById('variantFormContainer').style.display = 'block';
    document.getElementById('variantFormContainer').scrollIntoView({ behavior: 'smooth', block: 'nearest' });
}

function openEditVariantForm(variantId) {
    fetch(`/api/warehouse/medicine/variant/${variantId}`)
        .then(res => res.json())
        .then(data => {
            document.getElementById('variantModalTitle').textContent = 'Cập nhật dạng thuốc';

            // Set values safely with null checks
            const setValue = (id, value) => {
                const el = document.getElementById(id);
                if (el) el.value = value || '';
            };

            setValue('variantId', data.id);
            setValue('dosageForm', data.dosageFormId); // Set the dosageFormId in dropdown
            setValue('dosage', data.dosage);
            setValue('strength', data.strength);
            setValue('packaging', data.packaging);
            setValue('barcode', data.barcode || data.Barcode);
            setValue('registrationNumber', data.registrationNumber);
            setValue('storageConditions', data.storageConditions);
            setValue('instructions', data.instructions);
            setValue('note', data.note);

            // Store current dosageFormId as previous-value
            const dosageFormSelect = document.getElementById('dosageForm');
            if (dosageFormSelect && data.dosageFormId) {
                $(dosageFormSelect).data('previous-value', data.dosageFormId);
            }

            const prescriptionField = document.getElementById('prescriptionRequired');
            if (prescriptionField) {
                prescriptionField.value = (data.prescription_require !== undefined ? data.prescription_require : data.prescriptionRequired) ? 'true' : 'false';
            }

            // Load available units for the dosage form first, then load unit conversions
            if (data.dosageFormId) {
                fetch(`/api/warehouse/unit/available?dosageFormId=${data.dosageFormId}`)
                    .then(res => res.json())
                    .then(availableUnits => {
                        // Update global units variable
                        if (Array.isArray(availableUnits)) {
                            units = availableUnits;
                        }
                        // Then load unit conversions
                        loadUnitConversions(variantId);
                    })
                    .catch(err => {
                        console.error('Error loading available units:', err);
                        loadUnitConversions(variantId);
                    });
            } else {
                loadUnitConversions(variantId);
            }

            // Hide variant list and show form
            const variantListContainer = document.getElementById('variantList').parentElement;
            if (variantListContainer) {
                variantListContainer.style.display = 'none';
            }

            document.getElementById('variantFormContainer').style.display = 'block';
            document.getElementById('variantFormContainer').scrollIntoView({ behavior: 'smooth', block: 'nearest' });
        })
        .catch(err => {
            showToast('Không thể tải thông tin dạng thuốc', 'error');
        });
}

function cancelVariantForm() {
    document.getElementById('variantFormContainer').style.display = 'none';
    document.getElementById('variantForm').reset();
    document.getElementById('variantModalTitle').textContent = 'Quản lý dạng thuốc';

    // Show variant list again
    const variantListContainer = document.getElementById('variantList').parentElement;
    if (variantListContainer) {
        variantListContainer.style.display = 'block';
    }
}

function viewVariantDetail(variantId) {
    fetch(`/api/warehouse/medicine/variant/${variantId}`)
        .then(res => res.json())
        .then(data => {
            document.getElementById('variantModalTitle').textContent = 'Chi tiết dạng thuốc';

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
            setValue('packaging', data.packaging);
            setValue('barcode', data.barcode || data.Barcode);
            setValue('registrationNumber', data.registrationNumber);
            setValue('storageConditions', data.storageConditions);
            setValue('instructions', data.instructions);

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
                document.querySelectorAll('#unitConversionTableBody .note-input').forEach(el => el.disabled = true);
                document.querySelectorAll('#unitConversionTableBody .btn-link.delete').forEach(el => el.style.display = 'none');

                // Hide add button
                const addButtons = document.querySelectorAll('button[onclick="addUnitConversionRow()"]');
                addButtons.forEach(btn => btn.style.display = 'none');
            }, 500);

            // Hide variant list and show form
            const variantListContainer = document.getElementById('variantList').parentElement;
            if (variantListContainer) {
                variantListContainer.style.display = 'none';
            }

            // Show form container
            document.getElementById('variantFormContainer').style.display = 'block';

            // Hide submit button, change cancel button text
            const variantForm = document.getElementById('variantForm');
            const btnGroup = variantForm.querySelector('.btn-group');
            if (btnGroup) {
                btnGroup.innerHTML = `
                    <button type="button" class="btn-close-view" onclick="cancelViewVariant()">✓ Đóng xem chi tiết</button>
                `;
            }

            document.getElementById('variantFormContainer').scrollIntoView({ behavior: 'smooth', block: 'nearest' });
        })
        .catch(err => {
            showToast('Không thể tải thông tin dạng thuốc', 'error');
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
    document.querySelectorAll('#unitConversionTableBody .note-input').forEach(el => el.disabled = false);
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
    if (confirm('Bạn có chắc chắn muốn xóa dạng thuốc này không?')) {
        deleteVariant(variantId);
    }
}

function deleteVariant(variantId) {
    fetch(`/api/warehouse/medicine/variant/${variantId}`, {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' }
    })
    .then(res => {
        if (res.ok) {
            showToast('Xóa dạng thuốc thành công!', 'success');
            const medicineId = document.getElementById('variantMedicineId').value;
            loadVariants(medicineId);
        } else {
            return res.json().then(data => {
                const errorMessage = data.message || 'Không thể xóa dạng thuốc';
                showToast(errorMessage, 'error');
            });
        }
    })
    .catch(err => {
        showToast('Có lỗi xảy ra: ' + err.message, 'error');
    });
}

function viewDetails(id) {
    // Close variant modal if it's open
    closeVariantModal();

    fetch(`/api/warehouse/medicine/${id}`)
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
    fetch(`/api/warehouse/medicine/${id}`, {
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
                medicineName: document.getElementById('medicineName').value,
                categoryId: categoryIdValue ? parseInt(categoryIdValue) : null,
                brandName: document.getElementById('brandName').value || null,
                manufacturer: document.getElementById('manufacturer').value || null,
                countryOfOrigin: document.getElementById('countryOfOrigin').value || null,
                activeIngredient: document.getElementById('activeIngredient').value || null
            };

            // Remove null/empty values to match entity structure
            Object.keys(data).forEach(key => {
                if (data[key] === null || data[key] === '') {
                    delete data[key];
                }
            });

            const url = id ? `/api/warehouse/medicine/${id}` : '/api/warehouse/medicine';
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
                // Try to read detailed error message from backend
                return res.json()
                    .then(err => {
                        const msg = err.message || err.error || 'Có lỗi xảy ra khi lưu thuốc';
                        throw new Error(msg);
                    })
                    .catch(() => {
                        throw new Error('Có lỗi xảy ra khi lưu thuốc');
                    });
            })
            .then(data => {
                closeMedicineModal();
                medicineTable.ajax.reload();
                showToast(id ? 'Cập nhật thuốc thành công!' : 'Thêm thuốc mới thành công!', 'success');
            })
            .catch(err => {
                showToast(err.message, 'error');
            });
        });
    }

    if (variantForm) {
        variantForm.addEventListener('submit', function(e) {
            e.preventDefault();

            const variantId = document.getElementById('variantId').value;
            const medicineId = parseInt(document.getElementById('variantMedicineId').value);

            // Validate all required fields (only fields that exist in the form)
            const dosageFormId = document.getElementById('dosageForm').value.trim();
            const dosage = document.getElementById('dosage').value.trim();
            const strength = document.getElementById('strength').value.trim();
            const packaging = document.getElementById('packaging').value.trim();
            const barcode = document.getElementById('barcode').value.trim();
            const registrationNumber = document.getElementById('registrationNumber').value.trim();
            const storageConditions = document.getElementById('storageConditions').value.trim();
            const instructions = document.getElementById('instructions').value.trim();
            const prescriptionRequired = document.getElementById('prescriptionRequired').value;
            const note = document.getElementById('note') ? document.getElementById('note').value.trim() : '';

            // Validation checks
            if (!dosageFormId) {
                showToast('Dạng bào chế không được để trống', 'error');
                document.getElementById('dosageForm').focus();
                return;
            }
            if (!dosage) {
                showToast('Liều dùng không được để trống', 'error');
                document.getElementById('dosage').focus();
                return;
            }
            if (!strength) {
                showToast('Hàm lượng không được để trống', 'error');
                document.getElementById('strength').focus();
                return;
            }
            if (!barcode) {
                showToast('Mã vạch không được để trống', 'error');
                document.getElementById('barcode').focus();
                return;
            }
            if (!registrationNumber) {
                showToast('Số đăng ký không được để trống', 'error');
                document.getElementById('registrationNumber').focus();
                return;
            }
            if (!storageConditions) {
                showToast('Điều kiện bảo quản không được để trống', 'error');
                document.getElementById('storageConditions').focus();
                return;
            }
            if (!instructions) {
                showToast('Hướng dẫn sử dụng không được để trống', 'error');
                document.getElementById('instructions').focus();
                return;
            }

            // Get unit conversions from form
            const unitConversionsData = getUnitConversionsFromForm();

            // Validate unit conversion order
            if (!validateUnitConversionsOrder(unitConversionsData)) {
                return;
            }

            const data = {
                medicineId: medicineId,
                dosageFormId: parseInt(dosageFormId),
                dosage: dosage,
                strength: strength,
                packaging: packaging || null,
                barcode: barcode,
                registrationNumber: registrationNumber,
                storageConditions: storageConditions,
                instructions: instructions,
                prescription_require: prescriptionRequired === 'true',
                note: note || null,
                unitConversions: unitConversionsData
            };

            const url = variantId ? `/api/warehouse/medicine/variant/${variantId}` : '/api/warehouse/medicine/variant';
            const method = variantId ? 'PUT' : 'POST';

            fetch(url, {
                method: method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            })
            .then(res => {
                if (res.ok) {
                    return res.json();
                }
                // Try to read detailed error message from backend
                return res.json()
                    .then(err => {
                        const msg = err.message || err.error || 'Có lỗi xảy ra khi lưu dạng thuốc';
                        throw new Error(msg);
                    })
                    .catch(() => {
                        throw new Error('Có lỗi xảy ra khi lưu dạng thuốc');
                    });
            })
            .then(data => {
                cancelVariantForm();
                loadVariants(medicineId);
                showToast(variantId ? 'Cập nhật dạng thuốc thành công!' : 'Thêm dạng thuốc thành công!', 'success');
            })
            .catch(err => {
                showToast(err.message, 'error');
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

    // Note: Event listener for dosageForm dropdown is already handled in $(document).ready()
    // to avoid duplicate calls to loadAvailableUnitsForDosageForm
});

// ========== Unit Conversion Management ==========
let unitConversions = []; // Store unit conversions for the current variant

// Hàm thêm đơn vị cơ bản tự động khi chọn dạng bào chế
function addBaseUnitConversion(baseUnitId, baseUnitName) {
    const tbody = document.getElementById('unitConversionTableBody');

    // Check if base unit already exists
    const existingBaseUnit = tbody.querySelector('tr[data-is-base-unit="true"]');
    if (existingBaseUnit) {
        console.log('Base unit already exists, skipping duplicate addition');
        return;
    }

    const row = document.createElement('tr');
    row.style.borderBottom = '1px solid #E5E7EB';
    row.setAttribute('data-is-base-unit', 'true'); // Đánh dấu là đơn vị cơ bản

    row.innerHTML = `
        <td style="padding: 12px; background-color: #F9FAFB;">
            <input type="text" class="form-input" value="${baseUnitName || 'Đơn vị cơ bản'}"
                   readonly style="background-color: #F9FAFB; border-color: #E5E7EB;">
            <input type="hidden" class="base-unit-id" value="${baseUnitId}">
        </td>
        <td style="padding: 12px; background-color: #F9FAFB;">
            <input type="number" class="form-input multiplier-input" value="1"
                   readonly style="background-color: #F9FAFB; border-color: #E5E7EB;">
        </td>
        <td style="padding: 12px; text-align: center; background-color: #F9FAFB;">
            <input type="checkbox" class="show-in-pos-checkbox" checked
                   style="width: 18px; height: 18px; cursor: pointer;">
        </td>
        <td style="padding: 12px; text-align: center; background-color: #F9FAFB;">
            <span style="color: #6B7280; font-size: 12px;">Đơn vị cơ bản</span>
        </td>
    `;

    tbody.appendChild(row);
    updateTotalUnits();
    autoUpdatePackaging();
}

// Hàm tính giá trị min cho đơn vị mới (phải lớn hơn giá trị lớn nhất hiện có)
function getMinValueForNewUnit() {
    const tbody = document.getElementById('unitConversionTableBody');
    const rows = tbody.querySelectorAll('tr');
    let maxValue = 0;

    rows.forEach(row => {
        const multiplierInput = row.querySelector('.multiplier-input');
        if (multiplierInput && multiplierInput.value) {
            const value = parseFloat(multiplierInput.value);
            if (!isNaN(value) && value > maxValue) {
                maxValue = value;
            }
        }
    });

    return maxValue + 1; // Giá trị mới phải lớn hơn giá trị lớn nhất hiện có
}

// Hàm kiểm tra đơn vị trùng lặp
function validateUnitDuplication(selectElement) {
    const tbody = document.getElementById('unitConversionTableBody');
    const rows = Array.from(tbody.querySelectorAll('tr'));
    const currentValue = selectElement.value;

    if (!currentValue) {
        return true; // Bỏ qua nếu chưa chọn đơn vị
    }

    // Đếm số lần đơn vị này xuất hiện
    let count = 0;
    rows.forEach(row => {
        const select = row.querySelector('.unit-select');
        if (select && select.value === currentValue) {
            count++;
        }
    });

    // Nếu xuất hiện hơn 1 lần = trùng lặp
    if (count > 1) {
        selectElement.style.borderColor = '#DC2626';

        // Lấy tên đơn vị để hiển thị
        const unitName = selectElement.options[selectElement.selectedIndex].text;
        showToast(`Đơn vị "${unitName}" đã được sử dụng. Vui lòng chọn đơn vị khác.`, 'error');

        // Reset về giá trị rỗng
        selectElement.value = '';
        return false;
    }

    // Reset border nếu hợp lệ
    selectElement.style.borderColor = '';
    return true;
}

// Hàm validate thứ tự các giá trị quy đổi
function validateMultiplierOrder(input) {
    const tbody = document.getElementById('unitConversionTableBody');
    const rows = Array.from(tbody.querySelectorAll('tr'));
    const currentRow = input.closest('tr');
    const currentIndex = rows.indexOf(currentRow);
    const currentValue = parseFloat(input.value);

    // Bỏ qua validation cho đơn vị cơ bản
    if (currentRow.getAttribute('data-is-base-unit') === 'true') {
        return true;
    }

    if (isNaN(currentValue) || currentValue <= 0) {
        input.style.borderColor = '#DC2626';
        showToast('Giá trị quy đổi phải lớn hơn 0', 'error');
        return false;
    }

    // Kiểm tra đơn vị đầu tiên (không phải base unit) phải > 1
    const nonBaseRows = rows.filter(row => row.getAttribute('data-is-base-unit') !== 'true');
    const currentNonBaseIndex = nonBaseRows.indexOf(currentRow);

    if (currentNonBaseIndex === 0 && currentValue <= 1) {
        input.style.borderColor = '#DC2626';
        showToast('Đơn vị đầu tiên phải có giá trị lớn hơn 1 (vì đơn vị cơ bản có tỉ lệ = 1)', 'error');
        return false;
    }

    // Kiểm tra với các giá trị trước đó (chỉ các đơn vị quy đổi)
    for (let i = 0; i < currentNonBaseIndex; i++) {
        const prevInput = nonBaseRows[i].querySelector('.multiplier-input');
        if (prevInput) {
            const prevValue = parseFloat(prevInput.value);
            if (!isNaN(prevValue)) {
                if (currentValue <= prevValue) {
                    input.style.borderColor = '#DC2626';
                    showToast(`Giá trị quy đổi phải lớn hơn ${prevValue} (đơn vị trước đó)`, 'error');
                    input.value = '';
                    return false;
                }

                // Kiểm tra chia hết cho giá trị trước đó gần nhất
                if (i === currentNonBaseIndex - 1 && currentValue % prevValue !== 0) {
                    input.style.borderColor = '#DC2626';
                    showToast(`Giá trị quy đổi ${currentValue} phải chia hết cho ${prevValue}`, 'error');
                    input.value = '';
                    return false;
                }
            }
        }
    }

    // Kiểm tra với các giá trị sau đó (chỉ các đơn vị quy đổi)
    for (let i = currentNonBaseIndex + 1; i < nonBaseRows.length; i++) {
        const nextInput = nonBaseRows[i].querySelector('.multiplier-input');
        if (nextInput && nextInput.value) {
            const nextValue = parseFloat(nextInput.value);
            if (!isNaN(nextValue) && currentValue >= nextValue) {
                input.style.borderColor = '#DC2626';
                showToast(`Giá trị quy đổi phải nhỏ hơn ${nextValue} (đơn vị sau đó)`, 'error');
                input.value = '';
                return false;
            }
        }
    }

    // Nếu hợp lệ, reset border color
    input.style.borderColor = '';
    updateTotalUnits();
    return true;
}

function addUnitConversionRow() {
    // Check if dosage form is selected
    const dosageFormSelect = document.getElementById('dosageForm');
    if (!dosageFormSelect || !dosageFormSelect.value) {
        showToast('Vui lòng chọn dạng bào chế trước khi thêm đơn vị quy đổi', 'error');
        return;
    }

    // Check if there are available units
    if (!units || units.length === 0) {
        showToast('Không có đơn vị khả dụng cho dạng bào chế này', 'error');
        return;
    }

    const tbody = document.getElementById('unitConversionTableBody');
    const rowIndex = tbody.children.length;

    // Tính giá trị min cho đơn vị mới (phải lớn hơn giá trị lớn nhất hiện tại)
    const minValue = getMinValueForNewUnit();

    const row = document.createElement('tr');
    row.style.borderBottom = '1px solid #E5E7EB';
    row.innerHTML = `
        <td style="padding: 12px;">
            <select class="form-select unit-select" data-index="${rowIndex}">
                <option value="">Chọn đơn vị</option>
                ${units.map(unit => `<option value="${unit.id}">${unit.name}</option>`).join('')}
            </select>
        </td>
        <td style="padding: 12px;">
            <input type="number" class="form-input multiplier-input" data-index="${rowIndex}" 
                   value=""
                   placeholder="Nhập giá trị quy đổi" step="0.01" min="0.01" required
                   onchange="validateMultiplierOrder(this)" style="width: 100%;">
        </td>
        <td style="padding: 12px; text-align: center;">
            <input type="checkbox" class="show-in-pos-checkbox" data-index="${rowIndex}" checked
                   style="width: 18px; height: 18px; cursor: pointer;">
        </td>
        <td style="padding: 12px; text-align: center;">
            <button type="button" onclick="removeUnitConversionRow(this)" class="btn-link delete">Xóa</button>
        </td>
    `;

    tbody.appendChild(row);

    // Add event listener for unit select to validate duplication
    const unitSelect = row.querySelector('.unit-select');
    const multiplierInput = row.querySelector('.multiplier-input');

    if (unitSelect) {
        unitSelect.addEventListener('change', function() {
            validateUnitDuplication(this);
            updateTotalUnits();
            autoUpdatePackaging();
        });
    }

    if (multiplierInput) {
        multiplierInput.addEventListener('input', function() {
            updateTotalUnits();
            autoUpdatePackaging();
        });
    }

    updateTotalUnits();
}

// Function to auto-update packaging when unit conversions change
function autoUpdatePackaging() {
    const tbody = document.getElementById('unitConversionTableBody');
    const rows = tbody.querySelectorAll('tr');

    // Check if we have enough complete data for auto-generation
    let completeUnits = 0;
    rows.forEach(row => {
        const isBaseUnit = row.getAttribute('data-is-base-unit') === 'true';

        if (isBaseUnit) {
            completeUnits++;
        } else {
            const unitSelect = row.querySelector('.unit-select');
            const multiplierInput = row.querySelector('.multiplier-input');

            if (unitSelect && multiplierInput && unitSelect.value && multiplierInput.value && parseFloat(multiplierInput.value) > 0) {
                completeUnits++;
            }
        }
    });

    // Auto-generate if we have at least 2 complete units
    if (completeUnits >= 2) {
        // Silently generate without showing toast
        const packagingField = document.getElementById('packaging');
        const unitData = [];

        rows.forEach(row => {
            const isBaseUnit = row.getAttribute('data-is-base-unit') === 'true';

            if (isBaseUnit) {
                const baseUnitInput = row.querySelector('input[type="text"]');
                if (baseUnitInput && baseUnitInput.value) {
                    unitData.push({
                        name: baseUnitInput.value,
                        multiplier: 1,
                        isBase: true
                    });
                }
            } else {
                const unitSelect = row.querySelector('.unit-select');
                const multiplierInput = row.querySelector('.multiplier-input');

                if (unitSelect && multiplierInput) {
                    const unitId = parseInt(unitSelect.value);
                    const multiplier = parseFloat(multiplierInput.value);

                    if (unitId && !isNaN(multiplier) && multiplier > 0) {
                        const unitName = unitSelect.options[unitSelect.selectedIndex].text;
                        unitData.push({
                            name: unitName,
                            multiplier: multiplier,
                            isBase: false
                        });
                    }
                }
            }
        });

        if (unitData.length >= 2) {
            unitData.sort((a, b) => b.multiplier - a.multiplier);

            let packagingSpec = unitData[0].name;

            for (let i = 0; i < unitData.length - 1; i++) {
                const currentUnit = unitData[i];
                const nextUnit = unitData[i + 1];
                const quantity = currentUnit.multiplier / nextUnit.multiplier;

                // All levels have the same format: "x 10 Hộp"
                packagingSpec += ` x ${quantity} ${nextUnit.name}`;
            }

            packagingField.value = packagingSpec;
        }
    }
}

function removeUnitConversionRow(button) {
    const row = button.closest('tr');
    row.remove();
    updateTotalUnits();

    // Re-index remaining rows and update min values
    const tbody = document.getElementById('unitConversionTableBody');
    Array.from(tbody.children).forEach((row, index) => {
        const select = row.querySelector('.unit-select');
        const input = row.querySelector('.multiplier-input');
        if (select) select.setAttribute('data-index', index);
        if (input) {
            input.setAttribute('data-index', index);
        }
    });

    // Auto-update packaging after removal
    autoUpdatePackaging();
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
        const isBaseUnit = row.getAttribute('data-is-base-unit') === 'true';
        const showInPosCheckbox = row.querySelector('.show-in-pos-checkbox');
        const isSale = showInPosCheckbox ? showInPosCheckbox.checked : true;

        if (isBaseUnit) {
            // Xử lý đơn vị cơ bản
            const baseUnitId = row.querySelector('.base-unit-id');
            if (baseUnitId && baseUnitId.value) {
                conversions.push({
                    unitId: parseInt(baseUnitId.value),
                    multiplier: 1,
                    isSale: isSale
                });
            }
        } else {
            // Xử lý đơn vị quy đổi thông thường
            const unitSelect = row.querySelector('.unit-select');
            const multiplierInput = row.querySelector('.multiplier-input');

            if (unitSelect && multiplierInput) {
                const unitId = parseInt(unitSelect.value);
                const multiplier = parseFloat(multiplierInput.value);

                if (unitId && !isNaN(multiplier) && multiplier > 0) {
                    conversions.push({
                        unitId: unitId,
                        multiplier: multiplier,
                        isSale: isSale
                    });
                }
            }
        }
    });

    return conversions;
}

function validateUnitConversionsOrder(conversions) {
    if (!conversions || conversions.length === 0) {
        return true; // Cho phép không có đơn vị quy đổi
    }

    // Kiểm tra đơn vị trùng lặp
    const unitIds = conversions.map(c => c.unitId);
    const uniqueUnitIds = new Set(unitIds);
    if (unitIds.length !== uniqueUnitIds.size) {
        showToast('Không được chọn trùng đơn vị quy đổi', 'error');
        return false;
    }

    // Tìm đơn vị cơ bản (multiplier = 1) và các đơn vị quy đổi
    const baseUnitIndex = conversions.findIndex(c => c.multiplier === 1);
    const conversionUnits = conversions.filter(c => c.multiplier > 1);

    if (baseUnitIndex === -1) {
        showToast('Phải có đơn vị cơ bản với tỉ lệ quy đổi = 1', 'error');
        return false;
    }

    // Kiểm tra thứ tự tăng dần và chia hết cho các đơn vị quy đổi (không bao gồm đơn vị cơ bản)
    conversionUnits.sort((a, b) => a.multiplier - b.multiplier);

    for (let i = 1; i < conversionUnits.length; i++) {
        if (conversionUnits[i].multiplier <= conversionUnits[i - 1].multiplier) {
            showToast(`Giá trị quy đổi phải tăng dần! Đơn vị (${conversionUnits[i].multiplier}) phải lớn hơn đơn vị trước (${conversionUnits[i - 1].multiplier})`, 'error');
            return false;
        }

        // Kiểm tra chia hết
        if (conversionUnits[i].multiplier % conversionUnits[i - 1].multiplier !== 0) {
            showToast(`Đơn vị (${conversionUnits[i].multiplier}) phải chia hết cho đơn vị trước (${conversionUnits[i - 1].multiplier})`, 'error');
            return false;
        }
    }

    return true;
}

function loadUnitConversions(variantId) {
    fetch(`/api/warehouse/medicine/variant/${variantId}/unit-conversions`)
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
            const multiplier = conversion.multiplier || '';
            const isBaseUnit = multiplier === 1;

            if (isBaseUnit) {
                // Render đơn vị cơ bản (readonly)
                row.setAttribute('data-is-base-unit', 'true');
                const isSale = conversion.isSale !== false; // Default true if not specified
                row.innerHTML = `
                    <td style="padding: 12px; background-color: #F9FAFB;">
                        <input type="text" class="form-input" value="${unitName || 'Đơn vị cơ bản'}"
                               readonly style="background-color: #F9FAFB; border-color: #E5E7EB;">
                        <input type="hidden" class="base-unit-id" value="${unitId}">
                    </td>
                    <td style="padding: 12px; background-color: #F9FAFB;">
                        <input type="number" class="form-input multiplier-input" value="1"
                               readonly style="background-color: #F9FAFB; border-color: #E5E7EB;">
                    </td>
                    <td style="padding: 12px; text-align: center; background-color: #F9FAFB;">
                        <input type="checkbox" class="show-in-pos-checkbox" data-index="${index}" ${isSale ? 'checked' : ''}
                               style="width: 18px; height: 18px; cursor: pointer;">
                    </td>
                    <td style="padding: 12px; text-align: center; background-color: #F9FAFB;">
                        <span style="color: #6B7280; font-size: 12px;">Đơn vị cơ bản</span>
                    </td>
                `;
            } else {
                // Render đơn vị quy đổi thông thường
                const isSale = conversion.isSale !== false; // Default true if not specified
                row.innerHTML = `
                    <td style="padding: 12px;">
                        <select class="form-select unit-select" data-index="${index}">
                            <option value="">Chọn đơn vị</option>
                            ${units.map(unit => {
                                const selected = unit.id == unitId ? 'selected' : '';
                                return `<option value="${unit.id}" ${selected}>${unit.name}</option>`;
                            }).join('')}
                        </select>
                    </td>
                    <td style="padding: 12px;">
                        <input type="number" class="form-input multiplier-input" data-index="${index}"
                               value="${multiplier}"
                               placeholder="Phải > 1" step="1" min="2" required
                               onchange="validateMultiplierOrder(this)" style="width: 100%;">
                    </td>
                    <td style="padding: 12px; text-align: center;">
                        <input type="checkbox" class="show-in-pos-checkbox" data-index="${index}" ${isSale ? 'checked' : ''}
                               style="width: 18px; height: 18px; cursor: pointer;">
                    </td>
                    <td style="padding: 12px; text-align: center;">
                        <button type="button" onclick="removeUnitConversionRow(this)" class="btn-link delete">Xóa</button>
                    </td>
                `;

                // Add event listener for unit select to validate duplication
                const unitSelect = row.querySelector('.unit-select');
                const multiplierInput = row.querySelector('.multiplier-input');

                if (unitSelect) {
                    unitSelect.addEventListener('change', function() {
                        validateUnitDuplication(this);
                        updateTotalUnits();
                        autoUpdatePackaging();
                    });
                }

                if (multiplierInput) {
                    multiplierInput.addEventListener('input', function() {
                        updateTotalUnits();
                        autoUpdatePackaging();
                    });
                }
            }

            tbody.appendChild(row);
        });
    }

    updateTotalUnits();
    autoUpdatePackaging();
}

function clearUnitConversions() {
    const tbody = document.getElementById('unitConversionTableBody');
    tbody.innerHTML = '';
    unitConversions = [];
    updateTotalUnits();

    // Clear packaging field when resetting unit conversions
    const packagingField = document.getElementById('packaging');
    if (packagingField) {
        packagingField.value = '';
    }
}

// Function to generate packaging specification from unit conversions
function generatePackagingFromUnits() {
    const tbody = document.getElementById('unitConversionTableBody');
    const rows = tbody.querySelectorAll('tr');
    const packagingField = document.getElementById('packaging');

    if (!rows.length) {
        showToast('Vui lòng thêm các đơn vị quy đổi trước', 'error');
        return;
    }

    // Collect all units and their multipliers
    const unitData = [];
    let hasError = false;

    rows.forEach(row => {
        const isBaseUnit = row.getAttribute('data-is-base-unit') === 'true';

        if (isBaseUnit) {
            // Base unit
            const baseUnitInput = row.querySelector('input[type="text"]');
            if (baseUnitInput && baseUnitInput.value) {
                unitData.push({
                    name: baseUnitInput.value,
                    multiplier: 1,
                    isBase: true
                });
            }
        } else {
            // Conversion units
            const unitSelect = row.querySelector('.unit-select');
            const multiplierInput = row.querySelector('.multiplier-input');

            if (unitSelect && multiplierInput) {
                const unitId = parseInt(unitSelect.value);
                const multiplier = parseFloat(multiplierInput.value);

                if (unitId && !isNaN(multiplier) && multiplier > 0) {
                    const unitName = unitSelect.options[unitSelect.selectedIndex].text;
                    unitData.push({
                        name: unitName,
                        multiplier: multiplier,
                        isBase: false
                    });
                } else if (unitSelect.value || multiplierInput.value) {
                    hasError = true;
                }
            }
        }
    });

    if (hasError) {
        showToast('Vui lòng hoàn thành thông tin các đơn vị quy đổi', 'error');
        return;
    }

    if (unitData.length < 2) {
        showToast('Cần ít nhất 2 đơn vị để tạo quy cách đóng gói', 'error');
        return;
    }

    // Sort by multiplier (descending order for packaging display)
    unitData.sort((a, b) => b.multiplier - a.multiplier);

    // Generate packaging specification
    // Example: If we have Thùng(100), Hộp(10), Gói(1)
    // Result: "Thùng x 10 Hộp x 10 Gói"
    let packagingSpec = '';

    for (let i = 0; i < unitData.length - 1; i++) {
        const currentUnit = unitData[i];
        const nextUnit = unitData[i + 1];

        // Calculate quantity: how many of next unit fit in current unit
        const quantity = currentUnit.multiplier / nextUnit.multiplier;

        // Add "x" before all units including the first one
        if (packagingSpec === '') {
            packagingSpec = `${currentUnit.name} x ${quantity} ${nextUnit.name}`;
        } else {
            packagingSpec += ` x ${quantity} ${nextUnit.name}`;
        }
    }

    // Set the generated packaging specification
    packagingField.value = packagingSpec;
    showToast('Đã tạo quy cách đóng gói tự động', 'success');
}

