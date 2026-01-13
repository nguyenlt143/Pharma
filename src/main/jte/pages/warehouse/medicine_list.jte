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

        .btn-close-variant {
            background: linear-gradient(135deg, #10B981 0%, #059669 100%);
            color: white;
            padding: 11px 24px;
            border-radius: 10px;
            border: none;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            box-shadow: 0 4px 12px rgba(16, 185, 129, 0.25);
        }

        .btn-close-variant:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 16px rgba(16, 185, 129, 0.35);
        }

        .btn-close-detail {
            background: linear-gradient(135deg, #8B5CF6 0%, #7C3AED 100%);
            color: white;
            padding: 11px 24px;
            border-radius: 10px;
            border: none;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            box-shadow: 0 4px 12px rgba(139, 92, 246, 0.25);
        }

        .btn-close-detail:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 16px rgba(139, 92, 246, 0.35);
        }

        /* Action Buttons in Table */
        .action-buttons {
            display: flex;
            gap: 6px;
            align-items: center;
            justify-content: center;
            flex-wrap: wrap;
        }

        .btn-close-view {
            background: linear-gradient(135deg, #06B6D4 0%, #0891B2 100%) !important;
            color: white !important;
            padding: 11px 24px !important;
            border-radius: 10px !important;
            border: none !important;
            font-weight: 600 !important;
            cursor: pointer !important;
            transition: all 0.3s ease !important;
            box-shadow: 0 4px 12px rgba(6, 182, 212, 0.25) !important;
            display: inline-flex !important;
            align-items: center !important;
            gap: 8px !important;
            font-size: 14px !important;
        }

        .btn-close-view:hover {
            transform: translateY(-2px) !important;
            box-shadow: 0 6px 16px rgba(6, 182, 212, 0.35) !important;
            background: linear-gradient(135deg, #0891B2 0%, #0E7490 100%) !important;
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

        .btn-action.variant {
            border-color: #8B5CF6;
            color: #8B5CF6;
        }

        .btn-action.variant:hover {
            background: #EDE9FE;
            border-color: #7C3AED;
            color: #7C3AED;
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
        #medicineTable {
            border-collapse: separate !important;
            border-spacing: 0;
        }

        #medicineTable thead th {
            background: linear-gradient(135deg, #F9FAFB 0%, #F3F4F6 100%);
            color: #374151;
            font-weight: 700;
            font-size: 13px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            padding: 16px 12px;
            border-bottom: 2px solid #E5E7EB;
        }

        #medicineTable tbody td {
            padding: 14px 12px;
            color: #1F2937;
            font-size: 14px;
            border-bottom: 1px solid #F3F4F6;
        }

        #medicineTable tbody tr {
            transition: all 0.2s ease;
        }

        #medicineTable tbody tr:hover {
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

        /* Status Badges */
        .status-badge {
            padding: 6px 12px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 600;
            display: inline-block;
        }

        .status-active {
            background-color: #D1FAE5;
            color: #065F46;
        }

        .status-inactive {
            background-color: #FEE2E2;
            color: #991B1B;
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
            max-width: 800px;
            max-height: 85vh;
            overflow-y: auto;
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

        .form-input, .form-select, .form-textarea {
            width: 100%;
            padding: 12px 16px;
            border: 2px solid #E5E7EB;
            border-radius: 10px;
            font-size: 14px;
            box-sizing: border-box;
            transition: all 0.3s ease;
        }

        .form-input:focus, .form-select:focus, .form-textarea:focus {
            outline: none;
            border-color: #3B82F6;
            box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
        }

        .form-textarea {
            min-height: 100px;
            resize: vertical;
        }

        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
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
            max-width: 600px;
            max-height: 90vh;
            overflow-y: auto;
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
    </style>
`,
content = @`
<div class="page-container">
    <div class="page-header">
        <h1 class="page-title">Quản lý thuốc</h1>
        <button class="btn-primary" onclick="openCreateModal()">
            <span class="material-icons" style="font-size: 18px;">add</span>
            Thêm thuốc mới
        </button>
    </div>

    <div class="data-table-container">
        <table id="medicineTable" class="display" style="width:100%">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Tên thuốc</th>
                    <th>Danh mục</th>
                    <th>Thương hiệu</th>
                    <th>Nhà sản xuất</th>
                    <th>Hoạt chất</th>
                    <th>Hành động</th>
                </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    </div>
</div>

<!-- Create/Edit Medicine Modal -->
<div id="medicineModal" class="modal">
    <div class="modal-content">
        <h2 style="margin-bottom: 24px; font-size: 20px; font-weight: 700;" id="modalTitle">Thêm thuốc mới</h2>
        <form id="medicineForm">
            <input type="hidden" id="medicineId">
            
            <div class="form-group">
                <label class="form-label">Tên thuốc <span style="color: red;">*</span></label>
                <input type="text" id="medicineName" class="form-input" required>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Danh mục <span style="color: red;">*</span></label>
                    <select id="categoryId" class="form-select" required>
                        <option value="">Chọn danh mục</option>
                    </select>
                </div>
                <div class="form-group">
                    <label class="form-label">Thương hiệu</label>
                    <input type="text" id="brandName" class="form-input">
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Nhà sản xuất</label>
                    <input type="text" id="manufacturer" class="form-input">
                </div>
                <div class="form-group">
                    <label class="form-label">Xuất xứ</label>
                    <input type="text" id="countryOfOrigin" class="form-input">
                </div>
            </div>

            <div class="form-group">
                <label class="form-label">Hoạt chất</label>
                <input type="text" id="activeIngredient" class="form-input">
            </div>

            <div class="form-group">
                <label class="form-label">Chỉ định</label>
                <textarea id="indications" class="form-textarea" rows="3" placeholder="Mô tả các chỉ định sử dụng thuốc..."></textarea>
            </div>

            <div class="form-group">
                <label class="form-label">Chống chỉ định</label>
                <textarea id="contraindications" class="form-textarea" rows="3" placeholder="Mô tả các trường hợp chống chỉ định..."></textarea>
            </div>

            <div class="form-group">
                <label class="form-label">Tác dụng phụ</label>
                <textarea id="sideEffects" class="form-textarea" rows="3" placeholder="Mô tả các tác dụng phụ có thể xảy ra..."></textarea>
            </div>

            <div class="form-group">
                <label class="form-label">Công dụng</label>
                <textarea id="uses" class="form-textarea" rows="3" placeholder="Mô tả công dụng chính của thuốc..."></textarea>
            </div>

            <div class="btn-group">
                <button type="button" class="btn-secondary" onclick="closeMedicineModal()">Hủy</button>
                <button type="submit" class="btn-primary">Lưu</button>
            </div>
        </form>
    </div>
</div>

<!-- Variant Management Modal -->
<div id="variantModal" class="modal">
    <div class="modal-content" style="max-width: 1000px;">
        <h2 style="margin-bottom: 24px; font-size: 20px; font-weight: 700;" id="variantModalTitle">Quản lý dạng thuốc</h2>

        <div style="margin-bottom: 24px; padding: 12px; background: #F3F4F6; border-radius: 6px;">
            <strong>Thuốc:</strong> <span id="variantMedicineName"></span>
            <input type="hidden" id="variantMedicineId">
        </div>

        <!-- Variant List -->
        <div style="margin-bottom: 24px;">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
                <h3 style="font-size: 16px; font-weight: 600; margin: 0;">Danh sách dạng thuốc</h3>
                <button type="button" class="btn-primary" onclick="openCreateVariantForm()" style="padding: 8px 16px; font-size: 14px;">
                    <span class="material-icons" style="font-size: 16px;">add</span>
                    Thêm dạng thuốc
                </button>
            </div>
            <div id="variantList" style="border: 1px solid #E5E7EB; border-radius: 6px; overflow: hidden;">
                <table style="width: 100%; border-collapse: collapse;">
                    <thead style="background: #F9FAFB;">
                        <tr>
                            <th style="padding: 12px; text-align: left; font-weight: 600; font-size: 14px; border-bottom: 1px solid #E5E7EB;">Dạng bào chế</th>
                            <th style="padding: 12px; text-align: left; font-weight: 600; font-size: 14px; border-bottom: 1px solid #E5E7EB;">Liều dùng</th>
                            <th style="padding: 12px; text-align: left; font-weight: 600; font-size: 14px; border-bottom: 1px solid #E5E7EB;">Hàm lượng</th>
                            <th style="padding: 12px; text-align: left; font-weight: 600; font-size: 14px; border-bottom: 1px solid #E5E7EB;">Mã vạch</th>
                            <th style="padding: 12px; text-align: center; font-weight: 600; font-size: 14px; border-bottom: 1px solid #E5E7EB;">Hành động</th>
                        </tr>
                    </thead>
                    <tbody id="variantTableBody">
                        <tr>
                            <td colspan="5" style="padding: 24px; text-align: center; color: #6B7280;">Đang tải...</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- Variant Form -->
        <div id="variantFormContainer" style="display: none; border-top: 2px solid #E5E7EB; padding-top: 24px;">
            <form id="variantForm">
                <input type="hidden" id="variantId">

                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label">Dạng bào chế <span style="color: red;">*</span></label>
                        <select id="dosageForm" class="form-select" required>
                            <option value="">Chọn dạng bào chế</option>
                        </select>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label">Hàm lượng <span style="color: red;">*</span></label>
                        <input type="text" id="strength" class="form-input" placeholder="VD: 500mg/ml" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Quy cách đóng gói <span style="color: red;">*</span></label>
                        <div style="display: flex; gap: 8px; align-items: center;">
                            <input type="text" id="packaging" class="form-input" placeholder="VD: Hộp x 10 Vỉ x 10 Viên" readonly style="background-color: #F9FAFB;">
                            <button type="button" onclick="generatePackagingFromUnits()" class="btn-primary" style="padding: 8px 12px; font-size: 14px; white-space: nowrap;">
                                <span class="material-icons" style="font-size: 16px;">autorenew</span>
                                Tạo tự động
                            </button>
                        </div>
                        <div style="font-size: 12px; color: #6B7280; margin-top: 4px;">
                            Quy cách sẽ được tự động tạo từ đơn vị quy đổi
                        </div>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label">Mã vạch <span style="color: red;">*</span></label>
                        <input type="text" id="barcode" class="form-input" required>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label">Số đăng ký <span style="color: red;">*</span></label>
                        <input type="text" id="registrationNumber" class="form-input" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Cần kê đơn <span style="color: red;">*</span></label>
                        <select id="prescriptionRequired" class="form-select" required>
                            <option value="false">Không</option>
                            <option value="true">Có</option>
                        </select>
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label">Điều kiện bảo quản <span style="color: red;">*</span></label>
                    <textarea id="storageConditions" class="form-textarea" required></textarea>
                </div>

                <div class="form-group">
                    <label class="form-label">Liều dùng <span style="color: red;">*</span></label>
                    <textarea id="dosage" class="form-textarea" placeholder="VD: Người lớn: 1-2 viên/lần, ngày 2-3 lần..." required></textarea>
                </div>

                <div class="form-group">
                    <label class="form-label">Hướng dẫn sử dụng <span style="color: red;">*</span></label>
                    <textarea id="instructions" class="form-textarea" required></textarea>
                </div>

                <div class="form-group">
                    <label class="form-label">Quy cách</label>
                    <textarea id="note" class="form-textarea" placeholder="VD: 90ml, 10g, 500mg, 250ml, 100g, v.v." rows="3"></textarea>
                </div>

                <!-- Unit Conversions Section -->
                <div style="margin-top: 32px; padding-top: 24px; border-top: 2px solid #E5E7EB;">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
                        <h3 style="font-size: 16px; font-weight: 600; margin: 0;">Danh sách đơn vị quy đổi</h3>
                        <button type="button" onclick="addUnitConversionRow()" class="btn-primary" style="padding: 6px 12px; font-size: 14px;">
                            <span class="material-icons" style="font-size: 16px;">add</span>
                            Thêm đơn vị tính
                        </button>
                    </div>

                    <!-- Info về đơn vị cơ bản -->
                    <div style="background: #EFF6FF; border: 1px solid #BFDBFE; border-radius: 6px; padding: 12px; margin-bottom: 16px; font-size: 14px;">
                        <div style="display: flex; align-items: center; gap: 8px; color: #1E40AF;">
                            <span style="font-weight: 600;">Lưu ý:</span>
                            <span>Khi chọn dạng bào chế, đơn vị cơ bản sẽ tự động được thêm với tỉ lệ quy đổi = 1 (không thể chỉnh sửa). Các đơn vị quy đổi phải có giá trị > 1 và chia hết cho đơn vị trước đó.</span>
                        </div>
                    </div>

                    <div style="border: 1px solid #E5E7EB; border-radius: 6px; overflow: hidden; margin-bottom: 16px;">
                        <table style="width: 100%; border-collapse: collapse;">
                            <thead style="background: #3B82F6; color: white;">
                                <tr>
                                    <th style="padding: 12px; text-align: left; font-weight: 600; font-size: 14px;">Tên đơn vị</th>
                                    <th style="padding: 12px; text-align: left; font-weight: 600; font-size: 14px;">Quy đổi</th>
                                    <th style="padding: 12px; text-align: center; font-weight: 600; font-size: 14px; width: 120px;">Hiển thị bán hàng</th>
                                    <th style="padding: 12px; text-align: center; font-weight: 600; font-size: 14px; width: 80px;">Hành động</th>
                                </tr>
                            </thead>
                            <tbody id="unitConversionTableBody">
                                <!-- Unit conversion rows will be added here -->
                            </tbody>
                        </table>
                    </div>
                </div>

                <div class="btn-group">
                    <button type="button" class="btn-secondary" onclick="cancelVariantForm()">Hủy</button>
                    <button type="submit" class="btn-primary">Lưu</button>
                </div>
            </form>
        </div>

        <div class="btn-group" style="border-top: 1px solid #E5E7EB; padding-top: 16px; margin-top: 16px;">
            <button type="button" class="btn-close-variant" onclick="closeVariantModal()">
                <span class="material-icons" style="font-size: 18px;">check</span>
                Đóng quản lý
            </button>
        </div>
    </div>
</div>

<!-- Detail Medicine Modal -->
<div id="detailModal" class="modal">
    <div class="detail-modal-content">
        <h2 style="margin-bottom: 24px; font-size: 20px; font-weight: 700;">Chi tiết thuốc</h2>
        <div id="detailContent">
            <!-- Detail content will be loaded here -->
        </div>
        <div class="btn-group" style="margin-top: 24px;">
            <button type="button" class="btn-close-detail" onclick="closeDetailModal()">
                <span class="material-icons" style="font-size: 18px;">check</span>
                Đóng chi tiết
            </button>
        </div>
    </div>
</div>

<!-- Toast Notification -->
<div id="toast" class="toast"></div>
`,
pageScript = @`<script src="/assets/js/warehouse/medicine_list.js" defer></script>`
)
