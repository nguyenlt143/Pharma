@param java.util.List<vn.edu.fpt.pharma.dto.requestform.RequestFormVM> imports

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh sách phiếu nhập</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,typography"></script>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Outlined" rel="stylesheet">
</head>
<body class="bg-gray-100 font-sans text-gray-900">
<div class="flex h-screen">

    <!-- Sidebar -->
    @template.layouts.inventory(active = "import")

    <!-- Main -->
    <main class="flex-1 p-6 overflow-y-auto">
        <header class="flex justify-between items-center mb-6">
            <h2 class="text-2xl font-bold">LỊCH SỬ PHIẾU NHẬP</h2>
            <div class="flex items-center gap-2 text-sm text-gray-600">
                <span class="material-icons-outlined text-base">inventory_2</span>
                <span>Tổng: @if(imports != null)${imports.size()}@endif phiếu</span>
            </div>
        </header>

        <div class="bg-white p-6 rounded-lg shadow-md">
            <div class="flex justify-between items-center mb-4">
                <div class="flex items-center gap-4">
                    <div class="relative">
                        <span class="material-icons-outlined absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">search</span>
                        <input id="importSearchInput" type="text" placeholder="Tìm theo mã phiếu (#RQ...)" class="pl-10 pr-4 py-2 w-72 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500" />
                    </div>
                    <input id="dateFilterInput" type="date" class="py-2 px-3 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500" />
                </div>
                <a href="/inventory/import/create" class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-md flex items-center gap-2">
                    <span class="material-icons-outlined">add_circle</span>
                    Tạo phiếu nhập mới
                </a>
            </div>

            <div class="overflow-x-auto">
                <table class="w-full table-auto">
                    <thead class="bg-gray-50 text-left text-sm text-gray-600 uppercase">
                    <tr>
                        <th class="px-4 py-3 font-medium">STT</th>
                        <th class="px-4 py-3 font-medium">Mã phiếu</th>
                        <th class="px-4 py-3 font-medium">Ngày tạo</th>
                        <th class="px-4 py-3 font-medium text-center">Số loại thuốc</th>
                        <th class="px-4 py-3 font-medium text-right">Tổng giá tiền</th>
                        <th class="px-4 py-3 font-medium">Trạng thái</th>
                        <th class="px-4 py-3 font-medium">Ghi chú</th>
                        <th class="px-4 py-3 font-medium text-center">Hành động</th>
                    </tr>
                    </thead>
                    <tbody id="importTableBody" class="divide-y divide-gray-200">
                    @if(imports != null && !imports.isEmpty())
                        @for(vn.edu.fpt.pharma.dto.requestform.RequestFormVM item : imports)
                            <tr class="hover:bg-gray-50" data-import-code="${item.code}" data-created-date="${item.createdAt}" data-type="${item.type}" data-status="${item.status}">
                                <td class="px-4 py-3">@if(imports.indexOf(item) >= 0)${imports.indexOf(item) + 1}@endif</td>
                                <td class="px-4 py-3 font-medium text-blue-600">${item.code}</td>
                                <td class="px-4 py-3">${item.createdAt}</td>
                                <td class="px-4 py-3 text-center">${item.medicineCount}</td>
                                <td class="px-4 py-3 text-right font-medium">${String.format("%,.0f", item.totalAmount)} ₫</td>
                                <td class="px-4 py-3">
                                    @if(item.status.equals("REQUESTED"))
                                        <span class="text-yellow-600 font-medium">ĐANG DUYỆT</span>
                                    @elseif(item.status.equals("RECEIVED"))
                                        <span class="text-green-600 font-medium">ĐÃ NHẬP</span>
                                    @elseif(item.status.equals("CANCELLED"))
                                        <span class="text-red-600 font-medium">ĐÃ HỦY</span>
                                    @else
                                        <span class="text-gray-500">${item.status}</span>
                                    @endif
                                </td>
                                <td class="px-4 py-3">${item.note}</td>
                                <td class="px-4 py-3 text-center">
                                    <a href="/inventory/import/detail/${item.code.substring(3)}" class="text-blue-600 hover:text-blue-800 font-medium">Xem</a>
                                </td>
                            </tr>
                        @endfor
                    @else
                        <tr>
                            <td colspan="8" class="px-4 py-8 text-center text-gray-500">Chưa có phiếu nhập</td>
                        </tr>
                    @endif
                    </tbody>
                </table>
            </div>
        </div>
        <p class="text-xs text-gray-400 mt-4">Hiển thị thời gian: dd/MM/yyyy HH:mm</p>
    </main>
</div>
<script src="/assets/js/inventory/inventory_import_list.js"></script>
</body>
</html>
