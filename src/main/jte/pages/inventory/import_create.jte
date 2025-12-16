@param java.util.List<vn.edu.fpt.pharma.dto.inventory.InventoryMedicineVM> medicines

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Tạo phiếu nhập kho</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,typography"></script>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Outlined" rel="stylesheet">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body class="bg-gray-100 font-sans text-gray-900">
<div class="flex h-screen">
    <!-- Sidebar -->
    @template.layouts.inventory(active = "import")

    <!-- Main -->
    <main class="flex-1 p-6 overflow-y-auto">
        <div class="mb-6">
            <div class="flex justify-between items-center">
                <h2 class="text-2xl font-bold">TẠO PHIẾU NHẬP KHO</h2>
                <a href="/inventory/import/list" class="text-blue-600 hover:text-blue-800 flex items-center gap-1">
                    <span class="material-icons-outlined">arrow_back</span>
                    Quay lại
                </a>
            </div>
            <p class="text-gray-600 mt-2">Chọn thuốc từ kho tổng để tạo phiếu yêu cầu nhập về chi nhánh</p>
        </div>

        <!-- Medicines from Warehouse -->
        <div class="bg-white p-6 rounded-lg shadow-md mb-6">
            <h3 class="text-lg font-semibold mb-4">Danh sách thuốc trong kho tổng</h3>
            <div class="mb-4 flex gap-4">
                <div class="relative w-80">
                    <span class="material-icons-outlined absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">search</span>
                    <input id="warehouseMedicineSearch" type="text" placeholder="Tìm theo tên / hoạt chất" maxlength="150" class="pl-10 pr-4 py-2 w-full border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500" />
                </div>
            </div>
            <div class="overflow-x-auto max-h-96 border rounded">
                <table class="w-full text-sm">
                    <thead class="bg-gray-50 text-gray-600 uppercase text-xs sticky top-0">
                        <tr>
                            <th class="px-3 py-2 text-left">Tên thuốc</th>
                            <th class="px-3 py-2 text-left">Hoạt chất</th>
                            <th class="px-3 py-2 text-left">Hàm lượng</th>
                            <th class="px-3 py-2 text-center">Tồn kho chi nhánh</th>
                            <th class="px-3 py-2 text-center">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody id="warehouseMedicineSource" class="divide-y">
                        @if(medicines != null && !medicines.isEmpty())
                            @for(var m : medicines)
                                <tr class="hover:bg-gray-50"
                                    data-variant-id="${m.getVariantId()}"
                                    data-batch-id="${m.getBatchId()}"
                                    data-medicine-name="${m.getMedicineName()}"
                                    data-active-ingredient="@if(m.getActiveIngredient() != null)${m.getActiveIngredient()}@endif"
                                    data-strength="@if(m.getStrength() != null)${m.getStrength()}@endif"
                                    data-dosage-form="@if(m.getDosageForm() != null)${m.getDosageForm()}@endif"
                                    data-manufacturer="@if(m.getManufacturer() != null)${m.getManufacturer()}@endif"
                                    data-branch-stock="@if(m.getBranchStock() != null)${m.getBranchStock()}@else0@endif"
                                    data-min-stock="@if(m.getMinStock() != null)${m.getMinStock()}@else${""}@endif"
                                    data-unit="@if(m.getUnit() != null)${m.getUnit()}@endif">
                                    <td class="px-3 py-2">
                                        <div class="font-medium text-gray-900">${m.getMedicineName()}</div>
                                        <div class="text-xs text-gray-500">@if(m.getDosageForm() != null)${m.getDosageForm()}@endif</div>
                                    </td>
                                    <td class="px-3 py-2 text-xs">@if(m.getActiveIngredient() != null)${m.getActiveIngredient()}@else-@endif</td>
                                    <td class="px-3 py-2 text-xs">@if(m.getStrength() != null)${m.getStrength()}@else-@endif</td>
                                    <td class="px-3 py-2 text-center font-semibold text-blue-600">
                                        @if(m.getBranchStock() != null)${m.getBranchStock()}@else0@endif @if(m.getUnit() != null)${m.getUnit()}@endif
                                    </td>
                                    <td class="px-3 py-2 text-center">
                                        <button class="add-medicine-btn text-sm text-blue-600 hover:text-blue-800 font-medium" type="button">
                                            <span class="material-icons-outlined text-base align-middle">add_circle</span>
                                            Thêm
                                        </button>
                                    </td>
                                </tr>
                            @endfor
                        @else
                            <tr><td colspan="5" class="px-3 py-6 text-center text-gray-500">Không có thuốc trong kho tổng</td></tr>
                        @endif
                    </tbody>
                </table>
            </div>
        </div>

        <!-- Selected Medicines Table -->
        <div class="bg-white p-6 rounded-lg shadow-md">
            <h3 class="text-lg font-semibold mb-4">Thuốc đã chọn để nhập</h3>

            <div class="overflow-x-auto">
                <table class="w-full text-sm">
                    <thead class="bg-gray-50 text-gray-600 uppercase text-xs">
                        <tr>
                            <th class="px-4 py-3 text-left">STT</th>
                            <th class="px-4 py-3 text-left">Tên thuốc</th>
                            <th class="px-4 py-3 text-left">Hoạt chất</th>
                            <th class="px-4 py-3 text-left">Hàm lượng</th>
                            <th class="px-4 py-3 text-center">Số lượng tối thiểu</th>
                            <th class="px-4 py-3 text-center">Tồn kho chi nhánh</th>
                            <th class="px-4 py-3 text-center">Số lượng yêu cầu</th>
                            <th class="px-4 py-3 text-center">Xóa</th>
                        </tr>
                    </thead>
                    <tbody id="selectedMedicinesList" class="divide-y">
                        <tr id="emptyRow">
                            <td colspan="8" class="px-4 py-6 text-center text-gray-500">
                                Chưa có thuốc nào được chọn. Vui lòng chọn thuốc từ danh sách bên trên.
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <!-- Note and Submit -->
            <div class="mt-6 space-y-4">
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">Ghi chú (tối đa 300 ký tự)</label>
                    <textarea
                        id="importNote"
                        rows="3"
                        maxlength="300"
                        class="w-full border border-gray-300 rounded-md p-3 focus:ring-blue-500 focus:border-blue-500"
                        placeholder="Nhập ghi chú cho phiếu nhập (không bắt buộc)"></textarea>
                </div>

                <div class="flex justify-end gap-4">
                    <button
                        type="button"
                        onclick="window.location.href='/inventory/import/list'"
                        class="px-6 py-3 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50">
                        Hủy
                    </button>
                    <button
                        type="button"
                        id="submitImport"
                        class="px-6 py-3 bg-blue-600 text-white rounded-md hover:bg-blue-700 flex items-center gap-2">
                        <span class="material-icons-outlined">check_circle</span>
                        Hoàn tất và gửi yêu cầu
                    </button>
                </div>
            </div>
        </div>
    </main>
</div>

<script src="/assets/js/inventory/import_create.js"></script>
<script>
// Preload items from medicine list selection
(function preloadSelected() {
    const raw = sessionStorage.getItem('preselectedImportItems');
    if (!raw) return;
    try {
        const items = JSON.parse(raw);
        if (!Array.isArray(items) || items.length === 0) return;
        
        const tbody = document.getElementById('selectedMedicinesList');
        const emptyRow = document.getElementById('emptyRow');
        if (emptyRow) emptyRow.remove();
        
        let index = 1;
        items.forEach(function(it){
            const tr = document.createElement('tr');
            const quantity = (typeof it.quantity === 'number' && it.quantity > 0) ? it.quantity : 1;
            
            const branchStock = (typeof it.branchStock === 'number') ? it.branchStock : 0;
            const minStock = (typeof it.minStock === 'number') ? it.minStock : null;
            const unit = it.unit || '';
            
            tr.innerHTML = ''
                + '<td class="px-4 py-3 text-center">' + (index++) + '</td>'
                + '<td class="px-4 py-3 font-medium">' + (it.medicineName || '') + '</td>'
                + '<td class="px-4 py-3 text-sm">' + (it.activeIngredient || '-') + '</td>'
                + '<td class="px-4 py-3 text-sm">' + (it.strength || '-') + '</td>'
                + '<td class="px-4 py-3 text-center text-sm text-gray-700">' + (minStock !== null ? minStock : '-') + '</td>'
                + '<td class="px-4 py-3 text-center font-semibold text-blue-600">' + branchStock + ' ' + unit + '</td>'
                + '<td class="px-4 py-3 text-center">'
                +   '<input type="number" class="qty-input border border-gray-300 rounded px-2 py-1 text-center w-24"'
                +   ' value="' + quantity + '" min="1" data-variant-id="' + it.variantId + '" />'
                + '</td>'
                + '<td class="px-4 py-3 text-center">'
                +   '<button type="button" class="remove-btn text-red-600 hover:text-red-800">'
                +     '<span class="material-icons-outlined text-sm">delete</span>'
                +   '</button>'
                + '</td>';
            
            tbody.appendChild(tr);
        });
        
        sessionStorage.removeItem('preselectedImportItems');
    } catch (e) {
        console.error('Failed to preload import items', e);
    }
})();
</script>
</body>
</html>