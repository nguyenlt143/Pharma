@import vn.edu.fpt.pharma.dto.warehouse.RequestDetailVM
@import vn.edu.fpt.pharma.dto.warehouse.RequestList
@import java.util.List

@param RequestList request
@param List<RequestDetailVM> details

@template.layouts.warehouse(
headContent = @`
    <title>Chi Tiết Yêu Cầu</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link rel="stylesheet" href="/assets/css/warehouse/request_detail.css">
`,
content = @`
    <div class="overlay">
        <div class="modal">

            <!-- HEADER -->
            <header class="modal-header">
                <h1 class="modal-title">Chi Tiết Yêu Cầu</h1>
                <button class="close-button" onclick="closeModal()">
                    <span class="material-icons">close</span>
                </button>
            </header>

            <!-- GENERAL INFO -->
            <div class="modal-content">

                <div class="info-section">
                    <div class="info-group">
                        <label class="info-label">Tên chi nhánh</label>
                        <div class="info-value">${request.branchName()}</div>
                    </div>

                    <div class="info-group">
                        <label class="info-label">Loại yêu cầu</label>
                        <div class="info-value">
                            @if(request.requestType().equals("IMPORT"))
                                Nhập hàng
                            @elseif(request.requestType().equals("RETURN"))
                                Trả hàng
                            @else
                                Không xác định
                            @endif
                        </div>
                    </div>
                </div>

                <!-- NOTE -->
                <div class="content-section">
                    <label class="content-label">Nội dung</label>
                    <div class="content-text">
                        ${request.note()}
                    </div>
                </div>

                <!-- DETAILS TABLE -->
                <div class="medicine-section">
                    <h2 class="section-title">Danh sách thuốc</h2>

                    <table class="medicine-table">
                        <thead>
                        <tr>
                            <th>STT</th>
                            <th>Tên thuốc</th>
                            <th>Hàm lượng</th>
                            <th>Đơn vị</th>
                            <th>Số lượng</th>
                        </tr>
                        </thead>

                        <tbody>
                        @for(RequestDetailVM detail : details)
                            <tr>
                                <td>${details.indexOf(detail) + 1}</td>
                                <td>${detail.medicineName()}</td>
                                <td>${detail.strength()}</td>
                                <td>${detail.unit()}</td>
                                <td>${detail.quantity()}</td>
                            </tr>
                        @endfor
                        </tbody>
                    </table>
                </div>

            </div>

            <!-- FOOTER -->
            <footer class="modal-footer">

                @if(request.requestStatus().equals("REQUESTED"))
                    <button class="reject-button" onclick="rejectRequest(${request.id()})">Từ Chối</button>
                    <button class="approve-button" onclick="approveRequest(${request.id()})">Đồng Ý</button>
                @elseif(request.requestStatus().equals("CONFIRMED"))
                    <button class="approve-button" onclick="createExportSlip(${request.id()})">Tạo Phiếu Xuất</button>
                    <button class="approve-button disabled">Đã duyệt</button>
                @elseif(request.requestStatus().equals("CANCELLED"))
                    <button class="reject-button disabled">Đã từ chối</button>
                @else
                    <button class="reject-button disabled">Không xác định</button>
                @endif

            </footer>

        </div>
    </div>
`,
pageScript = @`
    <script src="/assets/js/warehouse/request_detail.js" defer></script>
`
)
