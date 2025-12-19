// Danh sách thuốc đã chọn
let selectedMedicines = [];

// Toast notification helper
function showToast(message, type = 'info') {
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = message;
    toast.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 20px;
        background: ${type === 'error' ? '#dc3545' : type === 'success' ? '#28a745' : type === 'warning' ? '#ffc107' : '#17a2b8'};
        color: ${type === 'warning' ? '#000' : 'white'};
        border-radius: 8px;
        z-index: 10000;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    `;
    document.body.appendChild(toast);
    setTimeout(() => toast.remove(), 3000);
}

// Search function for warehouse medicines
function searchWarehouseMedicines(query) {
    const $rows = $('#warehouseMedicineSource tr');
    const searchLower = query.toLowerCase();

    $rows.each(function() {
        const $row = $(this);
        const name = $row.data('medicine-name') || '';
        const ingredient = $row.data('active-ingredient') || '';
        const batchCode = $row.data('batch-code') || '';

        const text = (name + ' ' + ingredient + ' ' + batchCode).toLowerCase();

        if (text.includes(searchLower)) {
            $row.show();
        } else {
            $row.hide();
        }
    });
}

// Add medicine from warehouse to selected list
function addMedicineFromRow($row) {
    const medicine = {
        variantId: $row.data('variant-id'),
        batchId: $row.data('batch-id'),
        medicineName: $row.data('medicine-name'),
        activeIngredient: $row.data('active-ingredient'),
        strength: $row.data('strength'),
        dosageForm: $row.data('dosage-form'),
        manufacturer: $row.data('manufacturer'),
        branchStock: $row.data('branch-stock'),
        // read minStock from data-min-stock (jQuery maps to minStock)
        minStock: (function(){ const v = $row.data('minStock'); return (typeof v === 'undefined' || v === null || v === '') ? null : (isNaN(v) ? v : Number(v)); })(),
        unit: $row.data('unit'),
        requestQuantity: 1
    };

    // Check if already added
    const exists = selectedMedicines.find(m =>
        m.variantId === medicine.variantId && m.batchId === medicine.batchId
    );

    if (exists) {
        showToast('Thuốc này đã được thêm vào danh sách!', 'warning');
        return;
    }

    selectedMedicines.push(medicine);
    renderSelectedMedicines();
}

// Render danh sách thuốc đã chọn
function renderSelectedMedicines() {
    const $tbody = $('#selectedMedicinesList');
    $tbody.empty();

    if (selectedMedicines.length === 0) {
        $tbody.append(`
            <tr id="emptyRow">
                <td colspan="8" class="px-4 py-6 text-center text-gray-500">
                    Chưa có thuốc nào được chọn. Vui lòng chọn thuốc từ danh sách bên trên.
                </td>
            </tr>
        `);
        return;
    }

    selectedMedicines.forEach((medicine, index) => {
        const branchStock = (typeof medicine.branchStock === 'number') ? medicine.branchStock : (medicine.branchStock ? Number(medicine.branchStock) : 0);
        const unit = medicine.unit || '';
        const minStock = (typeof medicine.minStock === 'number') ? medicine.minStock : (medicine.minStock ? Number(medicine.minStock) : null);
        
        const $row = $(`
            <tr class="hover:bg-gray-50">
                <td class="px-4 py-3 text-center">${index + 1}</td>
                <td class="px-4 py-3 font-medium">${medicine.medicineName}</td>
                <td class="px-4 py-3 text-sm">${medicine.activeIngredient || '-'}</td>
                <td class="px-4 py-3 text-sm">${medicine.strength || '-'}</td>
                <td class="px-4 py-3 text-center text-sm text-gray-700">${minStock !== null ? minStock : '-'}</td>
                <td class="px-4 py-3 text-center font-semibold text-blue-600">${branchStock} ${unit}</td>
                <td class="px-4 py-3 text-center">
                    <input 
                        type="number" 
                        class="qty-input border border-gray-300 rounded px-2 py-1 text-center w-24"
                        min="1"
                        value="${medicine.requestQuantity}"
                        data-index="${index}">
                </td>
                <td class="px-4 py-3 text-center">
                    <button 
                        class="text-red-600 hover:text-red-800 remove-btn"
                        data-index="${index}"
                        title="Xóa">
                        <span class="material-icons-outlined text-sm">delete</span>
                    </button>
                </td>
            </tr>
        `);

        // Handle quantity change
        $row.find('input[type="number"]').on('change', function() {
            const newQuantity = parseInt($(this).val());

            if (isNaN(newQuantity) || newQuantity < 1) {
                $(this).val(1);
                selectedMedicines[index].requestQuantity = 1;
            } else {
                selectedMedicines[index].requestQuantity = newQuantity;
            }
        });

        // Handle delete
        $row.find('.remove-btn').on('click', function() {
            const idx = parseInt($(this).attr('data-index'));
            if (confirm('Bạn có chắc muốn xóa thuốc này khỏi danh sách?')) {
                selectedMedicines.splice(idx, 1);
                renderSelectedMedicines();
            }
        });

        $tbody.append($row);
    });
}

// Check if expiry date is within 6 months
function isExpiringWarning(expiryDateStr) {
    if (!expiryDateStr) return false;

    const parts = expiryDateStr.split('/');
    if (parts.length !== 3) return false;

    const expiryDate = new Date(parts[2], parts[1] - 1, parts[0]);
    const today = new Date();
    const sixMonthsLater = new Date();
    sixMonthsLater.setMonth(sixMonthsLater.getMonth() + 6);

    return expiryDate < sixMonthsLater;
}

// Submit phiếu nhập
function submitImportRequest() {
    if (selectedMedicines.length === 0) {
        showToast('Vui lòng chọn ít nhất một thuốc!', 'warning');
        return;
    }

    // Validate all quantities
    const hasInvalidQuantity = selectedMedicines.some(m => !m.requestQuantity || m.requestQuantity < 1);
    if (hasInvalidQuantity) {
        showToast('Vui lòng nhập số lượng hợp lệ cho tất cả thuốc!', 'error');
        return;
    }

    const note = $('#importNote').val().trim();

    const requestData = {
        note: note,
        items: selectedMedicines.map(m => ({
            variantId: m.variantId,
            batchId: m.batchId,
            quantity: m.requestQuantity
        }))
    };

    // Disable submit button
    const $submitBtn = $('#submitImport');
    $submitBtn.prop('disabled', true).html('<span class="material-icons-outlined animate-spin">refresh</span> Đang xử lý...');

    $.ajax({
        url: '/inventory/import/submit',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(requestData),
        success: function(response) {
            if (response.success) {
                // Redirect to success page with code (URL encode to handle # character)
                showToast('Tạo phiếu nhập thành công!', 'success');
                setTimeout(() => {
                    window.location.href = '/inventory/import/success/' + encodeURIComponent(response.code);
                }, 1500);
            } else {
                showToast('Có lỗi: ' + (response.message || 'Unknown error'), 'error');
                $submitBtn.prop('disabled', false).html('<span class="material-icons-outlined">check_circle</span> Hoàn tất và gửi yêu cầu');
            }
        },
        error: function(xhr) {
            console.error('Error submitting import:', xhr);
            const errorMsg = xhr.responseJSON?.message || 'Có lỗi xảy ra khi tạo phiếu nhập. Vui lòng thử lại!';
            showToast(errorMsg, 'error');
            $submitBtn.prop('disabled', false).html('<span class="material-icons-outlined">check_circle</span> Hoàn tất và gửi yêu cầu');
        }
    });
}

// Hide expired rows in warehouse medicines
function hideExpiredRows() {
    const today = new Date();
    $('#warehouseMedicineSource tr').each(function() {
        const expiryStr = $(this).data('expiry-date');
        if (!expiryStr) return; // Không có hạn dùng thì không ẩn
        // Hỗ trợ cả định dạng yyyy-MM-dd và yyyy/MM/dd
        let expiry = null;
        if (/\d{4}-\d{2}-\d{2}/.test(expiryStr)) {
            expiry = new Date(expiryStr);
        } else if (/\d{4}\/\d{2}\/\d{2}/.test(expiryStr)) {
            expiry = new Date(expiryStr.replace(/\//g, '-'));
        } else {
            expiry = new Date(expiryStr);
        }
        if (expiry < today) {
            $(this).hide();
        }
    });
}

// Event Listeners
$(document).ready(function() {
    hideExpiredRows();

    // Search warehouse medicines
    $('#warehouseMedicineSearch').on('input', function() {
        const query = $(this).val().trim();
        searchWarehouseMedicines(query);
        hideExpiredRows();
    });

    // Add medicine button click
    $(document).on('click', '.add-medicine-btn', function() {
        const $row = $(this).closest('tr');
        addMedicineFromRow($row);
    });

    // Submit button
    $('#submitImport').on('click', submitImportRequest);

    // Initial render
    renderSelectedMedicines();
});