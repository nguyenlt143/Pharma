@import java.time.format.DateTimeFormatter
@param vn.edu.fpt.pharma.dto.invoice.InvoiceDetailVM invoice
@param vn.edu.fpt.pharma.config.CustomUserDetails user
@param java.util.List<vn.edu.fpt.pharma.dto.invoice.MedicineItemVM> medicines

@template.layouts.layout_test(
    headContent = @`<link rel="stylesheet" href="/assets/css/pharmacist/invoice_detail.css">`,
    content = @`
    <main class="main-content">
        <div class="content-wrapper">
            <!-- Page Header -->
            <header class="page-header">
                <h1 class="page-title">Chi tiết Hóa đơn</h1>
                <div class="page-actions">
                    <a href="/pharmacist/invoices" class="btn btn-secondary">← Quay lại</a>
                </div>
            </header>

            <!-- Invoice Info -->
            <div class="invoice-info">
                <div class="info-section">
                    <h3>Thông tin chi nhánh</h3>
                    <p><strong>Tên:</strong> ${invoice.branchName()}</p>
                    <p><strong>Địa chỉ:</strong> ${invoice.branchAddress()}</p>
                </div>

                <div class="info-section">
                    <h3>Thông tin khách hàng</h3>
                    <p><strong>Tên:</strong> ${invoice.customerName()}</p>
                    <p><strong>Số điện thoại:</strong> ${invoice.customerPhone()}</p>
                </div>

                <div class="info-section">
                    <h3>Thông tin hóa đơn</h3>
                    <p><strong>Ngày tạo:</strong> ${invoice.createdAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}</p>
                    <p><strong>Tổng tiền:</strong> ${String.format("%,.0f", invoice.totalPrice())} VNĐ</p>
                    @if(invoice.description() != null && !invoice.description().isEmpty())
                        <p><strong>Ghi chú:</strong> ${invoice.description()}</p>
                    @endif
                </div>
            </div>

            <!-- Medicine List -->
            <div class="medicine-list">
                <h3>Danh sách thuốc</h3>
                <table class="table">
                    <thead>
                        <tr>
                            <th>STT</th>
                            <th>Tên thuốc</th>
                            <th>Số lượng</th>
                            <th>Đơn giá</th>
                            <th>Thành tiền</th>
                        </tr>
                    </thead>
                    <tbody>
                        @for(var i = 0; i < medicines.size(); i++)
                            !{var medicine = medicines.get(i);}
                            <tr>
                                <td>${i + 1}</td>
                                <td>${medicine.medicineName()}</td>
                                <td>${medicine.quantity()}</td>
                                <td>${String.format("%,.0f", medicine.unitPrice())} VNĐ</td>
                                <td>${String.format("%,.0f", medicine.unitPrice() * medicine.quantity())} VNĐ</td>
                            </tr>
                        @endfor
                    </tbody>
                </table>
            </div>
        </div>
    </main>
    `,
    pageScript = @``
)
