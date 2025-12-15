@template.layouts.manager(
    headContent = @`
        <link rel="stylesheet" href="/assets/css/manager/report.css">
    `,
    content = @`
        <main class="main">
            <div class="main-container">
                <div class="page-header">
                    <h2 class="page-title">Báo cáo điều chỉnh & trả hàng</h2>
                </div>

                <div class="filter-bar">
                    <div class="filter-group">
                        <label for="dateFrom">Từ ngày:</label>
                        <input type="date" id="dateFrom" class="form-input">
                    </div>
                    <div class="filter-group">
                        <label for="dateTo">Đến ngày:</label>
                        <input type="date" id="dateTo" class="form-input">
                    </div>
                    <div class="filter-group">
                        <label for="typeFilter">Loại:</label>
                        <select id="typeFilter" class="form-select">
                            <option value="">Tất cả</option>
                            <option value="ADJUSTMENT">Điều chỉnh</option>
                            <option value="EXPIRED_RETURN">Trả hàng hết hạn</option>
                        </select>
                    </div>
                    <button id="filterBtn" class="btn btn-primary">Xem báo cáo</button>
                </div>

                <div class="report-container">
                    <table class="report-table">
                        <thead>
                            <tr>
                                <th>Ngày</th>
                                <th>Loại</th>
                                <th>Sản phẩm</th>
                                <th>Lô</th>
                                <th>Lý do</th>
                                <th>Số lượng thay đổi</th>
                                <th>Người tạo</th>
                            </tr>
                        </thead>
                        <tbody id="reportTableBody">
                            <!-- Data will be rendered here by JS -->
                        </tbody>
                    </table>
                </div>

                <div class="pagination-container">
                    <div class="pagination-controls">
                        <button id="prev-page" disabled>&laquo; Trước</button>
                        <span id="page-info">Trang 1 / 1</span>
                        <button id="next-page" disabled>Sau &raquo;</button>
                    </div>
                </div>
            </div>
        </main>
    `,
    pageScript = @`
        <script src="/assets/js/manager/adjustments.js" defer></script>
    `
)
