<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý danh mục</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,typography"></script>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="/assets/css/owner/layout.css">
</head>

<body class="font-[Inter] bg-gray-50 text-gray-800">
<div class="flex min-h-screen">
    <!-- Sidebar -->
    <aside class="sidebar">
        <div class="flex flex-col h-full justify-between">
            <div>
                <div class="p-4 border-b border-gray-700">
                    <h1 class="text-lg font-semibold text-white">PharmacyS</h1>
                    <p class="text-sm text-gray-300">Quản trị viên</p>
                </div>
                <nav class="mt-4 flex flex-col">
                    <a href="#" class="nav-item active">🏠 Quản lý danh mục</a>
                    <a href="#" class="nav-item">👥 Bảng giá</a>
                    <a href="#" class="nav-item">🚚 Nhà cung cấp</a>
                    <a href="#" class="nav-item">📦 Danh mục</a>
                    <a href="#" class="nav-item">📊 Báo cáo</a>
                </nav>
            </div>

            <div class="border-t border-gray-700 p-4 flex flex-col gap-2">
                <a href="#" class="nav-item">⚙️ Cài đặt</a>
                <a href="#" class="nav-item">↩️ Đăng xuất</a>
            </div>
        </div>
    </aside>

    <!-- Main content -->
    <main class="flex-1 p-8">
        <div class="max-w-6xl mx-auto">
            <div class="mb-6">
                <h1 class="text-2xl font-bold">Quản lý danh mục</h1>
            </div>

            <div class="flex items-center justify-between mb-6">
                <div class="relative w-1/3">
                    <input type="text" placeholder="Tìm kiếm theo tên nhóm..." class="w-full border border-gray-300 rounded-lg py-2 px-3 focus:ring-2 focus:ring-blue-500 focus:outline-none">
                </div>
                <button id="btnAddCategory" class="flex items-center bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium">
                    ➕ Thêm Danh mục mới
                </button>
            </div>

            <div class="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
                <table class="w-full text-left border-collapse">
                    <thead class="bg-gray-100">
                    <tr>
                        <th class="py-3 px-4 font-semibold text-gray-700">Mã hàng hóa</th>
                        <th class="py-3 px-4 font-semibold text-gray-700">Tên nhóm</th>
                        <th class="py-3 px-4 font-semibold text-gray-700">Mô tả</th>
                        <th class="py-3 px-4 text-right font-semibold text-gray-700">Hành động</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr class="border-t hover:bg-gray-50">
                        <td class="py-3 px-4">PNC001</td>
                        <td class="py-3 px-4">Thuốc kháng sinh</td>
                        <td class="py-3 px-4">Các loại thuốc phòng chống nhiễm khuẩn</td>
                        <td class="py-3 px-4 text-right space-x-3">
                            <button onclick="openUpdateModal('Thuốc kháng sinh', 'Các loại thuốc phòng chống nhiễm khuẩn')" class="text-blue-600 font-medium">Cập nhật</button>
                            <button onclick="openDetailModal('PNC001','Thuốc kháng sinh','Các loại thuốc phòng chống nhiễm khuẩn')" class="text-green-600 font-medium">Xem chi tiết</button>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>

            <!-- Popup: Thêm danh mục -->
            <div id="addCategoryModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center hidden">
                <div class="bg-white rounded-lg w-1/3 p-6">
                    <h2 class="text-xl font-bold mb-4">Thêm mới danh mục</h2>
                    <form id="addCategoryForm" class="flex flex-col gap-3">
                        <input type="text" placeholder="Tên danh mục" class="border border-gray-300 rounded-lg p-2">
                        <input type="text" placeholder="Mô tả" class="border border-gray-300 rounded-lg p-2">
                        <div class="flex justify-end gap-2 mt-4">
                            <button type="button" onclick="closeAddModal()" class="px-4 py-2 rounded-lg border border-gray-300">Hủy</button>
                            <button type="submit" class="px-4 py-2 rounded-lg bg-blue-600 text-white">Lưu</button>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Popup: Cập nhật danh mục -->
            <div id="updateCategoryModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center hidden">
                <div class="bg-white rounded-lg w-1/3 p-6">
                    <h2 class="text-xl font-bold mb-4">Cập nhật danh mục</h2>
                    <form id="updateCategoryForm" class="flex flex-col gap-3">
                        <input type="text" id="updateName" placeholder="Tên danh mục" class="border border-gray-300 rounded-lg p-2">
                        <input type="text" id="updateDesc" placeholder="Mô tả" class="border border-gray-300 rounded-lg p-2">
                        <div class="flex justify-end gap-2 mt-4">
                            <button type="button" onclick="closeUpdateModal()" class="px-4 py-2 rounded-lg border border-gray-300">Hủy</button>
                            <button type="submit" class="px-4 py-2 rounded-lg bg-blue-600 text-white">Cập nhật</button>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Popup: Chi tiết danh mục -->
            <div id="detailCategoryModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center hidden">
                <div class="bg-white rounded-lg w-1/3 p-6">
                    <h2 class="text-xl font-bold mb-4">Chi tiết danh mục</h2>
                    <div class="space-y-2">
                        <p><strong>Mã hàng hóa:</strong> <span id="detailCode"></span></p>
                        <p><strong>Tên danh mục:</strong> <span id="detailName"></span></p>
                        <p><strong>Mô tả:</strong> <span id="detailDesc"></span></p>
                    </div>
                    <div class="flex justify-end mt-6">
                        <button onclick="closeDetailModal()" class="px-4 py-2 rounded-lg border border-gray-300">Đóng</button>
                    </div>
                </div>
            </div>

            <script>
                const addModal = document.getElementById('addCategoryModal');
                const updateModal = document.getElementById('updateCategoryModal');
                const detailModal = document.getElementById('detailCategoryModal');

                // --- Thêm mới ---
                document.getElementById('btnAddCategory').addEventListener('click', () => {
                    addModal.classList.remove('hidden');
                });
                function closeAddModal() {
                    addModal.classList.add('hidden');
                }

                // --- Cập nhật ---
                function openUpdateModal(name, desc) {
                    document.getElementById('updateName').value = name;
                    document.getElementById('updateDesc').value = desc;
                    updateModal.classList.remove('hidden');
                }
                function closeUpdateModal() {
                    updateModal.classList.add('hidden');
                }

                // --- Chi tiết ---
                function openDetailModal(code, name, desc) {
                    document.getElementById('detailCode').innerText = code;
                    document.getElementById('detailName').innerText = name;
                    document.getElementById('detailDesc').innerText = desc;
                    detailModal.classList.remove('hidden');
                }
                function closeDetailModal() {
                    detailModal.classList.add('hidden');
                }

                // Ngăn reload trang khi submit demo
                document.getElementById('addCategoryForm').addEventListener('submit', e => e.preventDefault());
                document.getElementById('updateCategoryForm').addEventListener('submit', e => e.preventDefault());
            </script>

        </div>
    </main>
</div>
</body>
</html>
