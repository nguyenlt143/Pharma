@template.layouts.manager(
headContent = @`
    <link rel="stylesheet" href="/assets/css/manager/staff.css">
    <link rel="stylesheet" href="/assets/css/manager/form.css">
`,
pageScript = @`<script src="/assets/js/manager/staff.js" defer></script>`,
content = @`
    <main class="main">
        <div class="main-container">
            <div class="page-header">
                <h2 class="page-title">Nhân viên</h2>
                <div class="page-actions">
                    <button id="btnCreateStaff" class="btn btn-primary">+ Tạo mới</button>
                </div>
            </div>

            <div class="card">
                <div class="card-header">
                    <div class="search-wrap">
                        <span class="icon-search">🔍</span>
                        <input id="searchInput" class="input input-search" placeholder="Tìm theo tên đăng nhập, họ tên, email hoặc vai trò">
                    </div>
                    <div class="card-actions">
                        <button id="btnToggleDeleted" class="btn btn-outline">Hiển thị nhân viên đã xóa</button>
                    </div>
                </div>

                <div class="card-body">
                    <table class="table">
                        <thead>
                        <tr>
                            <th class="text-center">#</th>
                            <th>Tên đăng nhập</th>
                            <th>Họ tên</th>
                            <th>Vai trò</th>
                            <th>Email</th>
                            <th>SĐT</th>
                            <th>Mật khẩu</th>
                            <th class="text-center">Trạng thái</th>
                            <th class="text-center">Hành động</th>
                        </tr>
                        </thead>
                        <tbody id="staffTableBody">
                        <!-- rendered by staff.js -->
                        </tbody>
                    </table>
                    <div id="emptyState" class="empty hidden">Chưa có nhân viên.</div>
                </div>
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
        </div>
    </main>

    <!-- Modal (Create / Edit) -->
    <div id="staffModal" class="modal hidden" role="dialog" aria-modal="true" aria-hidden="true">
        <div class="modal-panel">
            <header class="modal-header">
                <h3 id="modalTitle">Tạo nhân viên</h3>
                <button id="modalClose" class="modal-close" aria-label="Đóng">✕</button>
            </header>

            <form id="staffForm" class="modal-form" autocomplete="off">
                <input type="hidden" id="staffId" name="id">

                <div class="form-grid">
                    <label>Username <span class="required">*</span>
                        <input id="userName" name="userName" required>
                        <div class="invalid-feedback" id="userName-error"></div>
                    </label>

                    <label>Họ tên <span class="required">*</span>
                        <input id="fullName" name="fullName" required>
                        <div class="invalid-feedback" id="fullName-error"></div>
                    </label>

                    <label>Vai trò <span class="required">*</span>
                        <select id="roleId" name="roleId" required>
                            <option value="">-- Chọn vai trò --</option>
                            <option value="6">Dược sĩ</option>
                            <option value="4">Nhân viên kho</option>

                        </select>
                        <div class="invalid-feedback" id="roleId-error"></div>
                    </label>

                    <label>Email
                        <input id="email" name="email" type="email">
                        <div class="invalid-feedback" id="email-error"></div>
                    </label>

                    <label>Số điện thoại
                        <input id="phoneNumber" name="phoneNumber" type="tel" pattern="0[0-9]{9}" title="Số điện thoại phải có 10 chữ số và bắt đầu bằng số 0.">
                        <div class="invalid-feedback" id="phoneNumber-error"></div>
                    </label>

                    <label id="passwordWrapper">Mật khẩu <span class="required">*</span>
                        <input id="password" name="password" type="password" pattern=".{6,100}" title="Mật khẩu phải có từ 6 đến 100 ký tự.">
                        <div class="invalid-feedback" id="password-error"></div>
                    </label>
                </div>

                <div class="form-actions">
                    <button type="button" id="btnCancel" class="btn btn-outline">Hủy</button>
                    <button type="submit" id="btnSave" class="btn btn-primary">Lưu</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Simple toast container -->
    <div id="toast" class="toast hidden" role="status" aria-live="polite"></div>
`)