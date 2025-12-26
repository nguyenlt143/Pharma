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
                               readonly
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

        <!-- Modal nhập thông tin thuốc -->
        <div id="productDetailModal" class="modal-overlay" style="display: none;">
            <div class="modal-content" style="max-width: 900px; max-height: 90vh; overflow-y: auto;">
                <div class="modal-header" style="position: sticky; top: 0; background: white; z-index: 10; padding: 20px 24px; border-bottom: 2px solid #e5e7eb;">
                    <h3 id="modalProductName" style="font-size: 20px; font-weight: 700; color: #111827; margin: 0;">Nhập thông tin thuốc</h3>
                    <button class="modal-close" onclick="closeProductDetailModal()" style="font-size: 28px; color: #6b7280; cursor: pointer; border: none; background: none; padding: 0; line-height: 1;">&times;</button>
                </div>
                <div class="modal-body" style="padding: 24px;">
                    <form id="productDetailForm">
                        <!-- Thông tin đơn vị -->
                        <div style="background: #f9fafb; border: 1px solid #e5e7eb; border-radius: 8px; padding: 20px; margin-bottom: 24px;">
                            <h4 style="font-size: 16px; font-weight: 600; color: #374151; margin: 0 0 16px 0;">📊 Thông tin đơn vị và quy đổi</h4>
                            <div class="form-row" style="display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 16px; margin-bottom: 0;">
                                <div class="form-group" style="margin: 0;">
                                    <label class="form-label" style="display: block; font-weight: 500; color: #374151; margin-bottom: 6px; font-size: 14px;">Đơn vị cơ bản</label>
                                    <input type="text" id="modal-base-unit" class="form-input" readonly style="background-color: #f3f4f6; border: 1px solid #d1d5db; padding: 10px 12px; border-radius: 6px; width: 100%; font-size: 15px;">
                                </div>
                                <div class="form-group" style="margin: 0;">
                                    <label class="form-label" style="display: block; font-weight: 500; color: #374151; margin-bottom: 6px; font-size: 14px;">Đơn vị nhập <span style="color: #dc2626;">*</span></label>
                                    <select id="modal-package-unit" class="form-input" required style="border: 2px solid #3b82f6; padding: 10px 12px; border-radius: 6px; width: 100%; font-size: 15px; font-weight: 500; cursor: pointer; background-color: white;">
                                        <option value="">Chọn đơn vị nhập</option>
                                    </select>
                                    <div class="invalid-feedback" style="display: none; color: #dc2626; font-size: 13px; margin-top: 6px; font-weight: 500;"></div>
                                </div>
                                <div class="form-group" style="margin: 0;">
                                    <label class="form-label" style="display: block; font-weight: 500; color: #374151; margin-bottom: 6px; font-size: 14px;">Tỉ lệ quy đổi</label>
                                    <input type="text" id="modal-conversion-rate" class="form-input" readonly style="background-color: #ecfdf5; border: 1px solid #10b981; color: #065f46; font-weight: 600; padding: 10px 12px; border-radius: 6px; width: 100%; font-size: 15px;">
                                </div>
                            </div>
                        </div>

                        <!-- Thông tin thuốc -->
                        <div style="background: #fffbeb; border: 1px solid #fbbf24; border-radius: 8px; padding: 20px; margin-bottom: 24px;">
                            <h4 style="font-size: 16px; font-weight: 600; color: #92400e; margin: 0 0 16px 0;">💊 Thông tin thuốc</h4>
                            <div class="form-group" style="margin: 0;">
                                <label class="form-label" style="display: block; font-weight: 500; color: #78350f; margin-bottom: 6px; font-size: 14px;">Hàm lượng / Nồng độ</label>
                                <input type="text" id="modal-concentration" class="form-input" readonly style="background-color: #fef3c7; border: 1px solid #fbbf24; padding: 10px 12px; border-radius: 6px; width: 100%; font-size: 15px; color: #78350f; font-weight: 500;">
                            </div>
                        </div>

                        <!-- Danh sách đơn vị quy đổi -->
                        <div style="background: #f0f9ff; border: 1px solid #3b82f6; border-radius: 8px; padding: 20px; margin-bottom: 24px;">
                            <h4 style="font-size: 16px; font-weight: 600; color: #1e40af; margin: 0 0 16px 0;">📊 Danh sách đơn vị quy đổi</h4>
                            <div style="border: 1px solid #bfdbfe; border-radius: 6px; overflow: hidden;">
                                <table style="width: 100%; border-collapse: collapse;">
                                    <thead style="background: #3b82f6; color: white;">
                                        <tr>
                                            <th style="padding: 10px 12px; text-align: left; font-weight: 600; font-size: 13px;">Tên đơn vị</th>
                                            <th style="padding: 10px 12px; text-align: left; font-weight: 600; font-size: 13px;">Quy đổi</th>
                                            <th style="padding: 10px 12px; text-align: left; font-weight: 600; font-size: 13px;">Ghi chú</th>
                                        </tr>
                                    </thead>
                                    <tbody id="modal-unit-conversion-list" style="background: white;">
                                        <!-- Rows will be populated by JavaScript -->
                                    </tbody>
                                </table>
                            </div>
                        </div>

                        <!-- Thông tin lô hàng -->
                        <div style="background: white; border: 2px solid #3b82f6; border-radius: 8px; padding: 20px; margin-bottom: 20px;">
                            <h4 style="font-size: 16px; font-weight: 600; color: #1e40af; margin: 0 0 16px 0;">📦 Thông tin lô hàng</h4>
                            <div class="form-row" style="display: grid; grid-template-columns: 1fr; gap: 16px; margin-bottom: 16px;">
                                <div class="form-group" style="margin: 0;">
                                    <label class="form-label" style="display: block; font-weight: 600; color: #1e40af; margin-bottom: 6px; font-size: 14px;">Số lô <span style="color: #dc2626;">*</span></label>
                                    <input type="text" id="modal-batch" class="form-input" placeholder="Nhập số lô (chỉ chữ, số và dấu gạch ngang)" required style="border: 2px solid #bfdbfe; padding: 12px 14px; border-radius: 6px; width: 100%; font-size: 15px; font-weight: 500;">
                                    <div class="invalid-feedback" style="display: none; color: #dc2626; font-size: 13px; margin-top: 6px; font-weight: 500;"></div>
                                </div>
                            </div>
                            <div class="form-row" style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 0;">
                                <div class="form-group" style="margin: 0;">
                                    <label class="form-label" style="display: block; font-weight: 600; color: #1e40af; margin-bottom: 6px; font-size: 14px;">Ngày sản xuất <span style="color: #dc2626;">*</span></label>
                                    <input type="date" id="modal-manufacture-date" class="form-input" required style="border: 2px solid #bfdbfe; padding: 12px 14px; border-radius: 6px; width: 100%; font-size: 15px;">
                                    <div class="invalid-feedback" style="display: none; color: #dc2626; font-size: 13px; margin-top: 6px; font-weight: 500;"></div>
                                </div>
                                <div class="form-group" style="margin: 0;">
                                    <label class="form-label" style="display: block; font-weight: 600; color: #1e40af; margin-bottom: 6px; font-size: 14px;">Hạn sử dụng <span style="color: #dc2626;">*</span></label>
                                    <input type="date" id="modal-expiry-date" class="form-input" required style="border: 2px solid #bfdbfe; padding: 12px 14px; border-radius: 6px; width: 100%; font-size: 15px;">
                                    <div class="invalid-feedback" style="display: none; color: #dc2626; font-size: 13px; margin-top: 6px; font-weight: 500;"></div>
                                </div>
                            </div>
                        </div>

                        <!-- Số lượng và giá -->
                        <div style="background: white; border: 2px solid #10b981; border-radius: 8px; padding: 20px;">
                            <h4 style="font-size: 16px; font-weight: 600; color: #065f46; margin: 0 0 16px 0;">💰 Số lượng và giá nhập</h4>
                            <div class="form-row" style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 0;">
                                <div class="form-group" style="margin: 0;">
                                    <label class="form-label" style="display: block; font-weight: 600; color: #065f46; margin-bottom: 6px; font-size: 14px;">Số lượng (theo đơn vị nhập) <span style="color: #dc2626;">*</span></label>
                                    <input type="number" id="modal-quantity" class="form-input" value="1" min="1" required style="border: 2px solid #86efac; padding: 12px 14px; border-radius: 6px; width: 100%; font-size: 15px; font-weight: 600;">
                                    <div class="invalid-feedback" style="display: none; color: #dc2626; font-size: 13px; margin-top: 6px; font-weight: 500;"></div>
                                </div>
                                <div class="form-group" style="margin: 0;">
                                    <label class="form-label" style="display: block; font-weight: 600; color: #065f46; margin-bottom: 6px; font-size: 14px;">Giá nhập (VNĐ/đơn vị nhập) <span style="color: #dc2626;">*</span></label>
                                    <input type="number" id="modal-price" class="form-input" min="1" placeholder="Nhập giá" required style="border: 2px solid #86efac; padding: 12px 14px; border-radius: 6px; width: 100%; font-size: 15px; font-weight: 600;">
                                    <div class="invalid-feedback" style="display: none; color: #dc2626; font-size: 13px; margin-top: 6px; font-weight: 500;"></div>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
                <div style="display: flex; gap: 12px; justify-content: flex-end; padding: 20px 24px; border-top: 2px solid #e5e7eb; background: #f9fafb; position: sticky; bottom: 0;">
                    <button type="button" class="btn-secondary" onclick="closeProductDetailModal()" style="padding: 12px 24px; font-size: 15px; font-weight: 600; border-radius: 8px; min-width: 120px;">Hủy</button>
                    <button type="button" class="btn-primary" onclick="confirmAddProduct()" style="padding: 12px 24px; font-size: 15px; font-weight: 600; border-radius: 8px; min-width: 180px; background: #2563eb; color: white; border: none; cursor: pointer;">✓ Thêm vào danh sách</button>
                </div>
            </div>
        </div>
`
)
