@param vn.edu.fpt.pharma.dto.warehouse.RequestList request
@param java.util.List<vn.edu.fpt.pharma.dto.warehouse.RequestDetailVM> details

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Chi tiết phiếu nhập</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,typography"></script>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Outlined" rel="stylesheet">
</head>
<body class="bg-gray-100 font-sans text-gray-900">
<div class="flex h-screen">

    <!-- Sidebar -->
    @template.layouts.inventory(active = "import")

    <!-- Main -->
    <main class="flex-1 p-6 overflow-y-auto">
        <div class="mb-4">
            <a href="/inventory/import/list" class="text-blue-600 hover:text-blue-800 flex items-center gap-1">
                <span class="material-icons-outlined text-sm">arrow_back</span>
                <span>Quay lại danh sách</span>
            </a>
        </div>

        <h2 class="text-2xl font-bold mb-6">CHI TIẾT PHIẾU NHẬP</h2>

        <!-- Thông tin phiếu -->
        <div class="bg-white p-6 rounded-lg shadow-md mb-6">
            <h3 class="text-lg font-semibold mb-4 flex items-center gap-2">
                <span class="material-icons-outlined text-blue-600">description</span>
                Thông tin phiếu
            </h3>
            <div class="grid grid-cols-2 gap-6">
                <div>
                    <p class="text-sm text-gray-600 mb-1">Mã phiếu</p>
                    <p class="font-semibold text-lg text-blue-600">#RQ${String.format("%03d", request.id())}</p>
                </div>
                <div>
                    <p class="text-sm text-gray-600 mb-1">Ngày tạo</p>
                    <p class="font-medium">${request.createdAt()}</p>
                </div>
                <div>
                    <p class="text-sm text-gray-600 mb-1">Chi nhánh</p>
                    <p class="font-medium">${request.branchName()}</p>
                </div>
                <div>
                    <p class="text-sm text-gray-600 mb-1">Loại phiếu</p>
                    <p class="font-medium">
                        <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-blue-100 text-blue-800">
                            ${request.requestType()}
                        </span>
                    </p>
                </div>
                <div>
                    <p class="text-sm text-gray-600 mb-1">Trạng thái</p>
                    <p class="font-medium">
                        @if(request.requestStatus().equals("REQUESTED"))
                            <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-yellow-100 text-yellow-800">
                                <span class="material-icons-outlined text-xs mr-1">schedule</span>
                                ĐANG DUYỆT
                            </span>
                        @elseif(request.requestStatus().equals("RECEIVED"))
                            <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-green-100 text-green-800">
                                <span class="material-icons-outlined text-xs mr-1">check_circle</span>
                                ĐÃ NHẬP
                            </span>
                        @elseif(request.requestStatus().equals("CANCELLED"))
                            <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-red-100 text-red-800">
                                <span class="material-icons-outlined text-xs mr-1">cancel</span>
                                ĐÃ HỦY
                            </span>
                        @else
                            <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-gray-100 text-gray-800">
                                ${request.requestStatus()}
                            </span>
                        @endif
                    </p>
                </div>
                <div>
                    <p class="text-sm text-gray-600 mb-1">Số loại thuốc</p>
                    <p class="font-semibold text-lg">@if(details != null)${details.size()}@else0@endif loại</p>
                </div>
            </div>
            @if(request.note() != null && !request.note().isEmpty())
                <div class="mt-4 pt-4 border-t">
                    <p class="text-sm text-gray-600 mb-1">Ghi chú</p>
                    <p class="text-gray-800">${request.note()}</p>
                </div>
            @endif
        </div>

        <!-- Danh sách thuốc -->
        <div class="bg-white p-6 rounded-lg shadow-md mb-6">
            <h3 class="text-lg font-semibold mb-4 flex items-center gap-2">
                <span class="material-icons-outlined text-blue-600">medication</span>
                Danh sách thuốc yêu cầu
            </h3>
            <div class="overflow-x-auto">
                <table class="w-full table-auto">
                    <thead class="bg-gray-50">
                    <tr>
                        <th class="px-4 py-3 text-left font-medium text-gray-600">STT</th>
                        <th class="px-4 py-3 text-left font-medium text-gray-600">Tên thuốc</th>
                        <th class="px-4 py-3 text-left font-medium text-gray-600">Hoạt chất</th>
                        <th class="px-4 py-3 text-left font-medium text-gray-600">Hàm lượng</th>
                        <th class="px-4 py-3 text-left font-medium text-gray-600">Dạng bào chế</th>
                        <th class="px-4 py-3 text-left font-medium text-gray-600">Danh mục</th>
                        <th class="px-4 py-3 text-left font-medium text-gray-600">Đơn vị</th>
                        <th class="px-4 py-3 text-center font-medium text-gray-600">Số lượng yêu cầu</th>
                        <th class="px-4 py-3 text-center font-medium text-gray-600">Số lô khả dụng</th>
                    </tr>
                    </thead>
                    <tbody class="divide-y divide-gray-200">
                    @if(details != null && !details.isEmpty())
                        @for(vn.edu.fpt.pharma.dto.warehouse.RequestDetailVM detail : details)
                            <tr class="hover:bg-gray-50">
                                <td class="px-4 py-3 text-gray-600">@if(details.indexOf(detail) >= 0)${details.indexOf(detail) + 1}@endif</td>
                                <td class="px-4 py-3 font-medium text-gray-900">${detail.medicineName()}</td>
                                <td class="px-4 py-3 text-gray-700">@if(detail.activeIngredient() != null)${detail.activeIngredient()}@else-@endif</td>
                                <td class="px-4 py-3 text-gray-700">${detail.strength()}</td>
                                <td class="px-4 py-3 text-gray-700">${detail.dosageForm()}</td>
                                <td class="px-4 py-3 text-gray-700">@if(detail.categoryName() != null)${detail.categoryName()}@else-@endif</td>
                                <td class="px-4 py-3 text-gray-700">${detail.unit()}</td>
                                <td class="px-4 py-3 text-center">
                                    <span class="inline-flex items-center px-3 py-1 rounded-md text-sm font-semibold bg-blue-50 text-blue-700">
                                        ${detail.quantity()} ${detail.unit()}
                                    </span>
                                </td>
                                <td class="px-4 py-3 text-center">
                                    <span class="inline-flex items-center px-3 py-1 rounded-md text-sm font-semibold bg-gray-100 text-gray-800">
                                        @if(detail.batchCount() != null)${detail.batchCount()}@else0@endif lô
                                    </span>
                                </td>
                            </tr>
                        @endfor
                        <tr class="bg-gray-50 font-semibold">
                            <td colspan="6" class="px-4 py-3 text-right">Tổng số lượng:</td>
                            <td class="px-4 py-3 text-center text-blue-600">
                                @if(details != null)
                                    ${details.stream().mapToLong(d -> d.quantity()).sum()} đơn vị
                                @else
                                    0 đơn vị
                                @endif
                            </td>
                        </tr>
                    @else
                        <tr>
                            <td colspan="6" class="px-4 py-8 text-center text-gray-500">
                                <div class="flex flex-col items-center gap-2">
                                    <span class="material-icons-outlined text-4xl text-gray-400">inventory_2</span>
                                    <span>Không có thuốc nào trong phiếu này</span>
                                </div>
                            </td>
                        </tr>
                    @endif
                    </tbody>
                </table>
            </div>
        </div>

        <!-- Thông tin bổ sung -->
        <div class="bg-blue-50 p-4 rounded-lg border border-blue-200">
            <div class="flex items-start gap-2">
                <span class="material-icons-outlined text-blue-600">info</span>
                <div class="text-sm text-blue-800">
                    <p class="font-medium mb-1">Lưu ý:</p>
                    <ul class="list-disc list-inside space-y-1">
                        <li>Phiếu nhập đang ở trạng thái: <strong>${request.requestStatus()}</strong></li>
                        <li>Khi kho tổng xử lý xong, bạn sẽ nhận được thông báo để xác nhận nhập hàng</li>
                        <li>Kiểm tra kỹ danh sách thuốc trước khi xác nhận</li>
                    </ul>
                </div>
            </div>
        </div>
    </main>
</div>
</body>
</html>
