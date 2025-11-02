@import java.util.List
@import vn.edu.fpt.pharma.dto.importform.ImportFormVM
@param List<ImportFormVM> importForms

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh sách phiếu nhập</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,typography"></script>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Outlined" rel="stylesheet">
</head>
<body class="bg-gray-100 font-display text-gray-900">

<div class="flex h-screen">
    <!-- Sidebar (giữ nguyên code sidebar của bạn ở trên) -->
    <aside class="w-64 bg-[#0F172A] text-white flex flex-col">
        <div class="p-4 border-b border-gray-700">
            <h1 class="text-xl font-bold">QUẢN LÝ KHO</h1>
        </div>
        <nav class="flex-1 mt-4">
            <ul>
                <li class="px-4 py-2 hover:bg-gray-700 rounded-md">
                    <a class="flex items-center gap-2" href="/inventory/dashboard">
                        <span class="material-icons-outlined">dashboard</span> Tổng quan
                    </a>
                </li>
                <li class="px-4 py-2 bg-primary rounded-md">
                    <a class="flex items-center gap-2" href="/inventory/import">
                        <span class="material-icons-outlined">inventory_2</span> Nhập kho
                    </a>
                </li>
                <li class="px-4 py-2 hover:bg-gray-700 rounded-md">
                    <a class="flex items-center gap-2" href="#">
                        <span class="material-icons-outlined">unarchive</span> Xuất kho
                    </a>
                </li>
            </ul>
        </nav>
    </aside>

    <!-- Main -->
    <main class="flex-1 p-6 overflow-y-auto">
        <h2 class="text-2xl font-bold mb-6">DANH SÁCH PHIẾU NHẬP</h2>

        <div class="bg-white p-6 rounded-lg shadow-md">
            <div class="overflow-x-auto">
                <table class="w-full table-auto">
                    <thead class="bg-gray-50 text-left text-sm text-gray-500 uppercase">
                    <tr>
                        <th class="px-4 py-3">STT</th>
                        <th class="px-4 py-3">Mã phiếu</th>
                        <th class="px-4 py-3">Ngày nhập</th>
                        <th class="px-4 py-3 text-center">Số mặt hàng</th>
                        <th class="px-4 py-3 text-right">Tổng tiền</th>
                        <th class="px-4 py-3">Trạng thái</th>
                    </tr>
                    </thead>
                    <tbody>
                    @for(var i = 0; i < importForms.size(); i++) {
                    var form = importForms.get(i);
                    <tr class="hover:bg-gray-50 cursor-pointer">
                        <td class="px-4 py-3">${i + 1}</td>
                        <td class="px-4 py-3 text-blue-600 font-medium">#${form.code}</td>
                        <td class="px-4 py-3">${form.date}</td>
                        <td class="px-4 py-3 text-center">${form.itemCount}</td>
                        <td class="px-4 py-3 text-right">${form.total}₫</td>
                        <td class="px-4 py-3">
                                    <span class="px-2 py-1 rounded-full text-xs font-medium
                                        ${form.status == "COMPLETED" ? "bg-green-100 text-green-700" :
                                          form.status == "CANCELLED" ? "bg-red-100 text-red-700" :
                                          "bg-blue-100 text-blue-700"}">
                                        ${form.statusLabel}
                                    </span>
                        </td>
                    </tr>
                    }
                    </tbody>
                </table>
            </div>
        </div>
    </main>
</div>

</body>
</html>
