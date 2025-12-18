@import vn.edu.fpt.pharma.dto.warehouse.ReceiptDetailVM
@import vn.edu.fpt.pharma.dto.warehouse.ReceiptInfo
@import java.util.List

@param ReceiptInfo receipt
@param List<ReceiptDetailVM> details

@template.layouts.warehouse(
headContent = @`
    <title>Chi Tiết Phiếu</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,typography"></script>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Outlined" rel="stylesheet">
`,
content = @`
    <!-- Main Content -->
    <main class="flex-1 p-6 overflow-y-auto bg-gray-100">
        <!-- Back button -->
        <div class="mb-4">
            <a href="/warehouse/receipt-list" class="text-blue-600 hover:text-blue-800 flex items-center gap-1">
                <span class="material-icons-outlined text-sm">arrow_back</span>
                <span>Quay lại danh sách</span>
            </a>
        </div>

        <h2 class="text-2xl font-bold mb-6">CHI TIẾT PHIẾU</h2>

        <!-- Thông tin phiếu -->
        <div class="bg-white p-6 rounded-lg shadow-md mb-6">
            <h3 class="text-lg font-semibold mb-4 flex items-center gap-2">
                <span class="material-icons-outlined text-blue-600">description</span>
                Thông tin phiếu
            </h3>
            <div class="grid grid-cols-2 gap-6">
                <div>
                    <p class="text-sm text-gray-600 mb-1">Chi nhánh</p>
                    <p class="font-medium">${receipt.getBranchName()}</p>
                </div>
                <div>
                    <p class="text-sm text-gray-600 mb-1">Loại phiếu</p>
                    <p class="font-medium">
                        <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-blue-100 text-blue-800">
                            @if(receipt.getType().equals("SUP_TO_WARE"))
                                Phiếu Nhập
                            @elseif(receipt.getType().equals("WARE_TO_BR"))
                                Phiếu Xuất
                            @elseif(receipt.getType().equals("BR_TO_WARE"))
                                Chuyển Về Kho Tổng
                            @elseif(receipt.getType().equals("BR_TO_WARE2"))
                                Hàng Hết Hạn
                            @elseif(receipt.getType().equals("WARE_TO_SUP"))
                                Trả Hàng
                            @elseif(receipt.getType().equals("DISPOSAL"))
                                Xuất Hủy
                            @elseif(receipt.getType().equals("INVENTORY_ADJUSTMENT"))
                                Kiểm Kho
                            @else
                                Không xác định
                            @endif
                        </span>
                    </p>
                </div>
                <div>
                    <p class="text-sm text-gray-600 mb-1">Trạng thái</p>
                    <p class="font-medium">
                        @if(receipt.getStatus().equals("DRAFT"))
                            <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-yellow-100 text-yellow-800">
                                <span class="material-icons-outlined text-xs mr-1">schedule</span>
                                Đang duyệt
                            </span>
                        @elseif(receipt.getStatus().equals("APPROVED"))
                            <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-green-100 text-green-800">
                                <span class="material-icons-outlined text-xs mr-1">check_circle</span>
                                Chấp nhận
                            </span>
                        @elseif(receipt.getStatus().equals("SHIPPED"))
                            <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-blue-100 text-blue-800">
                                <span class="material-icons-outlined text-xs mr-1">local_shipping</span>
                                Đang vận chuyển
                            </span>
                        @elseif(receipt.getStatus().equals("RECEIVED"))
                            <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-green-100 text-green-800">
                                <span class="material-icons-outlined text-xs mr-1">check_circle</span>
                                Đã nhận
                            </span>
                        @elseif(receipt.getStatus().equals("CANCELLED"))
                            <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-red-100 text-red-800">
                                <span class="material-icons-outlined text-xs mr-1">cancel</span>
                                Từ chối
                            </span>
                        @elseif(receipt.getStatus().equals("CLOSED"))
                            <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-gray-100 text-gray-800">
                                <span class="material-icons-outlined text-xs mr-1">done_all</span>
                                Hoàn tất
                            </span>
                        @else
                            <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-gray-100 text-gray-800">
                                Không xác định
                            </span>
                        @endif
                    </p>
                </div>
                <div>
                    <p class="text-sm text-gray-600 mb-1">Số loại thuốc</p>
                    <p class="font-semibold text-lg">${details.size()} loại</p>
                </div>
            </div>
        </div>

        <!-- Danh sách thuốc -->
        <div class="bg-white p-6 rounded-lg shadow-md mb-6">
            <h3 class="text-lg font-semibold mb-4 flex items-center gap-2">
                <span class="material-icons-outlined text-blue-600">medication</span>
                Danh sách thuốc
            </h3>
            <div class="overflow-x-auto">
                <table class="w-full table-auto">
                    <thead class="bg-gray-50">
                    <tr>
                        <th class="px-4 py-3 text-left font-medium text-gray-600">STT</th>
                        <th class="px-4 py-3 text-left font-medium text-gray-600">Tên Thuốc</th>
                        <th class="px-4 py-3 text-left font-medium text-gray-600">ĐVT</th>
                        <th class="px-4 py-3 text-left font-medium text-gray-600">Hàm Lượng</th>
                        <th class="px-4 py-3 text-left font-medium text-gray-600">Số Lô</th>
                        <th class="px-4 py-3 text-left font-medium text-gray-600">NSX</th>
                        <th class="px-4 py-3 text-left font-medium text-gray-600">HSD</th>
                        <th class="px-4 py-3 text-center font-medium text-gray-600">Số Lượng</th>
                        <th class="px-4 py-3 text-right font-medium text-gray-600">Giá Nhập</th>
                    </tr>
                    </thead>
                    <tbody class="divide-y divide-gray-200">
                    @for(ReceiptDetailVM detail : details)
                        <tr class="hover:bg-gray-50">
                            <td class="px-4 py-3 text-gray-600">${details.indexOf(detail) + 1}</td>
                            <td class="px-4 py-3 font-medium text-gray-900">${detail.getMedicineName()}</td>
                            <td class="px-4 py-3 text-gray-700">${detail.getUnit()}</td>
                            <td class="px-4 py-3 text-gray-700">${detail.getConcentration()}</td>
                            <td class="px-4 py-3 text-gray-700">${detail.getBatchCode()}</td>
                            <td class="px-4 py-3 text-gray-700">
                                @if(detail.getMfgDate() != null)
                                    ${detail.getMfgDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))}
                                @else
                                    -
                                @endif
                            </td>
                            <td class="px-4 py-3 text-gray-700">
                                @if(detail.getExpiryDate() != null)
                                    ${detail.getExpiryDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))}
                                @else
                                    -
                                @endif
                            </td>
                            <td class="px-4 py-3 text-center">
                                <span class="inline-flex items-center px-3 py-1 rounded-md text-sm font-semibold bg-blue-50 text-blue-700">
                                    ${detail.getQuantity()}
                                </span>
                            </td>
                            <td class="px-4 py-3 text-right text-gray-900 font-medium">${String.format("%,.0f", detail.getImportPrice())} VNĐ</td>
                        </tr>
                    @endfor
                    <tr class="bg-gray-50 font-semibold">
                        <td colspan="8" class="px-4 py-3 text-right">Tổng tiền:</td>
                        <td class="px-4 py-3 text-right text-blue-600">
                            !{var totalMoney = details.stream().mapToDouble(d -> d.getQuantity() * d.getImportPrice()).sum();}
                            ${String.format("%,.0f", totalMoney)} VNĐ
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>

            <!-- Action Buttons - Đặt dưới bảng theo yêu cầu FixDetail.txt -->
            <div class="mt-6 pt-6 border-t border-gray-200 flex items-center justify-end gap-3">
                @if(receipt.getStatus().equals("DRAFT"))
                    <%-- Phiếu BR_TO_WARE/BR_TO_WARE2 được điều khiển bởi Return Request, không có nút trực tiếp --%>
                    @if(receipt.getType().equals("BR_TO_WARE") || receipt.getType().equals("BR_TO_WARE2"))
                        <div class="flex-1 bg-yellow-50 p-4 rounded-lg border border-yellow-200">
                            <div class="flex items-start gap-2">
                                <span class="material-icons-outlined text-yellow-600">info</span>
                                <p class="text-sm text-yellow-800">
                                    Phiếu này được quản lý qua Yêu cầu trả hàng. Vui lòng duyệt/từ chối yêu cầu tương ứng.
                                </p>
                            </div>
                        </div>
                    @else
                        <button onclick="cancelReceipt(${receipt.getId()})"
                                class="px-6 py-2.5 bg-red-600 hover:bg-red-700 text-white font-medium rounded-lg shadow-sm transition-colors">
                            Từ chối
                        </button>
                        <button onclick="approveReceipt(${receipt.getId()})"
                                class="px-6 py-2.5 bg-green-600 hover:bg-green-700 text-white font-medium rounded-lg shadow-sm transition-colors">
                            Chấp nhận
                        </button>
                    @endif
                @elseif(receipt.getStatus().equals("APPROVED"))
                    <%-- Không hiển thị nút "Gửi hàng" cho phiếu kiểm kho --%>
                    @if(!receipt.getType().equals("INVENTORY_ADJUSTMENT"))
                        <button onclick="shipReceipt(${receipt.getId()})"
                                class="px-6 py-2.5 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg shadow-sm transition-colors">
                            Gửi hàng
                        </button>
                    @endif
                @elseif(receipt.getStatus().equals("SHIPPED"))
                    <%-- Không hiển thị nút "Đã nhận" cho phiếu xuất WARE_TO_BR vì warehouse không phải bên nhận --%>
                    @if(!receipt.getType().equals("WARE_TO_BR"))
                        <button onclick="receiveReceipt(${receipt.getId()})"
                                class="px-6 py-2.5 bg-green-600 hover:bg-green-700 text-white font-medium rounded-lg shadow-sm transition-colors">
                            Đã nhận
                        </button>
                    @endif
                @elseif(receipt.getStatus().equals("RECEIVED"))
                    <button disabled
                            class="px-6 py-2.5 bg-gray-400 text-white font-medium rounded-lg shadow-sm cursor-not-allowed opacity-60">
                        Đã nhận
                    </button>
                @elseif(receipt.getStatus().equals("CANCELLED"))
                    <button disabled
                            class="px-6 py-2.5 bg-gray-400 text-white font-medium rounded-lg shadow-sm cursor-not-allowed opacity-60">
                        Đã từ chối
                    </button>
                @elseif(receipt.getStatus().equals("CLOSED"))
                    <button disabled
                            class="px-6 py-2.5 bg-gray-400 text-white font-medium rounded-lg shadow-sm cursor-not-allowed opacity-60">
                        Đã hoàn tất
                    </button>
                @endif
            </div>
        </div>

        <!-- Thông tin bổ sung -->
        <div class="bg-blue-50 p-4 rounded-lg border border-blue-200">
            <div class="flex items-start gap-2">
                <span class="material-icons-outlined text-blue-600">info</span>
                <div class="text-sm text-blue-800">
                    <p class="font-medium mb-1">Lưu ý:</p>
                    <ul class="list-disc list-inside space-y-1">
                        <li>Phiếu đang ở trạng thái: <strong>
                            @if(receipt.getStatus().equals("DRAFT"))
                                Đang duyệt
                            @elseif(receipt.getStatus().equals("APPROVED"))
                                Chấp nhận
                            @elseif(receipt.getStatus().equals("SHIPPED"))
                                Đang vận chuyển
                            @elseif(receipt.getStatus().equals("RECEIVED"))
                                Đã nhận
                            @elseif(receipt.getStatus().equals("CANCELLED"))
                                Từ chối
                            @elseif(receipt.getStatus().equals("CLOSED"))
                                Hoàn tất
                            @else
                                Không xác định
                            @endif
                        </strong></li>
                        <li>Kiểm tra kỹ thông tin trước khi thao tác với phiếu</li>
                        <li>Mọi thay đổi trạng thái phiếu sẽ được ghi nhận trong hệ thống</li>
                    </ul>
                </div>
            </div>
        </div>
    </main>
`,
pageScript = @`
    <script>
        function approveReceipt(id) {
            if (confirm('Xác nhận chấp nhận phiếu này?')) {
                fetch('/warehouse/receipts/' + id + '/approve', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }).then(response => {
                    if (response.ok) {
                        alert('Chấp nhận thành công!');
                        window.location.reload();
                    } else {
                        alert('Có lỗi xảy ra. Vui lòng thử lại.');
                    }
                });
            }
        }

        function shipReceipt(id) {
            if (confirm('Xác nhận gửi hàng?')) {
                fetch('/warehouse/receipts/' + id + '/ship', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }).then(response => {
                    if (response.ok) {
                        alert('Gửi hàng thành công!');
                        window.location.reload();
                    } else {
                        alert('Có lỗi xảy ra. Vui lòng thử lại.');
                    }
                });
            }
        }

        function receiveReceipt(id) {
            if (confirm('Xác nhận đã nhận phiếu này?')) {
                fetch('/warehouse/receipts/' + id + '/receive', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }).then(response => {
                    if (response.ok) {
                        alert('Xác nhận thành công!');
                        window.location.reload();
                    } else {
                        alert('Có lỗi xảy ra. Vui lòng thử lại.');
                    }
                });
            }
        }

        function cancelReceipt(id) {
            if (confirm('Xác nhận từ chối phiếu này?')) {
                fetch('/warehouse/receipts/' + id + '/cancel', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }).then(response => {
                    if (response.ok) {
                        alert('Từ chối thành công!');
                        window.location.reload();
                    } else {
                        alert('Có lỗi xảy ra. Vui lòng thử lại.');
                    }
                });
            }
        }
    </script>
`
)
