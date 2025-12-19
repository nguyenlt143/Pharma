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
                <div class="pagination-left">
                    <label for="recordsPerPage" style="display: inline-flex; align-items: center; gap: 8px;">
                        <span>Hiển thị</span>
                        <select id="recordsPerPage" class="form-select" style="width: auto; padding: 6px 32px 6px 12px;">
                            <option value="10" selected>10</option>
                            <option value="25">25</option>
                            <option value="50">50</option>
                            <option value="100">100</option>
                        </select>
                        <span>ca</span>
                    </label>
                </div>
                <div class="pagination-info">
                    Hiển thị <span id="showingFrom">1</span> - <span id="showingTo">10</span> / <span id="totalItems">0</span> ca
                </div>
                <div class="pagination-controls" id="paginationButtons">
                    <!-- Dynamic pagination buttons rendered by JS -->
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

                    </label>
                    <div class="invalid-feedback" id="shiftName-error"></div>

                    <label>Giờ bắt đầu <span class="required">*</span>
                        <div class="time-inputs">
                            <input type="number" id="startHour" name="startHour" min="0" max="23" placeholder="0-23" required style="width:80px;" aria-describedby="startTime-error"> :
                            <input type="number" id="startMinute" name="startMinute" min="0" max="59" placeholder="0-59" required style="width:80px;">
                            <div class="invalid-feedback" id="startTime-error"></div>
                        </div>
                    </label>

                    <label>Giờ kết thúc <span class="required">*</span>
                        <div class="time-inputs">
                            <input type="number" id="endHour" name="endHour" min="0" max="23" placeholder="0-23" required style="width:80px;" aria-describedby="endTime-error"> :
                            <input type="number" id="endMinute" name="endMinute" min="0" max="59" placeholder="0-59" required style="width:80px;">
                            <div class="invalid-feedback" id="endTime-error"></div>
                        </div>
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
