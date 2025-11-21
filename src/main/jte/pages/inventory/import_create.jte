<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Tạo phiếu nhập kho</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,typography"></script>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Outlined" rel="stylesheet">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body class="bg-gray-100 font-sans text-gray-900">
<div class="flex h-screen">
    <!-- Sidebar -->
    @template.layouts.inventory(active = "import")

    <!-- Main -->
    <main class="flex-1 p-6 overflow-y-auto">
        <div class="mb-6">
            <div class="flex justify-between items-center">
                <h2 class="text-2xl font-bold">TẠO PHIẾU NHẬP KHO</h2>
                <a href="/inventory/import/list" class="text-blue-600 hover:text-blue-800">
                    <span class="material-icons-outlined align-middle">arrow_back</span>
                    Quay lại
                </a>
            </div>
            <p class="text-gray-600 mt-2">Tìm kiếm và chọn thuốc từ kho tổng để tạo phiếu yêu cầu nhập</p>
        </div>

        <!-- Search Box -->
        <div class="bg-white p-6 rounded-lg shadow-md mb-6">
            <div class="relative">
                <label class="block text-sm font-medium text-gray-700 mb-2">Tìm kiếm thuốc từ kho tổng</label>
                <div class="relative">
                    <span class="material-icons-outlined absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">search</span>
                    <input
                        type="text"
                        id="medicineSearch"
                        class="pl-10 pr-4 py-3 w-full border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                        placeholder="Nhập tên thuốc, hoạt chất hoặc mã lô..."
                        autocomplete="off">
                </div>

                <!-- Search Results Dropdown -->
                <div id="searchResults" class="hidden absolute z-50 w-full mt-1 bg-white border border-gray-300 rounded-md shadow-lg max-h-96 overflow-y-auto">
                    <!-- Results will be inserted here -->
                </div>
            </div>
        </div>

        <!-- Selected Medicines Table -->
        <div class="bg-white p-6 rounded-lg shadow-md">
            <h3 class="text-lg font-semibold mb-4">Danh sách thuốc đã chọn</h3>

            <div class="overflow-x-auto">
                <table class="w-full table-auto">
                    <thead class="bg-gray-50 text-left text-sm text-gray-600 uppercase">
                        <tr>
                            <th class="px-4 py-3 font-medium">STT</th>
                            <th class="px-4 py-3 font-medium">Tên thuốc</th>
                            <th class="px-4 py-3 font-medium">Hoạt chất</th>
                            <th class="px-4 py-3 font-medium">Hàm lượng</th>
                            <th class="px-4 py-3 font-medium">Mã lô</th>
                            <th class="px-4 py-3 font-medium">HSD</th>
                            <th class="px-4 py-3 font-medium">Tồn kho tổng</th>
                            <th class="px-4 py-3 font-medium">Số lượng yêu cầu</th>
                            <th class="px-4 py-3 font-medium text-center">Hành động</th>
                        </tr>
                    </thead>
                    <tbody id="selectedMedicinesList" class="divide-y divide-gray-200">
                        <tr id="emptyRow">
                            <td colspan="9" class="px-4 py-8 text-center text-gray-500">
                                Chưa có thuốc nào được chọn. Vui lòng tìm kiếm và chọn thuốc từ kho tổng.
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <!-- Note and Submit -->
            <div class="mt-6 space-y-4">
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">Ghi chú</label>
                    <textarea
                        id="importNote"
                        rows="3"
                        class="w-full border border-gray-300 rounded-md p-3 focus:ring-blue-500 focus:border-blue-500"
                        placeholder="Nhập ghi chú cho phiếu nhập (không bắt buộc)"></textarea>
                </div>

                <div class="flex justify-end gap-4">
                    <button
                        type="button"
                        onclick="window.location.href='/inventory/import/list'"
                        class="px-6 py-3 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50">
                        Hủy
                    </button>
                    <button
                        type="button"
                        id="submitImport"
                        class="px-6 py-3 bg-blue-600 text-white rounded-md hover:bg-blue-700 flex items-center gap-2">
                        <span class="material-icons-outlined">check_circle</span>
                        Hoàn tất và gửi yêu cầu
                    </button>
                </div>
            </div>
        </div>
    </main>
</div>

<script src="/assets/js/inventory/import_create.js"></script>
</body>
</html>

