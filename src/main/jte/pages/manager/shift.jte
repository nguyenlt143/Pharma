@template.layouts.manager(
headContent = @`<link rel="stylesheet" href="/assets/css/manager/shift.css">`,
content = @`
    <main class="main">
        <div class="main-container">
            <div class="page-header">
                <h2 class="page-title">Quản lý ca làm việc</h2>
                <button class="btn-primary" id="btnAddShift">+ Thêm ca mới</button>
            </div>

            <div class="search-bar">
                <input type="text" id="searchInput" placeholder="Tìm kiếm theo tên ca...">
                <button class="btn-search">Tìm</button>
            </div>

            <table class="shift-table">
                <thead>
                <tr>
                    <th>Tên ca</th>
                    <th>Giờ bắt đầu</th>
                    <th>Giờ kết thúc</th>
                    <th>Ghi chú</th>
                    <th>Hành động</th>
                </tr>
                </thead>
                <tbody id="shiftTableBody">
                <!-- render bằng JS -->
                </tbody>
            </table>
        </div>
    </main>

    <!-- Popup thêm/sửa ca -->
    <div id="shiftModal" class="modal hidden">
        <div class="modal-content">
            <h3 id="modalTitle">Thêm ca làm việc</h3>
            <form id="shiftForm">
                <input type="hidden" id="shiftId">
                <label>Tên ca</label>
                <input type="text" id="shiftName" required>

                <label>Giờ bắt đầu</label>
                <input type="time" id="startTime" required>

                <label>Giờ kết thúc</label>
                <input type="time" id="endTime" required>

                <label>Ghi chú</label>
                <textarea id="note"></textarea>

                <div class="form-actions">
                    <button type="submit" class="btn-primary">Lưu</button>
                    <button type="button" class="btn-secondary" id="btnCancel">Hủy</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Popup xem nhân viên trong ca -->
    <div id="employeeModal" class="modal hidden">
        <div class="modal-content">
            <h3>Nhân viên trong ca</h3>
            <table class="employee-table">
                <thead>
                <tr>
                    <th>Họ tên</th>
                    <th>Số điện thoại</th>
                    <th>Trạng thái làm việc</th>
                </tr>
                </thead>
                <tbody id="employeeTableBody">
                <!-- render bằng JS -->
                </tbody>
            </table>
            <button class="btn-secondary" id="closeEmployeeModal">Đóng</button>
        </div>
    </div>
`,
pageScript = @`<script src="/assets/js/manager/shift.js" defer></script>`
)
