@param int pendingRequests
@param int shippedOrders
@param int nearlyExpiredCount
@param int expiredCount

@template.layouts.warehouse(
    content = @`
        <div class="page-header">
            <h1 class="page-title">TỔNG QUAN KHO TỔNG</h1>
        </div>

        <!-- Summary Cards -->
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-10">
            <!-- Pending Requests -->
            <div class="bg-white shadow-md rounded-lg p-6 flex items-center justify-between hover:shadow-lg transition-shadow">
                <div>
                    <p class="text-gray-500 text-sm mb-1">Số yêu cầu cần xác nhận</p>
                    <p class="text-3xl font-bold text-blue-600">${pendingRequests}</p>
                </div>
                <span class="material-icons text-blue-500 text-5xl">pending_actions</span>
            </div>

            <!-- Shipped Orders -->
            <div class="bg-white shadow-md rounded-lg p-6 flex items-center justify-between hover:shadow-lg transition-shadow">
                <div>
                    <p class="text-gray-500 text-sm mb-1">Số đơn đang giao</p>
                    <p class="text-3xl font-bold text-green-600">${shippedOrders}</p>
                </div>
                <span class="material-icons text-green-500 text-5xl">local_shipping</span>
            </div>

            <!-- Nearly Expired -->
            <div class="bg-white shadow-md rounded-lg p-6 flex items-center justify-between hover:shadow-lg transition-shadow">
                <div>
                    <p class="text-gray-500 text-sm mb-1">Thuốc sắp hết hạn</p>
                    <p class="text-sm text-gray-400 mb-2">(trong 90 ngày)</p>
                    <p class="text-3xl font-bold text-orange-600">${nearlyExpiredCount}</p>
                </div>
                <span class="material-icons text-orange-500 text-5xl">warning</span>
            </div>

            <!-- Expired -->
            <div class="bg-white shadow-md rounded-lg p-6 flex items-center justify-between hover:shadow-lg transition-shadow">
                <div>
                    <p class="text-gray-500 text-sm mb-1">Thuốc đã hết hạn</p>
                    <p class="text-sm text-gray-400 mb-2">(cần xử lý)</p>
                    <p class="text-3xl font-bold text-red-600">${expiredCount}</p>
                </div>
                <span class="material-icons text-red-500 text-5xl">dangerous</span>
            </div>
        </div>

        <!-- Quick Actions -->
        <section>
            <h3 class="text-xl font-semibold mb-6">Truy cập nhanh</h3>

            <!-- Group 1: Phiếu -->
            <div class="mb-8">
                <h4 class="text-lg font-medium text-gray-700 mb-4 flex items-center">
                    <span class="material-icons text-blue-600 mr-2">receipt_long</span>
                    Phiếu
                </h4>
                <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                    <a href="/warehouse/receipt-list"
                       class="bg-blue-600 hover:bg-blue-700 text-white font-semibold py-4 px-6 rounded-lg flex items-center justify-center gap-2 shadow-md transform hover:scale-105 transition">
                        <span class="material-icons">list</span>
                        Danh sách phiếu
                    </a>
                    <a href="/warehouse/receipt/create"
                       class="bg-blue-600 hover:bg-blue-700 text-white font-semibold py-4 px-6 rounded-lg flex items-center justify-center gap-2 shadow-md transform hover:scale-105 transition">
                        <span class="material-icons">add_circle_outline</span>
                        Tạo phiếu nhập
                    </a>
                    <a href="/warehouse/check"
                       class="bg-blue-600 hover:bg-blue-700 text-white font-semibold py-4 px-6 rounded-lg flex items-center justify-center gap-2 shadow-md transform hover:scale-105 transition">
                        <span class="material-icons">fact_check</span>
                        Kiểm kho
                    </a>
                    <a href="/warehouse/disposal/create"
                       class="bg-blue-600 hover:bg-blue-700 text-white font-semibold py-4 px-6 rounded-lg flex items-center justify-center gap-2 shadow-md transform hover:scale-105 transition">
                        <span class="material-icons">delete_forever</span>
                        Xuất hủy
                    </a>
                </div>
            </div>

            <!-- Group 2: Yêu cầu & Kho -->
            <div class="mb-8">
                <h4 class="text-lg font-medium text-gray-700 mb-4 flex items-center">
                    <span class="material-icons text-green-600 mr-2">inventory_2</span>
                    Yêu cầu & Kho
                </h4>
                <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                    <a href="/warehouse/request/list"
                       class="bg-green-600 hover:bg-green-700 text-white font-semibold py-4 px-6 rounded-lg flex items-center justify-center gap-2 shadow-md transform hover:scale-105 transition">
                        <span class="material-icons">list_alt</span>
                        Danh sách yêu cầu
                    </a>
                    <a href="/warehouse/inventory"
                       class="bg-green-600 hover:bg-green-700 text-white font-semibold py-4 px-6 rounded-lg flex items-center justify-center gap-2 shadow-md transform hover:scale-105 transition">
                        <span class="material-icons">inventory</span>
                        Thuốc tồn kho
                    </a>
                </div>
            </div>

            <!-- Group 3: Quản lý -->
            <div class="mb-8">
                <h4 class="text-lg font-medium text-gray-700 mb-4 flex items-center">
                    <span class="material-icons text-purple-600 mr-2">settings</span>
                    Quản lý
                </h4>
                <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                    <a href="/warehouse/medicine"
                       class="bg-purple-600 hover:bg-purple-700 text-white font-semibold py-4 px-6 rounded-lg flex items-center justify-center gap-2 shadow-md transform hover:scale-105 transition">
                        <span class="material-icons">medication</span>
                        Quản lý thuốc
                    </a>
                    <a href="/warehouse/category"
                       class="bg-purple-600 hover:bg-purple-700 text-white font-semibold py-4 px-6 rounded-lg flex items-center justify-center gap-2 shadow-md transform hover:scale-105 transition">
                        <span class="material-icons">category</span>
                        Quản lý danh mục
                    </a>
                    <a href="/warehouse/price"
                       class="bg-purple-600 hover:bg-purple-700 text-white font-semibold py-4 px-6 rounded-lg flex items-center justify-center gap-2 shadow-md transform hover:scale-105 transition">
                        <span class="material-icons">attach_money</span>
                        Quản lý giá
                    </a>
                </div>
            </div>
        </section>
    `,
    headContent = @`
        <script src="https://cdn.tailwindcss.com"></script>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
        <style>
            body {
                font-family: 'Inter', sans-serif;
            }
            .page-title {
                color: #1f2937;
                margin-bottom: 2rem;
            }
        </style>
    `,
    pageScript = @`
        <script>
            console.log('Warehouse Dashboard loaded');
        </script>
    `
)

