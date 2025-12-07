<!DOCTYPE html>
<html lang="vi"><head>
    <meta charset="utf-8"/>
    <meta content="width=device-width, initial-scale=1.0" name="viewport"/>
    <title>Thuốc sắp hết hàng</title>
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
                        "warning": "#F59E0B",
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
        <div class="p-4 border-b border-gray-700">
            <h1 class="text-xl font-bold">QUẢN LÝ NHÀ THUỐC</h1>
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
                        <span class="material-icons-outlined">point_of_sale</span>
                        Bán hàng
                    </a>
                </li>
                <li class="px-4 py-2 bg-primary rounded-md">
                    <a class="flex items-center gap-2" href="#">
                        <span class="material-icons-outlined">inventory_2</span>
                        Quản lý danh sách đơn thuốc
                    </a>
                </li>
                <li class="px-4 py-2 hover:bg-gray-700 rounded-md">
                    <a class="flex items-center gap-2" href="#">
                        <span class="material-icons-outlined">assessment</span>
                        Tổng kết cơ
                    </a>
                </li>
                <li class="px-4 py-2 hover:bg-gray-700 rounded-md">
                    <a class="flex items-center gap-2" href="#">
                        <span class="material-icons-outlined">work</span>
                        Weekly work
                    </a>
                </li>
                <li class="px-4 py-2 hover:bg-gray-700 rounded-md">
                    <a class="flex items-center gap-2" href="#">
                        <span class="material-icons-outlined">trending_up</span>
                        Báo cáo danh thu
                    </a>
                </li>
            </ul>
        </nav>
        <div class="p-4">
            <button class="w-full bg-gray-700 hover:bg-gray-600 text-white font-bold py-2 px-4 rounded-md flex items-center justify-center">
                <span class="material-icons-outlined">arrow_back</span>
            </button>
        </div>
    </aside>
    <main class="flex-1 p-6 overflow-y-auto">
        <header class="flex justify-between items-center mb-6">
            <div>
                <h2 class="text-2xl font-bold text-text-light dark:text-text-dark">Thuốc sắp hết hàng</h2>
                <p class="text-text-secondary-light dark:text-text-secondary-dark">Chọn thuốc để tạo phiếu xuất trả về kho tổng.</p>
            </div>
            <div class="flex items-center gap-4">
                <span class="text-sm">Nhà Thuốc Chi nhánh DEMO</span>
                <span class="material-icons-outlined">expand_more</span>
            </div>
        </header>
        <div class="bg-card-light dark:bg-card-dark p-6 rounded-lg shadow-md">
            <div class="flex justify-between items-center mb-4">
                <div class="flex items-center gap-4">
                    <div class="relative">
                        <span class="material-icons-outlined absolute left-3 top-1/2 -translate-y-1/2 text-text-secondary-light">search</span>
                        <input class="pl-10 pr-4 py-2 border border-border-light dark:border-border-dark rounded-md bg-card-light dark:bg-card-dark focus:outline-none focus:ring-2 focus:ring-primary" placeholder="Tìm kiếm thuốc..." maxlength="150" type="text"/>
                    </div>
                    <button class="flex items-center gap-2 px-4 py-2 border border-border-light dark:border-border-dark rounded-md hover:bg-gray-50 dark:hover:bg-slate-700">
                        <span class="material-icons-outlined text-text-secondary-light dark:text-text-secondary-dark">filter_alt</span>
                        Bộ lọc
                    </button>
                </div>
                <button class="bg-primary hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-md flex items-center gap-2 disabled:bg-gray-400 disabled:cursor-not-allowed" disabled="">
                    <span class="material-icons-outlined">unarchive</span>
                    Tạo phiếu xuất trả (0)
                </button>
            </div>
            <div class="overflow-x-auto">
                <table class="w-full text-left">
                    <thead>
                    <tr class="bg-gray-50 dark:bg-slate-700">
                        <th class="p-4 w-12"><input class="form-checkbox" type="checkbox"/></th>
                        <th class="p-4 font-semibold">Tên thuốc</th>
                        <th class="p-4 font-semibold">Mã thuốc</th>
                        <th class="p-4 font-semibold text-right">Tồn kho</th>
                        <th class="p-4 font-semibold text-right">Mức tồn kho tối thiểu</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr class="border-b border-border-light dark:border-border-dark">
                        <td class="p-4"><input class="form-checkbox" type="checkbox"/></td>
                        <td class="p-4">
                            <p class="font-medium">Paracetamol 500mg</p>
                            <p class="text-sm text-text-secondary-light dark:text-text-secondary-dark">Hộp 10 vỉ x 10 viên</p>
                        </td>
                        <td class="p-4 text-text-secondary-light dark:text-text-secondary-dark">PARA500</td>
                        <td class="p-4 text-right font-medium text-warning">8</td>
                        <td class="p-4 text-right text-text-secondary-light dark:text-text-secondary-dark">10</td>
                    </tr>
                    <tr class="border-b border-border-light dark:border-border-dark">
                        <td class="p-4"><input class="form-checkbox" type="checkbox"/></td>
                        <td class="p-4">
                            <p class="font-medium">Amoxicillin 250mg</p>
                            <p class="text-sm text-text-secondary-light dark:text-text-secondary-dark">Chai 60ml</p>
                        </td>
                        <td class="p-4 text-text-secondary-light dark:text-text-secondary-dark">AMOX250</td>
                        <td class="p-4 text-right font-medium text-warning">5</td>
                        <td class="p-4 text-right text-text-secondary-light dark:text-text-secondary-dark">5</td>
                    </tr>
                    <tr class="border-b border-border-light dark:border-border-dark">
                        <td class="p-4"><input class="form-checkbox" type="checkbox"/></td>
                        <td class="p-4">
                            <p class="font-medium">Loratadin 10mg</p>
                            <p class="text-sm text-text-secondary-light dark:text-text-secondary-dark">Hộp 1 vỉ x 10 viên</p>
                        </td>
                        <td class="p-4 text-text-secondary-light dark:text-text-secondary-dark">LORA10</td>
                        <td class="p-4 text-right font-medium text-warning">12</td>
                        <td class="p-4 text-right text-text-secondary-light dark:text-text-secondary-dark">15</td>
                    </tr>
                    <tr class="border-b border-border-light dark:border-border-dark">
                        <td class="p-4"><input class="form-checkbox" type="checkbox"/></td>
                        <td class="p-4">
                            <p class="font-medium">Vitamin C 500mg</p>
                            <p class="text-sm text-text-secondary-light dark:text-text-secondary-dark">Tuýp 20 viên sủi</p>
                        </td>
                        <td class="p-4 text-text-secondary-light dark:text-text-secondary-dark">VITC500</td>
                        <td class="p-4 text-right font-medium text-warning">3</td>
                        <td class="p-4 text-right text-text-secondary-light dark:text-text-secondary-dark">5</td>
                    </tr>
                    <tr class="border-b border-border-light dark:border-border-dark">
                        <td class="p-4"><input class="form-checkbox" type="checkbox"/></td>
                        <td class="p-4">
                            <p class="font-medium">Berberin 100mg</p>
                            <p class="text-sm text-text-secondary-light dark:text-text-secondary-dark">Lọ 100 viên</p>
                        </td>
                        <td class="p-4 text-text-secondary-light dark:text-text-secondary-dark">BERB100</td>
                        <td class="p-4 text-right font-medium text-warning">18</td>
                        <td class="p-4 text-right text-text-secondary-light dark:text-text-secondary-dark">20</td>
                    </tr>
                    </tbody>
                </table>
            </div>
            <div class="flex justify-between items-center mt-4">
                <span class="text-sm text-text-secondary-light dark:text-text-secondary-dark">Hiển thị 1-5 trên 8 kết quả</span>
                <div class="flex gap-1">
                    <button class="px-3 py-1 border rounded-md bg-gray-200 dark:bg-slate-600">
                        <span class="material-icons-outlined text-base">navigate_before</span>
                    </button>
                    <button class="px-3 py-1 border rounded-md bg-primary text-white">1</button>
                    <button class="px-3 py-1 border rounded-md hover:bg-gray-100 dark:hover:bg-slate-700">2</button>
                    <button class="px-3 py-1 border rounded-md hover:bg-gray-100 dark:hover:bg-slate-700">
                        <span class="material-icons-outlined text-base">navigate_next</span>
                    </button>
                </div>
            </div>
        </div>
    </main>
</div>
</body></html>