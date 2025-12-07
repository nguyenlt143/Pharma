@param java.util.List<vn.edu.fpt.pharma.dto.inventory.InventoryMedicineVM> medicines
@param Long branchId

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Phiếu kiểm kho</title>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Outlined" rel="stylesheet">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.tailwindcss.com?plugins=forms,typography"></script>
</head>
<body class="bg-gray-100 font-sans text-gray-900">
<div class="flex h-screen">
    @template.layouts.inventory(active = "check")
    <main class="flex-1 p-6 overflow-y-auto">
        <div class="mb-6 flex justify-between items-center">
            <div>
                <h2 class="text-2xl font-bold">TẠO PHIẾU KIỂM KHO</h2>
                <p class="text-gray-600">Chi nhánh: ${branchId}. Chọn thuốc và nhập số lượng thực tế sau kiểm.</p>
            </div>
            <a href="/inventory/check" class="text-blue-600 hover:text-blue-800 flex items-center gap-1">
                <span class="material-icons-outlined">arrow_back</span> Quay lại
            </a>
        </div>

        <div class="bg-white p-6 rounded-lg shadow mb-6">
            <h3 class="text-lg font-semibold mb-4">Danh sách thuốc trong kho</h3>
            <div class="mb-4 flex gap-4">
                <div class="relative w-80">
                    <span class="material-icons-outlined absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">search</span>
                    <input id="inventoryCheckSearch" type="text" placeholder="Tìm theo tên / hoạt chất / mã lô" maxlength="150" class="pl-10 pr-4 py-2 w-full border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500" />
                </div>
                <button id="addAllButton" class="px-4 py-2 bg-blue-100 text-blue-700 rounded hover:bg-blue-200 flex items-center gap-1">
                    <span class="material-icons-outlined">playlist_add</span> Thêm tất cả
                </button>
            </div>
            <div class="overflow-x-auto max-h-80 border rounded">
                <table class="w-full text-sm">
                    <thead class="bg-gray-50 text-gray-600 uppercase text-xs">
                        <tr>
                            <th class="px-3 py-2 text-left">Tên thuốc</th>
                            <th class="px-3 py-2 text-left">Hoạt chất</th>
                            <th class="px-3 py-2">Lô / HSD</th>
                            <th class="px-3 py-2 text-center">Tồn hệ thống</th>
                            <th class="px-3 py-2 text-center">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody id="inventoryMedicineSource" class="divide-y">
                        @if(medicines != null && !medicines.isEmpty())
                            @for(var m : medicines)
                                <tr class="hover:bg-gray-50" data-inventory-id="${m.getInventoryId()}" data-variant-id="${m.getVariantId()}" data-batch-id="${m.getBatchId()}" data-system-qty="${m.getQuantity()}">
                                    <td class="px-3 py-2">
                                        <div class="font-medium text-gray-900">${m.getMedicineName()}</div>
                                        <div class="text-xs text-gray-500">${m.getDosageForm()}</div>
                                    </td>
                                    <td class="px-3 py-2 text-xs">${m.getActiveIngredient()}</td>
                                    <td class="px-3 py-2 text-xs">
                                        @if(m.getBatchCode() != null)
                                            <span class="font-mono bg-gray-100 px-1 py-0.5 rounded">${m.getBatchCode()}</span>
                                        @endif
                                        @if(m.getExpiryDate() != null)
                                            <span class="ml-1 ${m.getExpiryDate().isBefore(java.time.LocalDate.now().plusMonths(6)) ? "text-red-600 font-semibold" : "text-gray-600"}">${m.getExpiryDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))}</span>
                                        @endif
                                    </td>
                                    <td class="px-3 py-2 text-center font-semibold text-blue-600">${m.getQuantity()} ${m.getUnit()}</td>
                                    <td class="px-3 py-2 text-center">
                                        <button class="add-one-btn text-sm text-blue-600 hover:text-blue-800" type="button">Thêm</button>
                                    </td>
                                </tr>
                            @endfor
                        @else
                            <tr><td colspan="5" class="px-3 py-6 text-center text-gray-500">Không có thuốc trong kho</td></tr>
                        @endif
                    </tbody>
                </table>
            </div>
        </div>

        <div class="bg-white p-6 rounded-lg shadow">
            <h3 class="text-lg font-semibold mb-4">Thuốc đã chọn kiểm kho</h3>
            <div class="overflow-x-auto">
                <table class="w-full text-sm">
                    <thead class="bg-gray-50 text-gray-600 uppercase text-xs">
                        <tr>
                            <th class="px-3 py-2 text-left">Tên thuốc</th>
                            <th class="px-3 py-2 text-center">Tồn hệ thống</th>
                            <th class="px-3 py-2 text-center">Số thực tế</th>
                            <th class="px-3 py-2 text-center">Chênh lệch</th>
                            <th class="px-3 py-2 text-center">Xóa</th>
                        </tr>
                    </thead>
                    <tbody id="inventoryCheckSelected" class="divide-y">
                        <tr id="emptySelectedRow"><td colspan="5" class="px-3 py-6 text-center text-gray-500">Chưa chọn thuốc nào</td></tr>
                    </tbody>
                </table>
            </div>

            <div class="mt-6">
                <label class="block text-sm font-medium text-gray-700 mb-2">Ghi chú</label>
                <textarea id="inventoryCheckNote" rows="3" class="w-full border border-gray-300 rounded-md p-3 focus:ring-blue-500 focus:border-blue-500" placeholder="Ghi chú kiểm kho (tuỳ chọn)"></textarea>
            </div>
            <div class="mt-6 flex justify-end gap-4">
                <button type="button" onclick="window.location.href='/inventory/check'" class="px-5 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50">Huỷ</button>
                <button type="button" id="submitInventoryCheck" class="px-5 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 flex items-center gap-2">
                    <span class="material-icons-outlined">playlist_add_check</span>
                    Hoàn tất kiểm kho
                </button>
            </div>
        </div>
    </main>
</div>
<script src="/assets/js/inventory/inventory_check_create.js"></script>
</body>
</html>
