@param vn.edu.fpt.pharma.dto.inventory.ConfirmImportVM confirmImport
@param java.util.List<vn.edu.fpt.pharma.dto.warehouse.ReceiptDetailVM> details

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Chi tiết nhập kho</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,typography"></script>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Outlined" rel="stylesheet">
</head>
<body class="bg-gray-100 font-sans text-gray-900">
<div class="flex h-screen">

    <!-- Sidebar -->
    @template.layouts.inventory(active = "confirm")

    <!-- Main -->
    <main class="flex-1 p-6 overflow-y-auto">
        <div class="mb-4">
            <a href="/inventory/confirm/list" class="text-blue-600 hover:text-blue-800 flex items-center gap-1">
                <span class="material-icons-outlined text-sm">arrow_back</span>
                <span>Quay lại danh sách</span>
            </a>
        </div>

        <h2 class="text-2xl font-bold mb-6">CHI TIẾT XÁC NHẬN NHẬP HÀNG</h2>

        <div class="bg-white p-6 rounded-lg shadow-md mb-6">
            <div class="grid grid-cols-2 gap-4">
                <div>
                    <p class="text-sm text-gray-600">Mã phiếu</p>
                    <p class="font-semibold text-lg">${confirmImport.getCode()}</p>
                </div>
                <div>
                    <p class="text-sm text-gray-600">Ngày tạo</p>
                    <p class="font-medium">${confirmImport.getCreatedAt()}</p>
                </div>
                <div>
                    <p class="text-sm text-gray-600">Trạng thái</p>
                    <p class="font-medium">
                        @if(confirmImport.getStatus().equals("SHIPPED"))
                            <span class="text-blue-600">ĐANG GIAO</span>
                        @elseif(confirmImport.getStatus().equals("RECEIVED"))
                            <span class="text-green-600">ĐÃ NHẬN</span>
                        @else
                            <span class="text-gray-500">${confirmImport.getStatus()}</span>
                        @endif
                    </p>
                </div>
                <div>
                    <p class="text-sm text-gray-600">Tổng giá tiền</p>
                    <p class="font-semibold text-lg">@if(confirmImport.getTotalMoney() != null)${String.format("%,.0f", confirmImport.getTotalMoney())}@else0@endif ₫</p>
                </div>
            </div>
        </div>

        <div class="bg-white p-6 rounded-lg shadow-md mb-6">
            <h3 class="text-lg font-semibold mb-4">DANH SÁCH THUỐC</h3>
            <div class="overflow-x-auto">
                <table class="w-full table-auto">
                    <thead class="bg-gray-50">
                    <tr>
                        <th class="px-4 py-3 text-left">STT</th>
                        <th class="px-4 py-3 text-left">Tên thuốc</th>
                        <th class="px-4 py-3 text-left">Hoạt chất</th>
                        <th class="px-4 py-3 text-left">Hàm lượng</th>
                        <th class="px-4 py-3 text-left">Dạng bào chế</th>
                        <th class="px-4 py-3 text-left">Danh mục</th>
                        <th class="px-4 py-3 text-left">Đơn vị</th>
                        <th class="px-4 py-3 text-center">Số lượng</th>
                        <th class="px-4 py-3 text-center">Mã lô</th>
                    </tr>
                    </thead>
                    <tbody class="divide-y divide-gray-200">
                    @if(details != null && !details.isEmpty())
                        @for(vn.edu.fpt.pharma.dto.warehouse.ReceiptDetailVM detail : details)
                            <tr class="hover:bg-gray-50">
                                <td class="px-4 py-3">@if(details.indexOf(detail) >= 0)${details.indexOf(detail) + 1}@endif</td>
                                <td class="px-4 py-3 font-medium">${detail.getMedicineName()}</td>
                                <td class="px-4 py-3">@if(detail.getActiveIngredient() != null)${detail.getActiveIngredient()}@else-@endif</td>
                                <td class="px-4 py-3">${detail.getConcentration()}</td>
                                <td class="px-4 py-3">@if(detail.getDosageForm() != null)${detail.getDosageForm()}@else-@endif</td>
                                <td class="px-4 py-3">@if(detail.getCategoryName() != null)${detail.getCategoryName()}@else-@endif</td>
                                <td class="px-4 py-3">${detail.getUnit()}</td>
                                <td class="px-4 py-3 text-center font-medium">
                                    <span class="inline-flex items-center px-3 py-1 rounded-md text-sm font-semibold bg-blue-50 text-blue-700">
                                        ${detail.getQuantity()} ${detail.getUnit()}
                                    </span>
                                </td>
                                <td class="px-4 py-3 text-center">
                                    <span class="inline-flex items-center px-3 py-1 rounded-md text-sm font-mono font-semibold bg-gray-100 text-gray-800">
                                        @if(detail.getBatchCode() != null && !detail.getBatchCode().equals("-"))${detail.getBatchCode()}@else-@endif
                                    </span>
                                </td>
                            </tr>
                        @endfor
                    @else
                        <tr>
                            <td colspan="9" class="px-4 py-8 text-center text-gray-500">Không có dữ liệu</td>
                        </tr>
                    @endif
                    </tbody>
                </table>
            </div>
        </div>

        @if(confirmImport.getStatus().equals("SHIPPED"))
            <div class="flex justify-end">
                <button onclick="confirmReceipt()" class="bg-green-600 hover:bg-green-700 text-white px-6 py-3 rounded-md flex items-center gap-2">
                    <span class="material-icons-outlined">check_circle</span>
                    <span>Xác nhận đã nhận hàng</span>
                </button>
            </div>
        @endif
    </main>
</div>

<script>
function confirmReceipt() {
    if (!confirm('Bạn có chắc chắn muốn xác nhận đã nhận hàng?')) {
        return;
    }

    fetch('/inventory/confirm/${confirmImport.getId()}', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert(data.message);
            window.location.href = '/inventory/confirm/list';
        } else {
            alert('Lỗi: ' + data.message);
        }
    })
    .catch(error => {
        alert('Có lỗi xảy ra: ' + error);
    });
}
</script>
</body>
</html>

