<!DOCTYPE html>
<html lang="vi"><head>
    <meta charset="utf-8"/>
    <meta content="width=device-width, initial-scale=1.0" name="viewport"/>
    <title>Tạo phiếu xuất kho</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,typography"></script>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&amp;display=swap" rel="stylesheet"/>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Outlined" rel="stylesheet"/>
    <script>
        tailwind.config = {
            darkMode: "class",
            theme: {
                extend: {
                    colors: {
                        primary: "#2563EB",
                        "background-light": "#F8FAFC",
                        "background-dark": "#1E293B",
                        "card-light": "#FFFFFF",
                        "card-dark": "#334155",
                        "text-light": "#1E293B",
                        "text-dark": "#E2E8F0",
                        "text-secondary-light": "#64748B",
                        "text-secondary-dark": "#94A3B8",
                        "border-light": "#E2E8F0",
                        "border-dark": "#475569",
                        "success": "#22C55E",
                        "danger": "#EF4444"
                    },
                    fontFamily: {
                        display: ["Roboto", "sans-serif"],
                    },
                    borderRadius: {
                        DEFAULT: "0.5rem",
                    },
                },
            },
        };
    </script>
</head>
<body class="bg-background-light dark:bg-background-dark font-display text-text-light dark:text-text-dark">
<div class="flex h-screen">
    <aside class="w-64 bg-[#0F172A] text-white flex flex-col">
        <div class="p-4 border-b border-gray-700 flex items-center gap-4">
            <span class="material-icons-outlined">inventory_2</span>
            <h1 class="text-xl font-bold">Quản lý kho</h1>
        </div>
        <nav class="flex-1 mt-4">
            <ul>
                <li class="px-4 py-2 hover:bg-gray-700 rounded-md">
                    <a class="flex items-center gap-2" href="#">
                        <span class="material-icons-outlined">dashboard</span>
                        Tổng quan
                    </a>
                </li>
                <li class="px-4 py-2 hover:bg-gray-700 rounded-md">
                    <a class="flex items-center gap-2" href="#">
                        <span class="material-icons-outlined">receipt_long</span>
                        Phiếu nhập kho
                    </a>
                </li>
                <li class="px-4 py-2 bg-primary rounded-md">
                    <a class="flex items-center gap-2" href="#">
                        <span class="material-icons-outlined">outbox</span>
                        Phiếu xuất / trả / cấp phát
                    </a>
                </li>
                <li class="px-4 py-2 hover:bg-gray-700 rounded-md">
                    <a class="flex items-center gap-2" href="#">
                        <span class="material-icons-outlined">inventory</span>
                        Kiểm kho
                    </a>
                </li>
                <li class="px-4 py-2 hover:bg-gray-700 rounded-md">
                    <a class="flex items-center gap-2" href="#">
                        <span class="material-icons-outlined">assessment</span>
                        Báo cáo
                    </a>
                </li>
                <li class="px-4 py-2 hover:bg-gray-700 rounded-md">
                    <a class="flex items-center gap-2" href="#">
                        <span class="material-icons-outlined">settings</span>
                        Cài đặt
                    </a>
                </li>
            </ul>
        </nav>
        <div class="p-4 border-t border-gray-700">
            <button class="w-full bg-gray-700 hover:bg-gray-600 text-white font-bold py-2 px-4 rounded-md flex items-center justify-center gap-2">
                <span class="material-icons-outlined">arrow_back</span>
                <span>Quay lại</span>
            </button>
        </div>
    </aside>
    <main class="flex-1 p-6 overflow-y-auto">
        <header class="flex justify-between items-center mb-6">
            <h2 class="text-2xl font-bold text-text-light dark:text-text-dark">Tạo phiếu xuất / trả / cấp phát</h2>
            <div class="flex items-center gap-4">
                <span class="text-sm">Nhà Thuốc Chi nhánh DEMO</span>
                <span class="material-icons-outlined">expand_more</span>
            </div>
        </header>
        <div class="bg-card-light dark:bg-card-dark p-6 rounded-lg shadow-md">
            <form>
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div>
                        <label class="block text-sm font-medium text-text-secondary-light dark:text-text-secondary-dark mb-1" for="issue-type">Loại phiếu</label>
                        <select class="w-full bg-background-light dark:bg-background-dark border border-border-light dark:border-border-dark rounded-md shadow-sm p-2 focus:ring-primary focus:border-primary" id="issue-type" name="issue-type">
                            <option value="return">Xuất trả kho tổng</option>
                            <option value="internal">Xuất cấp phát nội bộ</option>
                        </select>
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-text-secondary-light dark:text-text-secondary-dark mb-1" for="reason">Lý do xuất</label>
                        <input class="w-full bg-background-light dark:bg-background-dark border border-border-light dark:border-border-dark rounded-md shadow-sm p-2 focus:ring-primary focus:border-primary" id="reason" name="reason" placeholder="Ví dụ: Trả hàng cận date, cấp phát cho tủ trực" type="text"/>
                    </div>
                </div>
                <div class="mt-8">
                    <h3 class="text-lg font-semibold text-text-light dark:text-text-dark mb-4">Danh sách sản phẩm</h3>
                    <div class="relative">
                        <span class="material-icons-outlined absolute top-2.5 left-3 text-text-secondary-light dark:text-text-secondary-dark">search</span>
                        <input class="w-full bg-background-light dark:bg-background-dark border border-border-light dark:border-border-dark rounded-md shadow-sm pl-10 p-2 mb-4 focus:ring-primary focus:border-primary" placeholder="Tìm kiếm thuốc theo tên hoặc mã..." type="text"/>
                    </div>
                    <div class="border border-border-light dark:border-border-dark rounded-lg overflow-hidden">
                        <table class="w-full">
                            <thead class="bg-background-light dark:bg-background-dark border-b border-border-light dark:border-border-dark">
                            <tr>
                                <th class="p-3 text-left text-sm font-semibold text-text-secondary-light dark:text-text-secondary-dark tracking-wider w-12">STT</th>
                                <th class="p-3 text-left text-sm font-semibold text-text-secondary-light dark:text-text-secondary-dark tracking-wider">Tên sản phẩm</th>
                                <th class="p-3 text-left text-sm font-semibold text-text-secondary-light dark:text-text-secondary-dark tracking-wider">ĐVT</th>
                                <th class="p-3 text-left text-sm font-semibold text-text-secondary-light dark:text-text-secondary-dark tracking-wider">Tồn kho</th>
                                <th class="p-3 text-left text-sm font-semibold text-text-secondary-light dark:text-text-secondary-dark tracking-wider">Số lượng xuất</th>
                                <th class="p-3 text-center text-sm font-semibold text-text-secondary-light dark:text-text-secondary-dark tracking-wider"></th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr class="border-b border-border-light dark:border-border-dark">
                                <td class="p-3 text-center text-text-secondary-light dark:text-text-secondary-dark">1</td>
                                <td class="p-3">
                                    <p class="font-medium text-text-light dark:text-text-dark">Panadol Extra</p>
                                    <p class="text-sm text-text-secondary-light dark:text-text-secondary-dark">PND123</p>
                                </td>
                                <td class="p-3 text-text-secondary-light dark:text-text-secondary-dark">
                                    <p>Vỉ</p>
                                    <p class="text-xs">(10 viên)</p>
                                </td>
                                <td class="p-3 text-text-secondary-light dark:text-text-secondary-dark">150</td>
                                <td class="p-3">
                                    <input class="w-24 bg-background-light dark:bg-background-dark border border-border-light dark:border-border-dark rounded-md shadow-sm p-1.5 focus:ring-primary focus:border-primary" type="number" value="10"/>
                                </td>
                                <td class="p-3 text-center">
                                    <button class="text-danger hover:text-red-700" type="button">
                                        <span class="material-icons-outlined">delete</span>
                                    </button>
                                </td>
                            </tr>
                            <tr class="border-b border-border-light dark:border-border-dark">
                                <td class="p-3 text-center text-text-secondary-light dark:text-text-secondary-dark">2</td>
                                <td class="p-3">
                                    <p class="font-medium text-text-light dark:text-text-dark">Decolgen Forte</p>
                                    <p class="text-sm text-text-secondary-light dark:text-text-secondary-dark">DCG456</p>
                                </td>
                                <td class="p-3 text-text-secondary-light dark:text-text-secondary-dark">
                                    <p>Hộp</p>
                                    <p class="text-xs">(20 vỉ)</p>
                                </td>
                                <td class="p-3 text-text-secondary-light dark:text-text-secondary-dark">80</td>
                                <td class="p-3">
                                    <input class="w-24 bg-background-light dark:bg-background-dark border border-border-light dark:border-border-dark rounded-md shadow-sm p-1.5 focus:ring-primary focus:border-primary" type="number" value="5"/>
                                </td>
                                <td class="p-3 text-center">
                                    <button class="text-danger hover:text-red-700" type="button">
                                        <span class="material-icons-outlined">delete</span>
                                    </button>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                    <div class="text-center text-text-secondary-light dark:text-text-secondary-dark py-10 hidden">
                        <span class="material-icons-outlined text-6xl opacity-50">medication</span>
                        <p class="mt-4 text-lg">Chưa có sản phẩm nào trong phiếu.</p>
                        <p>Sử dụng thanh tìm kiếm ở trên để thêm sản phẩm.</p>
                    </div>
                </div>
                <div class="mt-8 flex justify-end gap-4">
                    <button class="bg-card-light dark:bg-card-dark hover:bg-gray-100 dark:hover:bg-slate-600 text-text-light dark:text-text-dark font-bold py-2 px-4 rounded-lg border border-border-light dark:border-border-dark" type="button">
                        Hủy
                    </button>
                    <button class="bg-primary hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-lg flex items-center gap-2" type="submit">
                        <span class="material-icons-outlined">send</span>
                        Tạo phiếu
                    </button>
                </div>
            </form>
        </div>
    </main>
</div>

</body></html>