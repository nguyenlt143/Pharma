@template.layouts.manager(
headContent = @`<link rel="stylesheet" href="/assets/css/manager/dashboard.css">`,
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
                    <div class="stat-change">
                        <img src="https://static.codia.ai/image/2025-10-26/W1PWnLJdXO.png" alt="Up Arrow" class="change-icon">
                        <span class="change-text positive" id="changeRevenue"></span>
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
                    <div class="stat-change">
                        <img src="https://static.codia.ai/image/2025-10-26/edmzoyKK8e.png" alt="Up Arrow" class="change-icon">
                        <span class="change-text positive" id="changeProfit"></span>
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
                    <div class="stat-change">
                        <img src="https://static.codia.ai/image/2025-10-26/iDxrcNDhiN.png" alt="Up Arrow" class="change-icon">
                        <span class="change-text positive" id="changeOrders"></span>
                    </div>
                </div>
            </div>

            <div class="charts-container">
                <div class="chart-card">
                    <h3 class="chart-title">Doanh thu 7 ngày gần nhất</h3>
                    <div class="chart-content">
                        <div class="bar-chart">
                            <div class="bar-container">
                                <div class="bar" style="height: 153.59px;">
                                    <img src="https://static.codia.ai/image/2025-10-26/so8mVbqdGm.png" alt="Bar" class="bar-fill">
                                </div>
                                <span class="bar-label">T2</span>
                            </div>
                            <div class="bar-container">
                                <div class="bar" style="height: 204.8px;">
                                    <img src="https://static.codia.ai/image/2025-10-26/hoPDraQ5EW.png" alt="Bar" class="bar-fill">
                                </div>
                                <span class="bar-label">T3</span>
                            </div>
                            <div class="bar-container">
                                <div class="bar" style="height: 102.39px;">
                                    <img src="https://static.codia.ai/image/2025-10-26/ab98agZ2ZS.png" alt="Bar" class="bar-fill">
                                </div>
                                <span class="bar-label">T4</span>
                            </div>
                            <div class="bar-container">
                                <div class="bar" style="height: 228px;">
                                    <img src="https://static.codia.ai/image/2025-10-26/ZMNK4L55tE.png" alt="Bar" class="bar-fill">
                                </div>
                                <span class="bar-label active">T5</span>
                            </div>
                            <div class="bar-container">
                                <div class="bar" style="height: 192px;">
                                    <img src="https://static.codia.ai/image/2025-10-26/HM00nbE2fq.png" alt="Bar" class="bar-fill">
                                </div>
                                <span class="bar-label">T6</span>
                            </div>
                            <div class="bar-container">
                                <div class="bar" style="height: 128px;">
                                    <img src="https://static.codia.ai/image/2025-10-26/XDU2hXqeZh.png" alt="Bar" class="bar-fill">
                                </div>
                                <span class="bar-label">T7</span>
                            </div>
                            <div class="bar-container">
                                <div class="bar" style="height: 228px;">
                                    <img src="https://static.codia.ai/image/2025-10-26/rfFEiyPdGq.png" alt="Bar" class="bar-fill">
                                </div>
                                <span class="bar-label">CN</span>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="chart-card">
                    <h3 class="chart-title">Top 5 nhóm thuốc bán chạy</h3>
                    <div class="progress-list">
                        <div class="progress-item">
                            <span class="progress-label">Thuốc giảm đau</span>
                            <div class="progress-bar">
                                <img src="https://static.codia.ai/image/2025-10-26/fNQpXDUou2.png" alt="Progress" class="progress-fill">
                            </div>
                        </div>
                        <div class="progress-item">
                            <span class="progress-label">Kháng sinh</span>
                            <div class="progress-bar">
                                <img src="https://static.codia.ai/image/2025-10-26/N37z1VTQCJ.png" alt="Progress" class="progress-fill">
                            </div>
                        </div>
                        <div class="progress-item">
                            <span class="progress-label">Vitamin</span>
                            <div class="progress-bar">
                                <img src="https://static.codia.ai/image/2025-10-26/VmV7dfcemE.png" alt="Progress" class="progress-fill">
                            </div>
                        </div>
                        <div class="progress-item">
                            <span class="progress-label">Thuốc cảm cúm</span>
                            <div class="progress-bar">
                                <img src="https://static.codia.ai/image/2025-10-26/dmYnVrj22V.png" alt="Progress" class="progress-fill">
                            </div>
                        </div>
                        <div class="progress-item">
                            <span class="progress-label">Tiêu hoá</span>
                            <div class="progress-bar">
                                <img src="https://static.codia.ai/image/2025-10-26/is1A3PSHX9.png" alt="Progress" class="progress-fill">
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </main>
`,
pageScript = @`<script src="/assets/js/manager/dashboard.js" defer></script>`
)

