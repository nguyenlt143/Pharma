
@template.layouts.warehouse(
headContent = @`
    <title>Phiếu xuất kho</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link rel="stylesheet" href="/assets/css/warehouse/export_create.css">
`,
content = @`

    <div class="container">
        <div class="frame">
            <main class="main-content">
                <h1 class="heading">Tạo Phiếu Xuất Kho</h1>
                <p class="subtitle">Điền thông tin phiếu và danh sách thuốc cần xuất.</p>

                <div class="form-section">
                    <div class="form-row">
                        <div class="form-group">
                            <label class="form-label">Mã phiếu</label>
                            <input type="text" class="form-input" value="PXK20240521001" readonly>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Ngày tạo phiếu</label>
                            <div class="date-input">
                                <input type="text" class="form-input date-field" value="05/21/2024" readonly>
                                <span class="calendar-icon">
                  <img src="https://static.codia.ai/image/2025-11-05/Pq9eykSDb6.png" alt="Calendar" width="20" height="20">
                </span>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Chi nhánh nhận</label>
                            <select class="form-select">
                                <option value="hoang-mai">Hoàng Mai</option>
                            </select>
                        </div>
                    </div>

                    <div class="form-group full-width">
                        <label class="form-label">Ghi chú</label>
                        <textarea class="form-textarea" placeholder="Thêm ghi chú nếu cần"></textarea>
                    </div>
                </div>

                <div class="table-section">
                    <div class="search-container">
                        <div class="search-input">
                            <span class="material-icons search-icon">search</span>
                            <input type="text" placeholder="Tìm kiếm thuốc">
                        </div>
                    </div>

                    <div class="table-container">
                        <table class="medicine-table">
                            <thead>
                            <tr>
                                <th>STT</th>
                                <th>Tên thuốc</th>
                                <th>ĐVT</th>
                                <th>Hàm lượng</th>
                                <th>S. Lg yêu cầu</th>
                                <th>Số lượng gửi</th>
                                <th>Giá Bán</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr>
                                <td class="row-number">1</td>
                                <td class="medicine-name">Amoxicillin</td>
                                <td class="unit">Hộp</td>
                                <td class="dosage">250mg</td>
                                <td class="requested-qty">1000</td>
                                <td class="sent-qty">
                                    <input type="number" class="qty-input" value="1000">
                                </td>
                                <td class="price"></td>
                            </tr>
                            <tr>
                                <td class="row-number">2</td>
                                <td class="medicine-name">Paracetamol</td>
                                <td class="unit">Viên</td>
                                <td class="dosage">500mg</td>
                                <td class="requested-qty">2000</td>
                                <td class="sent-qty">
                                    <input type="number" class="qty-input" value="500">
                                </td>
                                <td class="price"></td>
                            </tr>
                            <tr>
                                <td class="row-number">3</td>
                                <td class="medicine-name">Ibuprofen</td>
                                <td class="unit">Gói</td>
                                <td class="dosage">200mg</td>
                                <td class="requested-qty">1500</td>
                                <td class="sent-qty">
                                    <input type="number" class="qty-input" value="200">
                                </td>
                                <td class="price"></td>
                            </tr>
                            <tr>
                                <td class="row-number">4</td>
                                <td class="medicine-name">Ciprofloxacin</td>
                                <td class="unit">Hộp</td>
                                <td class="dosage">500mg</td>
                                <td class="requested-qty">500</td>
                                <td class="sent-qty">
                                    <input type="number" class="qty-input" value="50">
                                </td>
                                <td class="price"></td>
                            </tr>
                            <tr>
                                <td class="row-number">5</td>
                                <td class="medicine-name">Omeprazole</td>
                                <td class="unit">Hộp</td>
                                <td class="dosage">20mg</td>
                                <td class="requested-qty">1200</td>
                                <td class="sent-qty">
                                    <input type="number" class="qty-input" value="20">
                                </td>
                                <td class="price"></td>
                            </tr>
                            </tbody>
                        </table>
                    </div>

                    <div class="action-buttons">
                        <button class="btn btn-secondary">Lưu nháp</button>
                        <button class="btn btn-primary">Tạo phiếu xuất</button>
                    </div>
                </div>
            </main>
        </div>
    </div>
`,
pageScript = @`
    <script src="/assets/js/warehouse/export_create.js" defer></script>
`
)

