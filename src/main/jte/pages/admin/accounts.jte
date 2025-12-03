@template.layouts.admin(
headContent = @`
    <link rel="stylesheet" href="/assets/css/admin/accounts.css">
    <link rel="stylesheet" href="/assets/css/admin/form.css">
`,
pageScript = @`<script src="/assets/js/admin/accounts.js" defer></script>`,
content = @`
    <main class="main">
        <div class="main-container">
            <div class="page-header">
                <h2 class="page-title">Quản lý tài khoản</h2>
                <div class="page-actions">
                    <button id="btnCreateAccount" class="btn btn-primary">+ Tạo tài khoản</button>
                </div>
            </div>

            <div class="card">
                <div class="card-header">
                    <div class="search-wrap">
                        <span class="icon-search">🔍</span>
                        <input id="searchInput" class="input input-search" placeholder="Tìm theo tên đăng nhập, họ tên, email, vai trò hoặc chi nhánh">
                    </div>
                    <div class="card-actions">
                        <button id="btnToggleDeleted" class="btn btn-outline">Hiển thị tài khoản đã xóa</button>
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
                            <th>Chi nhánh</th>
                            <th>Email</th>
                            <th>SĐT</th>
                            <th class="text-center">Trạng thái</th>
                            <th class="text-center">Hành động</th>
                        </tr>
                        </thead>
                        <tbody id="accountTableBody">
                        <!-- rendered by accounts.js -->
                        </tbody>
                    </table>
                    <div id="emptyState" class="empty hidden">Chưa có tài khoản.</div>
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
    <div id="accountModal" class="modal hidden" role="dialog" aria-modal="true" aria-hidden="true">
        <div class="modal-panel">
            <header class="modal-header">
                <h3 id="modalTitle">Tạo tài khoản</h3>
                <button id="modalClose" class="modal-close" aria-label="Đóng">✕</button>
            </header>

            <form id="accountForm" class="modal-form" autocomplete="off">
                <input type="hidden" id="accountId" name="id">

                <div class="form-grid">
                    <label>Username <span class="required">*</span>
                        <input id="userName" name="userName" required>
                    </label>

                    <label>Họ tên <span class="required">*</span>
                        <input id="fullName" name="fullName" required>
                    </label>

                    <label>Vai trò <span class="required">*</span>
                        <select id="roleId" name="roleId" required>
                            <option value="">-- Chọn vai trò --</option>
                            <option value="2">Chủ doanh nghiệp (Owner)</option>
                            <option value="3">Quản lý chi nhánh (Manager)</option>
                            <option value="5">Nhân viên kho tổng (Warehouse)</option>
                        </select>
                    </label>

                    <label id="branchWrapper">Chi nhánh <span class="required">*</span>
                        <select id="branchId" name="branchId">
                            <option value="">-- Chọn chi nhánh --</option>
                            <!-- populated by accounts.js -->
                        </select>
                    </label>

                    <label>Email
                        <input id="email" name="email" type="email">
                    </label>

                    <label>Số điện thoại
                        <input id="phoneNumber" name="phoneNumber" type="tel">
                    </label>

                    <label id="passwordWrapper">Mật khẩu <span class="required">*</span>
                        <input id="password" name="password" type="password">
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

