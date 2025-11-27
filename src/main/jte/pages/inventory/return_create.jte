@param java.util.List<vn.edu.fpt.pharma.dto.inventory.InventoryMedicineVM> medicines
@param Long branchId

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Tạo phiếu trả hàng</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,typography"></script>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Outlined" rel="stylesheet">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body class="bg-gray-100 font-sans text-gray-900">
<div class="flex h-screen">
    <!-- Sidebar -->
    @template.layouts.inventory(active = "export")

    <!-- Main -->
    <main class="flex-1 p-6 overflow-y-auto">
        <div class="mb-6">
            <div class="flex justify-between items-center">
                <h2 class="text-2xl font-bold">TẠO PHIẾU TRẢ HÀNG</h2>
                <a href="/inventory/return/list" class="text-blue-600 hover:text-blue-800 flex items-center gap-1">
                    <span class="material-icons-outlined">arrow_back</span>
                    Quay lại
                </a>
            </div>
            <p class="text-gray-600 mt-2">Chọn thuốc từ kho chi nhánh để trả về kho tổng</p>
        </div>

        <!-- Medicines from Branch -->
        <div class="bg-white p-6 rounded-lg shadow-md mb-6">
            <h3 class="text-lg font-semibold mb-4">Danh sách thuốc trong kho chi nhánh</h3>
            <div class="mb-4 flex gap-4">
                <div class="relative w-80">
                    <span class="material-icons-outlined absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">search</span>
                    <input id="branchMedicineSearch" type="text" placeholder="Tìm theo tên / hoạt chất / mã lô" class="pl-10 pr-4 py-2 w-full border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500" />
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
                                <tr class="hover:bg-gray-50 medicine-row"
                                    data-variant-id="${med.getVariantId()}"
                                    data-batch-id="${med.getBatchId()}"
                                    data-inventory-id="@if(med.getInventoryId() != null)${med.getInventoryId()}@endif"
                                    data-medicine-name="${med.getMedicineName()}"
                                    data-active-ingredient="@if(med.getActiveIngredient() != null)${med.getActiveIngredient()}@endif"
                                    data-strength="@if(med.getStrength() != null)${med.getStrength()}@else@endif"
                                    data-batch-code="@if(med.getBatchCode() != null)${med.getBatchCode()}@else@endif"
                                    data-expiry-date="@if(med.getExpiryDate() != null)${med.getExpiryDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))}@endif"
                                    data-available-qty="${med.getQuantity()}"
                                    data-unit="@if(med.getUnit() != null)${med.getUnit()}@else@endif">
                                    <td class="px-3 py-2 font-medium">${med.getMedicineName()}</td>
                                    <td class="px-3 py-2">@if(med.getActiveIngredient() != null)${med.getActiveIngredient()}@else-@endif</td>
                                    <td class="px-3 py-2">@if(med.getStrength() != null)${med.getStrength()}@else-@endif</td>
                                    <td class="px-3 py-2 text-center">
                                        <span class="font-mono text-xs bg-gray-100 px-2 py-1 rounded">@if(med.getBatchCode() != null)${med.getBatchCode()}@else-@endif</span><br/>
                                        <span class="text-xs text-gray-500">@if(med.getExpiryDate() != null)HSD: ${med.getExpiryDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))}@endif</span>
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
                                <td colspan="6" class="px-3 py-8 text-center text-gray-500">Không có thuốc trong kho chi nhánh</td>
                            </tr>
                        @endif
                    </tbody>
                </table>
            </div>
        </div>

        <!-- Selected Medicines -->
        <div class="bg-white p-6 rounded-lg shadow-md mb-6">
            <h3 class="text-lg font-semibold mb-4">Danh sách thuốc đã chọn</h3>
            <div class="overflow-x-auto">
                <table class="w-full text-sm">
                    <thead class="bg-gray-50 text-gray-600 uppercase text-xs">
                        <tr>
                            <th class="px-3 py-2 text-left">Tên thuốc</th>
                            <th class="px-3 py-2 text-left">Mã lô</th>
                            <th class="px-3 py-2 text-left">Hàm lượng</th>
                            <th class="px-3 py-2 text-left">Đóng gói</th>
                            <th class="px-3 py-2 text-center">Tồn kho</th>
                            <th class="px-3 py-2 text-center">Số lượng trả</th>
                            <th class="px-3 py-2 text-center">Xóa</th>
                        </tr>
                    </thead>
                    <tbody id="selectedMedicines" class="divide-y">
                        <tr class="empty-state">
                            <td colspan="7" class="px-3 py-8 text-center text-gray-500">Chưa chọn thuốc nào</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- Note and Submit -->
        <div class="bg-white p-6 rounded-lg shadow-md">
            <div class="mb-4">
                <label class="block text-sm font-medium mb-2">Ghi chú</label>
                <textarea id="note" rows="3" class="w-full border border-gray-300 rounded-md px-3 py-2" placeholder="Nhập ghi chú (nếu có)"></textarea>
            </div>
            <div class="flex justify-end gap-3">
                <a href="/inventory/return/list" class="px-6 py-2 border border-gray-300 rounded-md hover:bg-gray-50">Hủy</a>
                <button id="submitBtn" type="button" class="px-6 py-2 bg-green-600 hover:bg-green-700 text-white rounded-md flex items-center gap-2">
                    <span class="material-icons-outlined">send</span>
                    Hoàn tất và gửi
                </button>
            </div>
        </div>
    </main>
</div>

<script>
const selectedItems = new Map();
const branchIdValue = ${branchId};

// Load preselected items from sessionStorage (from medicine list)
document.addEventListener('DOMContentLoaded', function() {
    const preselected = sessionStorage.getItem('preselectedReturnItems');
    if (preselected) {
        try {
            const items = JSON.parse(preselected);
            items.forEach(item => {
                const key = item.variantId + '_' + item.batchId;
                selectedItems.set(key, {
                    variantId: item.variantId,
                    batchId: item.batchId,
                    inventoryId: item.inventoryId,
                    medicineName: item.medicineName,
                    batchCode: item.batchCode,
                    strength: item.strength || '',
                    unit: item.unit,
                    availableQty: item.availableQty,
                    quantity: item.quantity
                });
            });
            renderSelectedItems();
            // Clear sessionStorage after loading
            sessionStorage.removeItem('preselectedReturnItems');
        } catch (e) {
            console.error('Error loading preselected items:', e);
        }
    }
});

// Search functionality
$('#branchMedicineSearch').on('input', function() {
    const searchTerm = $(this).val().toLowerCase();
    $('.medicine-row').each(function() {
        const name = $(this).data('medicine-name').toLowerCase();
        const ingredient = $(this).data('active-ingredient') ? $(this).data('active-ingredient').toLowerCase() : '';
        const batchCode = $(this).data('batch-code') ? $(this).data('batch-code').toLowerCase() : '';
        const matches = name.includes(searchTerm) || ingredient.includes(searchTerm) || batchCode.includes(searchTerm);
        $(this).toggle(matches);
    });
});

// Add medicine
$(document).on('click', '.add-medicine-btn', function() {
    const row = $(this).closest('tr');
    const variantId = row.data('variant-id');
    const batchId = row.data('batch-id');
    const inventoryId = row.data('inventory-id');
    const key = variantId + '_' + batchId; // replaced template literal

    if (selectedItems.has(key)) {
        alert('Thuốc này đã được thêm vào danh sách!');
        return;
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
        quantity: 1
    };

    selectedItems.set(key, item);
    renderSelectedItems();
});

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
        tbody.append('<tr class="empty-state"><td colspan="7" class="px-3 py-8 text-center text-gray-500">Chưa chọn thuốc nào</td></tr>');
        return;
    }
    selectedItems.forEach((item, key) => {
        const rowHtml =
            '<tr>' +
            '<td class="px-3 py-2 font-medium">' + item.medicineName + '</td>' +
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
    });
}

// Submit
$('#submitBtn').on('click', function() {
    if (selectedItems.size === 0) {
        alert('Vui lòng chọn ít nhất một thuốc!');
        return;
    }

    const items = Array.from(selectedItems.values()).map(item => ({
        variantId: item.variantId,
        batchId: item.batchId,
        inventoryId: item.inventoryId,
        quantity: item.quantity
    }));

    const data = {
        branchId: branchIdValue,
        note: $('#note').val().trim(),
        items: items
    };

    $(this).prop('disabled', true).html('<span class="material-icons-outlined animate-spin">refresh</span> Đang xử lý...');

    $.ajax({
        url: '/inventory/return/create',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: function(response) {
            if (response.success) {
                window.location.href = '/inventory/return/success?code=' + encodeURIComponent(response.code);
            } else {
                alert('Lỗi: ' + response.message);
                $('#submitBtn').prop('disabled', false).html('<span class="material-icons-outlined">send</span> Hoàn tất và gửi');
            }
        },
        error: function(xhr) {
            alert('Có lỗi xảy ra: ' + (xhr.responseJSON ? xhr.responseJSON.message : 'Không thể kết nối server'));
            $('#submitBtn').prop('disabled', false).html('<span class="material-icons-outlined">send</span> Hoàn tất và gửi');
        }
    });
});
</script>
</body>
</html>
