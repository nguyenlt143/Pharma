@import vn.edu.fpt.pharma.dto.inventorycheck.InventoryCheckHistoryVM
@param java.util.List<vn.edu.fpt.pharma.dto.inventorycheck.InventoryCheckHistoryVM> inventoryChecks
@param String branchName

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="utf-8"/>
    <meta content="width=device-width, initial-scale=1.0" name="viewport"/>
    <title>Danh sách kiểm kho</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,typography"></script>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet"/>
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
            <h2 class="text-2xl font-bold text-text-light dark:text-text-dark">LỊCH SỬ KIỂM KHO</h2>
            <div class="flex items-center gap-4">
                <span class="text-sm">${branchName}</span>
                <span class="material-icons-outlined">person_outline</span>
            </div>
        </header>

        <div class="bg-card-light dark:bg-card-dark p-6 rounded-lg shadow-md">
            <div class="flex justify-between items-center mb-4">
                <div class="flex items-center gap-4">
                    <div class="relative">
                        <span class="material-icons-outlined absolute left-3 top-1/2 -translate-y-1/2 text-text-secondary-light dark:text-text-secondary-dark">search</span>
                        <input id="searchInput" class="pl-10 pr-4 py-2 w-80 border border-border-light dark:border-border-dark bg-background-light dark:bg-background-dark rounded-md focus:ring-primary focus:border-primary" placeholder="Tìm kiếm theo mã phiếu..." type="text"/>
                    </div>
                </div>
                <button onclick="window.location.href='/inventory/check/create'" class="bg-primary hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-md flex items-center gap-2">
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
                        <th class="px-4 py-3 font-medium text-center">Số lượng thuốc đã kiểm</th>
                        <th class="px-4 py-3 font-medium text-center">Hành động</th>
                    </tr>
                    </thead>
                    <tbody id="checkTableBody" class="divide-y divide-border-light dark:divide-border-dark">
                    @if(inventoryChecks != null && !inventoryChecks.isEmpty())
                        @for(var check : inventoryChecks)
                            <tr class="hover:bg-gray-50 dark:hover:bg-slate-800" data-check-code="${check.getCheckCode()}">
                                <td class="px-4 py-3">@if(inventoryChecks.indexOf(check) >= 0)${inventoryChecks.indexOf(check) + 1}@endif</td>
                                <td class="px-4 py-3 font-medium text-primary">${check.getCheckCode()}</td>
                                <td class="px-4 py-3">${check.getCheckDate()}</td>
                                <td class="px-4 py-3 text-center">${check.getCheckedCount()}</td>
                                <td class="px-4 py-3 flex items-center justify-center gap-2">
                                    <a href="/inventory/check/detail?checkDate=${check.getCheckDateRaw()}" class="text-primary hover:text-blue-700 p-1" title="Xem chi tiết">
                                        <span class="material-icons-outlined">visibility</span>
                                    </a>
                                </td>
                            </tr>
                        @endfor
                    @else
                        <tr>
                            <td colspan="5" class="px-4 py-8 text-center text-text-secondary-light dark:text-text-secondary-dark">
                                Chưa có lịch sử kiểm kho
                            </td>
                        </tr>
                    @endif
                    </tbody>
                </table>
            </div>

            <div class="flex justify-between items-center mt-4">
                <span class="text-sm text-text-secondary-light dark:text-text-secondary-dark">
                    Hiển thị 1-@if(inventoryChecks != null)${inventoryChecks.size()}@else 0@endif trên @if(inventoryChecks != null)${inventoryChecks.size()}@else 0@endif phiếu
                </span>
                <div class="flex items-center gap-2">
                    <button class="p-2 rounded-md hover:bg-gray-100 dark:hover:bg-slate-700 disabled:opacity-50" disabled>
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

<script src="/assets/js/inventory/inventory_check_list.js"></script>
</body>
</html>