@template.layouts.warehouse(
headContent = @`
    <link rel="stylesheet" href="https://cdn.datatables.net/1.13.7/css/jquery.dataTables.min.css">
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script src="https://cdn.datatables.net/1.13.7/js/jquery.dataTables.min.js"></script>
    <style>
        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 24px;
        }
        .page-title {
            font-size: 24px;
            font-weight: 700;
            color: #111827;
        }
        .btn-primary {
            background-color: #2563EB;
            color: white;
            padding: 10px 20px;
            border-radius: 8px;
            border: none;
            font-weight: 500;
            cursor: pointer;
            display: inline-flex;
            align-items: center;
            gap: 8px;
        }
        .data-table-container {
            background: white;
            border-radius: 8px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        .modal {
            display: none;
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0,0,0,0.5);
        }
        .modal-content {
            background-color: white;
            margin: 5% auto;
            padding: 24px;
            border-radius: 8px;
            width: 90%;
            max-width: 600px;
        }
        .form-group {
            margin-bottom: 16px;
        }
        .form-label {
            display: block;
            margin-bottom: 6px;
            font-weight: 500;
            color: #374151;
        }
        .form-input {
            width: 100%;
            padding: 10px;
            border: 1px solid #D1D5DB;
            border-radius: 6px;
            font-size: 14px;
            box-sizing: border-box;
        }
        .btn-group {
            display: flex;
            gap: 12px;
            justify-content: flex-end;
            margin-top: 24px;
        }
        .btn-secondary {
            background-color: #6B7280;
            color: white;
            padding: 10px 20px;
            border-radius: 8px;
            border: none;
            font-weight: 500;
            cursor: pointer;
        }
        /* Toast Notification */
        .toast {
            position: fixed;
            bottom: 24px;
            right: 24px;
            padding: 12px 24px;
            border-radius: 8px;
            color: white;
            font-weight: 500;
            z-index: 10000;
            opacity: 0;
            transform: translateY(20px);
            transition: all 0.3s ease;
        }
        .toast.show {
            opacity: 1;
            transform: translateY(0);
        }
        .toast.success {
            background-color: #10B981;
        }
        .toast.error {
            background-color: #EF4444;
        }
        .toast.info {
            background-color: #3B82F6;
        }
        /* Detail Modal */
        .detail-modal-content {
            background-color: white;
            margin: 5% auto;
            padding: 24px;
            border-radius: 8px;
            width: 90%;
            max-width: 500px;
        }
        .detail-item {
            margin-bottom: 16px;
            padding-bottom: 16px;
            border-bottom: 1px solid #E5E7EB;
        }
        .detail-item:last-child {
            border-bottom: none;
        }
        .detail-label {
            font-weight: 600;
            color: #6B7280;
            font-size: 14px;
            margin-bottom: 4px;
        }
        .detail-value {
            color: #111827;
            font-size: 16px;
        }
        .filter-section {
            background: white;
            padding: 16px;
            border-radius: 8px;
            margin-bottom: 16px;
            display: flex;
            gap: 16px;
            align-items: end;
        }
        .filter-group {
            flex: 1;
        }
        .filter-label {
            display: block;
            margin-bottom: 6px;
            font-weight: 500;
            color: #374151;
            font-size: 14px;
        }
        .filter-input {
            width: 100%;
            padding: 8px;
            border: 1px solid #D1D5DB;
            border-radius: 6px;
        }
        .filter-select {
            width: 100%;
            padding: 8px;
            border: 1px solid #D1D5DB;
            border-radius: 6px;
            font-size: 14px;
            background-color: white;
        }
        .filter-select:focus {
            outline: none;
            border-color: #2563EB;
            box-shadow: 0 0 0 2px rgba(37, 99, 235, 0.1);
        }
        .btn-action {
            padding: 6px 12px;
            border: 1px solid #D1D5DB;
            border-radius: 6px;
            font-size: 13px;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.15s ease;
            display: inline-flex;
            align-items: center;
            gap: 6px;
            margin-right: 6px;
            background: white;
            color: #374151;
        }
        .btn-action:last-child {
            margin-right: 0;
        }
        .btn-action i {
            font-size: 12px;
        }
        .btn-action:hover {
            background: #F3F4F6;
            border-color: #9CA3AF;
        }
        .btn-action:active {
            transform: scale(0.98);
        }
        .btn-info {
            border-color: #3B82F6;
            color: #3B82F6;
        }
        .btn-info:hover {
            background: #DBEAFE;
            border-color: #2563EB;
            color: #2563EB;
        }
        .btn-view {
            border-color: #8B5CF6;
            color: #8B5CF6;
        }
        .btn-view:hover {
            background: #EDE9FE;
            border-color: #7C3AED;
            color: #7C3AED;
        }
        .btn-delete {
            border-color: #EF4444;
            color: #EF4444;
        }
        .btn-delete:hover {
            background: #FEE2E2;
            border-color: #DC2626;
            color: #DC2626;
        }
        .action-buttons-container {
            display: flex;
            gap: 6px;
            justify-content: flex-start;
            flex-wrap: wrap;
        }
    </style>
`,
content = @`
    <div class="page-container">
        <div class="page-header">
            <h1 class="page-title">Quản lý giá</h1>
            <button class="btn-primary" onclick="openCreateModal()">
                <span class="material-icons" style="font-size: 18px;">add</span>
                Điều chỉnh giá
            </button>
        </div>

        <div class="filter-section">
            <div class="filter-group">
                <label class="filter-label">Biến thể thuốc</label>
                <select id="filterVariantId" class="filter-select">
                    <option value="">Tất cả biến thể</option>
                </select>
            </div>
            <div class="filter-group">
                <label class="filter-label">Chi nhánh</label>
                <select id="filterBranchId" class="filter-select">
                    <option value="">Tất cả chi nhánh</option>
                </select>
            </div>
            <button class="btn-primary" onclick="applyFilter()" style="height: fit-content;">Lọc</button>
            <button class="btn-secondary" onclick="clearFilter()" style="height: fit-content;">Xóa lọc</button>
        </div>

        <div class="data-table-container">
            <table id="priceTable" class="display" style="width:100%">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Biến thể thuốc</th>
                    <th>Giá bán</th>
                    <th>Giá chi nhánh</th>
                    <th>Thời gian cập nhật</th>
                    <th>Hành động</th>
                </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
        </div>
    </div>

    <!-- Create/Edit Price Modal -->
    <div id="priceModal" class="modal">
        <div class="modal-content">
            <h2 style="margin-bottom: 24px; font-size: 20px; font-weight: 700;" id="modalTitle">Điều chỉnh giá</h2>
            <form id="priceForm">
                <input type="hidden" id="priceId">

                <div class="form-group">
                    <label class="form-label">Biến thể thuốc <span style="color: red;">*</span></label>
                    <select id="variantId" class="form-input" required>
                        <option value="">Chọn biến thể thuốc</option>
                    </select>
                </div>

                <div class="form-group">
                    <label class="form-label">Chi nhánh</label>
                    <select id="branchId" class="form-input">
                        <option value="">Chọn chi nhánh</option>
                    </select>
                </div>

                <div class="form-group">
                    <label class="form-label">Giá bán <span style="color: red;">*</span></label>
                    <input type="number" id="salePrice" class="form-input" step="0.01" required>
                </div>

                <div class="form-group">
                    <label class="form-label">Giá chi nhánh</label>
                    <input type="number" id="branchPrice" class="form-input" step="0.01">
                </div>



                <div class="btn-group">
                    <button type="button" class="btn-secondary" onclick="closePriceModal()">Hủy</button>
                    <button type="submit" class="btn-primary">Lưu</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Detail Price Modal -->
    <div id="detailModal" class="modal">
        <div class="detail-modal-content">
            <h2 style="margin-bottom: 24px; font-size: 20px; font-weight: 700;">Chi tiết giá</h2>
            <div id="detailContent">
                <!-- Detail content will be loaded here -->
            </div>
            <div class="btn-group" style="margin-top: 24px;">
                <button type="button" class="btn-secondary" onclick="closeDetailModal()">Đóng</button>
            </div>
        </div>
    </div>

    <!-- Toast Notification -->
    <div id="toast" class="toast"></div>
`,
pageScript = @`<script src="/assets/js/warehouse/price_list.js" defer></script>`
)