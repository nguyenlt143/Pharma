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

            <li class="px-4 py-2 rounded-md ${active == "expiring" ? "bg-blue-600" : "hover:bg-gray-700"}">
                <a href="/inventory/expiring_medicine" class="flex items-center gap-2">
                    <span class="material-icons-outlined">warning</span>
                    <span>Thuốc sắp hết hạn</span>
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
</aside>
