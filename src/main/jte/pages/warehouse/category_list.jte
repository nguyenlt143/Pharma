@template.layouts.warehouse(
headContent = @`
    <link rel="stylesheet" href="https://cdn.datatables.net/1.13.7/css/jquery.dataTables.min.css">
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script src="https://cdn.datatables.net/1.13.7/js/jquery.dataTables.min.js"></script>
    <style>
        /* Page Layout */
        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 28px;
            padding-bottom: 16px;
            border-bottom: 2px solid #E5E7EB;
        }

        .page-title {
            font-size: 28px;
            font-weight: 700;
            color: #1F2937;
            letter-spacing: -0.5px;
        }

        /* Buttons */
        .btn-primary {
            background: linear-gradient(135deg, #3B82F6 0%, #2563EB 100%);
            color: white;
            padding: 11px 24px;
            border-radius: 10px;
            border: none;
            font-weight: 600;
            cursor: pointer;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            transition: all 0.3s ease;
            box-shadow: 0 4px 12px rgba(37, 99, 235, 0.25);
        }

        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 16px rgba(37, 99, 235, 0.35);
        }

        .btn-primary:active {
            transform: translateY(0);
        }

        .btn-secondary {
            background-color: #6B7280;
            color: white;
            padding: 11px 24px;
            border-radius: 10px;
            border: none;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
        }

        .btn-secondary:hover {
            background-color: #4B5563;
            transform: translateY(-1px);
        }

        .btn-danger {
            background: linear-gradient(135deg, #EF4444 0%, #DC2626 100%);
            color: white;
            padding: 11px 24px;
            border-radius: 10px;
            border: none;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            box-shadow: 0 4px 12px rgba(239, 68, 68, 0.25);
        }

        .btn-danger:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 16px rgba(239, 68, 68, 0.35);
        }

        /* Action Buttons in Table */
        .action-buttons {
            display: flex;
            gap: 6px;
            align-items: center;
            justify-content: center;
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
            background: white;
            color: #374151;
            white-space: nowrap;
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

        .btn-action.view {
            border-color: #3B82F6;
            color: #3B82F6;
        }

        .btn-action.view:hover {
            background: #DBEAFE;
            border-color: #2563EB;
            color: #2563EB;
        }

        .btn-action.edit {
            border-color: #F59E0B;
            color: #F59E0B;
        }

        .btn-action.edit:hover {
            background: #FEF3C7;
            border-color: #D97706;
            color: #D97706;
        }

        .btn-action.delete {
            border-color: #EF4444;
            color: #EF4444;
        }

        .btn-action.delete:hover {
            background: #FEE2E2;
            border-color: #DC2626;
            color: #DC2626;
        }

        /* Data Table Container */
        .data-table-container {
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.07), 0 2px 4px rgba(0, 0, 0, 0.05);
            overflow: hidden;
            border: 1px solid #E5E7EB;
        }

        /* DataTable Customization */
        #categoryTable {
            border-collapse: separate !important;
            border-spacing: 0;
        }

        #categoryTable thead th {
            background: linear-gradient(135deg, #F9FAFB 0%, #F3F4F6 100%);
            color: #374151;
            font-weight: 700;
            font-size: 13px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            padding: 16px 12px;
            border-bottom: 2px solid #E5E7EB;
        }

        #categoryTable tbody td {
            padding: 14px 12px;
            color: #1F2937;
            font-size: 14px;
            border-bottom: 1px solid #F3F4F6;
        }

        #categoryTable tbody tr {
            transition: all 0.2s ease;
        }

        #categoryTable tbody tr:hover {
            background-color: #F9FAFB;
        }

        /* DataTable Controls */
        .dataTables_wrapper .dataTables_length,
        .dataTables_wrapper .dataTables_filter {
            margin: 16px;
        }

        .dataTables_wrapper .dataTables_length select,
        .dataTables_wrapper .dataTables_filter input {
            padding: 8px 12px;
            border: 2px solid #E5E7EB;
            border-radius: 8px;
            font-size: 14px;
            transition: all 0.3s ease;
        }

        .dataTables_wrapper .dataTables_length select:focus,
        .dataTables_wrapper .dataTables_filter input:focus {
            outline: none;
            border-color: #3B82F6;
            box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
        }

        .dataTables_wrapper .dataTables_paginate .paginate_button {
            padding: 6px 12px;
            margin: 0 4px;
            border-radius: 6px;
            border: 1px solid #E5E7EB;
            background: white;
            color: #374151 !important;
            transition: all 0.2s ease;
        }

        .dataTables_wrapper .dataTables_paginate .paginate_button:hover {
            background: #F3F4F6 !important;
            border-color: #D1D5DB;
            color: #1F2937 !important;
        }

        .dataTables_wrapper .dataTables_paginate .paginate_button.current {
            background: linear-gradient(135deg, #3B82F6 0%, #2563EB 100%) !important;
            border-color: #2563EB;
            color: white !important;
        }

        .dataTables_wrapper .dataTables_info {
            margin: 16px;
            color: #6B7280;
            font-size: 14px;
        }

        /* Modal */
        .modal {
            display: none;
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0, 0, 0, 0.6);
            backdrop-filter: blur(2px);
            animation: fadeIn 0.3s ease;
        }

        @keyframes fadeIn {
            from { opacity: 0; }
            to { opacity: 1; }
        }

        .modal-content {
            background-color: white;
            margin: 5% auto;
            padding: 32px;
            border-radius: 16px;
            width: 90%;
            max-width: 600px;
            box-shadow: 0 20px 25px rgba(0, 0, 0, 0.15);
            animation: slideUp 0.3s ease;
        }

        @keyframes slideUp {
            from {
                opacity: 0;
                transform: translateY(20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        /* Form Elements */
        .form-group {
            margin-bottom: 20px;
        }

        .form-label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            color: #374151;
            font-size: 14px;
        }

        .form-input {
            width: 100%;
            padding: 12px 16px;
            border: 2px solid #E5E7EB;
            border-radius: 10px;
            font-size: 14px;
            box-sizing: border-box;
            transition: all 0.3s ease;
        }

        .form-input:focus {
            outline: none;
            border-color: #3B82F6;
            box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
        }

        .btn-group {
            display: flex;
            gap: 12px;
            justify-content: flex-end;
            margin-top: 28px;
        }

        /* Toast Notification */
        .toast {
            position: fixed;
            bottom: 24px;
            right: 24px;
            padding: 14px 24px;
            border-radius: 12px;
            color: white;
            font-weight: 600;
            z-index: 10000;
            opacity: 0;
            transform: translateY(20px);
            transition: all 0.3s ease;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
        }

        .toast.show {
            opacity: 1;
            transform: translateY(0);
        }

        .toast.success {
            background: linear-gradient(135deg, #10B981 0%, #059669 100%);
        }

        .toast.error {
            background: linear-gradient(135deg, #EF4444 0%, #DC2626 100%);
        }

        .toast.info {
            background: linear-gradient(135deg, #3B82F6 0%, #2563EB 100%);
        }

        /* Detail Modal */
        .detail-modal-content {
            background-color: white;
            margin: 5% auto;
            padding: 32px;
            border-radius: 16px;
            width: 90%;
            max-width: 500px;
            box-shadow: 0 20px 25px rgba(0, 0, 0, 0.15);
            animation: slideUp 0.3s ease;
        }

        .detail-item {
            margin-bottom: 20px;
            padding-bottom: 20px;
            border-bottom: 2px solid #F3F4F6;
        }

        .detail-item:last-child {
            border-bottom: none;
            margin-bottom: 0;
            padding-bottom: 0;
        }

        .detail-label {
            font-weight: 600;
            color: #6B7280;
            font-size: 13px;
            margin-bottom: 6px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .detail-value {
            color: #1F2937;
            font-size: 16px;
            font-weight: 500;
        }

        /* Modal Headers */
        h2 {
            margin: 0 0 24px 0;
            font-size: 22px;
            font-weight: 700;
            color: #1F2937;
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
        <h2 id="modalTitle">Thêm danh mục mới</h2>
        <form id="categoryForm">
            <input type="hidden" id="categoryId">
            
            <div class="form-group">
                <label for="categoryName" class="form-label">Tên danh mục <span style="color: #EF4444;">*</span></label>
                <input type="text" id="categoryName" class="form-input" required placeholder="Nhập tên danh mục...">
            </div>

            <div class="form-group">
                <label for="description" class="form-label">Mô tả</label>
                <input type="text" id="description" class="form-input" placeholder="Nhập mô tả (tùy chọn)...">
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
        <h2>Chi tiết danh mục</h2>
        <div id="detailContent">
            <!-- Detail content will be loaded here -->
        </div>
        <div class="btn-group">
            <button type="button" class="btn-secondary" onclick="closeDetailModal()">Đóng</button>
        </div>
    </div>
</div>

<!-- Toast Notification -->
<div id="toast" class="toast"></div>
`,
pageScript = @`<script src="/assets/js/warehouse/category_list.js" defer></script>`
)
