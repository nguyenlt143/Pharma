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

    <!-- SIDEBAR -->
    @template.layouts.inventory(active = "dashboard")

    <!-- MAIN CONTENT -->
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
            <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
                <a href="/inventory/import/create" class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-4 px-6 rounded-lg flex items-center justify-center gap-2 shadow-md transform hover:scale-105 transition">
                    <span class="material-icons-outlined">add_circle_outline</span>
                    Tạo phiếu nhập
                </a>
                <a href="/inventory/return/create" class="bg-orange-600 hover:bg-orange-700 text-white font-bold py-4 px-6 rounded-lg flex items-center justify-center gap-2 shadow-md transform hover:scale-105 transition">
                    <span class="material-icons-outlined">assignment_return</span>
                    Tạo phiếu trả hàng
                </a>
                <a href="/inventory/check/create" class="bg-green-600 hover:bg-green-700 text-white font-bold py-4 px-6 rounded-lg flex items-center justify-center gap-2 shadow-md transform hover:scale-105 transition">
                    <span class="material-icons-outlined">checklist</span>
                    Tạo phiếu kiểm kho
                </a>
            </div>
        </section>
    </main>
</div>
</body>
</html>
