<!DOCTYPE html>
<html lang="vi"><head>
    <meta charset="utf-8"/>
    <meta content="width=device-width, initial-scale=1.0" name="viewport"/>
    <title>Chi tiết phiếu kiểm kho</title>
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
    @template.layouts.inventory(active = "check")
    <main class="flex-1 p-6 overflow-y-auto">
        <header class="flex justify-between items-center mb-6">
            <div class="flex items-center gap-4">
                <button class="p-2 rounded-md hover:bg-gray-200 dark:hover:bg-slate-700">
                    <span class="material-icons-outlined">arrow_back</span>
                </button>
                <div>
                    <h2 class="text-2xl font-bold text-text-light dark:text-text-dark">Chi tiết phiếu kiểm kho #PKK0008</h2>
                    <span class="text-sm text-text-secondary-light dark:text-text-secondary-dark">Trạng thái: <span class="text-warning font-medium">Chờ xử lý</span></span>
                </div>
            </div>
            <div class="flex items-center gap-4">
                <button class="bg-danger/10 text-danger font-bold py-2 px-4 rounded-md flex items-center gap-2 hover:bg-danger/20">
                    <span class="material-icons-outlined">delete</span>
                    Hủy phiếu
                </button>
                <button class="bg-primary hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-md flex items-center gap-2">
                    <span class="material-icons-outlined">save</span>
                    Lưu
                </button>
            </div>
        </header>
        <div class="bg-card-light dark:bg-card-dark p-6 rounded-lg shadow-md mb-6">
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 items-end">
                <div>
                    <label class="block text-sm font-medium text-text-secondary-light dark:text-text-secondary-dark mb-1">Ngày kiểm</label>
                    <p class="font-semibold">15/06/2024</p>
                </div>
                <div>
                    <label class="block text-sm font-medium text-text-secondary-light dark:text-text-secondary-dark mb-1">Giờ kiểm</label>
                    <p class="font-semibold">09:30</p>
                </div>
                <div class="lg:col-span-2">
                    <label class="block text-sm font-medium text-text-secondary-light dark:text-text-secondary-dark mb-1" for="note">Ghi chú</label>
                    <input class="w-full border border-border-light dark:border-border-dark bg-background-light dark:bg-background-dark rounded-md focus:ring-primary focus:border-primary py-1.5" id="note" name="note" placeholder="Thêm ghi chú..." type="text" value="Kiểm kho định kỳ tháng 6"/>
                </div>
                <div>
                    <label class="block text-sm font-medium text-text-secondary-light dark:text-text-secondary-dark mb-1">Tổng tiền chênh lệch</label>
                    <p class="font-semibold text-danger">-100,000 VNĐ</p>
                </div>
            </div>
        </div>
        <div class="bg-card-light dark:bg-card-dark p-6 rounded-lg shadow-md">
            <div class="flex justify-between items-center mb-4 gap-4">
                <h3 class="text-lg font-bold">Danh sách hàng hóa</h3>
                <div class="flex items-center gap-4">
                    <div class="relative">
                        <span class="material-icons-outlined absolute left-3 top-1/2 -translate-y-1/2 text-text-secondary-light dark:text-text-secondary-dark">search</span>
                        <input class="pl-10 pr-4 py-2 w-full border border-border-light dark:border-border-dark bg-background-light dark:bg-background-dark rounded-md focus:ring-primary focus:border-primary" placeholder="Tìm kiếm hàng hóa..." type="text"/>
                    </div>
                    <button class="bg-yellow-500 hover:bg-yellow-600 text-white font-bold py-2 px-4 rounded-md flex items-center gap-2">
                        <span class="material-icons-outlined">file_upload</span>
                        Tạo phiếu xuất mất hàng
                    </button>
                </div>
            </div>
            <div class="overflow-x-auto">
                <table class="w-full table-auto">
                    <thead class="bg-gray-50 dark:bg-slate-700 text-left text-sm text-text-secondary-light dark:text-text-secondary-dark uppercase">
                    <tr>
                        <th class="px-4 py-3 font-medium">Tên thuốc</th>
                        <th class="px-4 py-3 font-medium">Đơn vị</th>
                        <th class="px-4 py-3 font-medium text-center">Tồn kho hệ thống</th>
                        <th class="px-4 py-3 font-medium text-center">Tồn kho thực tế</th>
                        <th class="px-4 py-3 font-medium text-center">Chênh lệch</th>
                        <th class="px-4 py-3 font-medium text-right">Giá vốn</th>
                        <th class="px-4 py-3 font-medium text-right">Giá trị chênh lệch</th>
                        <th class="px-4 py-3 font-medium text-center">Hành động</th>
                    </tr>
                    </thead>
                    <tbody class="divide-y divide-border-light dark:divide-border-dark">
                    <tr class="hover:bg-gray-50 dark:hover:bg-slate-800">
                        <td class="px-4 py-3">Paracetamol 500mg</td>
                        <td class="px-4 py-3">Viên</td>
                        <td class="px-4 py-3 text-center">100</td>
                        <td class="px-4 py-3 text-center"><input class="w-20 text-center bg-transparent border-b border-border-light dark:border-border-dark focus:outline-none focus:ring-0 focus:border-primary" type="number" value="100"/></td>
                        <td class="px-4 py-3 text-center font-medium">-</td>
                        <td class="px-4 py-3 text-right">2,000</td>
                        <td class="px-4 py-3 text-right font-medium">-</td>
                        <td class="px-4 py-3 text-center">
                            <button class="text-danger hover:text-red-700 p-1" title="Xóa">
                                <span class="material-icons-outlined">delete_outline</span>
                            </button>
                        </td>
                    </tr>
                    <tr class="hover:bg-gray-50 dark:hover:bg-slate-800">
                        <td class="px-4 py-3">Amoxicillin 250mg</td>
                        <td class="px-4 py-3">Hộp</td>
                        <td class="px-4 py-3 text-center">50</td>
                        <td class="px-4 py-3 text-center"><input class="w-20 text-center bg-transparent border-b border-border-light dark:border-border-dark focus:outline-none focus:ring-0 focus:border-primary" type="number" value="48"/></td>
                        <td class="px-4 py-3 text-center font-medium text-danger">-2</td>
                        <td class="px-4 py-3 text-right">50,000</td>
                        <td class="px-4 py-3 text-right font-medium text-danger">-100,000</td>
                        <td class="px-4 py-3 text-center">
                            <button class="text-danger hover:text-red-700 p-1" title="Xóa">
                                <span class="material-icons-outlined">delete_outline</span>
                            </button>
                        </td>
                    </tr>
                    <tr class="hover:bg-gray-50 dark:hover:bg-slate-800">
                        <td class="px-4 py-3">Vitamin C 1000mg</td>
                        <td class="px-4 py-3">Tuýp</td>
                        <td class="px-4 py-3 text-center">30</td>
                        <td class="px-4 py-3 text-center"><input class="w-20 text-center bg-transparent border-b border-border-light dark:border-border-dark focus:outline-none focus:ring-0 focus:border-primary" type="number" value="31"/></td>
                        <td class="px-4 py-3 text-center font-medium">-</td>
                        <td class="px-4 py-3 text-right">30,000</td>
                        <td class="px-4 py-3 text-right font-medium">-</td>
                        <td class="px-4 py-3 text-center">
                            <button class="text-danger hover:text-red-700 p-1" title="Xóa">
                                <span class="material-icons-outlined">delete_outline</span>
                            </button>
                        </td>
                    </tr>
                    <tr class="hover:bg-gray-50 dark:hover:bg-slate-800">
                        <td class="px-4 py-3">Berberin 10mg</td>
                        <td class="px-4 py-3">Chai</td>
                        <td class="px-4 py-3 text-center">25</td>
                        <td class="px-4 py-3 text-center"><input class="w-20 text-center bg-transparent border-b border-border-light dark:border-border-dark focus:outline-none focus:ring-0 focus:border-primary" type="number" value="25"/></td>
                        <td class="px-4 py-3 text-center font-medium">-</td>
                        <td class="px-4 py-3 text-right">15,000</td>
                        <td class="px-4 py-3 text-right font-medium">-</td>
                        <td class="px-4 py-3 text-center">
                            <button class="text-danger hover:text-red-700 p-1" title="Xóa">
                                <span class="material-icons-outlined">delete_outline</span>
                            </button>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </main>
</div>
</body></html>