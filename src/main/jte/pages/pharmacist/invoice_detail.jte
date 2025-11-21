@param vn.edu.fpt.pharma.dto.invoice.InvoiceDetailVM invoice
@param java.util.List<vn.edu.fpt.pharma.dto.invoice.MedicineItemVM> medicines
@param vn.edu.fpt.pharma.config.CustomUserDetails user

@template.layouts.pharmacist(
headContent = @`<link rel="stylesheet" href="/assets/css/pharmacist/invoice_detail.css">`,
content = @`
    <div class="invoice-container">
        <!-- Header Section -->
        <header class="invoice-header">
            <h1 class="pharmacy-name">${invoice.branchName()}</h1>
            <p class="pharmacy-address">${invoice.branchAddress()}</p>
        </header>

        <!-- Invoice Title Section -->
        <section class="invoice-title-section">
            <h2 class="invoice-title">HOÁ ĐƠN THANH TOÁN</h2>
            <p class="invoice-date">
                ${invoice.invoiceDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}
            </p>
        </section>

        <!-- Customer and Staff Info -->
        <section class="info-section">
            <div class="customer-info">
                <p class="customer-name">Khách hàng: <span class="name-value">${invoice.customerName()}</span></p>
                <p class="customer-phone">Điện thoại:${invoice.customerPhone()}</p>
            </div>
            <div class="staff-info">
                <p class="staff-name">NV bán hàng: <span class="name-value">${user.getFullName()}</span></p>
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
                @for(int i = 0; i < medicines.size(); i++)
                    <tr class="table-row">
                        <td class="col-stt">${i + 1}</td>
                        <td class="col-medicine">${medicines.get(i).medicineName()}</td>
                        <td class="col-unit">${medicines.get(i).unit()}</td>
                        <td class="col-quantity">${medicines.get(i).quantity()}</td>
                        <td class="col-price">${String.format("%,.0f", medicines.get(i).costPrice())}</td>
                        <td class="col-total">${String.format("%,.0f", medicines.get(i).costPrice() * medicines.get(i).quantity())}</td>
                    </tr>
                @endfor
                </tbody>
            </table>
        </section>

        <!-- Total Section -->
        <section class="total-section">
            <div class="total-row">
                <span class="total-label">Tổng tiền:</span>
                <span class="total-amount">${invoice.totalAmount()}</span>
            </div>
        </section>

        <!-- Notes Section -->
        <section class="notes-section">
            <p class="notes-label">Ghi chú: ${invoice.note()}</p>
        </section>

        <!-- Signature Section -->
        <section class="signature-section">
            <div class="seller-signature">
                <p class="signature-title">Người bán</p>
                <p class="signature-name">${user.getFullName()}</p>
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
