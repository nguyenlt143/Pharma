@template.layouts.owner(
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
        }
        .form-input.error {
            border-color: #DC2626;
        }
        .error-message {
            color: #DC2626;
            font-size: 13px;
            margin-top: 4px;
            display: none;
        }
        .error-message.show {
            display: block;
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
        .btn-action {
            padding: 6px 12px;
            border: 1px solid #D1D5DB;
            background: white;
            border-radius: 6px;
            cursor: pointer;
            font-size: 14px;
            color: #374151;
            font-weight: 500;
            transition: all 0.2s;
        }
        .btn-action:hover {
            background: #F3F4F6;
            border-color: #9CA3AF;
        }
        .btn-action.delete {
            color: #DC2626;
            border-color: #FCA5A5;
        }
        .btn-action.delete:hover {
            background: #FEE2E2;
            border-color: #DC2626;
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
        .btn-close-view {
            background-color: #6B7280;
            color: white;
            padding: 10px 20px;
            border-radius: 8px;
            border: none;
            font-weight: 500;
            cursor: pointer;
            transition: background-color 0.2s;
        }
        .btn-close-view:hover {
            background-color: #4B5563;
        }
    </style>
`,
content = @`
<div class="page-container">
    <div class="page-header">
        <h1 class="page-title">Quản lý nhà cung cấp</h1>
        <button class="btn-primary" onclick="openCreateModal()">
            <span class="material-icons" style="font-size: 18px;">add</span>
            Thêm nhà cung cấp mới
        </button>
    </div>

    <div class="data-table-container">
        <table id="supplierTable" class="display" style="width:100%">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Tên nhà cung cấp</th>
                    <th>Số điện thoại</th>
                    <th>Địa chỉ</th>
                    <th>Hành động</th>
                </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    </div>
</div>

<!-- Create/Edit Supplier Modal -->
<div id="supplierModal" class="modal">
    <div class="modal-content">
        <h2 style="margin-bottom: 24px; font-size: 20px; font-weight: 700;" id="modalTitle">Thêm nhà cung cấp mới</h2>
        <form id="supplierForm">
            <input type="hidden" id="supplierId">
            
            <div class="form-group">
                <label class="form-label">Tên nhà cung cấp <span style="color: red;">*</span></label>
                <input type="text" id="supplierName" class="form-input" required>
            </div>

            <div class="form-group">
                <label class="form-label">Số điện thoại</label>
                <input type="text" id="phone" class="form-input" pattern="[0-9]{10,11}" maxlength="11" placeholder="Nhập 10-11 số">
                <span class="error-message" id="phoneError">Số điện thoại phải có 10-11 chữ số</span>
            </div>

            <div class="form-group">
                <label class="form-label">Địa chỉ</label>
                <input type="text" id="address" class="form-input">
            </div>

            <div class="btn-group">
                <button type="button" class="btn-secondary" onclick="closeSupplierModal()">Hủy</button>
                <button type="submit" class="btn-primary">Lưu</button>
            </div>
        </form>
    </div>
</div>

<!-- Detail Supplier Modal -->
<div id="detailModal" class="modal">
    <div class="detail-modal-content">
        <h2 style="margin-bottom: 24px; font-size: 20px; font-weight: 700;">Chi tiết nhà cung cấp</h2>
        <div id="detailContent">
            <!-- Detail content will be loaded here -->
        </div>
        <div class="btn-group" style="margin-top: 24px;">
            <button type="button" class="btn-close-view" onclick="closeDetailModal()">✓ Đóng</button>
        </div>
    </div>
</div>

<!-- Toast Notification -->
<div id="toast" class="toast"></div>
`,
pageScript = @`<script src="/assets/js/owner/supplier_list.js" defer></script>`
)
