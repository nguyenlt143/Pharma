@import vn.edu.fpt.pharma.dto.warehouse.ReceiptDetailVM
@import vn.edu.fpt.pharma.dto.warehouse.ReceiptInfo
@import java.util.List

@param ReceiptInfo receipt
@param List<ReceiptDetailVM> details

@template.layouts.warehouse(
headContent = @`
    <title>Chi Tiết Phiếu</title>
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
                <h1 class="modal-title">Chi Tiết Phiếu</h1>
                <a href="/warehouse/receipt-list" class="close-button">
                    <span class="material-icons">close</span>
                </a>
            </header>

            <!-- GENERAL INFO -->
            <div class="modal-content">

                <div class="info-section">
                    <div class="info-group">
                        <label class="info-label">Tên chi nhánh</label>
                        <div class="info-value">${receipt.getBranchName()}</div>
                    </div>

                    <div class="info-group">
                        <label class="info-label">Loại Phiếu</label>
                        <div class="info-value">
                            @if(receipt.getType().equals("SUP_TO_WARE"))
                                Phiếu Nhập
                            @elseif(receipt.getType().equals("WARE_TO_BR"))
                                Phiếu Xuất
                            @elseif(receipt.getType().equals("BR_TO_WARE"))
                                Chuyển Về Kho Tổng
                            @elseif(receipt.getType().equals("WARE_TO_SUP"))
                                Trả Hàng
                            @elseif(receipt.getType().equals("DISPOSAL"))
                                Xuất Hủy
                            @else
                                Không xác định
                            @endif
                        </div>
                    </div>

<%--                    <div class="info-group">--%>
<%--                        <label class="info-label">Trạng Thái</label>--%>
<%--                        <div class="info-value">--%>
<%--                            @if(receipt.getStatus().equals("DRAFT"))--%>
<%--                                Đang duyệt--%>
<%--                            @elseif(receipt.getStatus().equals("APPROVED"))--%>
<%--                                Chấp nhận--%>
<%--                            @elseif(receipt.getStatus().equals("SHIPPED"))--%>
<%--                                Đang vận chuyển--%>
<%--                            @elseif(receipt.getStatus().equals("RECEIVED"))--%>
<%--                                Đã nhận--%>
<%--                            @elseif(receipt.getStatus().equals("CANCELLED"))--%>
<%--                                Từ chối--%>
<%--                            @elseif(receipt.getStatus().equals("CLOSED"))--%>
<%--                                Hoàn tất--%>
<%--                            @else--%>
<%--                                Không xác định--%>
<%--                            @endif--%>
<%--                        </div>--%>
<%--                    </div>--%>
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
                        @for(ReceiptDetailVM detail : details)
                            <tr>
                                <td>${details.indexOf(detail) + 1}</td>
                                <td>${detail.getMedicineName()}</td>
                                <td>${detail.getConcentration()}</td>
                                <td>${detail.getUnit()}</td>
                                <td>${detail.getQuantity()}</td>
                            </tr>
                        @endfor
                        </tbody>
                    </table>
                </div>

            </div>

            <!-- FOOTER -->
            <footer class="modal-footer">
                <div class="footer-status">
                    <span class="status-label">Trạng thái: </span>
                    <span class="status-value">
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
                    </span>
                </div>
                <div class="footer-actions">
                    @if(receipt.getStatus().equals("DRAFT"))
                        <button class="approve-button" onclick="approveReceipt(${receipt.getId()})">Chấp nhận</button>
                        <button class="cancel-button" onclick="cancelReceipt(${receipt.getId()})">Từ chối</button>
                    @elseif(receipt.getStatus().equals("APPROVED"))
                        <button class="ship-button" onclick="shipReceipt(${receipt.getId()})">Gửi hàng</button>
                    @elseif(receipt.getStatus().equals("SHIPPED"))
                        <button class="receive-button" onclick="receiveReceipt(${receipt.getId()})">Đã nhận</button>
                    @elseif(receipt.getStatus().equals("RECEIVED"))
                        <button class="approve-button disabled" disabled>Đã nhận</button>
                    @elseif(receipt.getStatus().equals("CANCELLED"))
                        <button class="approve-button disabled" disabled>Đã từ chối</button>
                    @elseif(receipt.getStatus().equals("CLOSED"))
                        <button class="approve-button disabled" disabled>Đã hoàn tất</button>
                    @endif
                </div>
            </footer>

        </div>
    </div>
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

