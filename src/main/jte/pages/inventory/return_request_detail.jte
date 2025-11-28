@param vn.edu.fpt.pharma.dto.inventory.ReturnRequestVM request
@param java.util.List<vn.edu.fpt.pharma.dto.warehouse.RequestDetailVM> details

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Chi tiết phiếu trả hàng</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,typography"></script>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Outlined" rel="stylesheet">
</head>
<body class="bg-gray-100 font-sans text-gray-900">
<div class="flex h-screen">

    <!-- Sidebar -->
    @template.layouts.inventory(active = "export")

    <!-- Main -->
    <main class="flex-1 p-6 overflow-y-auto">
        <div class="mb-4">
            <a href="/inventory/return/list" class="text-blue-600 hover:text-blue-800 flex items-center gap-1">
                <span class="material-icons-outlined text-sm">arrow_back</span>
                <span>Quay lại danh sách</span>
            </a>
        </div>

        <h2 class="text-2xl font-bold mb-6">CHI TIẾT PHIẾU TRẢ HÀNG</h2>

        <!-- Thông tin phiếu -->
        <div class="bg-white p-6 rounded-lg shadow-md mb-6">
            <div class="grid grid-cols-2 gap-6">
                <div>
                    <p class="text-sm text-gray-600 mb-1">Mã phiếu</p>
                    <p class="font-semibold text-lg text-blue-600">${request.getCode()}</p>
                </div>
                <div>
                    <p class="text-sm text-gray-600 mb-1">Ngày tạo</p>
                    <p class="font-medium">${request.getCreatedAt()}</p>
                </div>
                <div>
                    <p class="text-sm text-gray-600 mb-1">Trạng thái</p>
                    <p class="font-medium">
                        @if(request.getStatus().equals("REQUESTED"))
                            <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-yellow-100 text-yellow-800">
                                <span class="material-icons-outlined text-xs mr-1">schedule</span>
                                CHỜ XÁC NHẬN
                            </span>
                        @elseif(request.getStatus().equals("CONFIRMED"))
                            <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-green-100 text-green-800">
                                <span class="material-icons-outlined text-xs mr-1">check_circle</span>
                                ĐÃ XÁC NHẬN
                            </span>
                        @elseif(request.getStatus().equals("CANCELLED"))
                            <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-red-100 text-red-800">
                                <span class="material-icons-outlined text-xs mr-1">cancel</span>
                                ĐÃ HỦY
                            </span>
                        @else
                            <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-gray-100 text-gray-800">
                                ${request.getStatus()}
                            </span>
                        @endif
                    </p>
                </div>
                <div>
                    <p class="text-sm text-gray-600 mb-1">Ghi chú</p>
                    <p class="text-gray-800">@if(request.getNote() != null)${request.getNote()}@else-@endif</p>
                </div>
                <div>
                    <p class="text-sm text-gray-600 mb-1">Số loại thuốc</p>
                    <p class="font-semibold text-lg">${request.getMedicineCount()} loại</p>
                </div>
            </div>
        </div>

        <!-- Danh sách thuốc -->
        <div class="bg-white p-6 rounded-lg shadow-md mb-6">
            <h3 class="text-lg font-semibold mb-4 flex items-center gap-2">
                <span class="material-icons-outlined text-blue-600">medication</span>
                Danh sách thuốc trong phiếu trả
            </h3>
            <div class="overflow-x-auto">
                <table class="w-full table-auto">
                    <thead class="bg-gray-50">
                    <tr>
                        <th class="px-4 py-3 text-left">STT</th>
                        <th class="px-4 py-3 text-left">Tên thuốc</th>
                        <th class="px-4 py-3 text-left">Hoạt chất</th>
                        <th class="px-4 py-3 text-left">Hàm lượng</th>
                        <th class="px-4 py-3 text-left">Dạng bào chế</th>
                        <th class="px-4 py-3 text-left">Đóng gói</th>
                        <th class="px-4 py-3 text-center">Số lượng yêu cầu</th>
                    </tr>
                    </thead>
                    <tbody class="divide-y divide-gray-200">
                    @if(details != null && !details.isEmpty())
                        @for(vn.edu.fpt.pharma.dto.warehouse.RequestDetailVM detail : details)
                            <tr class="hover:bg-gray-50">
                                <td class="px-4 py-3">@if(details.indexOf(detail) >= 0)${details.indexOf(detail) + 1}@endif</td>
                                <td class="px-4 py-3 font-medium">${detail.medicineName()}</td>
                                <td class="px-4 py-3">@if(detail.activeIngredient() != null)${detail.activeIngredient()}@else-@endif</td>
                                <td class="px-4 py-3">${detail.strength()}</td>
                                <td class="px-4 py-3">@if(detail.dosageForm() != null)${detail.dosageForm()}@else-@endif</td>
                                <td class="px-4 py-3">${detail.unit()}</td>
                                <td class="px-4 py-3 text-center">
                                    <span class="inline-flex items-center px-3 py-1 rounded-md text-sm font-semibold bg-blue-50 text-blue-700">
                                        ${detail.quantity()} ${detail.unit()}
                                    </span>
                                </td>
                            </tr>
                        @endfor
                    @else
                        <tr>
                            <td colspan="7" class="px-4 py-8 text-center text-gray-500">Không có dữ liệu</td>
                        </tr>
                    @endif
                    </tbody>
                </table>
            </div>
        </div>
    </main>
</div>
</body>
</html>

