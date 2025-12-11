@param vn.edu.fpt.pharma.dto.warehouse.InventoryMovementVM inventoryMovementVM
@param java.util.List<vn.edu.fpt.pharma.dto.warehouse.InventoryMovementDetailVM> inventoryMovementDetails

@template.layouts.warehouse(
headContent = @`
    <link rel="stylesheet" href="/assets/css/warehouse/receipt_create.css">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
`,
pageScript = @`
    <script src="/assets/js/warehouse/receipt_create.js" defer></script>
`,
content = @`
        <header class="page-header">
            <h1 class="page-title">Nhập từ nhà cung cấp</h1>
        </header>

        <section class="form-section">
            <div class="form-container">
                <div class="form-row">
                    <div class="form-group">
                        <label for="supplier" class="form-label">Nhà cung cấp (F4) *</label>
                        <div class="input-wrapper">
                            <span class="material-icons input-icon">search</span>
                            <input type="text"
                                   id="supplier"
                                   class="form-input"
                                   data-supplier-id=""
                                   value=""
                                   placeholder="Chọn nhà cung cấp"
                                   required>
                        </div>
                    </div>
<%--                    <div class="form-group">--%>
<%--                        <label for="receipt-code" class="form-label">Mã phiếu nhập</label>--%>
<%--                        <input type="text"--%>
<%--                               id="receipt-code"--%>
<%--                               class="form-input disabled"--%>
<%--                               value="Tự động tạo"--%>
<%--                               disabled>--%>
<%--                    </div>--%>
                    <div class="form-group">
                        <label for="import-date" class="form-label">Ngày nhập *</label>
                        <input type="date"
                               id="import-date"
                               class="form-input"
                               value=""
                               required>
                    </div>
                </div>
            </div>
        </section>

        <section class="product-section">
            <div class="product-container">
                <div class="search-add-row">
<%--                    <div class="search-wrapper">--%>
<%--                        <span class="material-icons search-icon">search</span>--%>
<%--                        <input type="text" class="search-input" placeholder="Tìm kiếm thuốc">--%>
<%--                    </div>--%>
                    <button class="add-button" type="button" id="btnAddFromCatalog">
                        <span class="material-icons">add</span>
                        <span>THÊM THUỐC VÀO DANH SÁCH (F7)</span>
                    </button>
                </div>

                <div class="table-container">
                    <table class="product-table">
                        <thead>
                        <tr>
                            <th>STT</th>
                            <th>Tên thuốc</th>
                            <th>ĐVT</th>
                            <th>S.lg/Hộp</th>
                            <th>Hàm lượng</th>
                            <th>Số lô</th>
                            <th>NSX</th>
                            <th>HSD</th>
                            <th>Số lượng</th>
                            <th>Giá nhập(VNĐ)</th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody id="productTableBody">
                        <!-- Product rows will be added dynamically via JavaScript -->
                        </tbody>
                    </table>
                </div>

                <div class="total-section">
                    <span class="total-label">TỔNG SỐ TIỀN:</span>
                    <span class="total-amount" id="totalAmount">0</span>
                    <span class="total-currency">VNĐ</span>
                </div>
            </div>
        </section>

        <div class="action-buttons">
            <button class="btn-secondary" type="button" id="btnCancel">HỦY</button>
            <button class="btn-primary" type="button" id="btnComplete">HOÀN THÀNH</button>
        </div>
    </main>
`
)
