@param String code

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Tạo phiếu nhập hàng thành công</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,typography"></script>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Outlined" rel="stylesheet">
</head>
<body class="bg-gray-100 font-sans text-gray-900">
<div class="flex h-screen">
    <!-- Sidebar -->
    @template.layouts.inventory(active = "import")

    <!-- Main -->
    <main class="flex-1 flex items-center justify-center p-6">
        <div class="bg-white p-8 rounded-lg shadow-lg max-w-md w-full text-center">
            <div class="mb-6">
                <div class="w-20 h-20 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
                    <span class="material-icons-outlined text-green-600 text-5xl">check_circle</span>
                </div>
                <h2 class="text-2xl font-bold text-gray-900 mb-2">Tạo phiếu nhập hàng thành công!</h2>
                <p class="text-gray-600">Yêu cầu nhập hàng đã được gửi đến kho tổng</p>
            </div>

            <div class="bg-gray-50 p-4 rounded-lg mb-6">
                <p class="text-sm text-gray-600 mb-1">Mã phiếu</p>
                <p class="text-xl font-bold text-blue-600">${code}</p>
            </div>

            <div class="bg-blue-50 p-4 rounded-lg mb-6 text-left">
                <div class="flex items-start gap-2">
                    <span class="material-icons-outlined text-blue-600 text-sm mt-0.5">info</span>
                    <div class="text-sm text-blue-800">
                        <p class="font-medium mb-1">Lưu ý:</p>
                        <ul class="list-disc list-inside space-y-1">
                            <li>Yêu cầu nhập hàng đã được gửi tới kho tổng</li>
                            <li>Kho tổng sẽ kiểm tra và xác nhận đơn hàng</li>
                            <li>Sau khi xác nhận, hàng sẽ được gửi về chi nhánh</li>
                            <li>Bạn có thể theo dõi trạng thái trong danh sách yêu cầu nhập hàng</li>
                        </ul>
                    </div>
                </div>
            </div>

            <div class="flex gap-3">
                <a href="/inventory/import/list" class="flex-1 px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-md flex items-center justify-center gap-2">
                    <span class="material-icons-outlined">list</span>
                    Xem danh sách
                </a>
                <a href="/inventory/import/create" class="flex-1 px-4 py-2 border border-gray-300 hover:bg-gray-50 rounded-md flex items-center justify-center gap-2">
                    <span class="material-icons-outlined">add</span>
                    Tạo tiếp
                </a>
            </div>
        </div>
    </main>
</div>
</body>
</html>
