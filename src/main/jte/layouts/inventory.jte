@param String active

<aside class="w-64 bg-[#0F172A] text-white flex flex-col">
    <div class="p-4 border-b border-gray-700">
        <h1 class="text-xl font-bold">QUẢN LÝ NHÀ THUỐC</h1>
    </div>

    <nav class="flex-1 mt-4">
        <ul class="space-y-1">
            <li class="px-4 py-2 rounded-md ${active == "dashboard" ? "bg-blue-600" : "hover:bg-gray-700"}">
                <a href="/inventory/dashboard" class="flex items-center gap-2">
                    <span class="material-icons-outlined">dashboard</span>
                    <span>Tổng quan</span>
                </a>
            </li>

            <li class="px-4 py-2 rounded-md ${active == "medicine" ? "bg-blue-600" : "hover:bg-gray-700"}">
                <a href="/inventory/medicine/list" class="flex items-center gap-2">
                    <span class="material-icons-outlined">medication</span>
                    <span>Danh sách thuốc</span>
                </a>
            </li>

            <li class="px-4 py-2 rounded-md ${active == "import" ? "bg-blue-600" : "hover:bg-gray-700"}">
                <a href="/inventory/import/list" class="flex items-center gap-2">
                    <span class="material-icons-outlined">inventory_2</span>
                    <span>Nhập kho</span>
                </a>
            </li>

            <li class="px-4 py-2 rounded-md ${active == "export" ? "bg-blue-600" : "hover:bg-gray-700"}">
                <a href="/inventory/export/list" class="flex items-center gap-2">
                    <span class="material-icons-outlined">exit_to_app</span>
                    <span>Xuất kho</span>
                </a>
            </li>

            <li class="px-4 py-2 rounded-md ${active == "check" ? "bg-blue-600" : "hover:bg-gray-700"}">
                <a href="/inventory/check" class="flex items-center gap-2">
                    <span class="material-icons-outlined">fact_check</span>
                    <span>Kiểm kho</span>
                </a>
            </li>

            <li class="px-4 py-2 rounded-md ${active == "confirm" ? "bg-blue-600" : "hover:bg-gray-700"}">
                <a href="/inventory/confirm" class="flex items-center gap-2">
                    <span class="material-icons-outlined">task_alt</span>
                    <span>Xác nhận nhập</span>
                </a>
            </li>

            <li class="px-4 py-2 rounded-md ${active == "report" ? "bg-blue-600" : "hover:bg-gray-700"}">
                <a href="/inventory/report" class="flex items-center gap-2">
                    <span class="material-icons-outlined">bar_chart</span>
                    <span>Báo cáo</span>
                </a>
            </li>
        </ul>
    </nav>

    <!-- Logout Section -->
    <div class="p-4 border-t border-gray-700">
        <div class="flex items-center gap-3 mb-3">
            <div class="w-10 h-10 bg-blue-500 rounded-full flex items-center justify-center">
                <span class="material-icons-outlined">person</span>
            </div>
            <div class="flex-1">
                <p class="text-sm font-semibold">Người dùng</p>
                <p class="text-xs text-gray-400">Inventory</p>
            </div>
        </div>
        <form action="/logout" method="post" class="w-full">
            <button type="submit" class="w-full bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-md flex items-center justify-center gap-2 transition-colors">
                <span class="material-icons-outlined">logout</span>
                <span>Đăng xuất</span>
            </button>
        </form>
    </div>
</aside>
