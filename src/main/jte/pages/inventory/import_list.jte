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
    <aside class="w-64 bg-[#0F172A] text-white flex flex-col">
        <div class="p-4 border-b border-gray-700">
            <h1 class="text-xl font-bold">QUẢN LÝ NHÀ THUỐC</h1>
        </div>
        <nav class="flex-1 mt-4">
            <ul>
                <li class="px-4 py-2 hover:bg-gray-700 rounded-md">
                    <a class="flex items-center gap-2" href="/inventory/dashboard">
                        <span class="material-icons-outlined">dashboard</span> Tổng quan
                    </a>
                </li>
                <li class="px-4 py-2 bg-blue-600 rounded-md">
                    <a class="flex items-center gap-2" href="/inventory/import/list">
                        <span class="material-icons-outlined">inventory_2</span> Nhập kho
                    </a>
                </li>
            </ul>
        </nav>
    </aside>

    <!-- Main -->
    <main class="flex-1 p-6 overflow-y-auto">
        <h2 class="text-2xl font-bold mb-6">DANH SÁCH PHIẾU NHẬP</h2>

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
                @for(vn.edu.fpt.pharma.dto.requestform.RequestFormVM item : imports)
                    <tr class="border-t hover:bg-gray-50">
                        <td class="px-4 py-3 text-blue-600 font-semibold">${item.code}</td>
                        <td class="px-4 py-3">${item.type}</td>
                        <td class="px-4 py-3">${item.status}</td>
                        <td class="px-4 py-3">${item.note}</td>
                        <td class="px-4 py-3">${item.createdAt}</td>
                    </tr>
                @endfor
                </tbody>
            </table>
        </div>
    </main>
</div>
</body>
</html>
