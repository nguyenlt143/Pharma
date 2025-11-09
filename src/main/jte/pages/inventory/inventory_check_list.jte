<!DOCTYPE html>
<html lang="vi"><head>
    <meta charset="utf-8"/>
    <meta content="width=device-width, initial-scale=1.0" name="viewport"/>
    <title>Danh sách kiểm kho</title>
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
                        "warning": "#FBBF24",
                        "danger": "#EF4444",
                        "info": "#3B82F6"
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
            <h1 class="text-xl font-bold">QUẢN LÝ KHO</h1>
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
                        <span class="material-icons-outlined">inventory_2</span>
                        Nhập kho
                    </a>
                </li>
                <li class="px-4 py-2 hover:bg-gray-700 rounded-md">
                    <a class="flex items-center gap-2" href="#">
                        <span class="material-icons-outlined">unarchive</span>
                        Xuất trả
                    </a>
                </li>
                <li class="px-4 py-2 bg-primary rounded-md">
                    <a class="flex items-center gap-2" href="#">
                        <span class="material-icons-outlined">checklist</span>
                        Kiểm kho
                    </a>
                </li>
                <li class="px-4 py-2 hover:bg-gray-700 rounded-md">
                    <a class="flex items-center gap-2" href="#">
                        <span class="material-icons-outlined">assessment</span>
                        Báo cáo
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
            <h2 class="text-2xl font-bold text-text-light dark:text-text-dark">LỊCH SỬ KIỂM KHO</h2>
            <div class="flex items-center gap-4">
                <span class="text-sm">Kho chi nhánh A</span>
                <span class="material-icons-outlined">person_outline</span>
            </div>
        </header>
        <div class="bg-card-light dark:bg-card-dark p-6 rounded-lg shadow-md">
            <div class="flex justify-between items-center mb-4">
                <div class="flex items-center gap-4">
                    <div class="relative">
                        <span class="material-icons-outlined absolute left-3 top-1/2 -translate-y-1/2 text-text-secondary-light dark:text-text-secondary-dark">search</span>
                        <input class="pl-10 pr-4 py-2 w-80 border border-border-light dark:border-border-dark bg-background-light dark:bg-background-dark rounded-md focus:ring-primary focus:border-primary" placeholder="Tìm kiếm theo mã phiếu, người thực hiện..." type="text"/>
                    </div>
                </div>
                <button class="bg-primary hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-md flex items-center gap-2">
                    <span class="material-icons-outlined">add</span>
                    Tạo phiếu kiểm kho
                </button>
            </div>
            <div class="overflow-x-auto">
                <table class="w-full table-auto">
                    <thead class="bg-gray-50 dark:bg-slate-700 text-left text-sm text-text-secondary-light dark:text-text-secondary-dark uppercase">
                    <tr>
                        <th class="px-4 py-3 font-medium">STT</th>
                        <th class="px-4 py-3 font-medium">Mã phiếu</th>
                        <th class="px-4 py-3 font-medium">Ngày kiểm</th>
                        <th class="px-4 py-3 font-medium">Người thực hiện</th>
                        <th class="px-4 py-3 font-medium text-center">Trạng thái</th>
                        <th class="px-4 py-3 font-medium text-center">Số lượng thuốc đã kiểm</th>
                        <th class="px-4 py-3 font-medium text-center">Hành động</th>
                    </tr>
                    </thead>
                    <tbody class="divide-y divide-border-light dark:divide-border-dark">
                    <tr class="hover:bg-gray-50 dark:hover:bg-slate-800">
                        <td class="px-4 py-3">1</td>
                        <td class="px-4 py-3 font-medium text-primary">#PKK0012</td>
                        <td class="px-4 py-3">25/07/2024</td>
                        <td class="px-4 py-3">Nguyễn Văn A</td>
                        <td class="px-4 py-3 text-center">
                            <span class="bg-success/10 text-success text-xs font-medium px-2.5 py-0.5 rounded-full">Đã cân bằng</span>
                        </td>
                        <td class="px-4 py-3 text-center">20/20</td>
                        <td class="px-4 py-3 flex items-center justify-center gap-2">
                            <button class="text-primary hover:text-blue-700 p-1" title="Xem chi tiết">
                                <span class="material-icons-outlined">visibility</span>
                            </button>
                        </td>
                    </tr>
                    <tr class="hover:bg-gray-50 dark:hover:bg-slate-800">
                        <td class="px-4 py-3">2</td>
                        <td class="px-4 py-3 font-medium text-primary">#PKK0011</td>
                        <td class="px-4 py-3">15/07/2024</td>
                        <td class="px-4 py-3">Trần Thị B</td>
                        <td class="px-4 py-3 text-center">
                            <span class="bg-danger/10 text-danger text-xs font-medium px-2.5 py-0.5 rounded-full">Có chênh lệch</span>
                        </td>
                        <td class="px-4 py-3 text-center">15/20</td>
                        <td class="px-4 py-3 flex items-center justify-center gap-2">
                            <button class="text-primary hover:text-blue-700 p-1" title="Xem chi tiết">
                                <span class="material-icons-outlined">visibility</span>
                            </button>
                        </td>
                    </tr>
                    <tr class="hover:bg-gray-50 dark:hover:bg-slate-800">
                        <td class="px-4 py-3">3</td>
                        <td class="px-4 py-3 font-medium text-primary">#PKK0010</td>
                        <td class="px-4 py-3">05/07/2024</td>
                        <td class="px-4 py-3">Lê Văn C</td>
                        <td class="px-4 py-3 text-center">
                            <span class="bg-success/10 text-success text-xs font-medium px-2.5 py-0.5 rounded-full">Đã cân bằng</span>
                        </td>
                        <td class="px-4 py-3 text-center">30/30</td>
                        <td class="px-4 py-3 flex items-center justify-center gap-2">
                            <button class="text-primary hover:text-blue-700 p-1" title="Xem chi tiết">
                                <span class="material-icons-outlined">visibility</span>
                            </button>
                        </td>
                    </tr>
                    <tr class="hover:bg-gray-50 dark:hover:bg-slate-800">
                        <td class="px-4 py-3">4</td>
                        <td class="px-4 py-3 font-medium text-primary">#PKK0009</td>
                        <td class="px-4 py-3">25/06/2024</td>
                        <td class="px-4 py-3">Nguyễn Văn A</td>
                        <td class="px-4 py-3 text-center">
                            <span class="bg-danger/10 text-danger text-xs font-medium px-2.5 py-0.5 rounded-full">Có chênh lệch</span>
                        </td>
                        <td class="px-4 py-3 text-center">22/20</td>
                        <td class="px-4 py-3 flex items-center justify-center gap-2">
                            <button class="text-primary hover:text-blue-700 p-1" title="Xem chi tiết">
                                <span class="material-icons-outlined">visibility</span>
                            </button>
                        </td>
                    </tr>
                    <tr class="hover:bg-gray-50 dark:hover:bg-slate-800">
                        <td class="px-4 py-3">5</td>
                        <td class="px-4 py-3 font-medium text-primary">#PKK0008</td>
                        <td class="px-4 py-3">15/06/2024</td>
                        <td class="px-4 py-3">Trần Thị B</td>
                        <td class="px-4 py-3 text-center">
                            <span class="bg-warning/10 text-warning text-xs font-medium px-2.5 py-0.5 rounded-full">Chờ xử lý</span>
                        </td>
                        <td class="px-4 py-3 text-center">10/25</td>
                        <td class="px-4 py-3 flex items-center justify-center gap-2">
                            <button class="text-primary hover:text-blue-700 p-1" title="Xem chi tiết">
                                <span class="material-icons-outlined">visibility</span>
                            </button>
                            <button class="text-warning hover:text-amber-600 p-1" title="Tiếp tục kiểm kê">
                                <span class="material-icons-outlined">play_circle_outline</span>
                            </button>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
            <div class="flex justify-between items-center mt-4">
                <span class="text-sm text-text-secondary-light dark:text-text-secondary-dark">Hiển thị 1-5 trên 25 phiếu</span>
                <div class="flex items-center gap-2">
                    <button class="p-2 rounded-md hover:bg-gray-100 dark:hover:bg-slate-700 disabled:opacity-50" disabled="">
                        <span class="material-icons-outlined">chevron_left</span>
                    </button>
                    <button class="p-2 rounded-md hover:bg-gray-100 dark:hover:bg-slate-700">
                        <span class="material-icons-outlined">chevron_right</span>
                    </button>
                </div>
            </div>
        </div>
    </main>
</div>
</body></html>