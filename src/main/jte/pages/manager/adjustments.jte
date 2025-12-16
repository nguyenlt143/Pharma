@template.layouts.manager(
    headContent = @`
        <link rel="stylesheet" href="/assets/css/manager/report.css">
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    `,
    content = @`
        <main class="main">
            <div class="main-container">
                <div class="page-header">
                    <h2 class="page-title">Báo cáo điều chỉnh & trả hàng hết hạn</h2>
                </div>

                <!-- Summary Cards -->
                <div class="summary-grid">
                    <div class="summary-card">
                        <div class="card-icon">📊</div>
                        <div class="card-content">
                            <div class="card-label">Kiểm kho </div>
                            <div class="card-value" id="adjustmentCount">-</div>
                        </div>
                    </div>
                    <div class="summary-card">
                        <div class="card-icon">📦</div>
                        <div class="card-content">
                            <div class="card-label">Trả hàng hết hạn </div>
                            <div class="card-value" id="expiredReturnCount">-</div>
                        </div>
                    </div>
                    <div class="summary-card">
                        <div class="card-icon">💰</div>
                        <div class="card-content">
                            <div class="card-label">Tổng giá trị </div>
                            <div class="card-value" id="totalValue">-</div>
                        </div>
                    </div>
                </div>

                <!-- Chart Section -->
                <div class="report-section">
                    <div class="section-header">
                        <h3>Biểu đồ kiểm kho & trả hàng</h3>
                        <div class="chart-controls">
                            <select id="rangeSelect" class="form-select">
                                <option value="week">7 ngày</option>
                                <option value="month">30 ngày</option>
                                <option value="quarter">3 tháng</option>
                            </select>
                            <div class="btn-group">
                                <button id="chartBarBtn" class="btn btn-sm">📊 Cột</button>
                                <button id="chartLineBtn" class="btn btn-sm">📈 Đường</button>
                            </div>
                        </div>
                    </div>
                    <div class="chart-container">
                        <canvas id="adjustmentChart"></canvas>
                    </div>
                </div>

                <!-- Activities List -->
                <div class="report-section">
                    <div class="section-header">
                        <h3>Hoạt động gần đây</h3>
                        <select id="typeFilter" class="form-select">
                            <option value="all">Tất cả</option>
                            <option value="INVENTORY_ADJUSTMENT">Kiểm kho</option>
                            <option value="BR_TO_WARE2">Trả hàng hết hạn</option>
                        </select>
                    </div>
                    <div class="report-container">
                        <table class="report-table">
                            <thead>
                                <tr>
                                    <th>Mã</th>
                                    <th>Loại</th>
                                    <th>Giá trị</th>
                                    <th>Thời gian</th>
                                    <th class="text-center">Hành động</th>
                                </tr>
                            </thead>
                            <tbody id="activitiesTableBody">
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
            </div>
        </main>

        <!-- Detail Modal -->
        <div id="detailModal" class="modal hidden" role="dialog" aria-modal="true" aria-hidden="true">
            <div class="modal-panel">
                <header class="modal-header">
                    <h3 id="modalTitle">Chi tiết</h3>
                    <button id="modalClose" class="modal-close" aria-label="Đóng">✕</button>
                </header>
                <div class="modal-body" id="modalBody">
                    <!-- Content loaded dynamically -->
                </div>
            </div>
        </div>

        <div id="toast" class="toast hidden"></div>
    `,
    pageScript = @`
        <script src="/assets/js/manager/adjustments.js" defer></script>
    `
)
