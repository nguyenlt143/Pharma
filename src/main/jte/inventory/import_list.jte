@import gg.jte.Content
@param java.util.List<vn.edu.fpt.pharma.dto.requestform.RequestFormVM> requestForms

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh sách phiếu yêu cầu</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,typography"></script>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Outlined" rel="stylesheet">
</head>
<body class="bg-gray-100 font-display text-gray-900">
<div class="flex h-screen">

    <!-- Sidebar -->
    <aside class="w-64 bg-[#0F172A] text-white flex flex-col">
        <div class="p-4 border-b border-gray-700">
            <h1 class="text-xl font-bold">QUẢN LÝ NHÀ THUỐC</h1>
        </div>
        <nav class="flex-1 mt-4">
            <ul class="space-y-1">
                <li class="px-4 py-2 hover:bg-gray-700 rounded-md">
                    <a class="flex items-center gap-2" href="/inventory/dashboard">
                        <span class="material-icons-outlined">dashboard</span>
                        <span>Tổng quan</span>
                    </a>
                </li>

                <li class="px-4 py-2 bg-blue-600 rounded-md">
                    <a class="flex items-center gap-2" href="/inventory/request-forms">
                        <span class="material-icons-outlined">inventory_2</span>
                        <span>Phiếu yêu cầu</span>
                    </a>
                </li>

                <li class="px-4 py-2 hover:bg-gray-700 rounded-md">
                    <a class="flex items-center gap-2" href="#">
                        <span class="material-icons-outlined">unarchive</span>
                        <span>Xuất kho</span>
                    </a>
                </li>

                <li class="px-4 py-2 hover:bg-gray-700 rounded-md">
                    <a class="flex items-center gap-2" href="#">
                        <span class="material-icons-outlined">checklist</span>
                        <span>Kiểm kho</span>
                    </a>
                </li>

                <li class="px-4 py-2 hover:bg-gray-700 rounded-md">
                    <a class="flex items-center gap-2" href="#">
                        <span class="material-icons-outlined">assessment</span>
                        <span>Báo cáo</span>
                    </a>
                </li>
            </ul>
        </nav>

        <div class="p-4 border-t border-gray-700">
            <button class="w-full bg-gray-700 hover:bg-gray-600 text-white font-bold py-2 px-4 rounded-md flex items-center justify-center">
                <span class="material-icons-outlined">logout</span>
                <span>Đăng xuất</span>
            </button>
        </div>
    </aside>

    <!-- Main -->
    <main class="flex-1 p-6 overflow-y-auto">
        <header class="flex justify-between items-center mb-6">
            <h2 class="text-2xl font-bold">Danh sách phiếu yêu cầu</h2>
            <button class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-md flex items-center gap-2">
                <span class="material-icons-outlined">add</span>
                Tạo phiếu yêu cầu mới
            </button>
        </header>

        <div class="bg-white shadow-md rounded-lg p-6">
            <table class="w-full table-auto">
                <thead class="bg-gray-50 text-left text-sm text-gray-500 uppercase">
                <tr>
                    <th class="px-4 py-3 font-medium">Mã phiếu</th>
                    <th class="px-4 py-3 font-medium">Loại phiếu</th>
                    <th class="px-4 py-3 font-medium">Trạng thái</th>
                    <th class="px-4 py-3 font-medium">Ghi chú</th>
                    <th class="px-4 py-3 font-medium">Ngày tạo</th>
                </tr>
                </thead>
                <tbody class="divide-y divide-gray-200">
                @for (vn.edu.fpt.pharma.dto.requestform.RequestFormVM form : requestForms) {
                    <tr class="hover:bg-gray-100 cursor-pointer">
                        <td class="px-4 py-3 text-blue-600 font-medium">${form.code()}</td>
                        <td class="px-4 py-3">${form.requestType()}</td>
                        <td class="px-4 py-3">${form.requestStatus()}</td>
                        <td class="px-4 py-3">${form.note()}</td>
                        <td class="px-4 py-3">${form.createdAt()}</td>
                    </tr>
                @endfor
                </tbody>
            </table>
        </div>
    </main>
</div>
</body>
</html>
