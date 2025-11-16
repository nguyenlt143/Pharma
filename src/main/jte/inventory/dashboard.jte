@import gg.jte.Content
@param int waitingOrders
@param String lastInventoryCheck
@param int nearlyExpiredCount
@param int lowStockCount

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tổng quan kho chi nhánh</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,typography"></script>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Outlined" rel="stylesheet">
</head>
<body class="bg-gray-100 font-display text-gray-900">
<div class="flex h-screen">

    <aside class="w-64 bg-[#0F172A] text-white flex flex-col">
        <div class="p-4 border-b border-gray-700">
            <h1 class="text-xl font-bold">QUẢN LÝ NHÀ THUỐC</h1>
        </div>
        <nav class="flex-1 mt-4">
            <ul class="space-y-1">
                <li class="px-4 py-2 bg-blue-600 hover:bg-blue-700 rounded-md">
                    <a class="flex items-center gap-2" href="/inventory/dashboard">
                        <span class="material-icons-outlined">dashboard</span>
                        <span>Tổng quan</span>
                    </a>
                </li>
                <li class="px-4 py-2 hover:bg-gray-700 rounded-md">
                    <a class="flex items-center gap-2" href="/inventory/import_list">
                        <span class="material-icons-outlined">inventory_2</span>
                        <span>Nhập kho</span>
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

    <main class="flex-1 p-6 overflow-y-auto">
        <h2 class="text-2xl font-bold mb-6">TỔNG QUAN KHO CHI NHÁNH</h2>

        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-10">
            <div class="bg-white shadow-md rounded-lg p-6 flex items-center justify-between">
                <div>
                    <p class="text-gray-500">Số đơn nhập chờ xác nhận</p>
                    <p class="text-3xl font-bold text-blue-600">${waitingOrders}</p>
                </div>
                <span class="material-icons-outlined text-blue-500 text-4xl">pending_actions</span>
            </div>

            <div class="bg-white shadow-md rounded-lg p-6 flex items-center justify-between">
                <div>
                    <p class="text-gray-500">Thời gian lần cuối kiểm kho</p>
                    <p class="text-3xl font-bold text-green-600">${lastInventoryCheck}</p>
                </div>
                <span class="material-icons-outlined text-green-500 text-4xl">event_available</span>
            </div>

            <div class="bg-white shadow-md rounded-lg p-6 flex items-center justify-between">
                <div>
                    <p class="text-gray-500">Thuốc sắp hết hạn</p>
                    <p class="text-3xl font-bold text-red-600">${nearlyExpiredCount}</p>
                </div>
                <span class="material-icons-outlined text-red-500 text-4xl">warning</span>
            </div>

            <div class="bg-white shadow-md rounded-lg p-6 flex items-center justify-between">
                <div>
                    <p class="text-gray-500">Thuốc sắp hết hàng</p>
                    <p class="text-3xl font-bold text-yellow-500">${lowStockCount}</p>
                </div>
                <span class="material-icons-outlined text-yellow-500 text-4xl">production_quantity_limits</span>
            </div>
        </div>

        <section>
            <h3 class="text-xl font-semibold mb-4">Truy cập nhanh</h3>
            <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-6">
                <a href="#" class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-4 px-6 rounded-lg flex items-center justify-center gap-2 shadow-md transform hover:scale-105 transition">
                    <span class="material-icons-outlined">add_circle_outline</span>
                    Tạo phiếu nhập mới
                </a>
                <a href="#" class="bg-yellow-500 hover:bg-yellow-600 text-white font-bold py-4 px-6 rounded-lg flex items-center justify-center gap-2 shadow-md transform hover:scale-105 transition">
                    <span class="material-icons-outlined">unarchive</span>
                    Tạo phiếu xuất kho
                </a>
                <a href="#" class="bg-green-600 hover:bg-green-700 text-white font-bold py-4 px-6 rounded-lg flex items-center justify-center gap-2 shadow-md transform hover:scale-105 transition">
                    <span class="material-icons-outlined">checklist</span>
                    Tạo phiếu kiểm kho
                </a>
                <a href="#" class="bg-purple-600 hover:bg-purple-700 text-white font-bold py-4 px-6 rounded-lg flex items-center justify-center gap-2 shadow-md transform hover:scale-105 transition">
                    <span class="material-icons-outlined">assessment</span>
                    Báo cáo tồn kho
                </a>
            </div>
        </section>
    </main>
</div>
</body>
</html>
