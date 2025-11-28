@param java.util.List<vn.edu.fpt.pharma.dto.inventory.ConfirmImportVM> confirmImports
@param String q

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Xác nhận nhập hàng</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,typography"></script>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Outlined" rel="stylesheet">
</head>
<body class="bg-gray-100 font-sans text-gray-900">
<div class="flex h-screen">

    <!-- Sidebar -->
    @template.layouts.inventory(active = "confirm")

    <!-- Main -->
    <main class="flex-1 p-6 overflow-y-auto">
        <header class="flex justify-between items-center mb-6">
            <h2 class="text-2xl font-bold">XÁC NHẬN NHẬP HÀNG</h2>
            <div class="flex items-center gap-2 text-sm text-gray-600">
                <span class="material-icons-outlined text-base">check_circle</span>
                <span>Tổng: @if(confirmImports != null)${confirmImports.size()}@endif đơn</span>
            </div>
        </header>

        <div class="bg-white p-6 rounded-lg shadow-md">
            <div class="flex justify-between items-center mb-4">
                <div class="flex items-center gap-4">
                    <div class="relative">
                        <span class="material-icons-outlined absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">search</span>
                        <input id="searchInput" type="text" placeholder="Tìm theo mã phiếu, ghi chú..." class="pl-10 pr-4 py-2 w-96 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500" />
                    </div>
                </div>
                <div></div>
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
                        <th class="px-4 py-3 font-medium text-center">Hành động</th>
                    </tr>
                    </thead>
                    <tbody id="confirmTableBody" class="divide-y divide-gray-200">
                    @if(confirmImports != null && !confirmImports.isEmpty())
                        @for(vn.edu.fpt.pharma.dto.inventory.ConfirmImportVM item : confirmImports)
                            <tr class="hover:bg-gray-50"
                                data-code="${item.getCode()}"
                                data-status="${item.getStatus()}">
                                <td class="px-4 py-3">@if(confirmImports.indexOf(item) >= 0)${confirmImports.indexOf(item) + 1}@endif</td>
                                <td class="px-4 py-3 font-medium text-blue-600">${item.getCode()}</td>
                                <td class="px-4 py-3">${item.getCreatedAt()}</td>
                                <td class="px-4 py-3 text-center">${item.getMedicineCount()}</td>
                                <td class="px-4 py-3 text-right font-medium">@if(item.getTotalMoney() != null)${String.format("%,.0f", item.getTotalMoney())}@else0@endif ₫</td>
                                <td class="px-4 py-3">
                                    @if(item.getStatus().equals("SHIPPED"))
                                        <span class="text-blue-600 font-medium">ĐANG GIAO</span>
                                    @elseif(item.getStatus().equals("RECEIVED"))
                                        <span class="text-green-600 font-medium">ĐÃ NHẬN</span>
                                    @else
                                        <span class="text-gray-500">${item.getStatus()}</span>
                                    @endif
                                </td>
                                <td class="px-4 py-3 text-center">
                                    <a href="/inventory/confirm/detail/${item.getId()}" class="text-blue-600 hover:text-blue-800 font-medium">Xem</a>
                                </td>
                            </tr>
                        @endfor
                    @else
                        <tr>
                            <td colspan="7" class="px-4 py-8 text-center text-gray-500">
                                Không có đơn hàng nào đang chờ xác nhận
                            </td>
                        </tr>
                    @endif
                    </tbody>
                </table>
            </div>
        </div>
    </main>
</div>
<script>
    const searchInput = document.getElementById('searchInput');
    const tableBody = document.getElementById('confirmTableBody');
    const rows = Array.from(tableBody.getElementsByTagName('tr'));

    searchInput?.addEventListener('input', function() {
        const searchTerm = this.value.toLowerCase().trim();

        rows.forEach(row => {
            if (row.querySelector('td[colspan]')) return; // Skip empty state row

            const code = row.dataset.code?.toLowerCase() || '';
            const status = row.dataset.status?.toLowerCase() || '';

            const matches = code.includes(searchTerm) ||
                          status.includes(searchTerm);

            row.style.display = matches ? '' : 'none';
        });
    });
</script>
</body>
</html>
