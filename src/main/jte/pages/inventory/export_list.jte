@param java.util.List<vn.edu.fpt.pharma.dto.requestform.RequestFormVM> exports

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh sách phiếu xuất</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,typography"></script>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Outlined" rel="stylesheet">
</head>
<body class="bg-gray-100 font-sans text-gray-900">
<div class="flex h-screen">

    <!-- Sidebar -->
    @template.layouts.inventory(active = "export")

    <!-- Main -->
    <main class="flex-1 p-6 overflow-y-auto">
        <header class="flex justify-between items-center mb-6">
            <h2 class="text-2xl font-bold">LỊCH SỬ PHIẾU XUẤT</h2>
            <div class="flex items-center gap-2 text-sm text-gray-600">
                <span class="material-icons-outlined text-base">local_shipping</span>
                <span>Tổng: @if(exports != null)${exports.size()}@endif phiếu</span>
            </div>
        </header>
        <div class="bg-card-light dark:bg-card-dark p-6 rounded-lg shadow-md">
            <div class="flex justify-between items-center mb-4">
                <div class="flex items-center gap-4">
                    <div class="relative">
                        <span class="material-icons-outlined absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">search</span>
                        <input id="searchInput" class="pl-10 pr-4 py-2 w-96 border border-gray-300 bg-white rounded-md focus:ring-blue-500 focus:border-blue-500" placeholder="Tìm theo mã phiếu, ghi chú..." type="text"/>
                    </div>
                </div>
                <button class="bg-primary hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-md flex items-center gap-2">
                    <span class="material-icons-outlined">add</span>
                    Tạo phiếu xuất mới
                </button>
            </div>
            <div class="overflow-x-auto">
                <table class="w-full table-auto">
                    <thead class="bg-gray-50 dark:bg-slate-700 text-left text-sm text-text-secondary-light dark:text-text-secondary-dark uppercase">
                    <tr>
                        <th class="px-4 py-3 font-medium text-center">STT</th>
                        <th class="px-4 py-3 font-medium">Mã phiếu</th>
                        <th class="px-4 py-3 font-medium">Ngày tạo</th>
                        <th class="px-4 py-3 font-medium">Loại xuất</th>
                        <th class="px-4 py-3 font-medium">Người tạo</th>
                        <th class="px-4 py-3 font-medium text-center">Hành động</th>
                    </tr>
                    </thead>
                    <tbody id="exportTableBody" class="divide-y divide-gray-200">
                    @if(exports != null && !exports.isEmpty())
                        @for(vn.edu.fpt.pharma.dto.requestform.RequestFormVM item : exports)
                        <tr class="hover:bg-gray-50"
                            data-code="${item.code}"
                            data-note="${item.note}"
                            data-type="${item.type}">
                            <td class="px-4 py-3 text-center">@if(exports.indexOf(item) >= 0)${exports.indexOf(item) + 1}@endif</td>
                            <td class="px-4 py-3 font-medium text-blue-600">${item.code}</td>
                            <td class="px-4 py-3">${item.createdAt}</td>
                            <td class="px-4 py-3">${item.type}</td>
                            <td class="px-4 py-3">—</td>
                            <td class="px-4 py-3 text-center">
                                <a href="/inventory/export/detail/${item.code.substring(3)}" class="text-blue-600 hover:text-blue-800 font-medium">Xem</a>
                            </td>
                        </tr>
                        @endfor
                    @else
                        <tr>
                            <td colspan="6" class="px-4 py-8 text-center text-gray-500">Chưa có phiếu xuất</td>
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
    const tableBody = document.getElementById('exportTableBody');
    const rows = Array.from(tableBody.getElementsByTagName('tr'));

    searchInput?.addEventListener('input', function() {
        const searchTerm = this.value.toLowerCase().trim();

        rows.forEach(row => {
            if (row.querySelector('td[colspan]')) return; // Skip empty state row

            const code = row.dataset.code?.toLowerCase() || '';
            const note = row.dataset.note?.toLowerCase() || '';
            const type = row.dataset.type?.toLowerCase() || '';

            const matches = code.includes(searchTerm) ||
                          note.includes(searchTerm) ||
                          type.includes(searchTerm);

            row.style.display = matches ? '' : 'none';
        });
    });
</script>
</body></html>