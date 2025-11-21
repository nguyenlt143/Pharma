<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Thuốc sắp hết hạn</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,typography"></script>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet" />
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Outlined" rel="stylesheet" />
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
    @template.layouts.inventory(active = "expiring")

    <!-- Main content -->
    <main class="flex-1 p-6 overflow-y-auto">
        <header class="flex justify-between items-center mb-6">
            <div>
                <h2 class="text-2xl font-bold">Thuốc sắp hết hạn</h2>
                <p class="text-text-secondary-light dark:text-text-secondary-dark">Chọn thuốc để tạo phiếu xuất trả về kho tổng.</p>
            </div>
            <div class="flex items-center gap-4">
                <span class="text-sm">Kho chi nhánh A</span>
                <span class="material-icons-outlined">expand_more</span>
            </div>
        </header>

        <div class="bg-card-light dark:bg-card-dark p-6 rounded-lg shadow-md">
            <div class="flex justify-between items-center mb-4">
                <div class="flex items-center gap-4">
                    <div class="relative">
                        <span class="material-icons-outlined absolute left-3 top-1/2 -translate-y-1/2 text-text-secondary-light">search</span>
                        <input type="text" placeholder="Tìm kiếm thuốc..." class="pl-10 pr-4 py-2 border border-border-light dark:border-border-dark rounded-md bg-card-light dark:bg-card-dark focus:outline-none focus:ring-2 focus:ring-primary" />
                    </div>
                    <button class="flex items-center gap-2 px-4 py-2 border border-border-light dark:border-border-dark rounded-md hover:bg-gray-50 dark:hover:bg-slate-700">
                        <span class="material-icons-outlined">filter_alt</span>Bộ lọc
                    </button>
                </div>
                <button class="bg-primary hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-md flex items-center gap-2 disabled:bg-gray-400 disabled:cursor-not-allowed" disabled>
                    <span class="material-icons-outlined">unarchive</span>Tạo phiếu xuất trả (0)
                </button>
            </div>

            <div class="overflow-x-auto">
                <table class="w-full text-left">
                    <thead>
                    <tr class="bg-gray-50 dark:bg-slate-700">
                        <th class="p-4 w-12"><input type="checkbox" class="form-checkbox rounded" /></th>
                        <th class="p-4 font-semibold">Tên thuốc</th>
                        <th class="p-4 font-semibold">Số lô</th>
                        <th class="p-4 font-semibold text-right">Tồn kho</th>
                        <th class="p-4 font-semibold text-center">Ngày hết hạn</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr class="border-b border-border-light dark:border-border-dark">
                        <td class="p-4"><input type="checkbox" class="form-checkbox rounded" /></td>
                        <td class="p-4">
                            <p class="font-medium">Paracetamol 500mg</p>
                            <p class="text-sm text-text-secondary-light">Hộp 10 vỉ x 10 viên</p>
                        </td>
                        <td class="p-4 text-text-secondary-light">PARA500-01</td>
                        <td class="p-4 text-right font-medium">50</td>
                        <td class="p-4 text-center text-danger font-medium">15/07/2024</td>
                    </tr>
                    <tr class="border-b border-border-light dark:border-border-dark">
                        <td class="p-4"><input type="checkbox" class="form-checkbox rounded" /></td>
                        <td class="p-4">
                            <p class="font-medium">Amoxicillin 250mg</p>
                            <p class="text-sm text-text-secondary-light">Chai 60ml</p>
                        </td>
                        <td class="p-4 text-text-secondary-light">AMOX250-12</td>
                        <td class="p-4 text-right font-medium">30</td>
                        <td class="p-4 text-center text-warning font-medium">20/08/2024</td>
                    </tr>
                    <tr class="border-b border-border-light dark:border-border-dark">
                        <td class="p-4"><input type="checkbox" class="form-checkbox rounded" /></td>
                        <td class="p-4">
                            <p class="font-medium">Loratadin 10mg</p>
                            <p class="text-sm text-text-secondary-light">Hộp 1 vỉ x 10 viên</p>
                        </td>
                        <td class="p-4 text-text-secondary-light">LORA10-05</td>
                        <td class="p-4 text-right font-medium">120</td>
                        <td class="p-4 text-center text-danger font-medium">01/08/2024</td>
                    </tr>
                    </tbody>
                </table>
            </div>

            <div class="flex justify-between items-center mt-4">
                <span class="text-sm text-text-secondary-light">Hiển thị 1–3 trên 3 kết quả</span>
                <div class="flex gap-1">
                    <button class="px-3 py-1 border rounded-md bg-gray-200 dark:bg-slate-600">
                        <span class="material-icons-outlined text-base">navigate_before</span>
                    </button>
                    <button class="px-3 py-1 border rounded-md bg-primary text-white">1</button>
                    <button class="px-3 py-1 border rounded-md hover:bg-gray-100 dark:hover:bg-slate-700">
                        <span class="material-icons-outlined text-base">navigate_next</span>
                    </button>
                </div>
            </div>
        </div>
    </main>
</div>
</body>
</html>
