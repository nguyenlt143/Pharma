<!DOCTYPE html>
<html lang="vi"><head>
    <meta charset="utf-8"/>
    <meta content="width=device-width, initial-scale=1.0" name="viewport"/>
    <title>Tạo phiếu nhập thành công</title>
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
                <li class="px-4 py-2 bg-primary rounded-md">
                    <a class="flex items-center gap-2" href="#">
                        <span class="material-icons-outlined">inventory_2</span>
                        Nhập kho
                    </a>
                </li>
                <li class="px-4 py-2 hover:bg-gray-700 rounded-md">
                    <a class="flex items-center gap-2" href="#">
                        <span class="material-icons-outlined">unarchive</span>
                        Xuất kho
                    </a>
                </li>
                <li class="px-4 py-2 hover:bg-gray-700 rounded-md">
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
    <main class="flex-1 p-6 flex items-center justify-center">
        <div class="bg-card-light dark:bg-card-dark p-8 rounded-lg shadow-md max-w-lg w-full text-center">
            <div class="mx-auto flex items-center justify-center h-20 w-20 rounded-full bg-green-100 dark:bg-green-900 mb-6">
                <span class="material-icons-outlined text-5xl text-success">check_circle</span>
            </div>
            <h2 class="text-2xl font-bold text-text-light dark:text-text-dark mb-2">Thành công!</h2>
            <p class="text-text-secondary-light dark:text-text-secondary-dark mb-6">Phiếu <span class="font-semibold text-primary">PN-20240726-001</span> đã được thao tác thành công!</p>
            <div class="flex flex-col sm:flex-row justify-center gap-4">
                <button class="bg-primary hover:bg-blue-700 text-white font-bold py-3 px-6 rounded-md flex items-center justify-center gap-2 w-full sm:w-auto">
                    <span class="material-icons-outlined">visibility</span>
                    Xem chi tiết phiếu
                </button>
                <button class="bg-gray-200 hover:bg-gray-300 dark:bg-slate-600 dark:hover:bg-slate-500 text-text-light dark:text-text-dark font-bold py-3 px-6 rounded-md flex items-center justify-center gap-2 w-full sm:w-auto">
                    <span class="material-icons-outlined">list_alt</span>
                    Về danh sách phiếu
                </button>
                <button class="bg-gray-200 hover:bg-gray-300 dark:bg-slate-600 dark:hover:bg-slate-500 text-text-light dark:text-text-dark font-bold py-3 px-6 rounded-md flex items-center justify-center gap-2 w-full sm:w-auto">
                    <span class="material-icons-outlined">add</span>
                    Tạo phiếu mới
                </button>
            </div>
        </div>
    </main>
</div>
</body></html>