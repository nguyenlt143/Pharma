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
    <main class="main-content">
                <!-- Page Header -->
    <header class="page-header">
        <h1 class="page-title">Nhập từ nhà cung cấp</h1>
    </header>

    <!-- Form Section -->
    <section class="form-section">
        <div class="form-container">
            <div class="form-row">
                <div class="form-group">
                    <label for="supplier" class="form-label">Nhà cung cấp (F4) *</label>
                    <div class="input-wrapper">
                        <span class="material-icons input-icon">search</span>
                        <input type="text" id="supplier" class="form-input"
                               value="${inventoryMovementVM.supplierName() != null ? inventoryMovementVM.supplierName() : ""}"
                               placeholder="Chọn nhà cung cấp">
                    </div>
                </div>
                <div class="form-group">
                    <label for="receipt-code" class="form-label">Mã phiếu nhập</label>
                    <input type="text" id="receipt-code" class="form-input disabled"
                           value="${inventoryMovementVM.requestFormCode() != null ? inventoryMovementVM.requestFormCode() : null}"
                           disabled>
                </div>
                <div class="form-group">
                    <label for="import-date" class="form-label">Ngày nhập *</label>
                    <input type="date" id="import-date" class="form-input"
                           value="${inventoryMovementVM.createdAt() != null ? inventoryMovementVM.createdAt() : ""}">
                </div>
            </div>
        </div>
    </section>

    <!-- Product Section -->
    <section class="product-section">
    <div class="product-container">
    <div class="search-add-row">
        <div class="search-wrapper">
            <span class="material-icons search-icon">search</span>
            <input type="text" class="search-input" placeholder="Tìm kiếm thuốc">
        </div>
        <button class="add-button">
            <span class="material-icons">add</span>
            <span>THÊM VÀO TỪ DANH MỤC (F7)</span>
        </button>
    </div>

    <div class="table-container">
    <table class="product-table">
    <thead>
    <tr>
        <th>STT</th>
        <th>Tên thuốc</th>
        <th>ĐVT</th>
        <th>Hàm lượng</th>
        <th>Số lô</th>
        <th>NSX</th>
        <th>HSD</th>
        <th>Số lượng</th>
        <th>Giá nhập</th>
        <th></th>
    </tr>
        </thead>
        <tbody id="productTableBody">
        <!-- Rows sẽ được JS thêm vào -->

<%--        @for(int i = 0; i < medicines.size(); i++)--%>
<%--            <tr class="table-row">--%>
<%--                <td class="col-stt">${i + 1}</td>--%>
<%--                <td class="col-medicine">${medicines.get(i).medicineName()}</td>--%>
<%--                <td class="col-unit">${medicines.get(i).unit()}</td>--%>
<%--                <td class="col-quantity">${medicines.get(i).quantity()}</td>--%>
<%--                <td class="col-price">${String.format("%,.0f", medicines.get(i).unitPrice())}</td>--%>
<%--                <td class="col-total">${String.format("%,.0f", medicines.get(i).unitPrice() * medicines.get(i).quantity())}</td>--%>
<%--            </tr>--%>
<%--        @endfor--%>

        </tbody>
            </table>
            </div>
            </div>
            </section>

            <!-- Action Buttons -->
            <div class="action-buttons">
                <button class="btn-secondary">LƯU NHÁP</button>
                <button class="btn-primary">HOÀN THÀNH</button>
            </div>
    </main>
`
)
