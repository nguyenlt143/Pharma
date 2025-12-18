@import vn.edu.fpt.pharma.dto.warehouse.RequestDetailVM
@import vn.edu.fpt.pharma.dto.warehouse.RequestList
@import java.util.List

@param RequestList request
@param List<RequestDetailVM> details

@template.layouts.warehouse(
headContent = @`
    <title>Chi Tiết Yêu Cầu</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,typography"></script>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Outlined" rel="stylesheet">
`,
content = @`
    <!-- Main Content -->
    <main class="flex-1 p-6 overflow-y-auto bg-gray-100">
        <!-- Back button -->
        <div class="mb-4">
            <a href="/warehouse/request/list" class="text-blue-600 hover:text-blue-800 flex items-center gap-1">
                <span class="material-icons-outlined text-sm">arrow_back</span>
                <span>Quay lại danh sách</span>
            </a>
        </div>

        <h2 class="text-2xl font-bold mb-6">CHI TIẾT YÊU CẦU</h2>

        <!-- Thông tin yêu cầu -->
        <div class="bg-white p-6 rounded-lg shadow-md mb-6">
            <h3 class="text-lg font-semibold mb-4 flex items-center gap-2">
                <span class="material-icons-outlined text-blue-600">description</span>
                Thông tin yêu cầu
            </h3>
            <div class="grid grid-cols-2 gap-6">
                <div>
                    <p class="text-sm text-gray-600 mb-1">Chi nhánh</p>
                    <p class="font-medium">${request.branchName()}</p>
                </div>
                <div>
                    <p class="text-sm text-gray-600 mb-1">Loại yêu cầu</p>
                    <p class="font-medium">
                        <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-blue-100 text-blue-800">
                            @if(request.requestType().equals("IMPORT"))
                                Nhập hàng
                            @elseif(request.requestType().equals("RETURN"))
                                Trả hàng
                            @else
                                Không xác định
                            @endif
                        </span>
                    </p>
                </div>
                <div>
                    <p class="text-sm text-gray-600 mb-1">Trạng thái</p>
                    <p class="font-medium">
                        @if(request.requestStatus().equals("REQUESTED"))
                            <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-yellow-100 text-yellow-800">
                                <span class="material-icons-outlined text-xs mr-1">schedule</span>
                                Đang chờ duyệt
                            </span>
                        @elseif(request.requestStatus().equals("CONFIRMED"))
                            <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-green-100 text-green-800">
                                <span class="material-icons-outlined text-xs mr-1">check_circle</span>
                                Đã duyệt
                            </span>
                        @elseif(request.requestStatus().equals("RECEIVED"))
                            <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-green-100 text-green-800">
                                <span class="material-icons-outlined text-xs mr-1">check_circle</span>
                                Hoàn thành
                            </span>
                        @elseif(request.requestStatus().equals("CANCELLED"))
                            <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-red-100 text-red-800">
                                <span class="material-icons-outlined text-xs mr-1">cancel</span>
                                Đã từ chối
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
            @if(request.note() != null && !request.note().isEmpty())
                <div class="mt-4 pt-4 border-t">
                    <p class="text-sm text-gray-600 mb-1">Nội dung</p>
                    <p class="text-gray-800">${request.note()}</p>
                </div>
            @endif
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
                        <th class="px-4 py-3 text-left font-medium text-gray-600">Tên thuốc</th>
                        <th class="px-4 py-3 text-left font-medium text-gray-600">Hàm lượng</th>
                        <th class="px-4 py-3 text-left font-medium text-gray-600">Đơn vị</th>
                        <th class="px-4 py-3 text-center font-medium text-gray-600">Số lượng</th>
                    </tr>
                    </thead>
                    <tbody class="divide-y divide-gray-200">
                    @for(RequestDetailVM detail : details)
                        <tr class="hover:bg-gray-50">
                            <td class="px-4 py-3 text-gray-600">${details.indexOf(detail) + 1}</td>
                            <td class="px-4 py-3 font-medium text-gray-900">${detail.medicineName()}</td>
                            <td class="px-4 py-3 text-gray-700">${detail.strength()}</td>
                            <td class="px-4 py-3 text-gray-700">${detail.unit()}</td>
                            <td class="px-4 py-3 text-center">
                                <span class="inline-flex items-center px-3 py-1 rounded-md text-sm font-semibold bg-blue-50 text-blue-700">
                                    ${detail.quantity()}
                                </span>
                            </td>
                        </tr>
                    @endfor
                    <tr class="bg-gray-50 font-semibold">
                        <td colspan="4" class="px-4 py-3 text-right">Tổng số lượng:</td>
                        <td class="px-4 py-3 text-center text-blue-600">
                            ${details.stream().mapToLong(d -> d.quantity()).sum()} đơn vị
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>

            <!-- Action Buttons - Đặt dưới bảng theo yêu cầu FixDetail.txt -->
            <div class="mt-6 pt-6 border-t border-gray-200 flex items-center justify-end gap-3">
                @if(request.requestStatus().equals("REQUESTED"))
                    <button onclick="rejectRequest(${request.id()})"
                            class="px-6 py-2.5 bg-red-600 hover:bg-red-700 text-white font-medium rounded-lg shadow-sm transition-colors">
                        Từ Chối
                    </button>
                    <button onclick="approveRequest(${request.id()})"
                            class="px-6 py-2.5 bg-green-600 hover:bg-green-700 text-white font-medium rounded-lg shadow-sm transition-colors">
                        Đồng Ý
                    </button>
                @elseif(request.requestStatus().equals("CONFIRMED"))
                    @if(request.requestType().equals("RETURN"))
                        <button disabled
                                class="px-6 py-2.5 bg-gray-400 text-white font-medium rounded-lg shadow-sm cursor-not-allowed opacity-60">
                            Đã duyệt
                        </button>
                    @else
                        <button onclick="createExportSlip(${request.id()})"
                                class="px-6 py-2.5 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg shadow-sm transition-colors">
                            Tạo Phiếu Xuất
                        </button>
                    @endif
                @elseif(request.requestStatus().equals("RECEIVED"))
                    <button disabled
                            class="px-6 py-2.5 bg-gray-400 text-white font-medium rounded-lg shadow-sm cursor-not-allowed opacity-60">
                        Hoàn thành
                    </button>
                @elseif(request.requestStatus().equals("CANCELLED"))
                    <button disabled
                            class="px-6 py-2.5 bg-gray-400 text-white font-medium rounded-lg shadow-sm cursor-not-allowed opacity-60">
                        Đã từ chối
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
                        <li>Yêu cầu đang ở trạng thái: <strong>
                            @if(request.requestStatus().equals("REQUESTED"))
                                Đang chờ duyệt
                            @elseif(request.requestStatus().equals("CONFIRMED"))
                                Đã duyệt
                            @elseif(request.requestStatus().equals("RECEIVED"))
                                Hoàn thành
                            @elseif(request.requestStatus().equals("CANCELLED"))
                                Đã từ chối
                            @else
                                Không xác định
                            @endif
                        </strong></li>
                        <li>Kiểm tra kỹ thông tin trước khi thao tác với yêu cầu</li>
                        <li>Mọi thay đổi trạng thái yêu cầu sẽ được ghi nhận trong hệ thống</li>
                    </ul>
                </div>
            </div>
        </div>
    </main>
`,
pageScript = @`
    <script src="/assets/js/warehouse/request_detail.js" defer></script>
`
)
