<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý thuốc</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,typography"></script>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
</head>
<body class="font-[Inter] bg-gray-50 text-gray-800">
<div class="flex min-h-screen">
    <!-- Sidebar -->
    <aside class="sidebar bg-gray-800 w-64 text-white">
        <div class="flex flex-col h-full justify-between">
            <div>
                <div class="p-4 border-b border-gray-700">
                    <h1 class="text-lg font-semibold text-white">PharmacyS</h1>
                    <p class="text-sm text-gray-300">Quản trị viên</p>
                </div>
                <nav class="mt-4 flex flex-col">
                    <a href="#" class="nav-item active p-2">🏠 Quản lý danh mục</a>
                    <a href="#" class="nav-item p-2">👥 Bảng giá </a>
                    <a href="#" class="nav-item p-2">🚚 Nhà cung cấp</a>
                    <a href="#" class="nav-item p-2">📦 Danh mục</a>
                    <a href="#" class="nav-item p-2">📊 Báo cáo</a>
                </nav>
            </div>
            <div class="border-t border-gray-700 p-4 flex flex-col gap-2">
                <a href="#" class="nav-item p-2">⚙️ Cài đặt</a>
                <a href="#" class="nav-item p-2">↩️ Đăng xuất</a>
            </div>
        </div>
    </aside>

    <!-- Main content -->
    <main class="flex-1 p-8">
        <div class="max-w-6xl mx-auto">
            <div class="mb-6">
                <h1 class="text-2xl font-bold">Quản lý nhà cung cấp </h1>
            </div>

            <div class="flex items-center justify-between mb-6">
                <div class="relative w-1/3">
                    <input type="text" placeholder="Tìm kiếm theo tên nhà cung cấp ..." class="w-full border border-gray-300 rounded-lg py-2 px-3 focus:ring-2 focus:ring-blue-500 focus:outline-none">
                </div>
                <button id="btnAddSupplier" class="flex items-center bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium">
                    ➕ Thêm Mới nhà cung cấp
                </button>
            </div>

            <div class="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
                <table class="w-full text-left border-collapse">
                    <thead class="bg-gray-100">
                    <tr>
                        <th class="py-3 px-4 font-semibold text-gray-700">Tên nhà cung cấp</th>
                        <th class="py-3 px-4 font-semibold text-gray-700">Thông tin</th>
                        <th class="py-3 px-4 font-semibold text-gray-700">Địa chỉ</th>
                        <th class="py-3 px-4 font-semibold text-gray-700">Hành động</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr class="border-t hover:bg-gray-50">
                        <td class="py-3 px-4">Công ty dược phẩm FPT</td>
                        <td class="py-3 px-4">23456789 </td>
                        <td class="py-3 px-4">123 ABC Đường Hồ Chí Minh </td>
                        <td class="py-3 px-4 text-right">
                            <span class="text-blue-600 font-medium cursor-pointer mr-4" onclick="openUpdateModal('Công ty dược phẩm FPT','23456789','123 ABC Đường Hồ Chí Minh')">Cập nhật</span>
                            <span class="text-blue-600 font-medium cursor-pointer">Xem chi tiết</span>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </main>
</div>

<!-- Popup thêm mới nhà cung cấp -->
<div id="addSupplierModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center hidden">
    <div class="bg-white rounded-lg w-1/3 p-6">
        <h2 class="text-xl font-bold mb-4">Thêm mới nhà cung cấp</h2>
        <form id="addSupplierForm" class="flex flex-col gap-3">
            <input type="text" placeholder="Tên nhà cung cấp" class="border border-gray-300 rounded-lg p-2">
            <input type="text" placeholder="Thông tin" class="border border-gray-300 rounded-lg p-2">
            <input type="text" placeholder="Địa chỉ" class="border border-gray-300 rounded-lg p-2">
            <div class="flex justify-end gap-2 mt-4">
                <button type="button" onclick="closeAddModal()" class="px-4 py-2 rounded-lg border border-gray-300">Hủy</button>
                <button type="submit" class="px-4 py-2 rounded-lg bg-blue-600 text-white">Lưu</button>
            </div>
        </form>
    </div>
</div>

<!-- Popup cập nhật nhà cung cấp -->
<div id="updateSupplierModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center hidden">
    <div class="bg-white rounded-lg w-1/3 p-6">
        <h2 class="text-xl font-bold mb-4">Cập nhật nhà cung cấp</h2>
        <form id="updateSupplierForm" class="flex flex-col gap-3">
            <input type="text" id="updateName" placeholder="Tên nhà cung cấp" class="border border-gray-300 rounded-lg p-2">
            <input type="text" id="updateInfo" placeholder="Thông tin" class="border border-gray-300 rounded-lg p-2">
            <input type="text" id="updateAddress" placeholder="Địa chỉ" class="border border-gray-300 rounded-lg p-2">
            <div class="flex justify-end gap-2 mt-4">
                <button type="button" onclick="closeUpdateModal()" class="px-4 py-2 rounded-lg border border-gray-300">Hủy</button>
                <button type="submit" class="px-4 py-2 rounded-lg bg-blue-600 text-white">Cập nhật</button>
            </div>
        </form>
    </div>
</div>

<script>
    const addModal = document.getElementById('addSupplierModal');
    const updateModal = document.getElementById('updateSupplierModal');

    // Mở popup thêm mới
    document.getElementById('btnAddSupplier').addEventListener('click', () => {
        addModal.classList.remove('hidden');
    });

    function closeAddModal() {
        addModal.classList.add('hidden');
    }

    function openUpdateModal(name, info, address) {
        document.getElementById('updateName').value = name;
        document.getElementById('updateInfo').value = info;
        document.getElementById('updateAddress').value = address;
        updateModal.classList.remove('hidden');
    }

    function closeUpdateModal() {
        updateModal.classList.add('hidden');
    }

    // Ngăn form submit reload page (demo)
    document.getElementById('addSupplierForm').addEventListener('submit', e => e.preventDefault());
    document.getElementById('updateSupplierForm').addEventListener('submit', e => e.preventDefault());
</script>
</body>
</html>
