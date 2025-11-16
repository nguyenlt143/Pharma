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
        <h2 class="text-2xl font-bold mb-6">DANH SÁCH PHIẾU NHẬP</h2>

        <!-- Search form -->
        <form method="get" action="/inventory/import/list" class="flex items-center gap-4 mb-6">
            <div class="relative">
                <span class="material-icons-outlined absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">search</span>
                <input
                        type="text"
                        name="code"
                        placeholder="Tìm theo mã phiếu..."
                        class="pl-10 pr-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500 w-64"
                >
            </div>
            <input
                    type="date"
                    name="createdAt"
                    class="py-2 px-4 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            >
            <button type="submit" class="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700">
                Tìm kiếm
            </button>
        </form>

        <!-- Table -->
        <div class="bg-white p-6 rounded-lg shadow">
            <table class="w-full table-auto border-collapse">
                <thead class="bg-gray-50">
                <tr>
                    <th class="px-4 py-3 text-left">Mã phiếu</th>
                    <th class="px-4 py-3 text-left">Loại</th>
                    <th class="px-4 py-3 text-left">Trạng thái</th>
                    <th class="px-4 py-3 text-left">Ghi chú</th>
                    <th class="px-4 py-3 text-left">Ngày tạo</th>
                </tr>
                </thead>
                <tbody>
                @if(imports.isEmpty())
                    <tr>
                        <td colspan="5" class="px-4 py-3 text-center text-gray-500">Không có dữ liệu</td>
                    </tr>
                @else
                    @for(vn.edu.fpt.pharma.dto.requestform.RequestFormVM item : imports)
                        <tr class="border-t hover:bg-gray-50">
                            <td class="px-4 py-3 text-blue-600 font-semibold">${item.code}</td>
                            <td class="px-4 py-3">${item.type}</td>
                            <td class="px-4 py-3">${item.status}</td>
                            <td class="px-4 py-3">${item.note}</td>
                            <td class="px-4 py-3">${item.createdAt}</td>
                        </tr>
                    @endfor
                @endif
                </tbody>
            </table>
        </div>
    </main>
</div>
</body>
</html>
