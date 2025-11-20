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
                    </label>

                    <label>Họ tên <span class="required">*</span>
                        <input id="fullName" name="fullName" required>
                    </label>

                    <label>Vai trò <span class="required">*</span>
                        <select id="roleId" name="roleId" required>
                            <option value="">-- Chọn vai trò --</option>
                            <option value="6">Dược sĩ</option>
                            <option value="4">Nhân viên kho</option>

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
