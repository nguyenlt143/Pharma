@param String checkDate
@param java.util.List<vn.edu.fpt.pharma.dto.inventorycheck.StockAdjustmentDetailVM> details

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Chi tiết phiếu kiểm kho</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,typography"></script>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Outlined" rel="stylesheet">
</head>
<body class="bg-gray-100 font-sans text-gray-900">
<div class="flex h-screen">

    <!-- Sidebar -->
    @template.layouts.inventory(active = "check")

    <!-- Main -->
    <main class="flex-1 p-6 overflow-y-auto">
        <div class="mb-6">
            <div class="flex justify-between items-center">
                <div class="flex items-center gap-4">
                    <a href="/inventory/check" class="p-2 rounded-md hover:bg-gray-200">
                        <span class="material-icons-outlined">arrow_back</span>
                    </a>
                    <div>
                        <h2 class="text-2xl font-bold">CHI TIẾT PHIẾU KIỂM KHO</h2>
                        <p class="text-gray-600 mt-1">Ngày kiểm: ${checkDate}</p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Summary Card -->
        <div class="bg-white p-6 rounded-lg shadow-md mb-6">
            <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div>
                    <label class="block text-sm font-medium text-gray-600 mb-1">Ngày kiểm kho</label>
                    <p class="font-semibold text-lg">${checkDate}</p>
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-600 mb-1">Tổng số thuốc đã kiểm</label>
                    <p class="font-semibold text-lg text-blue-600">@if(details != null)${details.size()}@else 0@endif mặt hàng</p>
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-600 mb-1">Trạng thái</label>
                    <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-green-100 text-green-800">
                        <span class="material-icons-outlined text-sm mr-1">check_circle</span>
                        Đã hoàn thành
                    </span>
                </div>
            </div>
            <!-- Show single note for the whole check -->
            <div class="mt-4">
                <label class="block text-sm font-medium text-gray-600 mb-1">Ghi chú</label>
                <p class="text-sm text-gray-700">@if(details != null && !details.isEmpty())
                    @for(vn.edu.fpt.pharma.dto.inventorycheck.StockAdjustmentDetailVM d : details)
                        @if(d.getReason() != null && !d.getReason().isBlank())${d.getReason()}@break@endif
                    @endfor
                @else - @endif</p>
            </div>
        </div>

        <!-- Details Table -->
        <div class="bg-white p-6 rounded-lg shadow-md">
            <h3 class="text-lg font-semibold mb-4 flex items-center gap-2">
                <span class="material-icons-outlined text-blue-600">inventory</span>
                Danh sách thuốc đã kiểm
            </h3>
            <div class="overflow-x-auto">
                <table class="w-full table-auto">
                    <thead class="bg-gray-50 text-left text-sm text-gray-600 uppercase">
                    <tr>
                        <th class="px-4 py-3 font-medium">STT</th>
                        <th class="px-4 py-3 font-medium">Tên thuốc</th>
                        <th class="px-4 py-3 font-medium">Mã lô</th>
                        <th class="px-4 py-3 font-medium">Hạn sử dụng</th>
                        <th class="px-4 py-3 font-medium text-right">SL trước kiểm</th>
                        <th class="px-4 py-3 font-medium text-right">SL sau kiểm</th>
                        <th class="px-4 py-3 font-medium text-right">Chênh lệch</th>
                        <!-- Removed per-row note column -->
                    </tr>
                    </thead>
                    <tbody class="divide-y divide-gray-200">
                    @if(details != null && !details.isEmpty())
                        @for(vn.edu.fpt.pharma.dto.inventorycheck.StockAdjustmentDetailVM detail : details)
                            <tr class="hover:bg-gray-50">
                                <td class="px-4 py-3">${details.indexOf(detail) + 1}</td>
                                <td class="px-4 py-3 font-medium">${detail.getMedicineName()}</td>
                                <td class="px-4 py-3">${detail.getBatchCode()}</td>
                                <td class="px-4 py-3">${detail.getExpiryDate()}</td>
                                <td class="px-4 py-3 text-right">${detail.getBeforeQuantity()}</td>
                                <td class="px-4 py-3 text-right font-semibold">${detail.getAfterQuantity()}</td>
                                <td class="px-4 py-3 text-right font-semibold @if(detail.getDifferenceQuantity() < 0) text-red-600 @elseif(detail.getDifferenceQuantity() > 0) text-green-600 @else text-gray-600 @endif">
                                    @if(detail.getDifferenceQuantity() > 0)+@endif${detail.getDifferenceQuantity()}
                                </td>
                            </tr>
                        @endfor
                    @else
                        <tr>
                            <td colspan="7" class="px-4 py-8 text-center text-gray-500">
                                Không có dữ liệu kiểm kho
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