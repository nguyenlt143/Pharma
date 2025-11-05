@template.layouts.pharmacist(
headContent = @`<link rel="stylesheet" href="/assets/css/pharmacist/invoice_detail.css">`,
content = @`
    <div class="invoice-container">
        <!-- Header Section -->
        <header class="invoice-header">
            <h1 class="pharmacy-name">NHÀ THUỐC Demo</h1>
            <p class="pharmacy-address">Địa chỉ: 225 Nguyễn Lương Bằng</p>
        </header>

        <!-- Invoice Title Section -->
        <section class="invoice-title-section">
            <h2 class="invoice-title">HOÁ ĐƠN THANH TOÁN</h2>
            <p class="invoice-date">Ngày 18 tháng 10 năm 2025</p>
        </section>

        <!-- Customer and Staff Info -->
        <section class="info-section">
            <div class="customer-info">
                <p class="customer-name">Khách hàng: <span class="name-value">TRẦN VĂN A</span></p>
                <p class="customer-phone">Điện thoại:</p>
            </div>
            <div class="staff-info">
                <p class="staff-name">NV bán hàng: <span class="name-value">DS Hoàng Thị Thu Trang</span></p>
            </div>
        </section>

        <!-- Invoice Table -->
        <section class="table-section">
            <table class="invoice-table">
                <thead>
                <tr class="table-header">
                    <th class="col-stt">ST</th>
                    <th class="col-medicine">Tên Thuốc</th>
                    <th class="col-unit">KĐG</th>
                    <th class="col-quantity">Số lượng</th>
                    <th class="col-price">Đơn giá</th>
                    <th class="col-total">Thành tiền</th>
                </tr>
                </thead>
                <tbody>
                <tr class="table-row">
                    <td class="col-stt">1</td>
                    <td class="col-medicine">Bestimac Q10 30mg (H/ 60v)</td>
                    <td class="col-unit">viên</td>
                    <td class="col-quantity">30</td>
                    <td class="col-price">4.950</td>
                    <td class="col-total">148.500</td>
                </tr>
                <tr class="table-row">
                    <td class="col-stt">2</td>
                    <td class="col-medicine">Devodil 50mg (Hộp 2 vỉ x 10 viên) 2018</td>
                    <td class="col-unit">viên</td>
                    <td class="col-quantity">60</td>
                    <td class="col-price">2.860</td>
                    <td class="col-total">171.600</td>
                </tr>
                </tbody>
            </table>
        </section>

        <!-- Total Section -->
        <section class="total-section">
            <div class="total-row">
                <span class="total-label">Tổng tiền:</span>
                <span class="total-amount">320.100</span>
            </div>
        </section>

        <!-- Notes Section -->
        <section class="notes-section">
            <p class="notes-label">Ghi chú :</p>
        </section>

        <!-- Signature Section -->
        <section class="signature-section">
            <div class="seller-signature">
                <p class="signature-title">Người bán</p>
                <p class="signature-name">DS Hoàng Thị Thu Trang</p>
            </div>
        </section>

        <!-- Action Buttons -->
        <section class="action-buttons">
            <button class="print-button" onclick="printInvoice()">
                <span class="button-text">IN ĐƠN</span>
            </button>
            <button class="back-button" onclick="goBack()">
                <span class="material-icons">arrow_back</span>
            </button>
        </section>
    </div>
`,
pageScript = @` <script src="/assets/js/pharmacist/invoice_detail.js" defer></script>`
)
