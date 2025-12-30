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
        .btn-primary:hover {
            background-color: #1D4ED8;
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
            max-width: 800px;
            max-height: 80vh;
            overflow-y: auto;
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
        .form-input, .form-select, .form-textarea {
            width: 100%;
            padding: 10px;
            border: 1px solid #D1D5DB;
            border-radius: 6px;
            font-size: 14px;
            box-sizing: border-box;
        }
        .form-textarea {
            min-height: 80px;
            resize: vertical;
        }
        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 16px;
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
        .btn-secondary:hover {
            background-color: #4B5563;
        }
        .btn-close-variant {
            background-color: #059669;
            color: white;
            padding: 10px 24px;
            border-radius: 8px;
            border: none;
            font-weight: 500;
            cursor: pointer;
        }
        .btn-close-variant:hover {
            background-color: #047857;
        }
        .btn-close-detail {
            background-color: #7C3AED;
            color: white;
            padding: 10px 24px;
            border-radius: 8px;
            border: none;
            font-weight: 500;
            cursor: pointer;
        }
        .btn-close-detail:hover {
            background-color: #6D28D9;
        }
        .btn-close-view {
            background-color: #F59E0B;
            color: white;
            padding: 10px 24px;
            border-radius: 8px;
            border: none;
            font-weight: 500;
            cursor: pointer;
        }
        .btn-close-view:hover {
            background-color: #D97706;
        }
        .status-badge {
            padding: 4px 12px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 500;
        }
        .status-active {
            background-color: #D1FAE5;
            color: #065F46;
        }
        .status-inactive {
            background-color: #FEE2E2;
            color: #991B1B;
        }
        .btn-link {
            background: none;
            border: none;
            cursor: pointer;
            text-decoration: underline;
            font-size: 14px;
            padding: 4px 8px;
            margin: 0 4px;
        }
        .btn-link:hover {
            opacity: 0.8;
        }
        .btn-link.delete {
            color: #DC2626;
        }
        .action-buttons {
            display: flex;
            flex-wrap: wrap;
            gap: 8px;
            align-items: center;
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
            max-width: 600px;
            max-height: 90vh;
            overflow-y: auto;
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
                    <span style="font-size: 16px;">+</span> Thêm dạng thuốc
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
                                🔄 Tạo tự động
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
                    <label class="form-label">Ghi chú</label>
                    <textarea id="note" class="form-textarea" placeholder="Ghi chú thêm về dạng thuốc này..." rows="3"></textarea>
                </div>

                <!-- Unit Conversions Section -->
                <div style="margin-top: 32px; padding-top: 24px; border-top: 2px solid #E5E7EB;">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
                        <h3 style="font-size: 16px; font-weight: 600; margin: 0;">Danh sách đơn vị quy đổi</h3>
                        <button type="button" onclick="addUnitConversionRow()" class="btn-primary" style="padding: 6px 12px; font-size: 14px;">
                            + Thêm đơn vị tính
                        </button>
                    </div>

                    <!-- Info về đơn vị cơ bản -->
                    <div style="background: #EFF6FF; border: 1px solid #BFDBFE; border-radius: 6px; padding: 12px; margin-bottom: 16px; font-size: 14px;">
                        <div style="display: flex; align-items: center; gap: 8px; color: #1E40AF;">
                            <span style="font-weight: 600;">ℹ️ Lưu ý:</span>
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
            <button type="button" class="btn-close-variant" onclick="closeVariantModal()">✓ Đóng quản lý</button>
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
            <button type="button" class="btn-close-detail" onclick="closeDetailModal()">✓ Đóng chi tiết</button>
        </div>
    </div>
</div>

<!-- Toast Notification -->
<div id="toast" class="toast"></div>
`,
pageScript = @`<script src="/assets/js/warehouse/medicine_list.js" defer></script>`
)
