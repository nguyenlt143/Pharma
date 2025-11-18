<!DOCTYPE html>
<html lang="vi"><head>
    <meta charset="utf-8"/>
    <meta content="width=device-width, initial-scale=1.0" name="viewport"/>
    <title>Kết quả kiểm kho</title>
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
                    <h2 class="text-2xl font-bold text-text-light dark:text-text-dark">Kiểm kho hoàn tất - #PKK0008</h2>
                    <span class="text-sm text-text-secondary-light dark:text-text-secondary-dark">Trạng thái: <span class="text-success font-medium">Hoàn thành</span></span>
                </div>
            </div>
            <div class="flex items-center gap-4">
                <button class="bg-primary/10 text-primary font-bold py-2 px-4 rounded-md flex items-center gap-2 hover:bg-primary/20">
                    <span class="material-icons-outlined">print</span> In phiếu
                </button>
                <button class="bg-primary hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-md flex items-center gap-2">
                    <span class="material-icons-outlined">check_circle</span> Hoàn thành
                </button>
            </div>
        </header>
        <div class="bg-card-light dark:bg-card-dark p-6 rounded-lg shadow-md mb-6">
            <div class="flex justify-center items-center flex-col text-center">
                <span class="material-icons-outlined text-success text-6xl mb-4">task_alt</span>
                <h3 class="text-xl font-bold mb-2">Đã hoàn tất kiểm kho</h3>
                <p class="text-text-secondary-light dark:text-text-secondary-dark">Phiếu kiểm kho đã được hoàn tất và cập nhật vào hệ thống.</p>
            </div>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-6">
            <div class="bg-card-light dark:bg-card-dark p-4 rounded-lg shadow-md">
                <p class="text-sm text-text-secondary-light dark:text-text-secondary-dark mb-1">Người thực hiện</p>
                <p class="font-semibold">Nguyễn Văn An</p>
            </div>
            <div class="bg-card-light dark:bg-card-dark p-4 rounded-lg shadow-md">
                <p class="text-sm text-text-secondary-light dark:text-text-secondary-dark mb-1">Thời gian hoàn thành</p>
                <p class="font-semibold">10:15 - 15/06/2024</p>
            </div>
            <div class="bg-card-light dark:bg-card-dark p-4 rounded-lg shadow-md">
                <p class="text-sm text-text-secondary-light dark:text-text-secondary-dark mb-1">Số mặt hàng đã kiểm</p>
                <p class="font-semibold">4</p>
            </div>
            <div class="bg-card-light dark:bg-card-dark p-4 rounded-lg shadow-md">
                <p class="text-sm text-text-secondary-light dark:text-text-secondary-dark mb-1">Tổng giá trị chênh lệch</p>
                <p class="font-semibold text-danger">-70,000 VNĐ</p>
            </div>
        </div>
        <div class="bg-card-light dark:bg-card-dark p-6 rounded-lg shadow-md">
            <h3 class="text-lg font-bold mb-4">Chi tiết chênh lệch</h3>
            <div class="overflow-x-auto">
                <table class="w-full table-auto">
                    <thead class="bg-gray-50 dark:bg-slate-700 text-left text-sm text-text-secondary-light dark:text-text-secondary-dark uppercase">
                    <tr>
                        <th class="px-4 py-3 font-medium">Tên thuốc</th>
                        <th class="px-4 py-3 font-medium text-center">Tồn kho hệ thống</th>
                        <th class="px-4 py-3 font-medium text-center">Tồn kho thực tế</th>
                        <th class="px-4 py-3 font-medium text-center">Chênh lệch</th>
                        <th class="px-4 py-3 font-medium text-right">Giá trị chênh lệch</th>
                    </tr>
                    </thead>
                    <tbody class="divide-y divide-border-light dark:divide-border-dark">
                    <tr class="hover:bg-gray-50 dark:hover:bg-slate-800">
                        <td class="px-4 py-3">Amoxicillin 250mg</td>
                        <td class="px-4 py-3 text-center">50</td>
                        <td class="px-4 py-3 text-center">48</td>
                        <td class="px-4 py-3 text-center font-medium text-danger">-2</td>
                        <td class="px-4 py-3 text-right font-medium text-danger">-100,000</td>
                    </tr>
                    <tr class="hover:bg-gray-50 dark:hover:bg-slate-800">
                        <td class="px-4 py-3">Vitamin C 1000mg</td>
                        <td class="px-4 py-3 text-center">30</td>
                        <td class="px-4 py-3 text-center">31</td>
                        <td class="px-4 py-3 text-center font-medium text-success">+1</td>
                        <td class="px-4 py-3 text-right font-medium text-success">+30,000</td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </main>
</div>

</body></html>