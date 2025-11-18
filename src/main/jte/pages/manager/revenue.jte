@template.layouts.manager(
    headContent = @`<link rel="stylesheet" href="/assets/css/manager/revenue.css">`,
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
                        <!-- rows sẽ được JS append -->
                        </tbody>
                    </table>
                </div>
            </div>
        </main>
    `,
    pageScript = @`<script src="/assets/js/manager/revenue.js" defer></script>`
)
