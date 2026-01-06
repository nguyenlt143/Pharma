@param String keyword
@param Boolean inShift
@param vn.edu.fpt.pharma.config.CustomUserDetails user
@template.layouts.layout_test(
    headContent = @`
        <link rel="stylesheet" href="/assets/css/pharmacist/pos.css">
        <script>
            // Set inShift status in body attribute for JS validation
            document.addEventListener('DOMContentLoaded', function() {
                document.body.setAttribute('data-in-shift', '${inShift}');
            });
        </script>
    `,
    content = @`
    @if(!inShift)
        <div class="shift-warning" id="shift-warning"
             style="
             position: fixed;
             top: 20px;
             right: 20px;
             padding: 15px 20px;
             background: #ff9800;
             color: #fff;
             border-radius: 6px;
             z-index: 9999;
             box-shadow: 0 4px 10px rgba(0,0,0,0.2);
         ">
            Bạn không trong ca làm việc! Chỉ được xem, không thể thao tác.
        </div>

        <script>
            setTimeout(() => {
                document.getElementById("shift-warning").remove();
            }, 4000);
        </script>
    @endif
        <section class="search-section">
            <div class="search-container">
                <!-- User nhập từ khóa → pos.js searchInput listener → API /pos/api/search -->
                <div class="search-input-wrapper">
                    <span class="material-icons search-icon">search</span>
                    <label>
                        <input type="text" class="search-input" name="keyword" placeholder="Tìm kiếm thuốc...." value="${keyword}">
                    </label>
                    <button id="search-button" class="search-button">Tìm kiếm</button>
                </div>

                <div id="medicine-list" class="medicine-list"></div>

            </div>
        </section>
        <!-- Right Panel -->
        <section class="right-panel">
            <!-- Prescription Section -->
            <div class="prescription-section">
                <div class="section-header">
                    <h3 class="section-title">Đơn Thuốc</h3>
                    <button type="button" class="clear-all-btn" id="clearAllBtn" title="Xóa tất cả sản phẩm">
                        <span class="material-icons">delete_sweep</span>
                        Xóa tất cả
                    </button>
                </div>

                <div class="prescription-table">
                    <table class="table">
                        <thead>
                        <tr class="table-header">
                            <th class="table-cell">#</th>
                            <th class="table-cell">Tên thuốc</th>
                            <th class="table-cell">Kiểu đóng gói</th>
                            <th class="table-cell">Số lượng</th>
                            <th class="table-cell">Giá tiền</th>
                            <th class="table-cell text-right">Thành tiền</th>
                        </tr>
                        </thead>

                        <tbody id="prescription-items">
                        </tbody>

                        <tfoot>
                        <tr class="table-footer">
                            <td colspan="5" class="table-cell text-right" style="font-weight: bold;">Tổng tiền</td>
                            <td class="table-cell text-right">
                                <div class="total-amount">0.00</div>
                                <div class="vat-info">VAT: 0 (0%)</div>
                            </td>
                        </tr>
                        </tfoot>
                    </table>
                </div>
            </div>

            <!-- Payment Section -->
            <div class="payment-section">
                <div class="payment-header">
                    <h3 class="section-title">Thanh toán</h3>
                </div>

                <div class="payment-content">
                    <!-- Error Alert -->
                    <div id="errorAlert" class="alert alert-danger" style="display: none;" role="alert">
                        <span id="errorMessage"></span>
                    </div>

                    <!-- Success Alert -->
                    <div id="successAlert" class="alert alert-success" style="display: none;" role="alert">
                        <span id="successMessage"></span>
                    </div>

                    <!-- Form thanh toán → pos.js processPayment() → API POST /pos/api/invoices -->
                    <form id="paymentForm" novalidate>
                        <div class="customer-info">
                            <div class="form-group">
                                <label class="form-label">
                                    Tên khách hàng
                                </label>
                                <div class="input-wrapper">
                                    <input type="text"
                                           class="form-input"
                                           id="customerName"
                                           name="customerName"
                                           maxlength="100"
                                           value=""
                                           placeholder="Nhập tên khách hàng (để trống = Khách lẻ)">
                                    <button type="button" class="clear-button" onclick="clearInput('customerName')">
                                        <span class="material-icons">close</span>
                                    </button>
                                </div>
                                <div class="invalid-feedback" id="customerName-error"></div>
                            </div>

                            <div class="form-group">
                                <label class="form-label">Số điện thoại</label>
                                <div class="input-wrapper">
                                    <input type="tel"
                                           class="form-input"
                                           id="phoneNumber"
                                           name="phoneNumber"
                                           title="Nhập số điện thoại hợp lệ (0xxxxxxxxx hoặc +84xxxxxxxxx) hoặc để trống"
                                           placeholder="Nhập số điện thoại (để trống = Không có)"
                                           value="">
                                    <button type="button" class="clear-button" onclick="clearInput('phoneNumber')">
                                        <span class="material-icons">close</span>
                                    </button>
                                </div>
                                <div class="invalid-feedback" id="phoneNumber-error"></div>
                            </div>
                        </div>

                        <div class="payment-details">
                            <div class="payment-row">
                                <span class="payment-label">Tổng tiền hàng</span>
                                <span class="payment-value" id="subtotal">0</span>
                            </div>

                            <div class="payment-row">
                                <span class="payment-label">Khách cần trả</span>
                                <span class="payment-value" id="totalAmount">0</span>
                            </div>

                            <div class="form-group" id="paidAmountGroup">
                                <label class="form-label">
                                    Khách thanh toán (F8) <span class="required">*</span>
                                </label>
                                <input type="number"
                                       class="form-input"
                                       id="paidAmount"
                                       name="paidAmount"
                                       step="0.01"
                                       min="0"
                                       required
                                       placeholder="0.00">
                                <div class="invalid-feedback" id="paidAmount-error"></div>
                            </div>

                            <div class="form-group">
                                <label class="form-label">
                                    Phương thức thanh toán <span class="required">*</span>
                                </label>
                                <select class="form-select" id="paymentMethod" name="paymentMethod" required>
                                    <option value="cash" selected>Tiền mặt</option>
                                    <option value="transfer">Chuyển khoản</option>
                                </select>
                                <div class="invalid-feedback" id="paymentMethod-error"></div>
                            </div>


                            <div class="payment-row" id="changeAmountRow">
                                <span class="payment-label">Tiền thừa trả khách</span>
                                <span class="payment-value" id="changeAmount">0</span>
                            </div>

                            <div class="form-group">
                                <label class="form-label">Ghi chú</label>
                                <textarea class="form-textarea"
                                         id="note"
                                         name="note"
                                         maxlength="500"
                                         placeholder="Ghi chú thêm (không bắt buộc)"></textarea>
                                <div class="invalid-feedback" id="note-error"></div>
                            </div>

                            <button class="payment-button"
                                   id="payButton"
                                   type="submit"
                                   disabled>
                                   Thanh toán
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </section>

        <!-- QR Code Modal Popup -->
        <div id="qrModal" class="qr-modal" style="display: none;">
            <div class="qr-modal-overlay"></div>
            <div class="qr-modal-content">
                <div class="qr-modal-header">
                    <h3 class="qr-modal-title">
                        <span class="material-icons">qr_code_2</span>
                        Quét mã QR để thanh toán
                    </h3>
                    <button class="qr-modal-close" id="closeQrModal" title="Đóng">
                        <span class="material-icons">close</span>
                    </button>
                </div>

                <div class="qr-modal-body">
                    <div class="qr-info-badge">
                        <span class="material-icons">info</span>
                        <span>Số tiền và nội dung chuyển khoản đã được điền sẵn</span>
                    </div>

                    <div class="qr-code-display">
                        <div class="qr-code-wrapper">
                            <img id="qrCodeImage"
                                 src=""
                                 alt="QR Code"
                                 class="qr-code-img">
                            <div class="qr-loading" id="qrLoading">
                                <div class="spinner"></div>
                                <span>Đang tạo mã QR...</span>
                            </div>
                        </div>
                    </div>

                    <div class="qr-amount-highlight">
                        <span class="qr-amount-label">Số tiền thanh toán</span>
                        <span class="qr-amount-value" id="qrDisplayAmount">0 ₫</span>
                    </div>

                    <div class="qr-bank-details">
                        <div class="qr-bank-row">
                            <span class="qr-bank-label">
                                <span class="material-icons">account_balance</span>
                                Ngân hàng
                            </span>
                            <span class="qr-bank-value" id="qrBankName">Techcombank (TCB)</span>
                        </div>
                        <div class="qr-bank-row">
                            <span class="qr-bank-label">
                                <span class="material-icons">credit_card</span>
                                Số tài khoản
                            </span>
                            <span class="qr-bank-value" id="qrAccountNumber">19038197626011</span>
                        </div>
                        <div class="qr-bank-row">
                            <span class="qr-bank-label">
                                <span class="material-icons">person</span>
                                Chủ tài khoản
                            </span>
                            <span class="qr-bank-value" id="qrAccountName">LE TUNG NGUYEN</span>
                        </div>
                        <div class="qr-bank-row">
                            <span class="qr-bank-label">
                                <span class="material-icons">description</span>
                                Nội dung
                            </span>
                            <span class="qr-bank-value" id="qrInvoiceCode">HD...</span>
                        </div>
                    </div>

                    <div class="qr-instructions-compact">
                        <div class="instruction-step">
                            <span class="step-number">1</span>
                            <span>Mở app ngân hàng</span>
                        </div>
                        <div class="instruction-step">
                            <span class="step-number">2</span>
                            <span>Quét mã QR</span>
                        </div>
                        <div class="instruction-step">
                            <span class="step-number">3</span>
                            <span>Xác nhận thanh toán</span>
                        </div>
                    </div>
                </div>

                <div class="qr-modal-footer">
                    <button class="qr-btn-primary" id="closeQrModalBtn">
                        <span class="material-icons">close</span>
                        Đóng
                    </button>
                </div>
            </div>
        </div>
    `,
    pageScript = @`
        <script>
            // Current user info for invoice
            window.currentUserName = '${user.getUser().getFullName()}';
        </script>
        <script src="/assets/js/pharmacist/pos.js" defer></script>
    `
)

