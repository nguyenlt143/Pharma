@param String keyword
@param Boolean inShift
@template.layouts.layout_test(
    headContent = @`<link rel="stylesheet" href="/assets/css/pharmacist/pos.css">`,
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
                            <td colspan="4" class="table-cell text-right">Tổng tiền</td>
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
                    <span class="payment-time">10/08/2022 09:55</span>
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
                                           pattern="^(0|\+84)[0-9]{9,10}$"
                                           title="Số điện thoại phải bắt đầu bằng 0 hoặc +84 và có 10-11 chữ số"
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

                            <div class="form-group">
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
                                    <option value="">-- Chọn phương thức --</option>
                                    <option value="cash">Tiền mặt</option>
                                    <option value="transfer">Chuyển khoản</option>
                                </select>
                                <div class="invalid-feedback" id="paymentMethod-error"></div>
                            </div>

                            <!-- QR Code Section for Bank Transfer -->
                            <div id="qrCodeSection" class="qr-code-section" style="display: none;">
                                <div class="qr-header">
                                    <h4 class="qr-title">Vui lòng quét mã QR bên dưới để thanh toán</h4>
                                    <p class="qr-subtitle">Số tiền và nội dung chuyển khoản đã được điền sẵn, bạn không cần nhập lại.</p>
                                </div>

                                <div class="qr-code-container">
                                    <div class="qr-placeholder">
                                        <img id="qrCodeImage" src="data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgdmlld0JveD0iMCAwIDIwMCAyMDAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CjxyZWN0IHdpZHRoPSIyMDAiIGhlaWdodD0iMjAwIiBmaWxsPSJ3aGl0ZSIvPgo8cGF0aCBkPSJNNTAgNTBIMTUwVjE1MEg1MFY1MFoiIGZpbGw9ImJsYWNrIi8+CjxyZWN0IHg9IjcwIiB5PSI3MCIgd2lkdGg9IjYwIiBoZWlnaHQ9IjYwIiBmaWxsPSJ3aGl0ZSIvPgo8L3N2Zz4="
                                             alt="QR Code"
                                             class="qr-code-img">
                                    </div>
                                </div>

                                <div class="qr-instructions">
                                    <ul class="instruction-list">
                                        <li>
                                            <span class="material-icons">check_circle</span>
                                            Hệ thống tự điền đúng số tiền
                                        </li>
                                        <li>
                                            <span class="material-icons">check_circle</span>
                                            Không cần nhập nội dung hay số tài khoản
                                        </li>
                                        <li>
                                            <span class="material-icons">check_circle</span>
                                            Chỉ cần quét và xác nhận trên ứng dụng ngân hàng
                                        </li>
                                    </ul>
                                </div>

                                <div class="qr-bank-info" style="margin-top: 16px; padding: 12px; background: rgba(59, 130, 246, 0.1); border-radius: 8px; border: 1px solid #3b82f6;">
                                    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 8px; font-size: 14px; color: #1e293b;">
                                        <div><strong>Ngân hàng:</strong> Vietcombank (VCB)</div>
                                        <div><strong>Số TK:</strong> <span id="qrAccountNumber">0123456789</span></div>
                                        <div><strong>Tên TK:</strong> Nhà Thuốc ABC</div>
                                        <div><strong>Nội dung:</strong> <span id="qrInvoiceCode">HD...</span></div>
                                    </div>
                                </div>

                                <div class="qr-amount-display" style="margin-top: 12px; padding: 12px; background: rgba(34, 197, 94, 0.1); border-radius: 8px; border: 1px solid #22c55e;">
                                    <strong style="color: #1e293b; font-size: 16px;">Tổng tiền: <span id="qrDisplayAmount">0</span> VNĐ</strong>
                                </div>
                            </div>

                            <div class="payment-row">
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
    `,
    pageScript = @`<script src="/assets/js/pharmacist/pos.js" defer></script>`
)

