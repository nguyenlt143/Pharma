@param String keyword
@param Boolean inShift
@template.layouts.pharmacist(
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
                        <input type="text" class="search-input" name="keyword" placeholder="Search for medication..." value="${keyword}">
                    </label>
                    <button id="search-button" class="search-button">Search</button>
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
                    <div class="customer-info">
                        <div class="form-group">
                            <label class="form-label">Tên khách hàng</label>
                            <div class="input-wrapper">
                                <input type="text" class="form-input">
                                <button class="clear-button">
                                    <span class="material-icons">close</span>
                                </button>
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="form-label">Số điện thoại</label>
                            <div class="input-wrapper">
                                <input type="text" class="form-input">
                                <button class="clear-button">
                                    <span class="material-icons">close</span>
                                </button>
                            </div>
                        </div>
                    </div>

                    <div class="payment-details">
                        <div class="payment-row">
                            <span class="payment-label">Tổng tiền hàng</span>
                            <span class="payment-value"></span>
                        </div>

                        <div class="payment-row">
                            <span class="payment-label">Khách cần trả</span>
                            <span class="payment-value"></span>
                        </div>

                        <div class="form-group">
                            <label class="form-label">Khách thanh toán (F8)</label>
                            <input type="text" class="form-input">
                        </div>

                        <div class="form-group">
                            <label class="form-label">Phương thức thanh toán</label>
                            <select class="form-select">
                                <option>Tiền mặt</option>
                                <option>Chuyển khoản</option>
                            </select>
                        </div>

                        <div class="payment-row">
                            <span class="payment-label">Tiền thừa trả khách</span>
                            <span class="payment-value" id="changeAmount">0</span>
                        </div>

                        <div class="form-group">
                            <label class="form-label">Ghi chú</label>
                            <textarea class="form-textarea"></textarea>
                        </div>

                        <button class="payment-button" id="payButton" type="button">Thanh toán</button>
                    </div>
                </div>
            </div>
        </section>
    `,
    pageScript = @`<script src="/assets/js/pharmacist/pos.js" defer></script>`
)

