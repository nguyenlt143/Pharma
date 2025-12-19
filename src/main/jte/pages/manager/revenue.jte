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
                        <div class="pagination-left">
                            <label for="recordsPerPage" style="display: inline-flex; align-items: center; gap: 8px;">
                                <span>Hiển thị</span>
                                <select id="recordsPerPage" class="form-select" style="width: auto; padding: 6px 32px 6px 12px;">
                                    <option value="10" selected>10</option>
                                    <option value="25" >25</option>
                                    <option value="50">50</option>
                                    <option value="100">100</option>
                                </select>
                                <span>hóa đơn</span>
                            </label>
                        </div>
                        <div class="pagination-info">
                            Hiển thị <span id="showingFrom">1</span> - <span id="showingTo">10</span> / <span id="totalItems">0</span> hóa đơn
                        </div>
                        <div class="pagination-controls" id="paginationButtons">
                            <!-- Dynamic pagination buttons rendered by JS -->
                        </div>
                    </div>
                </div>
            </div>
        </main>
    `,
    pageScript = @`<script src="/assets/js/manager/revenue.js" defer></script>`
)
