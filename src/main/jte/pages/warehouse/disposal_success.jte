@param String code

@template.layouts.warehouse(
headContent = @`
    <title>Tạo phiếu xuất hủy thành công</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,typography"></script>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Outlined" rel="stylesheet">
`,
content = @`
    <div class="flex items-center justify-center" style="min-height: 60vh;">
        <div class="bg-white p-8 rounded-lg shadow-lg max-w-md w-full text-center">
            <div class="mb-6">
                <span class="material-icons-outlined text-green-600 text-6xl">check_circle</span>
            </div>
            <h2 class="text-2xl font-bold text-gray-900 mb-2">Tạo phiếu xuất hủy thành công!</h2>
<%--            <p class="text-gray-600 mb-6">Phiếu xuất hủy đã được tạo với mã:</p>--%>
<%--            <div class="bg-gray-100 p-4 rounded-lg mb-6">--%>
<%--                <p class="text-2xl font-mono font-bold text-blue-600">${code}</p>--%>
<%--            </div>--%>
            <p class="text-sm text-gray-500 mb-6">Thuốc sẽ được chuyển đến kho hủy và xử lý theo quy định.</p>
            <div class="flex gap-3 justify-center">
                <a href="/warehouse/disposal/create" class="px-6 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-md flex items-center gap-2">
                    <span class="material-icons-outlined">add</span>
                    Tạo phiếu mới
                </a>
                <a href="/warehouse/receipt-list" class="px-6 py-2 border border-gray-300 rounded-md hover:bg-gray-50">
                    Về danh sách
                </a>
            </div>
        </div>
    </div>
`,
pageScript = @`
`
)

