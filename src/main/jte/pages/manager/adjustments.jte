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
                    <div class="summary-card summary-card-loss">
                        <div class="card-icon">📉</div>
                        <div class="card-content">
                            <div class="card-label">Tổng lỗ (Hết hạn + Thiếu)</div>
                            <div class="card-value card-value-red" id="totalLoss">-</div>
                        </div>
                    </div>
                    <div class="summary-card summary-card-offset">
                        <div class="card-icon">📈</div>
                        <div class="card-content">
                            <div class="card-label">Tổng bù đắp (Thừa)</div>
                            <div class="card-value card-value-green" id="totalOffset">-</div>
                        </div>
                    </div>
                    <div class="summary-card summary-card-net">
                        <div class="card-icon">💰</div>
                        <div class="card-content">
                            <div class="card-label">Thất thoát thực tế (NET)</div>
                            <div class="card-value card-value-highlight" id="netLoss">-</div>
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
                                    <th>Trạng thái</th>
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
                        <div class="pagination-left">
                            <label for="recordsPerPage" style="display: inline-flex; align-items: center; gap: 8px;">
                                <span>Hiển thị</span>
                                <select id="recordsPerPage" class="form-select" style="width: auto; padding: 6px 32px 6px 12px;">
                                    <option value="10" selected>10</option>
                                    <option value="25">25</option>
                                    <option value="50">50</option>
                                    <option value="100">100</option>
                                </select>
                                <span>hoạt động</span>
                            </label>
                        </div>
                        <div class="pagination-info">
                            Hiển thị <span id="showingFrom">1</span> - <span id="showingTo">10</span> / <span id="totalItems">0</span> hoạt động
                        </div>
                        <div class="pagination-controls" id="paginationButtons">
                            <!-- Dynamic pagination buttons rendered by JS -->
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
