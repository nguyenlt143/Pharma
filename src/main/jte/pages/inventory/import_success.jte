@param String code

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="utf-8"/>
    <meta content="width=device-width, initial-scale=1.0" name="viewport"/>
    <title>Tạo phiếu nhập thành công</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,typography"></script>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet"/>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Outlined" rel="stylesheet"/>
</head>
<body class="bg-gray-100 font-display text-gray-900">
<div class="flex h-screen">
    @template.layouts.inventory(active = "import")
    <main class="flex-1 p-6 flex items-center justify-center">
        <div class="bg-white p-8 rounded-lg shadow-md max-w-lg w-full text-center">
            <div class="mx-auto flex items-center justify-center h-20 w-20 rounded-full bg-green-100 mb-6">
                <span class="material-icons-outlined text-5xl text-green-600">check_circle</span>
            </div>
            <h2 class="text-2xl font-bold mb-2">Thành công!</h2>
            <p class="text-gray-600 mb-6">Phiếu <span class="font-semibold text-blue-600">${code}</span> đã được tạo và gửi duyệt thành công.</p>
            <div class="flex flex-col sm:flex-row justify-center gap-4">
                <a href="/inventory/import/detail/${code.substring(3)}" class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-6 rounded-md flex items-center justify-center gap-2 w-full sm:w-auto">
                    <span class="material-icons-outlined">visibility</span>
                    Xem chi tiết
                </a>
                <a href="/inventory/import/list" class="bg-gray-200 hover:bg-gray-300 text-gray-800 font-bold py-3 px-6 rounded-md flex items-center justify-center gap-2 w-full sm:w-auto">
                    <span class="material-icons-outlined">list_alt</span>
                    Về danh sách
                </a>
                <a href="/inventory/import/create" class="bg-gray-200 hover:bg-gray-300 text-gray-800 font-bold py-3 px-6 rounded-md flex items-center justify-center gap-2 w-full sm:w-auto">
                    <span class="material-icons-outlined">add</span>
                    Tạo phiếu mới
                </a>
            </div>
        </div>
    </main>
</div>
</body></html>

