@template.layouts.manager(
    headContent = @`
        <link rel="stylesheet" href="/assets/css/manager/revenue.css">
    `,
    content = @`
        <main class="main">
            <div class="breadcrumb">
                <span>Báo Cáo</span>
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
                    <path d="M9 18L15 12L9 6" stroke="currentColor" stroke-width="2"/>
                </svg>
                <span>Doanh Thu Hàng Ngày</span>
            </div>

            <!-- Filters -->
            <div class="filters">
                <div class="filter-group">
                    <label>Loại thời gian</label>
                    <select class="select-input" id="time-mode">
                        <option value="day">Ngày</option>
                        <option value="week">Tuần</option>
                        <option value="month">Tháng</option>
                    </select>
                </div>
                <div class="filter-group">
                    <label>Thời gian</label>
                    <input type="date" class="date-input">
                </div>
                <div class="filter-group">
                    <label>Ca làm việc</label>
                    <select class="select-input" id="shift-select">
                        <!-- options sẽ load từ API -->
                    </select>
                </div>
                <div class="filter-group">
                    <label>Nhân viên</label>
                    <select class="select-input" id="employee-select">
                        <!-- options sẽ load từ API -->
                    </select>
                </div>
            </div>

            <!-- KPI Cards -->
            <div class="kpi-section">
                <h2>Thông tin chung </h2>
                    <div class="kpi-card">
                        <div class="kpi-label">Tổng số đơn</div>
                        <div class="kpi-value" id="kpi-totalInvoices"></div>
                    </div>
                    <div class="kpi-card">
                        <div class="kpi-label">Doanh thu</div>
                        <div class="kpi-value" id="kpi-totalRevenue"></div>
                    </div>
                    <div class="kpi-card">
                        <div class="kpi-label">Lợi nhuận</div>
                        <div class="kpi-value" id="kpi-totalProfit"></div>
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
                        <!-- rows sẽ được JS append -->
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Bottom Section -->
            <div class="bottom-section">
                <!-- Product Statistics -->
                <div class="product-stats">
                    <h2>Thống kê theo loại sản phẩm</h2>
                    <div class="chart-container">
                        <div class="donut-chart">
                            <svg width="200" height="200" viewBox="0 0 200 200" id="donut-chart">
                                <circle cx="100" cy="100" r="80" fill="none" stroke="#e0e0e0" stroke-width="20"/>
                            </svg>
                            <div class="chart-center">
                                <div class="chart-number" id="total-products"></div>
                                <div class="chart-label">sản phẩm đã bán</div>
                            </div>
                        </div>
                        <div class="chart-legend" id="chart-legend">
                            <!-- legend items append từ JS -->
                        </div>
                    </div>
                </div>
            </div>
        </main>
    `,
    pageScript = @`
        <script src="/assets/js/manager/revenue.js" defer></script>
    `
)
