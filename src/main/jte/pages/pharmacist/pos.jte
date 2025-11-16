@template.layouts.pharmacist(
headContent = @`<link rel="stylesheet" href="/assets/css/pharmacist/pos.css">`,
    content = @`
        <section class="search-section">
            <div class="search-container">

                <div class="search-input-wrapper">
                    <span class="material-icons search-icon">search</span>
                    <input type="text" class="search-input" placeholder="Search for medication...">
                    <button class="search-button">Search</button>
                </div>

                <div class="medicine-list">
                    <div class="medicine-card">
                        <h3 class="medicine-name">Hapacol 250 - Gói</h3>
                        <p class="medicine-batch">Số lô: <span class="batch-number">230521</span> Hạn dùng: <span class="expiry-date">24/05/2023</span></p>
                        <p class="medicine-stock">Tồn kho: <span class="stock-info">Hộp 24 gói x 1.5g thuốc bột</span></p>
                        <p class="medicine-ingredient">Hoạt chất chính: <span class="ingredient-info">paracetamol 250 mg</span></p>
                        <p class="medicine-manufacturer">Nhà sản xuất: <span class="manufacturer-info">Công ty cổ phần dược phẩm DHG</span></p>
                        <p class="medicine-packaging">Quy cách đóng gói: <span class="packaging-info">Hộp 24 gói x 1.5g thuốc bột</span></p>
                        <p class="medicine-origin">Nước sản xuất:</p>
                        <p class="medicine-usage">Cách dùng:</p>
                        <p class="medicine-contraindication">Chống chỉ định:</p>
                        <p class="medicine-storage">Bảo quản:</p>
                    </div>

                    <div class="medicine-card">
                        <h3 class="medicine-name">Coldacmin Flu - Hộp</h3>
                        <p class="medicine-batch">Số lô: <span class="batch-number">623458</span> Hạn dùng: <span class="expiry-date">01/06/2023</span></p>
                        <p class="medicine-stock">Tồn kho: <span class="stock-info">Hộp 10 vỉ x 10 viên</span></p>
                        <p class="medicine-ingredient">Hoạt chất chính: <span class="ingredient-info">paracetamol, chlorpheniramin maleat - 325mg, 2mg</span></p>
                        <p class="medicine-manufacturer">Nhà sản xuất: <span class="manufacturer-info">Công ty TNHH một thành viên dược phẩm và sinh học y tế</span></p>
                        <p class="medicine-packaging">Quy cách đóng gói: <span class="packaging-info">Hộp 24 gói x 1.5g thuốc bột</span></p>
                        <p class="medicine-origin">Nước sản xuất:</p>
                        <p class="medicine-usage">Cách dùng:</p>
                        <p class="medicine-contraindication">Chống chỉ định:</p>
                        <p class="medicine-storage">Bảo quản:</p>
                    </div>
                </div>

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
                            <th class="table-cell text-right">Giá bán</th>
                            <th class="table-cell text-right">Thành tiền</th>
                        </tr>
                        </thead>

                        <tbody>
                        <tr>
                            <td>1</td>
                            <td>
                                <div class="medicine-info">
                                    <div class="medicine-title">Hapacol 250 Hạn dùng: 24/05/2023</div>
                                    <div class="medicine-detail">Quy cách đóng gói: Hộp 24 gói x 1.5g thuốc bột</div>
                                    <div class="medicine-detail">Hoạt chất: paracetamol - 250 mg</div>
                                </div>
                            </td>
                            <td>Gói</td>
                            <td>2</td>
                            <td>1,000.00</td>
                            <td>
                                <div class="price-info">
                                    <div class="total-price">2,000.00</div>
                                    <div class="vat-info">VAT: 0 (0%)</div>
                                </div>
                            </td>
                            <td>
                                <button class="delete-button">
                                    <span class="material-icons">delete_outline</span>
                                </button>
                            </td>
                        </tr>
                        <tr>
                            <td>2</td>
                            <td>
                                <div class="medicine-info">
                                    <div class="medicine-title">Allergan Hạn dùng: 24/05/2023</div>
                                    <div class="medicine-detail">Quy cách đóng gói: Hộp 10 vỉ x 10 viên nang cứng</div>
                                    <div class="medicine-detail">Hoạt chất: paracetamol - 500mg</div>
                                </div>
                            </td>
                            <td>Viên</td>
                            <td>3</td>
                            <td>1,000.00</td>
                            <td>
                                <div class="price-info">
                                    <div class="total-price">3,000.00</div>
                                    <div class="vat-info">VAT: 0 (0%)</div>
                                </div>
                            </td>
                            <td>
                                <button class="delete-button">
                                    <span class="material-icons">delete_outline</span>
                                </button>
                            </td>
                        </tr>
                        <tr>
                            <td>3</td>
                            <td>
                                <div class="medicine-info">
                                    <div class="medicine-title">Another Med Hạn dùng: 24/05/2023</div>
                                    <div class="medicine-detail">Quy cách đóng gói: Hộp 10 vỉ x 10 viên nang cứng</div>
                                    <div class="medicine-detail">Hoạt chất: paracetamol - 500mg</div>
                                </div>
                            </td>
                            <td>Viên</td>
                            <td>1</td>
                            <td>2,500.00</td>
                            <td>
                                <div class="price-info">
                                    <div class="total-price">2,500.00</div>
                                    <div class="vat-info">VAT: 0 (0%)</div>
                                </div>
                            </td>
                            <td>
                                <button class="delete-button">
                                    <span class="material-icons">delete_outline</span>
                                </button>
                            </td>
                        </tr>
                        <tr>
                            <td>4</td>
                            <td>
                                <div class="medicine-info">
                                    <div class="medicine-title">Yet Another Med Hạn dùng: 24/05/2023</div>
                                    <div class="medicine-detail">Quy cách đóng gói: Hộp 10 vỉ x 10 viên nang cứng</div>
                                    <div class="medicine-detail">Hoạt chất: paracetamol - 500mg</div>
                                </div>
                            </td>
                            <td>Viên</td>
                            <td>5</td>
                            <td>500.00</td>
                            <td>
                                <div class="price-info">
                                    <div class="total-price">2,500.00</div>
                                    <div class="vat-info">VAT: 0 (0%)</div>
                                </div>
                            </td>
                            <td>
                                <button class="delete-button">
                                    <span class="material-icons">delete_outline</span>
                                </button>
                            </td>
                        </tr>
                        </tbody>

                        <tfoot>
                        <tr class="table-footer">
                            <td colspan="5" class="table-cell text-right">Tổng tiền</td>
                            <td class="table-cell text-right">
                                <div class="total-amount"></div>
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
                            </select>
                        </div>

                        <div class="payment-row">
                            <span class="payment-label">Tiền thừa trả khách</span>
                            <span class="payment-value">0.00</span>
                        </div>

                        <div class="form-group">
                            <label class="form-label">Ghi chú</label>
                            <textarea class="form-textarea"></textarea>
                        </div>

                        <button class="payment-button">Thanh Toán</button>
                    </div>
                </div>
            </div>
        </section>
    `,
    pageScript = @`<script src="/assets/js/pharmacist/pos.js" defer></script>`
)

