@template.layouts.admin(
headContent = @`
    <link rel="stylesheet" href="/assets/css/admin/accounts.css">
    <link rel="stylesheet" href="/assets/css/admin/form.css">
`,
pageScript = @`<script src="/assets/js/admin/branches.js" defer></script>`,
content = @`
    <main class="main">
        <div class="main-container">
            <div class="page-header">
                <h2 class="page-title">Quản lý chi nhánh</h2>
                <div class="page-actions">
                    <button id="btnCreateBranch" class="btn btn-primary">+ Tạo chi nhánh</button>
                </div>
            </div>

            <div class="card">
                <div class="card-header">
                    <div class="search-wrap">
                        <span class="icon-search">🔍</span>
                        <input id="searchInput" class="input input-search" placeholder="Tìm theo tên chi nhánh hoặc địa chỉ">
                    </div>
                    <div class="card-actions">
                        <button id="btnToggleDeleted" class="btn btn-outline">Hiển thị chi nhánh đã xóa</button>
                    </div>
                </div>

                <div class="card-body">
                    <table class="table">
                        <thead>
                        <tr>
                            <th class="text-center">#</th>
                            <th>Tên chi nhánh</th>
                            <th>Loại</th>
                            <th>Địa chỉ</th>
                            <th class="text-center">Trạng thái</th>
                            <th class="text-center">Hành động</th>
                        </tr>
                        </thead>
                        <tbody id="branchTableBody">
                        <!-- rendered by branches.js -->
                        </tbody>
                    </table>
                    <div id="emptyState" class="empty hidden">Chưa có chi nhánh.</div>
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
    <div id="branchModal" class="modal hidden" role="dialog" aria-modal="true" aria-hidden="true">
        <div class="modal-panel">
            <header class="modal-header">
                <h3 id="modalTitle">Tạo chi nhánh</h3>
                <button id="modalClose" class="modal-close" aria-label="Đóng">✕</button>
            </header>

            <form id="branchForm" class="modal-form" autocomplete="off">
                <input type="hidden" id="branchId" name="id">

                <div class="form-grid">
                    <label>Tên chi nhánh <span class="required">*</span>
                        <input id="name" name="name" required placeholder="VD: Chi nhánh Hà Nội">
                        <div class="invalid-feedback" id="name-error"></div>
                    </label>

                    <label>Loại chi nhánh <span class="required">*</span>
                        <select id="branchType" name="branchType" required>
                            <option value="">-- Chọn loại --</option>
                            <option value="HEAD_QUARTER">Tổng công ty</option>
                            <option value="BRANCH">Chi nhánh</option>
                            <option value="DISPOSAL_AREA">Khu vực hủy</option>
                        </select>
                        <div class="invalid-feedback" id="branchType-error"></div>
                    </label>

                    <label class="full-width">Địa chỉ <span class="required">*</span>
                        <input id="address" name="address" required placeholder="VD: 123 Đường ABC, Quận XYZ">
                        <div class="invalid-feedback" id="address-error"></div>
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

