@param java.util.List<vn.edu.fpt.pharma.dto.inventory.InventoryMedicineVM> medicines

@template.layouts.warehouse(
headContent = @`
    <title>Tạo phiếu xuất hủy</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,typography"></script>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Outlined" rel="stylesheet">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
`,
content = @`
    <div class="mb-6">
        <div class="flex justify-between items-center">
            <h2 class="text-2xl font-bold">TẠO PHIẾU XUẤT HỦY</h2>
            <a href="/warehouse/receipt-list" class="text-blue-600 hover:text-blue-800 flex items-center gap-1">
                <span class="material-icons-outlined">arrow_back</span>
                Quay lại
            </a>
        </div>
        <p class="text-gray-600 mt-2">Chọn thuốc từ kho để xuất hủy (đặc biệt là thuốc hết hạn)</p>
    </div>

    <!-- Medicines from Warehouse -->
    <div class="bg-white p-6 rounded-lg shadow-md mb-6">
        <h3 class="text-lg font-semibold mb-4">Danh sách thuốc trong kho</h3>
        <div class="mb-4 flex gap-4">
            <div class="relative w-80">
                <span class="material-icons-outlined absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">search</span>
                <input id="branchMedicineSearch" type="text" placeholder="Tìm theo tên / hoạt chất / mã lô" class="pl-10 pr-4 py-2 w-full border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500" />
            </div>
            <div class="flex items-center gap-2">
                <input type="checkbox" id="showExpiredOnly" class="w-4 h-4 text-red-600 border-gray-300 rounded focus:ring-red-500">
                <label for="showExpiredOnly" class="text-sm font-medium text-gray-700 cursor-pointer">Chỉ hiển thị thuốc đã hết hạn</label>
            </div>
        </div>
        <div class="overflow-x-auto max-h-96 border rounded">
            <table class="w-full text-sm">
                <thead class="bg-gray-50 text-gray-600 uppercase text-xs sticky top-0">
                        <tr>
                            <th class="px-3 py-2 text-left">Tên thuốc</th>
                            <th class="px-3 py-2 text-left">Hoạt chất</th>
                            <th class="px-3 py-2 text-left">Hàm lượng</th>
                            <th class="px-3 py-2">Lô / HSD</th>
                            <th class="px-3 py-2 text-center">Tồn kho</th>
                            <th class="px-3 py-2 text-center">Thao tác</th>
                        </tr>
                    </thead>
                <tbody id="branchMedicineSource" class="divide-y">
                        @if(medicines != null && !medicines.isEmpty())
                            @for(vn.edu.fpt.pharma.dto.inventory.InventoryMedicineVM med : medicines)
                                !{var isExpired = med.getExpiryDate() != null && med.getExpiryDate().isBefore(java.time.LocalDate.now());}
                                <tr class="hover:bg-gray-50 medicine-row @if(isExpired)expired-medicine@endif"
                                    data-variant-id="${med.getVariantId()}"
                                    data-batch-id="${med.getBatchId()}"
                                    data-inventory-id="@if(med.getInventoryId() != null)${med.getInventoryId()}@endif"
                                    data-medicine-name="${med.getMedicineName()}"
                                    data-active-ingredient="@if(med.getActiveIngredient() != null)${med.getActiveIngredient()}@endif"
                                    data-strength="@if(med.getStrength() != null)${med.getStrength()}@else@endif"
                                    data-batch-code="@if(med.getBatchCode() != null)${med.getBatchCode()}@else@endif"
                                    data-expiry-date="@if(med.getExpiryDate() != null)${med.getExpiryDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))}@endif"
                                    data-expiry-date-raw="@if(med.getExpiryDate() != null)${med.getExpiryDate().toString()}@endif"
                                    data-is-expired="@if(isExpired)true@else false@endif"
                                    data-available-qty="${med.getQuantity()}"
                                    data-unit="@if(med.getUnit() != null)${med.getUnit()}@else@endif">
                                    <td class="px-3 py-2 font-medium">
                                        @if(isExpired)
                                            <span class="text-red-600" title="Thuốc đã hết hạn">⚠️</span>
                                        @endif
                                        ${med.getMedicineName()}
                                    </td>
                                    <td class="px-3 py-2">@if(med.getActiveIngredient() != null)${med.getActiveIngredient()}@else-@endif</td>
                                    <td class="px-3 py-2">@if(med.getStrength() != null)${med.getStrength()}@else-@endif</td>
                                    <td class="px-3 py-2 text-center">
                                        <span class="font-mono text-xs bg-gray-100 px-2 py-1 rounded">@if(med.getBatchCode() != null)${med.getBatchCode()}@else-@endif</span><br/>
                                        <span class="text-xs @if(isExpired)text-red-600 font-semibold@else text-gray-500@endif">
                                            @if(med.getExpiryDate() != null)HSD: ${med.getExpiryDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))}@endif
                                        </span>
                                    </td>
                                    <td class="px-3 py-2 text-center font-semibold">${med.getQuantity()}</td>
                                    <td class="px-3 py-2 text-center">
                                        <button type="button" class="add-medicine-btn bg-blue-600 hover:bg-blue-700 text-white px-3 py-1 rounded text-xs">
                                            <span class="material-icons-outlined text-sm">add</span> Thêm
                                        </button>
                                    </td>
                                </tr>
                            @endfor
                        @else
                            <tr>
                                <td colspan="6" class="px-3 py-8 text-center text-gray-500">Không có thuốc trong kho</td>
                            </tr>
                        @endif
                </tbody>
            </table>
        </div>
        <div class="mt-4 flex justify-end">
            <button type="button" id="addAllVisibleBtn" class="px-4 py-2 bg-green-600 hover:bg-green-700 text-white rounded-md flex items-center gap-2">
                <span class="material-icons-outlined">playlist_add</span>
                <span>Thêm tất cả thuốc đang hiển thị</span>
            </button>
        </div>
    </div>

    <!-- Selected Medicines -->
    <div class="bg-white p-6 rounded-lg shadow-md mb-6">
        <h3 class="text-lg font-semibold mb-4">Danh sách thuốc đã chọn</h3>
        <div class="overflow-x-auto">
            <table class="w-full text-sm">
                <thead class="bg-gray-50 text-gray-600 uppercase text-xs">
                    <tr>
                        <th class="px-3 py-2 text-center" style="width: 50px;">STT</th>
                        <th class="px-3 py-2 text-left">Tên thuốc</th>
                        <th class="px-3 py-2 text-left">Mã lô</th>
                        <th class="px-3 py-2 text-left">Hàm lượng</th>
                        <th class="px-3 py-2 text-left">Đóng gói</th>
                        <th class="px-3 py-2 text-center">Tồn kho</th>
                        <th class="px-3 py-2 text-center">Số lượng hủy</th>
                        <th class="px-3 py-2 text-center">Xóa</th>
                    </tr>
                </thead>
                <tbody id="selectedMedicines" class="divide-y">
                    <tr class="empty-state">
                        <td colspan="8" class="px-3 py-8 text-center text-gray-500">Chưa chọn thuốc nào</td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>

    <!-- Note and Submit -->
    <div class="bg-white p-6 rounded-lg shadow-md">
        <div class="mb-4">
            <label class="block text-sm font-medium mb-2">Ghi chú (tối đa 300 ký tự)</label>
            <textarea id="note" rows="3" maxlength="300" class="w-full border border-gray-300 rounded-md px-3 py-2" placeholder="Nhập ghi chú về lý do xuất hủy (tối đa 300 ký tự)"></textarea>
        </div>
        <div class="flex justify-end gap-3">
            <a href="/warehouse/receipt-list" class="px-6 py-2 border border-gray-300 rounded-md hover:bg-gray-50">Hủy</a>
            <button id="submitBtn" type="button" class="px-6 py-2 bg-red-600 hover:bg-red-700 text-white rounded-md flex items-center gap-2">
                <span class="material-icons-outlined">delete_forever</span>
                Tạo phiếu xuất hủy
            </button>
        </div>
    </div>
`,
pageScript = @`
<script>
const selectedItems = new Map();

// Search functionality
$('#branchMedicineSearch').on('input', function() {
    filterMedicines();
});

// Expired filter toggle
$('#showExpiredOnly').on('change', function() {
    filterMedicines();
});

function filterMedicines() {
    const searchTerm = $('#branchMedicineSearch').val().toLowerCase();
    const showExpiredOnly = $('#showExpiredOnly').is(':checked');

    $('.medicine-row').each(function() {
        const name = $(this).data('medicine-name').toLowerCase();
        const ingredient = $(this).data('active-ingredient') ? $(this).data('active-ingredient').toLowerCase() : '';
        const batchCode = $(this).data('batch-code') ? $(this).data('batch-code').toLowerCase() : '';
        const isExpired = $(this).data('is-expired') === true;

        const matchesSearch = name.includes(searchTerm) || ingredient.includes(searchTerm) || batchCode.includes(searchTerm);
        const matchesExpiredFilter = !showExpiredOnly || isExpired;

        $(this).toggle(matchesSearch && matchesExpiredFilter);
    });
}

// Add medicine (single)
$(document).on('click', '.add-medicine-btn', function() {
    const row = $(this).closest('tr');
    addMedicineFromRow(row);
});

// Add all visible medicines
$('#addAllVisibleBtn').on('click', function() {
    let addedCount = 0;
    let skippedCount = 0;

    // Find all visible medicine rows
    $('.medicine-row:visible').each(function() {
        const row = $(this);
        const variantId = row.data('variant-id');
        const batchId = row.data('batch-id');
        const key = variantId + '_' + batchId;

        // Skip if already added
        if (selectedItems.has(key)) {
            skippedCount++;
            return; // continue to next iteration
        }

        // Add the medicine
        if (addMedicineFromRow(row, true)) {
            addedCount++;
        }
    });

    // Show feedback message
    if (addedCount > 0) {
        const message = 'Đã thêm ' + addedCount + ' thuốc vào phiếu xuất hủy!' +
                       (skippedCount > 0 ? ' (' + skippedCount + ' thuốc đã có trong danh sách)' : '');
        showNotification(message, 'success');
    } else if (skippedCount > 0) {
        showNotification('Tất cả ' + skippedCount + ' thuốc đã có trong danh sách!', 'info');
    } else {
        showNotification('Không có thuốc nào để thêm!', 'warning');
    }
});

// Helper function to add medicine from a row
function addMedicineFromRow(row, silent = false) {
    const variantId = row.data('variant-id');
    const batchId = row.data('batch-id');
    const inventoryId = row.data('inventory-id');
    const key = variantId + '_' + batchId;

    if (selectedItems.has(key)) {
        if (!silent) {
            alert('Thuốc này đã được thêm vào danh sách!');
        }
        return false;
    }

    const item = {
        variantId: variantId,
        batchId: batchId,
        inventoryId: inventoryId,
        medicineName: row.data('medicine-name'),
        batchCode: row.data('batch-code'),
        strength: row.data('strength') || '',
        unit: row.data('unit') || '',
        availableQty: row.data('available-qty'),
        quantity: row.data('available-qty'), // Set to full available quantity
        isExpired: row.data('is-expired')
    };

    selectedItems.set(key, item);

    if (!silent) {
        renderSelectedItems();
    }

    return true;
}

// Show notification helper
function showNotification(message, type = 'success') {
    const colors = {
        success: 'bg-green-100 border-green-500 text-green-700',
        info: 'bg-blue-100 border-blue-500 text-blue-700',
        warning: 'bg-yellow-100 border-yellow-500 text-yellow-700',
        error: 'bg-red-100 border-red-500 text-red-700'
    };

    const icons = {
        success: 'check_circle',
        info: 'info',
        warning: 'warning',
        error: 'error'
    };

    const notification = $(document.createElement('div'))
        .addClass('fixed top-4 right-4 border-l-4 p-4 rounded shadow-lg z-50 flex items-center gap-2 ' + colors[type]);

    const iconSpan = $(document.createElement('span')).addClass('material-icons-outlined').text(icons[type]);
    const messageSpan = $(document.createElement('span')).text(message);
    notification.append(iconSpan).append(messageSpan);

    $('body').append(notification);

    // Auto-hide after 3 seconds
    setTimeout(function() {
        notification.fadeOut(300, function() {
            $(this).remove();
        });
    }, 3000);

    // Render selected items after notification
    renderSelectedItems();
}

// Remove medicine
$(document).on('click', '.remove-medicine-btn', function() {
    const key = $(this).data('key');
    selectedItems.delete(key);
    renderSelectedItems();
});

// Update quantity
$(document).on('input', '.quantity-input', function() {
    const key = $(this).data('key');
    const value = parseInt($(this).val()) || 0;
    const item = selectedItems.get(key);

    if (value > item.availableQty) {
        alert('Số lượng không được vượt quá tồn kho (' + item.availableQty + ')');
        $(this).val(item.availableQty);
        item.quantity = item.availableQty;
    } else if (value < 1) {
        $(this).val(1);
        item.quantity = 1;
    } else {
        item.quantity = value;
    }
    selectedItems.set(key, item);
});

function renderSelectedItems() {
    const tbody = $('#selectedMedicines');
    tbody.empty();
    if (selectedItems.size === 0) {
        tbody.append('<tr class="empty-state"><td colspan="8" class="px-3 py-8 text-center text-gray-500">Chưa chọn thuốc nào</td></tr>');
        return;
    }
    let rowIndex = 1;
    selectedItems.forEach((item, key) => {
        const expiredBadge = item.isExpired ? '<span class="text-red-600 mr-1" title="Thuốc đã hết hạn">⚠️</span>' : '';
        const rowHtml =
            '<tr>' +
            '<td class="px-3 py-2 text-center text-gray-600 font-medium">' + rowIndex + '</td>' +
            '<td class="px-3 py-2 font-medium">' + expiredBadge + item.medicineName + '</td>' +
            '<td class="px-3 py-2"><span class="font-mono text-xs bg-gray-100 px-2 py-1 rounded">' + item.batchCode + '</span></td>' +
            '<td class="px-3 py-2">' + (item.strength || '-') + '</td>' +
            '<td class="px-3 py-2">' + (item.unit || '-') + '</td>' +
            '<td class="px-3 py-2 text-center">' + item.availableQty + ' ' + item.unit + '</td>' +
            '<td class="px-3 py-2 text-center">' +
                '<input type="number" class="quantity-input w-24 border border-gray-300 rounded px-2 py-1 text-center" ' +
                'data-key="' + key + '" value="' + item.quantity + '" min="1" max="' + item.availableQty + '">' +
            '</td>' +
            '<td class="px-3 py-2 text-center">' +
                '<button type="button" class="remove-medicine-btn text-red-600 hover:text-red-800" data-key="' + key + '">' +
                    '<span class="material-icons-outlined text-sm">delete</span>' +
                '</button>' +
            '</td>' +
            '</tr>';
        tbody.append(rowHtml);
        rowIndex++;
    });
}

// Submit
$('#submitBtn').on('click', function() {
    if (selectedItems.size === 0) {
        alert('Vui lòng chọn ít nhất một thuốc để xuất hủy!');
        return;
    }

    const items = Array.from(selectedItems.values()).map(item => ({
        variantId: item.variantId,
        batchId: item.batchId,
        inventoryId: item.inventoryId,
        quantity: item.quantity
    }));

    const data = {
        note: $('#note').val().trim(),
        items: items
    };

    $(this).prop('disabled', true).html('<span class="material-icons-outlined animate-spin">refresh</span> Đang xử lý...');

    $.ajax({
        url: '/warehouse/disposal/create',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: function(response) {
            if (response.success) {
                window.location.href = '/warehouse/disposal/success?code=' + encodeURIComponent(response.code);
            } else {
                alert('Lỗi: ' + response.message);
                $('#submitBtn').prop('disabled', false).html('<span class="material-icons-outlined">delete_forever</span> Tạo phiếu xuất hủy');
            }
        },
        error: function(xhr) {
            alert('Có lỗi xảy ra: ' + (xhr.responseJSON ? xhr.responseJSON.message : 'Không thể kết nối server'));
            $('#submitBtn').prop('disabled', false).html('<span class="material-icons-outlined">delete_forever</span> Tạo phiếu xuất hủy');
        }
    });
});
</script>
`
)
