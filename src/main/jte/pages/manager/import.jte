@param String branchName
@param String totalValue
@param String valueDeltaLabel
@param Integer lowStockCount
@param Integer pendingInbound
@param Integer pendingOutbound
@param String range

@param java.util.List<java.util.Map<String, java.lang.Object>> recentActivities

@template.layouts.manager(
    headContent = @`
        <link rel="stylesheet" href="/assets/css/manager/import-export.css">
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    `,
    content = @`
        <main class="main">
            <div class="main-container">
                <div class="breadcrumb">
                    <span>Báo Cáo</span>
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none"><path d="M9 18L15 12L9 6" stroke="currentColor" stroke-width="2"/></svg>
                    <span>Xuất-Nhập kho</span>
                </div>

                <section class="filters">
                    <label>Thời gian:
                        <select name="range" id="rangeSelect" aria-label="Chọn khoảng thời gian">
                            @if("week".equals(range))
                                <option value="week" selected>Tuần</option>
                            @else
                                <option value="week">Tuần</option>
                            @endif
                            @if("month".equals(range))
                                <option value="month" selected>Tháng</option>
                            @else
                                <option value="month">Tháng</option>
                            @endif
                            @if("quarter".equals(range))
                                <option value="quarter" selected>Quý</option>
                            @else
                                <option value="quarter">Quý</option>
                            @endif
                        </select>
                    </label>
                </section>

                <section class="charts">
                    <div class="chart-card">
                        <div class="chart-header">
                            <h3>Biến động Nhập/Xuất</h3>
                            <div class="chart-controls">
                                <button class="small" id="chartBarBtn">Cột</button>
                                <button class="small" id="chartLineBtn">Đường</button>
                            </div>
                        </div>
                        <canvas id="inOutChart" aria-label="Biểu đồ nhập xuất" role="img"></canvas>
                    </div>
                </section>

                <section class="activity">
                    <div class="activity-header">
                        <h3>Hoạt động Nhập/Xuất gần đây</h3>
                        <div class="activity-filters">
                            <button class="filter-btn active" data-type="all" onclick="filterActivities('all')">Tất cả</button>
                            <button class="filter-btn" data-type="import" onclick="filterActivities('import')">Nhập kho</button>
                            <button class="filter-btn" data-type="export" onclick="filterActivities('export')">Xuất kho</button>
                        </div>
                    </div>

                    <table class="activity-table">
                        <thead>
                        <tr>
                            <th>Mã đơn</th>
                            <th>Loại</th>
                            <th>Người tạo</th>
                            <th>Tổng giá trị</th>
                            <th>Thời gian</th>
                            <th>Hành động</th>
                        </tr>
                        </thead>
                        <tbody id="recentActivities">
                            <!-- Populated by JavaScript -->
                        </tbody>
                    </table>

                    <!-- Pagination -->
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
                            <!-- Dynamic pagination buttons -->
                        </div>
                    </div>
                </section>
            </div>
        </main>

        <!-- Request Detail Modal -->
        <div id="requestDetailModal" class="modal-overlay">
            <div class="modal-content">
                <div class="modal-header">
                    <h3>Chi tiết yêu cầu</h3>
                    <button onclick="closeRequestModal()" class="btn-close">&times;</button>
                </div>
                <div id="requestDetailContent" class="modal-body">
                    <div class="loading-spinner">
                        <div class="spinner"></div>
                        <p>Đang tải...</p>
                    </div>
                </div>
            </div>
        </div>
    `,
    pageScript = @`<script src="/assets/js/manager/import-export.js" defer></script>`
)
