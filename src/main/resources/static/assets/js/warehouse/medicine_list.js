// Medicine Management JavaScript for Warehouse
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

    // Clear unit conversions
    clearUnitConversions();

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
            }

            // Load unit conversions for this variant
            loadUnitConversions(variantId);

            // Ensure add button and controls are enabled
            setTimeout(() => {
                // Re-enable all unit conversion controls
                document.querySelectorAll('#unitConversionTableBody .unit-select').forEach(el => el.disabled = false);
                document.querySelectorAll('#unitConversionTableBody .multiplier-input').forEach(el => el.disabled = false);
                document.querySelectorAll('#unitConversionTableBody .note-input').forEach(el => el.disabled = false);
                document.querySelectorAll('#unitConversionTableBody .btn-link.delete').forEach(el => el.style.display = '');

                // Show add button
                const addButtons = document.querySelectorAll('button[onclick="addUnitConversionRow()"]');
                addButtons.forEach(btn => btn.style.display = '');
            }, 500);

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
                    <div class="detail-label">Công dụng</div>
                    <div class="detail-value">${data.uses || '-'}</div>
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
            const dosageForm = document.getElementById('dosageForm').value.trim();
            const dosage = document.getElementById('dosage').value.trim();
            const strength = document.getElementById('strength').value.trim();
            const packaging = document.getElementById('packaging').value.trim();
            const barcode = document.getElementById('barcode').value.trim();
            const registrationNumber = document.getElementById('registrationNumber').value.trim();
            const storageConditions = document.getElementById('storageConditions').value.trim();
            const instructions = document.getElementById('instructions').value.trim();
            const prescriptionRequired = document.getElementById('prescriptionRequired').value;

            // Validation checks
            if (!dosageForm) {
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

            // Check if there are duplicate units
            if (unitConversionsData === null) {
                return; // Có đơn vị trùng lặp, dừng lại
            }

            // Validate must have at least 1 unit conversion
            if (unitConversionsData.length === 0) {
                showToast('Bắt buộc phải thêm ít nhất 1 đơn vị quy đổi', 'error');
                return;
            }

            // Validate unit conversion order
            if (!validateUnitConversionsOrder(unitConversionsData)) {
                return;
            }

            const data = {
                medicineId: medicineId,
                dosageForm: dosageForm,
                dosage: dosage,
                strength: strength,
                packaging: packaging || null,
                barcode: barcode,
                registrationNumber: registrationNumber,
                storageConditions: storageConditions,
                instructions: instructions,
                prescription_require: prescriptionRequired === 'true',
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
});

// ========== Unit Conversion Management ==========
let unitConversions = []; // Store unit conversions for the current variant

// Hàm kiểm tra đơn vị trùng lặp
function validateUniqueUnit(select) {
    const tbody = document.getElementById('unitConversionTableBody');
    const rows = Array.from(tbody.querySelectorAll('tr'));
    const currentRow = select.closest('tr');
    const currentValue = select.value;

    if (!currentValue) {
        return true; // Bỏ qua nếu chưa chọn
    }

    // Kiểm tra xem đơn vị này đã được chọn ở row khác chưa
    let isDuplicate = false;
    rows.forEach(row => {
        if (row !== currentRow) {
            const otherSelect = row.querySelector('.unit-select');
            if (otherSelect && otherSelect.value === currentValue) {
                isDuplicate = true;
            }
        }
    });

    if (isDuplicate) {
        select.style.borderColor = '#DC2626';
        const selectedUnit = units.find(u => u.id == currentValue);
        showToast(`Đơn vị "${selectedUnit ? selectedUnit.name : 'này'}" đã được chọn. Vui lòng chọn đơn vị khác!`, 'error');
        select.value = ''; // Reset lại giá trị
        return false;
    }

    // Nếu hợp lệ, reset border color
    select.style.borderColor = '';
    return true;
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

// Hàm validate thứ tự các giá trị quy đổi
function validateMultiplierOrder(input) {
    const tbody = document.getElementById('unitConversionTableBody');
    const rows = Array.from(tbody.querySelectorAll('tr'));
    const currentRow = input.closest('tr');
    const currentIndex = rows.indexOf(currentRow);
    const currentValue = parseFloat(input.value);

    if (isNaN(currentValue) || currentValue <= 0) {
        input.style.borderColor = '#DC2626';
        showToast('Giá trị quy đổi phải lớn hơn 0', 'error');
        return false;
    }

    // Kiểm tra với các giá trị trước đó
    for (let i = 0; i < currentIndex; i++) {
        const prevInput = rows[i].querySelector('.multiplier-input');
        if (prevInput) {
            const prevValue = parseFloat(prevInput.value);
            if (!isNaN(prevValue) && currentValue <= prevValue) {
                input.style.borderColor = '#DC2626';
                showToast(`Giá trị quy đổi phải lớn hơn ${prevValue} (đơn vị trước đó)`, 'error');
                input.value = '';
                return false;
            }
        }
    }

    // Kiểm tra với các giá trị sau đó
    for (let i = currentIndex + 1; i < rows.length; i++) {
        const nextInput = rows[i].querySelector('.multiplier-input');
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
    const tbody = document.getElementById('unitConversionTableBody');
    const rowIndex = tbody.children.length;

    // Nếu là đơn vị đầu tiên, giá trị mặc định là 1 và không cho sửa
    const defaultValue = rowIndex === 0 ? '1' : '';
    const isDisabled = rowIndex === 0 ? 'disabled' : '';
    const bgColor = rowIndex === 0 ? 'background-color: #F3F4F6;' : '';

    // Tính giá trị min cho đơn vị mới (phải lớn hơn giá trị lớn nhất hiện tại)
    const minValue = getMinValueForNewUnit();

    const row = document.createElement('tr');
    row.style.borderBottom = '1px solid #E5E7EB';
    row.innerHTML = `
        <td style="padding: 12px;">
            <select class="form-select unit-select" data-index="${rowIndex}" onchange="validateUniqueUnit(this); updateTotalUnits();">
                <option value="">Chọn đơn vị</option>
                ${units.map(unit => `<option value="${unit.id}">${unit.name}</option>`).join('')}
            </select>
        </td>
        <td style="padding: 12px;">
            <input type="number" class="form-input multiplier-input" data-index="${rowIndex}" 
                   value="${defaultValue}"
                   placeholder="Phải > ${minValue - 1}" step="1" min="${minValue}" required
                   ${isDisabled}
                   onchange="validateMultiplierOrder(this)" style="width: 100%; ${bgColor}">
        </td>
        <td style="padding: 12px;">
            <input type="text" class="form-input note-input" data-index="${rowIndex}"
                   placeholder="Ghi chú (tùy chọn)" style="width: 100%;">
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

    // Re-index remaining rows and update min values
    const tbody = document.getElementById('unitConversionTableBody');
    Array.from(tbody.children).forEach((row, index) => {
        const select = row.querySelector('.unit-select');
        const input = row.querySelector('.multiplier-input');
        if (select) select.setAttribute('data-index', index);
        if (input) {
            input.setAttribute('data-index', index);

            // Đơn vị đầu tiên phải có giá trị = 1 và disabled
            if (index === 0) {
                input.value = '1';
                input.disabled = true;
                input.style.backgroundColor = '#F3F4F6';
                input.setAttribute('min', '1');
                input.setAttribute('placeholder', 'VD: 1');
            } else {
                input.disabled = false;
                input.style.backgroundColor = '';

                // Cập nhật min value dựa trên giá trị của row trước đó
                const prevRow = tbody.children[index - 1];
                if (prevRow) {
                    const prevInput = prevRow.querySelector('.multiplier-input');
                    if (prevInput && prevInput.value) {
                        const prevValue = parseFloat(prevInput.value);
                        if (!isNaN(prevValue)) {
                            const newMin = prevValue + 1;
                            input.setAttribute('min', newMin);
                            input.setAttribute('placeholder', `Phải > ${prevValue}`);
                        }
                    }
                }
            }
        }
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
    const usedUnits = new Set(); // Để kiểm tra đơn vị trùng lặp

    for (let row of rows) {
        const unitSelect = row.querySelector('.unit-select');
        const multiplierInput = row.querySelector('.multiplier-input');
        const noteInput = row.querySelector('.note-input');

        if (unitSelect && multiplierInput) {
            const unitId = parseInt(unitSelect.value);
            const multiplier = parseFloat(multiplierInput.value);
            const note = noteInput ? noteInput.value.trim() : '';

            if (unitId && !isNaN(multiplier) && multiplier > 0) {
                // Kiểm tra đơn vị trùng lặp
                if (usedUnits.has(unitId)) {
                    const unit = units.find(u => u.id === unitId);
                    showToast(`Đơn vị "${unit ? unit.name : 'này'}" đã được chọn. Vui lòng kiểm tra lại!`, 'error');
                    unitSelect.style.borderColor = '#DC2626';
                    return null; // Trả về null để báo lỗi
                }
                usedUnits.add(unitId);

                conversions.push({
                    unitId: unitId,
                    multiplier: multiplier,
                    note: note || null
                });
            }
        }
    }

    return conversions;
}

function validateUnitConversionsOrder(conversions) {
    if (!conversions || conversions.length === 0) {
        return true; // Cho phép không có đơn vị quy đổi
    }

    // Kiểm tra thứ tự tăng dần
    for (let i = 1; i < conversions.length; i++) {
        if (conversions[i].multiplier <= conversions[i - 1].multiplier) {
            showToast(`Giá trị quy đổi phải tăng dần! Đơn vị thứ ${i + 1} (${conversions[i].multiplier}) phải lớn hơn đơn vị thứ ${i} (${conversions[i - 1].multiplier})`, 'error');
            return false;
        }
    }

    // Kiểm tra đơn vị đầu tiên phải bằng 1
    if (conversions[0].multiplier !== 1) {
        showToast('Đơn vị đầu tiên phải có giá trị quy đổi bằng 1', 'error');
        return false;
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

            // Đơn vị đầu tiên không cho sửa giá trị quy đổi
            const isDisabled = index === 0 ? 'disabled' : '';
            const bgColor = index === 0 ? 'background-color: #F3F4F6;' : '';

            // Tính giá trị min (phải lớn hơn giá trị trước đó)
            let minValue = 1;
            if (index > 0 && conversions[index - 1].multiplier) {
                minValue = conversions[index - 1].multiplier + 1;
            }

            row.innerHTML = `
                <td style="padding: 12px;">
                    <select class="form-select unit-select" data-index="${index}" onchange="validateUniqueUnit(this); updateTotalUnits();">
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
                           placeholder="${index === 0 ? 'VD: 1' : 'Phải > ' + (minValue - 1)}" step="1" min="${minValue}" required
                           ${isDisabled}
                           onchange="validateMultiplierOrder(this)" style="width: 100%; ${bgColor}">
                </td>
                <td style="padding: 12px;">
                    <input type="text" class="form-input note-input" data-index="${index}" 
                           value="${conversion.note || ''}"
                           placeholder="Ghi chú (tùy chọn)" style="width: 100%;">
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

