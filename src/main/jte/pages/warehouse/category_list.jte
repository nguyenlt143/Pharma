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
        .btn-danger {
            background-color: #DC2626;
            color: white;
            padding: 10px 20px;
            border-radius: 8px;
            border: none;
            font-weight: 500;
            cursor: pointer;
        }
        .btn-link {
            background: none;
            border: none;
            cursor: pointer;
            text-decoration: underline;
            font-size: 14px;
        }
        .btn-link:hover {
            opacity: 0.8;
        }
        .btn-link.delete {
            color: #DC2626;
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
    </style>
`,
content = @`
<div class="page-container">
    <div class="page-header">
        <h1 class="page-title">Quản lý nhóm thuốc</h1>
        <button class="btn-primary" onclick="openCreateModal()">
            <span class="material-icons" style="font-size: 18px;">add</span>
            Thêm nhóm thuốc mới
        </button>
    </div>

    <div class="data-table-container">
        <table id="categoryTable" class="display" style="width:100%">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Tên danh mục</th>
                    <th>Mô tả</th>
                    <th>Hành động</th>
                </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    </div>
</div>

<!-- Create/Edit Category Modal -->
<div id="categoryModal" class="modal">
    <div class="modal-content">
        <h2 style="margin-bottom: 24px; font-size: 20px; font-weight: 700;" id="modalTitle">Thêm danh mục mới</h2>
        <form id="categoryForm">
            <input type="hidden" id="categoryId">
            
            <div class="form-group">
                <label class="form-label">Tên danh mục <span style="color: red;">*</span></label>
                <input type="text" id="categoryName" class="form-input" required>
            </div>

            <div class="form-group">
                <label class="form-label">Mô tả</label>
                <input type="text" id="description" class="form-input">
            </div>

            <div class="btn-group">
                <button type="button" class="btn-secondary" onclick="closeCategoryModal()">Hủy</button>
                <button type="submit" class="btn-primary">Lưu</button>
            </div>
        </form>
    </div>
</div>

<!-- Detail Category Modal -->
<div id="detailModal" class="modal">
    <div class="detail-modal-content">
        <h2 style="margin-bottom: 24px; font-size: 20px; font-weight: 700;">Chi tiết danh mục</h2>
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
pageScript = @`<script src="/assets/js/warehouse/category_list.js" defer></script>`
)
