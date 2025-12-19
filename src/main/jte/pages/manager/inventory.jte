@template.layouts.manager(
    headContent = @`
        <link rel="stylesheet" href="/assets/css/manager/inventory.css">
    `,
    content = @`
        <main class="main">
            <div class="main-container">
                <div class="breadcrumb">
                    <span>Báo Cáo</span>
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none"><path d="M9 18L15 12L9 6" stroke="currentColor" stroke-width="2"/></svg>
                    <span>Kho - Báo cáo quản lý</span>
                </div>

                <div class="page-header">
                    <h2 class="page-title">Quản lý kho - Báo cáo</h2>
                </div>

                <section class="kpi-row mi-kpis">
                    <div class="kpi-card card">
                        <div class="kpi-title">Tổng số mặt hàng</div>
                        <div class="kpi-value" id="totalItems">
                            <div class="spinner"></div>
                        </div>
                        <div class="kpi-sub">Đang tồn kho</div>
                    </div>

                    <div class="kpi-card card warn">
                        <div class="kpi-title">Cần bổ sung</div>
                        <div class="kpi-value" id="lowStock">
                            <div class="spinner"></div>
                        </div>
                        <div class="kpi-sub">Hết hoặc sắp hết hàng</div>
                    </div>

                    <div class="kpi-card card">
                        <div class="kpi-title">Tổng giá trị</div>
                        <div class="kpi-value" id="totalValue">
                            <div class="spinner"></div>
                        </div>
                        <div class="kpi-sub">Giá trị tồn kho</div>
                    </div>

                    <div class="kpi-card card">
                        <div class="kpi-title">Sắp hết hạn</div>
                        <div class="kpi-value" id="nearExpiry">
                            <div class="spinner"></div>
                        </div>
                        <div class="kpi-sub">Trong 30 ngày</div>
                    </div>

                    <div class="kpi-card card">
                        <div class="kpi-title">Đã hết hạn</div>
                        <div class="kpi-value" id="expired">
                            <div class="spinner"></div>
                        </div>
                        <div class="kpi-sub">Cần xử lý</div>
                    </div>
                </section>

                <section class="mi-filters">
                    <label>
                        <span>Danh mục:</span>
                        <select id="categoryFilter" class="form-select">
                            <option value="">Tất cả</option>
                        </select>
                    </label>
                    <label>
                        <span>Trạng thái:</span>
                        <select id="statusFilter" class="form-select">
                            <option value="">Tất cả</option>
                            <option value="active">Hoạt động</option>
                            <option value="low-stock">Sắp hết hàng</option>
                            <option value="near-expiry">Sắp hết hạn</option>
                            <option value="expired">Đã hết hạn</option>
                            <option value="out-of-stock">Hết hàng</option>
                        </select>
                    </label>
                </section>

                <section class="mi-charts">
                    <div class="chart-card card">
                        <div class="chart-header">
                            <h3>Thống kê tồn kho theo danh mục</h3>
                        </div>
                        <canvas id="categoryChart" aria-label="Biểu đồ danh mục" role="img" style="max-height: 240px;"></canvas>
                        <div class="empty-chart hidden" id="categoryChartEmpty">
                            <p style="color: #6B7280; padding: 40px; text-align: center;">Không có dữ liệu</p>
                        </div>
                    </div>

                    <div class="chart-card card donut-card">
                        <div class="chart-header"><h3>Tỷ trọng giá trị</h3></div>
                        <canvas id="categoryDonut" aria-label="Tỷ trọng danh mục" role="img" style="max-height: 200px;"></canvas>
                        <ul id="donutLegend" class="donut-legend"></ul>
                        <div class="empty-chart hidden" id="donutChartEmpty">
                            <p style="color: #6B7280; padding: 40px; text-align: center;">Không có dữ liệu</p>
                        </div>
                    </div>
                </section>

                <section class="mi-activity">
                    <div class="activity-header">
                        <h3>Tồn kho hiện tại</h3>
                        <div class="flex gap-2">
                            <input type="search" id="searchInventory" class="form-input" placeholder="Tìm kiếm theo tên, hoạt chất, hàm lượng..." style="width: 350px;">
                            <button id="searchBtn" class="btn btn-outline">
                                <span class="material-icons" style="font-size: 18px;">search</span>
                                Tìm
                            </button>
                        </div>
                    </div>

                    <div class="table-container">
                        <table class="activity-table table">
                            <thead>
                            <tr>
                                <th>Tên thuốc</th>
                                <th>Hoạt chất</th>
                                <th>Hàm lượng</th>
                                <th>Dạng bào chế</th>
                                <th>Lô sản xuất</th>
                                <th>Số lượng</th>
                                <th>Đơn vị</th>
                                <th>Hạn dùng</th>
                                <th>Trạng thái</th>
                            </tr>
                            </thead>
                            <tbody id="inventoryTableBody">
                                <tr>
                                    <td colspan="9" style="text-align: center; padding: 40px;">
                                        <div class="spinner"></div>
                                        <p style="margin-top: 12px; color: #6B7280;">Đang tải dữ liệu...</p>
                                    </td>
                                </tr>
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
                                <span>thuốc</span>
                            </label>
                        </div>
                        <div class="pagination-info">
                            Hiển thị <span id="showingFrom">1</span> - <span id="showingTo">10</span> / <span id="totalItems">0</span> thuốc
                        </div>
                        <div class="pagination-controls" id="paginationButtons">
                            <!-- Dynamic pagination buttons rendered by JS -->
                        </div>
                    </div>
                </section>
            </div>
        </main>
    `,
    pageScript = @`
        <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
        <script src="/assets/js/manager/inventory.js" defer></script>
    `
)
