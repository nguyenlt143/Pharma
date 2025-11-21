@param java.util.List<vn.edu.fpt.pharma.dto.requestform.RequestFormVM> exports

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh sách phiếu xuất</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,typography"></script>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Outlined" rel="stylesheet">
</head>
<body class="bg-gray-100 font-sans text-gray-900">
<div class="flex h-screen">

    <!-- Sidebar -->
    @template.layouts.inventory(active = "export")

    <!-- Main -->
    <main class="flex-1 p-6 overflow-y-auto">
        <header class="flex justify-between items-center mb-6">
            <h2 class="text-2xl font-bold">LỊCH SỬ PHIẾU XUẤT</h2>
            <div class="flex items-center gap-2 text-sm text-gray-600">
                <span class="material-icons-outlined text-base">local_shipping</span>
                <span>Tổng: @if(exports != null)${exports.size()}@endif phiếu</span>
            </div>
        </header>
        <div class="bg-card-light dark:bg-card-dark p-6 rounded-lg shadow-md">
            <div class="flex justify-between items-center mb-4">
                <div class="flex items-center gap-4">
                    <div class="relative">
                        <span class="material-icons-outlined absolute left-3 top-1/2 -translate-y-1/2 text-text-secondary-light dark:text-text-secondary-dark">search</span>
                        <input class="pl-10 pr-4 py-2 w-80 border border-border-light dark:border-border-dark bg-background-light dark:bg-background-dark rounded-md focus:ring-primary focus:border-primary" placeholder="Tìm kiếm theo mã sản phẩm hoặc mã phiếu..." type="text"/>
                    </div>
                </div>
                <button class="bg-primary hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-md flex items-center gap-2">
                    <span class="material-icons-outlined">add</span>
                    Tạo phiếu xuất mới
                </button>
            </div>
            <div class="overflow-x-auto">
                <table class="w-full table-auto">
                    <thead class="bg-gray-50 dark:bg-slate-700 text-left text-sm text-text-secondary-light dark:text-text-secondary-dark uppercase">
                    <tr>
                        <th class="px-4 py-3 font-medium text-center">STT</th>
                        <th class="px-4 py-3 font-medium">Mã phiếu</th>
                        <th class="px-4 py-3 font-medium">Ngày tạo</th>
                        <th class="px-4 py-3 font-medium">Loại xuất</th>
                        <th class="px-4 py-3 font-medium">Người tạo</th>
                        <th class="px-4 py-3 font-medium text-center">Hành động</th>
                    </tr>
                    </thead>
                    <tbody class="divide-y divide-border-light dark:divide-border-dark">
                    <tr class="hover:bg-gray-50 dark:hover:bg-slate-800">
                        <td class="px-4 py-3 text-center">1</td>
                        <td class="px-4 py-3 font-medium text-primary">#PXK00056</td>
                        <td class="px-4 py-3">25/07/2024</td>
                        <td class="px-4 py-3">Trả hàng cho kho tổng</td>
                        <td class="px-4 py-3">Nguyễn Văn A</td>
                        <td class="px-4 py-3 flex items-center justify-center gap-2">
                            <button class="text-primary hover:text-blue-700 p-1" title="Xem chi tiết">
                                <span class="material-icons-outlined">visibility</span>
                            </button>
                        </td>
                    </tr>
                    <tr class="hover:bg-gray-50 dark:hover:bg-slate-800">
                        <td class="px-4 py-3 text-center">2</td>
                        <td class="px-4 py-3 font-medium text-primary">#PXK00055</td>
                        <td class="px-4 py-3">24/07/2024</td>
                        <td class="px-4 py-3">Cấp phát cho đơn vị</td>
                        <td class="px-4 py-3">Trần Thị B</td>
                        <td class="px-4 py-3 flex items-center justify-center gap-2">
                            <button class="text-primary hover:text-blue-700 p-1" title="Xem chi tiết">
                                <span class="material-icons-outlined">visibility</span>
                            </button>
                        </td>
                    </tr>
                    <tr class="hover:bg-gray-50 dark:hover:bg-slate-800">
                        <td class="px-4 py-3 text-center">3</td>
                        <td class="px-4 py-3 font-medium text-primary">#PXK00054</td>
                        <td class="px-4 py-3">23/07/2024</td>
                        <td class="px-4 py-3">Xuất hủy</td>
                        <td class="px-4 py-3">Lê Văn C</td>
                        <td class="px-4 py-3 flex items-center justify-center gap-2">
                            <button class="text-primary hover:text-blue-700 p-1" title="Xem chi tiết">
                                <span class="material-icons-outlined">visibility</span>
                            </button>
                        </td>
                    </tr>
                    <tr class="hover:bg-gray-50 dark:hover:bg-slate-800">
                        <td class="px-4 py-3 text-center">4</td>
                        <td class="px-4 py-3 font-medium text-primary">#PXK00053</td>
                        <td class="px-4 py-3">22/07/2024</td>
                        <td class="px-4 py-3">Trả hàng cho kho tổng</td>
                        <td class="px-4 py-3">Nguyễn Văn A</td>
                        <td class="px-4 py-3 flex items-center justify-center gap-2">
                            <button class="text-primary hover:text-blue-700 p-1" title="Xem chi tiết">
                                <span class="material-icons-outlined">visibility</span>
                            </button>
                        </td>
                    </tr>
                    <tr class="hover:bg-gray-50 dark:hover:bg-slate-800">
                        <td class="px-4 py-3 text-center">5</td>
                        <td class="px-4 py-3 font-medium text-primary">#PXK00052</td>
                        <td class="px-4 py-3">21/07/2024</td>
                        <td class="px-4 py-3">Cấp phát cho đơn vị</td>
                        <td class="px-4 py-3">Trần Thị B</td>
                        <td class="px-4 py-3 flex items-center justify-center gap-2">
                            <button class="text-primary hover:text-blue-700 p-1" title="Xem chi tiết">
                                <span class="material-icons-outlined">visibility</span>
                            </button>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
            <div class="flex justify-between items-center mt-4">
                <span class="text-sm text-text-secondary-light dark:text-text-secondary-dark">Hiển thị 1-5 trên 18 phiếu xuất</span>
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