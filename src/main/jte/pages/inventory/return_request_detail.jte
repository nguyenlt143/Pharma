@param vn.edu.fpt.pharma.dto.inventory.ReturnRequestVM request
@param java.util.List<vn.edu.fpt.pharma.dto.warehouse.RequestDetailVM> details

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Chi tiết yêu cầu trả hàng</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,typography"></script>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Outlined" rel="stylesheet">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body class="bg-gray-100 font-sans text-gray-900">
<div class="flex h-screen">

    <!-- Sidebar -->
    @template.layouts.inventory(active = "return")

    <!-- Main -->
    <main class="flex-1 p-6 overflow-y-auto">
        <div class="mb-6">
            <div class="flex justify-between items-center">
                <h2 class="text-2xl font-bold">CHI TIẾT YÊU CẦU TRẢ HÀNG</h2>
                <a href="/inventory/return/list" class="text-blue-600 hover:text-blue-800 flex items-center gap-1">
                    <span class="material-icons-outlined">arrow_back</span>
                    Quay lại
                </a>
            </div>
        </div>

        <!-- Request Info -->
        <div class="bg-white p-6 rounded-lg shadow-md mb-6">
            <h3 class="text-lg font-semibold mb-4 flex items-center gap-2">
                <span class="material-icons-outlined text-blue-600">info</span>
                Thông tin yêu cầu
            </h3>
            <div class="grid grid-cols-2 gap-4">
                <div>
                    <label class="text-sm text-gray-600">Mã yêu cầu</label>
                    <p class="font-semibold text-lg">${request.getCode()}</p>
                </div>
                <div>
                    <label class="text-sm text-gray-600">Ngày tạo</label>
                    <p class="font-semibold">${request.getCreatedAt()}</p>
                </div>
                <div>
                    <label class="text-sm text-gray-600">Trạng thái</label>
                    <p>
                        @if(request.getStatus().equals("REQUESTED"))
                            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800">
                                CHỜ XÁC NHẬN
                            </span>
                        @elseif(request.getStatus().equals("CONFIRMED"))
                            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                                ĐÃ XÁC NHẬN
                            </span>
                        @else
                            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-800">
                                ${request.getStatus()}
                            </span>
                        @endif
                    </p>
                </div>
                <div>
                    <label class="text-sm text-gray-600">Số loại thuốc</label>
                    <p class="font-semibold">${request.getMedicineCount()}</p>
                </div>
                <div class="col-span-2">
                    <label class="text-sm text-gray-600">Ghi chú</label>
                    <p class="text-gray-700">${request.getNote()}</p>
                </div>
            </div>
        </div>

        <!-- Medicine Details -->
        <div class="bg-white p-6 rounded-lg shadow-md mb-6">
            <h3 class="text-lg font-semibold mb-4 flex items-center gap-2">
                <span class="material-icons-outlined text-blue-600">medication</span>
                Danh sách thuốc trả về
            </h3>
            <div class="overflow-x-auto">
                <table class="w-full table-auto">
                    <thead class="bg-gray-50 text-left text-sm text-gray-600 uppercase">
                    <tr>
                        <th class="px-4 py-3 font-medium">STT</th>
                        <th class="px-4 py-3 font-medium">Mã Variant</th>
                        <th class="px-4 py-3 font-medium text-right">Số lượng</th>
                    </tr>
                    </thead>
                    <tbody class="divide-y divide-gray-200">
                    @if(details != null && !details.isEmpty())
                        @for(vn.edu.fpt.pharma.dto.warehouse.RequestDetailVM detail : details)
                            <tr class="hover:bg-gray-50">
                                <td class="px-4 py-3">@if(details.indexOf(detail) >= 0)${details.indexOf(detail) + 1}@endif</td>
                                <td class="px-4 py-3">${detail.variantId()}</td>
                                <td class="px-4 py-3 text-right font-semibold">${detail.quantity()}</td>
                            </tr>
                        @endfor
                    @else
                        <tr>
                            <td colspan="3" class="px-4 py-8 text-center text-gray-500">
                                Không có dữ liệu
                            </td>
                        </tr>
                    @endif
                    </tbody>
                </table>
            </div>
        </div>

        <!-- Actions -->
        @if(request.getStatus().equals("REQUESTED"))
            <div class="flex justify-end gap-4">
                <button type="button"
                        onclick="window.location.href='/inventory/return/list'"
                        class="px-6 py-3 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50">
                    Hủy
                </button>
                <button type="button"
                        id="confirmBtn"
                        onclick="confirmReturn(${request.getId()})"
                        class="px-6 py-3 bg-green-600 text-white rounded-md hover:bg-green-700 flex items-center gap-2">
                    <span class="material-icons-outlined">check_circle</span>
                    Xác nhận trả hàng
                </button>
            </div>
        @endif
    </main>
</div>

<script>
function confirmReturn(requestId) {
    if (!confirm('Bạn có chắc chắn muốn xác nhận trả hàng này?\nSau khi xác nhận, dữ liệu sẽ được ghi vào bảng Inventory Movement.')) {
        return;
    }

    const btn = $('#confirmBtn');
    btn.prop('disabled', true).html('<span class="material-icons-outlined animate-spin">refresh</span> Đang xử lý...');

    $.ajax({
        url: '/inventory/return/confirm/' + requestId,
        method: 'POST',
        success: function(response) {
            if (response.success) {
                alert('Xác nhận trả hàng thành công!\n' + response.message);
                window.location.href = '/inventory/return/list';
            } else {
                alert('Lỗi: ' + response.message);
                btn.prop('disabled', false).html('<span class="material-icons-outlined">check_circle</span> Xác nhận trả hàng');
            }
        },
        error: function(xhr) {
            console.error('Error:', xhr);
            const errorMsg = xhr.responseJSON?.message || 'Có lỗi xảy ra khi xác nhận trả hàng';
            alert(errorMsg);
            btn.prop('disabled', false).html('<span class="material-icons-outlined">check_circle</span> Xác nhận trả hàng');
        }
    });
}
</script>
</body>
</html>

