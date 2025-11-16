<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Quản lý thuốc</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,typography"></script>
    <link
            href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap"
            rel="stylesheet"
    />
    <link rel="stylesheet" href="/assets/css/owner/layout.css" />
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
                    <a href="#" class="nav-item active">🏠 Quản lý thuốc</a>
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
                <h1 class="text-2xl font-bold">Quản Lý Thuốc</h1>
            </div>

            <div class="flex items-center justify-between mb-6">
                <div class="relative w-1/3">
                    <input
                            type="text"
                            placeholder="Tìm kiếm thuốc..."
                            class="w-full border border-gray-300 rounded-lg py-2 px-3 focus:ring-2 focus:ring-blue-500 focus:outline-none"
                    />
                    <span class="absolute right-3 top-2.5 text-gray-400">🔍</span>
                </div>
                <button
                        id="btnAddMedicine"
                        class="flex items-center bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium"
                >
                    ➕ Thêm Thuốc Mới
                </button>
            </div>

            <div class="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
                <table class="w-full text-left border-collapse">
                    <thead class="bg-gray-100">
                    <tr>
                        <th class="py-3 px-4 font-semibold text-gray-700">Mã hàng hóa</th>
                        <th class="py-3 px-4 font-semibold text-gray-700">Tên hàng hóa</th>
                        <th class="py-3 px-4 font-semibold text-gray-700">Danh mục</th>
                        <th class="py-3 px-4 font-semibold text-gray-700">Thương hiệu</th>
                        <th class="py-3 px-4 text-right font-semibold text-gray-700">Hành động</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr class="border-t hover:bg-gray-50">
                        <td class="py-3 px-4">PNC001</td>
                        <td class="py-3 px-4">Paracetamol 500mg</td>
                        <td class="py-3 px-4">Giảm đau - hạ sốt</td>
                        <td class="py-3 px-4">DHG Pharma</td>
                        <td class="py-3 px-4 text-right space-x-3">
                            <button onclick="openUpdateModal('PNC001')" class="text-blue-600 font-medium">Cập nhật</button>
                            <button onclick="openDetailModal('PNC001','Paracetamol 500mg','Giảm đau - hạ sốt','DHG Pharma')" class="text-green-600 font-medium">Xem chi tiết</button>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </main>
</div>

<!-- Popup: Thêm thuốc mới -->
<div id="addMedicineModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center hidden">
    <div class="bg-white rounded-lg w-1/2 p-6">
        <h2 class="text-xl font-bold mb-4">Thêm thuốc mới</h2>
        <form id="addMedicineForm" class="grid grid-cols-2 gap-4">
            <input type="text" placeholder="Mã thuốc" class="border border-gray-300 rounded-lg p-2 col-span-1">
            <input type="text" placeholder="Tên thuốc" class="border border-gray-300 rounded-lg p-2 col-span-1">
            <input type="text" placeholder="Danh mục" class="border border-gray-300 rounded-lg p-2 col-span-1">
            <input type="text" placeholder="Thương hiệu" class="border border-gray-300 rounded-lg p-2 col-span-1">
            <textarea placeholder="Mô tả thuốc" class="border border-gray-300 rounded-lg p-2 col-span-2"></textarea>
            <div class="col-span-2 flex justify-end gap-2 mt-4">
                <button type="button" onclick="closeAddModal()" class="px-4 py-2 rounded-lg border border-gray-300">Hủy</button>
                <button type="submit" class="px-4 py-2 rounded-lg bg-blue-600 text-white">Lưu</button>
            </div>
        </form>
    </div>
</div>

<!-- Popup: Cập nhật biến thể -->
<div id="updateMedicineModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center hidden">
    <div class="bg-white rounded-lg w-1/2 p-6">
        <h2 class="text-xl font-bold mb-4">Cập nhật biến thể thuốc</h2>
        <form id="updateMedicineForm" class="flex flex-col gap-3">
            <div class="overflow-x-auto">
                <table class="min-w-full text-left border border-gray-300">
                    <thead class="bg-gray-100">
                    <tr>
                        <th class="px-3 py-2 border">Hàm lượng</th>
                        <th class="px-3 py-2 border">Dạng bào chế</th>
                        <th class="px-3 py-2 border">Giá</th>
                        <th class="px-3 py-2 border">Hành động</th>
                        <th class="px-3 py-2 border">Hướng dẫn sử dụng</th>
                        <th class="px-3 py-2 border">Tác dụng phụ</th>

                    </tr>
                    </thead>
                    <tbody id="variantTable">
                    <tr>
                        <td class="px-3 py-2 border"><input type="text" value="500mg" class="w-full border-gray-300 rounded p-1"></td>
                        <td class="px-3 py-2 border"><input type="text" value="Viên nén" class="w-full border-gray-300 rounded p-1"></td>
                        <td class="px-3 py-2 border"><input type="number" value="12000" class="w-full border-gray-300 rounded p-1"></td>
                        <td class="px-3 py-2 border text-center">
                            <button type="button" class="text-red-600 font-medium" onclick="deleteVariantRow(this)">Xóa</button>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
            <button type="button" onclick="addVariantRow()" class="mt-2 text-blue-600 font-medium">+ Thêm biến thể</button>
            <div class="flex justify-end gap-2 mt-4">
                <button type="button" onclick="closeUpdateModal()" class="px-4 py-2 rounded-lg border border-gray-300">Hủy</button>
                <button type="submit" class="px-4 py-2 rounded-lg bg-blue-600 text-white">Lưu thay đổi</button>
            </div>
        </form>
    </div>
</div>

<!-- Popup: Chi tiết thuốc -->
<div id="detailMedicineModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center hidden">
    <div class="bg-white rounded-lg w-1/3 p-6">
        <h2 class="text-xl font-bold mb-4">Chi tiết thuốc</h2>
        <div class="space-y-2">
            <p><strong>Mã thuốc:</strong> <span id="detailCode"></span></p>
            <p><strong>Tên thuốc:</strong> <span id="detailName"></span></p>
            <p><strong>Danh mục:</strong> <span id="detailCategory"></span></p>
            <p><strong>Thương hiệu:</strong> <span id="detailBrand"></span></p>
        </div>
        <div class="flex justify-end mt-6">
            <button onclick="closeDetailModal()" class="px-4 py-2 rounded-lg border border-gray-300">Đóng</button>
        </div>
    </div>
</div>

<script>
    const addModal = document.getElementById('addMedicineModal');
    const updateModal = document.getElementById('updateMedicineModal');
    const detailModal = document.getElementById('detailMedicineModal');
    const variantTable = document.getElementById('variantTable');

    // Mở popup thêm thuốc
    document.getElementById('btnAddMedicine').addEventListener('click', () => {
        addModal.classList.remove('hidden');
    });
    function closeAddModal() {
        addModal.classList.add('hidden');
    }

    // Mở popup cập nhật thuốc
    function openUpdateModal(code) {
        updateModal.classList.remove('hidden');
    }
    function closeUpdateModal() {
        updateModal.classList.add('hidden');
    }

    // Popup chi tiết
    function openDetailModal(code, name, category, brand) {
        document.getElementById('detailCode').innerText = code;
        document.getElementById('detailName').innerText = name;
        document.getElementById('detailCategory').innerText = category;
        document.getElementById('detailBrand').innerText = brand;
        detailModal.classList.remove('hidden');
    }
    function closeDetailModal() {
        detailModal.classList.add('hidden');
    }

    // Thêm biến thể mới
    function addVariantRow() {
        const row = document.createElement('tr');
        row.innerHTML = `
      <td class="px-3 py-2 border"><input type="text" placeholder="Hàm lượng" class="w-full border-gray-300 rounded p-1"></td>
      <td class="px-3 py-2 border"><input type="text" placeholder="Dạng bào chế" class="w-full border-gray-300 rounded p-1"></td>
      <td class="px-3 py-2 border"><input type="number" placeholder="Giá" class="w-full border-gray-300 rounded p-1"></td>
      <td class="px-3 py-2 border text-center">
        <button type="button" class="text-red-600 font-medium" onclick="deleteVariantRow(this)">Xóa</button>
      </td>`;
        variantTable.appendChild(row);
    }

    // Xóa biến thể
    function deleteVariantRow(button) {
        button.closest('tr').remove();
    }

    // Chặn reload form demo
    document.getElementById('addMedicineForm').addEventListener('submit', e => e.preventDefault());
    document.getElementById('updateMedicineForm').addEventListener('submit', e => e.preventDefault());
</script>
</body>
</html>
