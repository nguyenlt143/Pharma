@template.layouts.manager(
headContent = @`<link rel="stylesheet" href="/assets/css/manager/shift.css">
    <link rel="stylesheet" href="/assets/css/manager/form.css">`,
content = @`
    <main class="main">
        <div class="main-container">
            <div class="page-header">
                <h2 class="page-title">Quản lý ca làm việc</h2>
                <div class="page-actions">
                    <button class="btn btn-outline" id="btnToggleDeleted">Hiển thị ca đã xóa</button>
                    <button class="btn btn-primary" id="btnAddShift">+ Thêm ca mới</button>
                </div>
            </div>

            <table class="shift-table">
                <thead>
                <tr>
                    <th>TÊN CA</th>
                    <th>GIỜ BẮT ĐẦU</th>
                    <th>GIỜ KẾT THÚC</th>
                    <th>GHI CHÚ</th>
                    <th>TRẠNG THÁI</th>
                    <th>HÀNH ĐỘNG</th>
                </tr>
                </thead>
                <tbody id="shiftTableBody">
                <!-- render bằng JS -->
                </tbody>
            </table>
            <div class="pagination-container">
                <div class="pagination-controls">
                    <button id="prev-page" disabled>&laquo; Trước</button>
                    <span id="page-info">Trang 1 / 1</span>
                    <button id="next-page" disabled>Sau &raquo;</button>
                </div>
                <div class="records-per-page">
                    <label for="records-per-page">Số bản ghi:</label>
                    <select id="records-per-page" class="form-select">
                        <option value="10">10</option>
                        <option value="25" selected>25</option>
                        <option value="50">50</option>
                    </select>
                </div>
            </div>
        </div>
    </main>

    <!-- Popup thêm/sửa ca -->
    <div id="shiftModal" class="modal hidden" role="dialog" aria-modal="true" aria-hidden="true">
        <div class="modal-panel">
            <header class="modal-header">
                <h3 id="modalTitle">Thêm ca làm việc</h3>
                <button id="modalClose" class="modal-close" aria-label="Đóng">✕</button>
            </header>

            <form id="shiftForm" class="modal-form" autocomplete="off">
                <input type="hidden" id="shiftId">

                <div class="form-grid">
                    <label>Tên ca <span class="required">*</span>
                        <input type="text" id="shiftName" name="shiftName" placeholder="VD: Ca Sáng" required>

                        <div class="invalid-feedback" id="shiftName-error"></div>
                    </label>

                    <label>Giờ bắt đầu <span class="required">*</span>
                        <input type="time" id="startTime" name="startTime" required>
                        <div class="invalid-feedback" id="startTime-error"></div>
                    </label>

                    <label>Giờ kết thúc <span class="required">*</span>
                        <input type="time" id="endTime" name="endTime" required>
                        <div class="invalid-feedback" id="endTime-error"></div>
                    </label>

                    <label style="grid-column: 1 / -1;">Ghi chú
                        <textarea id="note" name="note" rows="3" placeholder="Nhập ghi chú về ca làm việc..."></textarea>
                    </label>
                </div>

                <div class="form-actions">
                    <button type="button" id="btnCancel" class="btn btn-outline">Hủy</button>
                    <button type="submit" id="btnSave" class="btn btn-primary">Lưu</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Popup xem nhân viên trong ca -->
    <div id="employeeModal" class="modal hidden" role="dialog" aria-modal="true" aria-hidden="true">
        <div class="modal-panel modal-panel-lg">
            <header class="modal-header">
                <h3 id="shiftEmployeeTitle" class="shift-title">Nhân viên trong ca</h3>
                <button id="closeEmployeeModal" class="modal-close" aria-label="Đóng">✕</button>
            </header>

            <div class="modal-body">
                <div class="card">
                    <div class="card-body">
                        <table class="table">
                            <thead>
                            <tr>
                                <th>Tên nhân viên</th>
                                <th>Chức vụ</th>
                                <th>Số ngày còn lại</th>
                                <th>Ngày cuối cùng</th>
                                <th class="text-center">Hành động</th>
                            </tr>
                            </thead>
                            <tbody id="employeeTableBody">
                            <!-- Render bằng JS -->
                            </tbody>
                        </table>
                    </div>
                </div>

                <div class="assign-section">
                    <h4 class="section-title">Phân công nhân viên mới</h4>
                    <div class="assign-controls">
                        <select id="employeeSelect" class="input">
                            <option value="">-- Chọn nhân viên thêm vào ca --</option>
                        </select>
                        <button id="assignBtn" class="btn btn-primary">+ Thêm vào ca</button>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Toast notification -->
    <div id="toast" class="toast hidden" role="status" aria-live="polite"></div>

`,
pageScript = @`<script src="/assets/js/manager/shift.js" defer></script>`
)
