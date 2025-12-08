// Danh sách thuốc đã chọn
let selectedItems = [];

function renderSelected() {
    const tbody = $('#inventoryCheckSelected');
    tbody.empty();
    if (selectedItems.length === 0) {
        tbody.append('<tr id="emptySelectedRow"><td colspan="5" class="px-3 py-6 text-center text-gray-500">Chưa chọn thuốc nào</td></tr>');
        return;
    }
    selectedItems.forEach((it, idx) => {
        const diff = it.counted - it.system;
        const diffClass = diff === 0 ? 'text-gray-600' : (diff > 0 ? 'text-green-600 font-semibold' : 'text-red-600 font-semibold');
        const row = $(`
            <tr data-index="${idx}">
                <td class="px-3 py-2">
                    <div class="font-medium text-gray-900">${it.name}</div>
                    <div class="text-xs text-gray-500">${it.dosageForm || ''}</div>
                    <div class="text-[10px] text-gray-400">Lô: ${it.batchCode || '-'} | HSD: ${it.expiry || '-'}</div>
                </td>
                <td class="px-3 py-2 text-center font-semibold text-blue-600">${it.system}</td>
                <td class="px-3 py-2 text-center">
                    <input type="number" min="0" value="${it.counted}" class="counted-input w-24 px-2 py-1 border border-gray-300 rounded focus:ring-blue-500 focus:border-blue-500 text-center" title="Nhập số lượng thực tế" />
                </td>
                <td class="px-3 py-2 text-center ${diffClass}">${diff}</td>
                <td class="px-3 py-2 text-center">
                    <button type="button" class="remove-btn text-red-600 hover:text-red-800" title="Xóa">
                        <span class="material-icons-outlined text-sm">delete</span>
                    </button>
                </td>
            </tr>
        `);

        row.find('.counted-input').on('input', function() {
            let val = $(this).val();

            // Chỉ cho phép số dương
            if (val === '' || val === null) {
                val = 0;
            } else {
                val = parseInt(val);
                if (isNaN(val) || val < 0) {
                    val = 0;
                }
            }

            $(this).val(val);
            it.counted = val;

            // Cập nhật số chênh lệch ngay lập tức
            const diff = val - it.system;
            const diffTd = row.find('td').eq(3);

            // Xóa class cũ
            diffTd.removeClass('text-gray-600 text-green-600 text-red-600 font-semibold');

            // Thêm class mới
            if (diff === 0) {
                diffTd.addClass('text-gray-600');
            } else if (diff > 0) {
                diffTd.addClass('text-green-600 font-semibold');
            } else {
                diffTd.addClass('text-red-600 font-semibold');
            }

            diffTd.text(diff);
        });

        row.find('.remove-btn').on('click', function() {
            selectedItems.splice(idx, 1);
            renderSelected();
        });

        tbody.append(row);
    });
}

function addItemFromRow($tr) {
    const inventoryId = parseInt($tr.data('inventory-id'));
    const variantId = parseInt($tr.data('variant-id'));
    const batchId = $tr.data('batch-id') != null && $tr.data('batch-id') !== '' ? parseInt($tr.data('batch-id')) : null;
    const systemQty = parseInt($tr.data('system-qty'));

    // Check if already added
    if (selectedItems.find(i => i.inventoryId === inventoryId)) {
        alert('Thuốc này đã được thêm vào danh sách!');
        return;
    }

    const name = $tr.find('td').eq(0).find('div.font-medium').text().trim();
    const dosageForm = $tr.find('td').eq(0).find('div.text-xs').text().trim();
    const activeIngredient = $tr.find('td').eq(1).text().trim();

    // Get batch code and expiry
    const batchCodeSpan = $tr.find('td').eq(2).find('span.font-mono');
    const expirySpan = $tr.find('td').eq(2).find('span.ml-1');
    const batchCode = batchCodeSpan.length > 0 ? batchCodeSpan.text().trim() : '-';
    const expiry = expirySpan.length > 0 ? expirySpan.text().trim() : '-';

    selectedItems.push({
        inventoryId: inventoryId,
        variantId: variantId,
        batchId: batchId,
        system: systemQty,
        counted: systemQty,
        name: name,
        dosageForm: dosageForm,
        activeIngredient: activeIngredient,
        batchCode: batchCode,
        expiry: expiry
    });

    renderSelected();
}

$(document).ready(function() {
    console.log('Warehouse check create JS loaded');

    // Add single item
    $('#inventoryMedicineSource').on('click', '.add-one-btn', function(e) {
        e.preventDefault();
        console.log('Add button clicked');
        const $tr = $(this).closest('tr');
        addItemFromRow($tr);
    });

    // Add all
    $('#addAllButton').on('click', function(e) {
        e.preventDefault();
        console.log('Add all clicked');
        $('#inventoryMedicineSource tr[data-inventory-id]').each(function() {
            const inventoryId = parseInt($(this).data('inventory-id'));
            // Only add if not already in list
            if (!selectedItems.find(i => i.inventoryId === inventoryId)) {
                addItemFromRow($(this));
            }
        });
    });

    // Search filter
    $('#inventoryCheckSearch').on('input', function() {
        const q = $(this).val().toLowerCase();
        $('#inventoryMedicineSource tr[data-inventory-id]').each(function() {
            const text = $(this).text().toLowerCase();
            $(this).toggle(text.indexOf(q) !== -1);
        });
    });

    // Submit
    $('#submitInventoryCheck').on('click', function() {
        if (selectedItems.length === 0) {
            alert('Chưa chọn thuốc nào để kiểm kho');
            return;
        }

        const note = $('#inventoryCheckNote').val().trim();
        const payload = {
            note: note,
            items: selectedItems.map(it => ({
                inventoryId: it.inventoryId,
                variantId: it.variantId,
                batchId: it.batchId,
                countedQuantity: it.counted
            }))
        };

        const $btn = $(this);
        $btn.prop('disabled', true).addClass('opacity-50');

        $.ajax({
            url: '/warehouse/check/submit',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(payload),
            success: function(response) {
                if (response.success) {
                    // Kiểm kho thành công
                    alert('Kiểm kho thành công!');
                    window.location.href = '/warehouse/check';
                }
            },
            error: function(xhr) {
                console.error('Error:', xhr);
                let errorMsg = 'Vui lòng thử lại';
                try {
                    const errorResponse = JSON.parse(xhr.responseText);
                    errorMsg = errorResponse.message || errorMsg;
                } catch(e) {
                    errorMsg = xhr.responseText || errorMsg;
                }
                alert('Lỗi kiểm kho: ' + errorMsg);
                $btn.prop('disabled', false).removeClass('opacity-50');
            }
        });
    });

    renderSelected();
});

