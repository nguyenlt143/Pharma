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
            <header class="modal-header">
                <h1 class="modal-title">Chi Tiết Yêu Cầu</h1>
                <button class="close-button" onclick="closeModal()">
                    <span class="material-icons">close</span>
                </button>
            </header>

            <div class="modal-content">
                <div class="info-section">
                    <div class="info-group">
                        <label class="info-label">Tên chi nhánh</label>
                        <div class="info-value">Hoàng Mai</div>
                    </div>
                    <div class="info-group">
                        <label class="info-label">Loại yêu cầu</label>
                        <div class="info-value">Nhập hàng</div>
                    </div>
                </div>

                <div class="content-section">
                    <label class="content-label">Nội dung</label>
                    <div class="content-text">
                        Yêu cầu nhập thêm một số loại thuốc phổ biến do nhu cầu tăng cao trong mùa dịch.
                    </div>
                </div>

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
                        <tr>
                            <td>1</td>
                            <td>Paracetamol</td>
                            <td>500mg</td>
                            <td>Hộp</td>
                            <td>50</td>
                        </tr>
                        <tr>
                            <td>2</td>
                            <td>Berberin</td>
                            <td>50mg</td>
                            <td>Chai</td>
                            <td>100</td>
                        </tr>
                        <tr>
                            <td>3</td>
                            <td>Oresol</td>
                            <td>20mg</td>
                            <td>Gói</td>
                            <td>200</td>
                        </tr>
                        </tbody>
                    </table>
                </div>

                <div class="notes-section">
                    <label class="notes-label">Ghi chú</label>
                    <textarea class="notes-textarea" placeholder="Thêm ghi chú..."></textarea>
                </div>
            </div>

            <footer class="modal-footer">
                <button class="reject-button" onclick="rejectRequest()">Từ Chối</button>
                <button class="approve-button" onclick="createExportSlip()">Tạo Phiếu Xuất</button>
            </footer>
        </div>
    </div>
`,
pageScript = @`
    <script src="/assets/js/warehouse/request_detail.js" defer></script>
`
)
