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
                                    Tên khách hàng <span class="required">*</span>
                                </label>
                                <div class="input-wrapper">
                                    <input type="text"
                                           class="form-input"
                                           id="customerName"
                                           name="customerName"
                                           required
                                           maxlength="100"
                                           placeholder="Nhập tên khách hàng">
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
                                           placeholder="Nhập số điện thoại">
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

