@param java.util.List<vn.edu.fpt.pharma.dto.inventory.ReturnRequestVM> returnRequests

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Xác nhận trả hàng</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,typography"></script>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Outlined" rel="stylesheet">
</head>
<body class="bg-gray-100 font-sans text-gray-900">
<div class="flex h-screen">

    <!-- Sidebar -->
    @template.layouts.inventory(active = "return")

    <!-- Main -->
    <main class="flex-1 p-6 overflow-y-auto">
        <header class="flex justify-between items-center mb-6">
            <div>
                <h2 class="text-2xl font-bold">XÁC NHẬN TRẢ HÀNG VỀ KHO TỔNG</h2>
                <p class="text-gray-600 mt-1">Danh sách yêu cầu trả hàng từ kho tổng đang chờ xác nhận</p>
            </div>
            <div class="flex items-center gap-2 text-sm text-gray-600">
                <span class="material-icons-outlined text-base">assignment_return</span>
                <span>Tổng: @if(returnRequests != null)${returnRequests.size()}@endif yêu cầu</span>
            </div>
        </header>

        <div class="bg-white p-6 rounded-lg shadow-md">
            <div class="overflow-x-auto">
                <table class="w-full table-auto">
                    <thead class="bg-gray-50 text-left text-sm text-gray-600 uppercase">
                    <tr>
                        <th class="px-4 py-3 font-medium">STT</th>
                        <th class="px-4 py-3 font-medium">Mã yêu cầu</th>
                        <th class="px-4 py-3 font-medium">Ngày tạo</th>
                        <th class="px-4 py-3 font-medium text-center">Số loại thuốc</th>
                        <th class="px-4 py-3 font-medium">Trạng thái</th>
                        <th class="px-4 py-3 font-medium">Ghi chú</th>
                        <th class="px-4 py-3 font-medium text-center">Hành động</th>
                    </tr>
                    </thead>
                    <tbody class="divide-y divide-gray-200">
                    @if(returnRequests != null && !returnRequests.isEmpty())
                        @for(vn.edu.fpt.pharma.dto.inventory.ReturnRequestVM item : returnRequests)
                            <tr class="hover:bg-gray-50">
                                <td class="px-4 py-3">@if(returnRequests.indexOf(item) >= 0)${returnRequests.indexOf(item) + 1}@endif</td>
                                <td class="px-4 py-3 font-medium text-blue-600">${item.getCode()}</td>
                                <td class="px-4 py-3">${item.getCreatedAt()}</td>
                                <td class="px-4 py-3 text-center">${item.getMedicineCount()}</td>
                                <td class="px-4 py-3">
                                    @if(item.getStatus().equals("REQUESTED"))
                                        <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800">
                                            <span class="material-icons-outlined text-xs mr-1">schedule</span>
                                            CHỜ XÁC NHẬN
                                        </span>
                                    @elseif(item.getStatus().equals("CONFIRMED"))
                                        <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                                            <span class="material-icons-outlined text-xs mr-1">check_circle</span>
                                            ĐÃ XÁC NHẬN
                                        </span>
                                    @elseif(item.getStatus().equals("CANCELLED"))
                                        <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-800">
                                            <span class="material-icons-outlined text-xs mr-1">cancel</span>
                                            ĐÃ HỦY
                                        </span>
                                    @else
                                        <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-800">
                                            ${item.getStatus()}
                                        </span>
                                    @endif
                                </td>
                                <td class="px-4 py-3 text-sm text-gray-600">${item.getNote()}</td>
                                <td class="px-4 py-3 text-center">
                                    <a href="/inventory/return/detail/${item.getId()}" class="text-blue-600 hover:text-blue-800 font-medium text-sm">
                                        Xem chi tiết
                                    </a>
                                </td>
                            </tr>
                        @endfor
                    @else
                        <tr>
                            <td colspan="7" class="px-4 py-8 text-center text-gray-500">
                                Không có yêu cầu trả hàng nào
                            </td>
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

