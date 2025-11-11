<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý thuốc</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,typography"></script>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="/assets/css/owner/layout.css">
</head>

<body class="font-[Inter] bg-gray-50 text-gray-800">
<div class="flex min-h-screen">
    <!-- Sidebar -->
    <aside class="sidebar">
        <div class="flex flex-col h-full justify-between">
            <div>
                <div class="p-4 border-b border-gray-700">
                    <h1 class="text-lg font-semibold text-white">PharmacyS</h1>
                    <p class="text-sm text-gray-300">Quản trị viên</p>
                </div>
                <nav class="mt-4 flex flex-col">
                    <a href="#" class="nav-item active">🏠 Quản lý thuốc</a>
                    <a href="#" class="nav-item">👥 Bảng giá </a>
                    <a href="#" class="nav-item">🚚 Nhà cung cấp</a>
                    <a href="#" class="nav-item">📦 Danh mục</a>
                    <a href="#" class="nav-item">📊 Báo cáo</a>
                </nav>
            </div>

            <div class="border-t border-gray-700 p-4 flex flex-col gap-2">
                <a href="#" class="nav-item">⚙️ Cài đặt</a>
                <a href="#" class="nav-item">↩️ Đăng xuất</a>
            </div>
        </div>
    </aside>

    <!-- Main content -->
    <main class="flex-1 p-8">
        <div class="max-w-6xl mx-auto">
            <div class="mb-6">
                <h1 class="text-2xl font-bold">Bảng giá </h1>
            </div>

            <div class="flex items-center justify-between mb-6">
                <div class="relative w-1/3">
                    <input type="text" placeholder="Tìm kiếm theo giá..." class="w-full border border-gray-300 rounded-lg py-2 px-3 focus:ring-2 focus:ring-blue-500 focus:outline-none">
                    <span class="absolute right-3 top-2.5 text-gray-400"></span>
                </div>
                <button class="flex items-center bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium">
                    ➕ Thêm bảng giá mới
                </button>
            </div>

            <div class="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
                <table class="w-full text-left border-collapse">
                    <thead class="bg-gray-100">
                    <tr>
                        <th class="py-3 px-4 font-semibold text-gray-700">Tên bảng giá </th>
                        <th class="py-3 px-4 font-semibold text-gray-700">Giá chi nhánh </th>
                        <th class="py-3 px-4 font-semibold text-gray-700">Giá khuyến mãi </th>
                        <th class="py-3 px-4 font-semibold text-gray-700">Thời hạn</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr class="border-t hover:bg-gray-50">
                        <td class="py-3 px-4">ABC</td>
                        <td class="py-3 px-4">30.000 VNĐ</td>
                        <td class="py-3 px-4">20.000</td>
                        <td class="py-3 px-4">20/12/2025</td>
                        <td class="py-3 px-4 text-right text-blue-600 font-medium cursor-pointer">Cập nhật</td>
                        <td class="py-3 px-4 text-right text-blue-600 font-medium cursor-pointer">Xem chi tiet</td>

                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </main>
</div>
</body>
</html>
