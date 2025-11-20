@template.layouts.manager(
headContent = @`
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <link rel="stylesheet" href="/assets/css/manager/dashboard.css">
`,
content = @`
    <main class="main">
        <div class="main-container">
            <div class="page-header">
                <h2 class="page-title">Bảng điều khiển</h2>
                <div class="time-filter">
                    <button class="filter-btn active" data-range="0">Hôm nay</button>
                    <button class="filter-btn" data-range="7">7 ngày</button>
                    <button class="filter-btn" data-range="30">30 ngày</button>
                </div>
            </div>

            <div class="stats-container">
                <div class="stat-card">
                    <div class="stat-content">
                        <div class="stat-info">
                            <p class="stat-label" id="revenue-label">Doanh thu hôm nay</p>
                            <p class="stat-value" id="revenue">...</p>
                        </div>
                        <img src="https://static.codia.ai/image/2025-10-26/RY2aKkT255.png" alt="Revenue Icon" class="stat-icon">
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-content">
                        <div class="stat-info">
                            <p class="stat-label" id="profit-label">Lợi nhuận</p>
                            <p class="stat-value" id="profit">...</p>
                        </div>
                        <img src="https://static.codia.ai/image/2025-10-26/osQnzvdUjA.png" alt="Profit Icon" class="stat-icon">
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-content">
                        <div class="stat-info">
                            <p class="stat-label" id="orders-label">Số đơn bán</p>
                            <p class="stat-value" id="orders">...</p>
                        </div>
                        <img src="https://static.codia.ai/image/2025-10-26/LCzn3hLH8S.png" alt="Orders Icon" class="stat-icon">
                    </div>
                </div>
            </div>

            <div class="charts-container">
                <div class="chart-card" id="revenue-chart-card">
                    <h3 class="chart-title" id="revenue-chart-title">Doanh thu 7 ngày gần nhất</h3>
                    <div class="chart-content">
                        <!-- Dynamic revenue bar chart: populated by JS -->
                        <div class="bar-chart" id="revenue-chart">
                            <!-- Bars will be injected here by /assets/js/manager/dashboard.js -->
                        </div>
                        <!-- Line chart canvas for 30 days view -->
                        <canvas id="revenue-line-chart" style="display: none;" height="60"></canvas>
                    </div>
                </div>

                <div class="chart-card">
                    <h3 class="chart-title">Thống kê sản phẩm theo danh mục</h3>
                    <!-- Dynamic product stats list: populated by JS -->
                    <div id="productStatsList">
                        <!-- Items will be injected here by /assets/js/manager/dashboard.js -->
                    </div>
                </div>
            </div>
        </div>
    </main>
`,
pageScript = @`<script src="/assets/js/manager/dashboard.js" defer></script>`
)
