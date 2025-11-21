@param java.util.List<vn.edu.fpt.pharma.dto.inventory.InventoryMedicineVM> medicines
@param Long branchId

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="utf-8"/>
    <meta content="width=device-width, initial-scale=1.0" name="viewport"/>
    <title>Danh sách thuốc trong kho</title>
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
    @template.layouts.inventory(active = "medicine")

    <main class="flex-1 p-6 overflow-y-auto">
        <header class="flex justify-between items-center mb-6">
            <div>
                <h2 class="text-2xl font-bold">DANH SÁCH THUỐC TRONG KHO</h2>
                <p class="text-text-secondary-light dark:text-text-secondary-dark">Xem chi tiết thuốc theo variant và batch</p>
            </div>
            <div class="flex items-center gap-2 text-sm text-gray-600">
                <span class="material-icons-outlined text-base">warehouse</span>
                <span>Chi nhánh ${branchId} | Tổng: @if(medicines != null)${medicines.size()}@endif loại</span>
            </div>
        </header>

        <div class="bg-card-light dark:bg-card-dark p-6 rounded-lg shadow-md">
            <div class="flex justify-between items-center mb-4">
                <div class="flex items-center gap-4">
                    <div class="relative">
                        <span class="material-icons-outlined absolute left-3 top-1/2 -translate-y-1/2 text-text-secondary-light dark:text-text-secondary-dark">search</span>
                        <input id="searchInput" class="pl-10 pr-4 py-2 w-96 border border-border-light dark:border-border-dark bg-background-light dark:bg-background-dark rounded-md focus:ring-primary focus:border-primary" placeholder="Tìm theo tên thuốc, hoạt chất, batch..." type="text"/>
                    </div>
                    <select id="categoryFilter" class="py-2 px-4 border border-border-light dark:border-border-dark bg-background-light dark:bg-background-dark rounded-md focus:ring-primary focus:border-primary">
                        <option value="">Tất cả danh mục</option>
                    </select>
                </div>
            </div>

            <div class="overflow-x-auto">
                <table class="w-full table-auto">
                    <thead class="bg-gray-50 dark:bg-slate-700 text-left text-sm text-text-secondary-light dark:text-text-secondary-dark uppercase">
                    <tr>
                        <th class="px-4 py-3 font-medium">STT</th>
                        <th class="px-4 py-3 font-medium">Tên thuốc</th>
                        <th class="px-4 py-3 font-medium">Hoạt chất</th>
                        <th class="px-4 py-3 font-medium">Hàm lượng</th>
                        <th class="px-4 py-3 font-medium">Dạng bào chế</th>
                        <th class="px-4 py-3 font-medium">Mã lô</th>
                        <th class="px-4 py-3 font-medium">HSD</th>
                        <th class="px-4 py-3 font-medium text-center">Số lượng</th>
                        <th class="px-4 py-3 font-medium">Nhà SX</th>
                        <th class="px-4 py-3 font-medium">Danh mục</th>
                        <th class="px-4 py-3 font-medium text-center">Chi tiết</th>
                    </tr>
                    </thead>
                    <tbody id="medicineTableBody" class="divide-y divide-border-light dark:divide-border-dark">
                    @if(medicines != null && !medicines.isEmpty())
                        @for(var item : medicines)
                            <tr class="hover:bg-gray-50 dark:hover:bg-slate-800"
                                data-medicine-name="${item.getMedicineName()}"
                                data-active-ingredient="${item.getActiveIngredient()}"
                                data-batch-code="${item.getBatchCode()}"
                                data-category="${item.getCategoryName()}"
                                data-variant-id="${item.getVariantId()}"
                                data-batch-id="${item.getBatchId()}">
                                <td class="px-4 py-3">@if(medicines.indexOf(item) >= 0)${medicines.indexOf(item) + 1}@endif</td>
                                <td class="px-4 py-3 font-medium text-primary">${item.getMedicineName()}</td>
                                <td class="px-4 py-3">${item.getActiveIngredient()}</td>
                                <td class="px-4 py-3">@if(item.getStrength() != null)${item.getStrength()}@else-@endif</td>
                                <td class="px-4 py-3">${item.getDosageForm()}</td>
                                <td class="px-4 py-3">@if(item.getBatchCode() != null)<span class="font-mono text-sm bg-gray-100 dark:bg-slate-700 px-2 py-1 rounded">${item.getBatchCode()}</span>@else-@endif</td>
                                <td class="px-4 py-3">
                                    @if(item.getExpiryDate() != null)
                                        @if(item.getExpiryDate().isBefore(java.time.LocalDate.now().plusMonths(6)))
                                            <span class="text-red-600 font-medium">${item.getExpiryDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))}</span>
                                        @else
                                            ${item.getExpiryDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))}
                                        @endif
                                    @else
                                        -
                                    @endif
                                </td>
                                <td class="px-4 py-3 text-center font-semibold">
                                    @if(item.getQuantity() > 0)
                                        ${item.getQuantity()} @if(item.getUnit() != null)${item.getUnit()}@endif
                                    @else
                                        <span class="text-red-600">0</span>
                                    @endif
                                </td>
                                <td class="px-4 py-3">${item.getManufacturer()}</td>
                                <td class="px-4 py-3">@if(item.getCategoryName() != null)<span class="text-xs bg-blue-100 dark:bg-blue-900 text-blue-800 dark:text-blue-200 px-2 py-1 rounded">${item.getCategoryName()}</span>@else-@endif</td>
                                <td class="px-4 py-3 text-center">
                                    <button onclick="viewDetail(${item.getVariantId()}, ${item.getBatchId()})" class="text-primary hover:text-blue-700 p-1" title="Xem chi tiết">
                                        <span class="material-icons-outlined">info</span>
                                    </button>
                                </td>
                            </tr>
                        @endfor
                    @else
                        <tr>
                            <td colspan="11" class="px-4 py-8 text-center text-text-secondary-light dark:text-text-secondary-dark">
                                Không có thuốc trong kho
                            </td>
                        </tr>
                    @endif
                    </tbody>
                </table>
            </div>
        </div>

        <div class="mt-4 text-xs text-text-secondary-light dark:text-text-secondary-dark">
            <p><strong>Variant ID:</strong> ID của biến thể thuốc (quy cách đóng gói)</p>
            <p><strong>Batch ID:</strong> ID của lô thuốc</p>
            <p class="text-red-600 font-medium mt-2">* Thuốc có HSD dưới 6 tháng được đánh dấu màu đỏ</p>
        </div>
    </main>
</div>

<script src="/assets/js/inventory/medicine_list.js"></script>
</body>
</html>

