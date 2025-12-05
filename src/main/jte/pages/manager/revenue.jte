@template.layouts.manager(
    headContent = @`<link rel="stylesheet" href="/assets/css/manager/revenue.css">`,
    content = @`
        <main class="main">
            <div class="main-container">
                <div class="page-header">
                    <h2 class="page-title">Báo cáo doanh thu</h2>
                </div>

                <!-- Filters -->
                <div class="filters">
                    <div class="filter-group">
                        <label>Loại thời gian</label>
                        <select class="select-input" id="time-mode">
                            <option value="day" selected>Ngày</option>
                            <option value="week">Tuần</option>
                            <option value="month">Tháng</option>
                        </select>
                    </div>
                    <div class="filter-group">
                        <label>Thời gian</label>
                        <input type="date" class="date-input" id="date-picker">
                        <input type="week" class="week-input" id="week-picker" style="display: none;">
                        <input type="month" class="month-input" id="month-picker" style="display: none;">
                    </div>
                    <div class="filter-group">
                        <label>Ca làm việc</label>
                        <select class="select-input" id="shift-select">
                            <option value="">Tất cả</option>
                        </select>
                    </div>
                    <div class="filter-group">
                        <label>Nhân viên</label>
                        <select class="select-input" id="employee-select">
                            <option value="">Tất cả</option>
                        </select>
                    </div>
                </div>

                <!-- KPI Cards -->
                <div class="kpi-section">
                    <div class="kpi-card">
                        <div class="kpi-label">Tổng số đơn</div>
                        <div class="kpi-value" id="kpi-totalInvoices">0</div>
                    </div>
                    <div class="kpi-card">
                        <div class="kpi-label">Doanh thu</div>
                        <div class="kpi-value" id="kpi-totalRevenue">0</div>
                    </div>
                    <div class="kpi-card">
                        <div class="kpi-label">Lợi nhuận</div>
                        <div class="kpi-value" id="kpi-totalProfit">0</div>
                    </div>
                </div>

                <div class="bottom-section">
                    <!-- Product Statistics -->
                    <div class="product-stats">
                        <h2>Thống kê sản phẩm theo danh mục</h2>
                        <p class="product-total">Tổng sản phẩm đã bán: <span id="total-products">0</span></p>
                        <div id="productStatsList"><!-- items injected by JS --></div>
                    </div>
                </div>
                <!-- Invoice Details Table -->
                <div class="table-section">
                    <h2>Chi tiết hóa đơn </h2>
                    <div class="table-container">
                        <table class="invoice-table">
                            <thead>
                            <tr>
                                <th>THỜI GIAN</th>
                                <th>MÃ HÓA ĐƠN</th>
                                <th>KHÁCH HÀNG</th>
                                <th>PHƯƠNG THỨC THANH TOÁN</th>
                                <th>TỔNG TIỀN</th>
                                <th>Lợi nhuận</th>
                            </tr>
                            </thead>
                            <tbody id="invoice-tbody">
                            <!-- rows will be appended by JS -->
                            </tbody>
                        </table>
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
    `,
    pageScript = @`<script src="/assets/js/manager/revenue.js" defer></script>`
)
