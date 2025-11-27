@param String branchName
@param String totalValue
@param String valueDeltaLabel
@param Integer lowStockCount
@param Integer pendingInbound
@param Integer pendingOutbound
@param String range
@param java.util.List<java.util.Map<String, Object>> categories
@param java.util.List<java.util.Map<String, Object>> recentActivities

@template.layouts.manager(
    headContent = @`
        <link rel="stylesheet" href="/assets/css/manager/import-export.css">
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    `,
    content = @`
        <main class="main">
            <div class="breadcrumb">
                <span>Báo Cáo</span>
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none"><path d="M9 18L15 12L9 6" stroke="currentColor" stroke-width="2"/></svg>
                <span>Nhập/Xuất kho - Chi nhánh</span>
            </div>

            <div class="page-header">
                <h2 class="page-title">Báo cáo Nhập/Xuất - ${branchName != null ? branchName : "Chi nhánh"}</h2>
            </div>

            <section class="kpi-row">
                <div class="kpi-card">
                    <div class="kpi-title">Tổng giá trị tồn kho</div>
                    <div class="kpi-value" id="totalValue">${totalValue != null ? totalValue : ""}</div>
                    <div class="kpi-sub" id="valueDeltaLabel">${valueDeltaLabel != null ? valueDeltaLabel : ""}</div>
                </div>

                <div class="kpi-card warn">
                    <div class="kpi-title">Sắp hết hàng</div>
                    <div class="kpi-value" id="lowStockCount">${lowStockCount != null ? lowStockCount : 0}</div>
                    <div class="kpi-sub">Sản phẩm cần nhập</div>
                </div>

                <div class="kpi-card">
                    <div class="kpi-title">Đơn nhập chờ xử lý</div>
                    <div class="kpi-value" id="pendingInbound">${pendingInbound != null ? pendingInbound : 0}</div>
                    <div class="kpi-sub">Phiếu yêu cầu</div>
                </div>

                <div class="kpi-card">
                    <div class="kpi-title">Đơn xuất chờ đi</div>
                    <div class="kpi-value" id="pendingOutbound">${pendingOutbound != null ? pendingOutbound : 0}</div>
                    <div class="kpi-sub">Đơn hàng</div>
                </div>
            </section>

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

                <div class="chart-card">
                    <div class="chart-header"><h3>Tỷ trọng danh mục</h3></div>
                    <canvas id="categoryDonut" aria-label="Tỷ trọng danh mục" role="img"></canvas>
                    <ul id="donutLegend" class="donut-legend"></ul>
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
                        <th>Số lượng</th>
                        <th>Trạng thái</th>
                        <th>Thời gian</th>
                        <th>Hành động</th>
                    </tr>
                    </thead>
                    <tbody id="recentActivities">
                        @if(recentActivities == null || recentActivities.size() == 0)
                            <tr><td colspan="7" class="muted">Không có hoạt động gần đây</td></tr>
                        @else
                            @for(var act : recentActivities)
                                <tr>
                                    <td><a href="javascript:void(0)" onclick="viewRequestDetail(${act.get("id") != null ? act.get("id").toString() : "0"})" class="mono link">${act.get("code") != null ? act.get("code").toString() : ""}</a></td>
                                    <td><span class="badge badge-${act.get("typeClass") != null ? act.get("typeClass").toString() : ""}">${act.get("type") != null ? act.get("type").toString() : ""}</span></td>
                                    <td>${act.get("creator") != null ? act.get("creator").toString() : "-"}</td>
                                    <td>${act.get("totalQty") != null ? act.get("totalQty").toString() : "0"}</td>
                                    <td><span class="status status-${act.get("statusClass") != null ? act.get("statusClass").toString() : ""}">${act.get("statusLabel") != null ? act.get("statusLabel").toString() : ""}</span></td>
                                    <td>${act.get("timeAgo") != null ? act.get("timeAgo").toString() : ""}</td>
                                    <td>
                                        <button class="btn btn-small" onclick="viewRequestDetail(${act.get("id") != null ? act.get("id").toString() : "0"})">Xem</button>
                                    </td>
                                </tr>
                            @endfor
                        @endif
                    </tbody>
                </table>
            </section>
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

