@param String active

<aside class="w-64 bg-[#0F172A] text-white flex flex-col">
    <div class="p-4 border-b border-gray-700">
        <h1 class="text-xl font-bold">PharmaSys</h1>
        <p class="text-xs text-gray-400">Quản lý kho chi nhánh</p>
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
                    <span>Yêu cầu nhập hàng</span>
                </a>
            </li>

            <li class="px-4 py-2 rounded-md ${active == "confirm" ? "bg-blue-600" : "hover:bg-gray-700"}">
                <a href="/inventory/confirm/list" class="flex items-center gap-2">
                    <span class="material-icons-outlined">check_circle</span>
                    <span>Danh sách nhập kho</span>
                </a>
            </li>

            <li class="px-4 py-2 rounded-md ${active == "export" ? "bg-blue-600" : "hover:bg-gray-700"}">
                <a href="/inventory/return/list" class="flex items-center gap-2">
                    <span class="material-icons-outlined">assignment_return</span>
                    <span>Danh sách phiếu trả hàng</span>
                </a>
            </li>

            <li class="px-4 py-2 rounded-md ${active == "check" ? "bg-blue-600" : "hover:bg-gray-700"}">
                <a href="/inventory/check" class="flex items-center gap-2">
                    <span class="material-icons-outlined">fact_check</span>
                    <span>Kiểm kho</span>
                </a>
            </li>

            <!--
            <li class="px-4 py-2 rounded-md ${active == "report" ? "bg-blue-600" : "hover:bg-gray-700"}">
                <a href="/inventory/report" class="flex items-center gap-2">
                    <span class="material-icons-outlined">bar_chart</span>
                    <span>Báo cáo</span>
                </a>
            </li>
            -->
        </ul>
    </nav>

    <!-- Logout Section -->
    <div class="p-4 border-t border-gray-700">
        <form action="/logout" method="post" class="w-full">
            <button type="submit" class=" text-white px-4 py-2 rounded-md flex items-left justify-center gap-2 transition-colors">
                <span class="material-icons-outlined">logout</span>
                <span>Đăng xuất</span>
            </button>
        </form>
    </div>
</aside>
