@param Boolean userCanExport
@param String totalValue
@param String valueDeltaLabel
@param Integer lowStockCount
@param Integer pendingInbound
@param Integer pendingOutbound
@param java.util.List<java.util.Map<String, Object>> branches
@param String range
@param java.util.List<java.util.Map<String, Object>> categories
@param java.util.List<java.util.Map<String, Object>> recentActivities

@template.layouts.owner(
    headContent = @`
        <link rel="stylesheet" href="/assets/css/owner/inventory.css">
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    `,
    content = @`
        <main class="main">
            <div class="breadcrumb">
                <span>Báo Cáo</span>
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none"><path d="M9 18L15 12L9 6" stroke="currentColor" stroke-width="2"/></svg>
                <span>Kho - Báo cáo tổng hợp</span>
            </div>

            <div class="page-header">
                <h2 class="page-title">Tổng quan kho</h2>
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
                <form id="filterForm" method="get" action="/owner/report/inventory">
                    <label>Chi nhánh:
                        <select name="branchId" id="branchSelect" aria-label="Chọn chi nhánh">
                            <option value="">Tất cả</option>
                            @for(var branch : branches)
                                @if(branch.get("selected") != null && Boolean.TRUE.equals(branch.get("selected")))
                                    <option value="${branch.get("id") != null ? branch.get("id").toString() : ""}" selected>${branch.get("name") != null ? branch.get("name").toString() : ""}</option>
                                @else
                                    <option value="${branch.get("id") != null ? branch.get("id").toString() : ""}">${branch.get("name") != null ? branch.get("name").toString() : ""}</option>
                                @endif
                            @endfor
                        </select>
                    </label>
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
                    <label>Danh mục:
                        <select name="category" id="categorySelect" aria-label="Chọn danh mục">
                            <option value="">Tất cả</option>
                            @for(var cat : categories)
                                @if(cat.get("selected") != null && Boolean.TRUE.equals(cat.get("selected")))
                                    <option value="${cat.get("id") != null ? cat.get("id").toString() : ""}" selected>${cat.get("name") != null ? cat.get("name").toString() : ""}</option>
                                @else
                                    <option value="${cat.get("id") != null ? cat.get("id").toString() : ""}">${cat.get("name") != null ? cat.get("name").toString() : ""}</option>
                                @endif
                            @endfor
                        </select>
                    </label>

                    <div class="filter-actions">
                        <button type="button" class="btn btn-primary" onclick="loadInventoryData()">Áp dụng</button>
                        <button type="button" class="btn btn-ghost" onclick="resetFilters()">Đặt lại</button>
                    </div>
                </form>
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
                    <div class="chart-header">
                        <h3>Thống kê tồn kho theo danh mục</h3>
                    </div>
                    <canvas id="categoryChart" aria-label="Biểu đồ danh mục" role="img" style="max-height: 240px;"></canvas>
                </div>

                <div class="chart-card donut-card">
                    <div class="chart-header"><h3>Tỷ trọng giá trị</h3></div>
                    <canvas id="categoryDonut" aria-label="Tỷ trọng danh mục" role="img" style="max-height: 200px;"></canvas>
                    <ul id="donutLegend" class="donut-legend"></ul>
                </div>
            </section>

            <section class="activity">
                <div class="activity-header">
                    <h3>Hoạt động gần đây</h3>
                </div>

                <table class="activity-table">
                    <thead>
                    <tr>
                        <th>Mã đơn</th>
                        <th>Loại</th>
                        <th>Chi nhánh</th>
                        <th>Người tạo</th>
                        <th>Số lượng</th>
                        <th>Trạng thái</th>
                        <th>Thời gian</th>
                        <th>Hành động</th>
                    </tr>
                    </thead>
                    <tbody id="recentActivities">
                        @if(recentActivities == null || recentActivities.size() == 0)
                            <tr><td colspan="8" class="muted">Không có hoạt động gần đây</td></tr>
                        @else
                            @for(var act : recentActivities)
                                <tr>
                                    <td><a href="${act.get("detailUrl") != null ? act.get("detailUrl").toString() : ""}" class="mono link">${act.get("code") != null ? act.get("code").toString() : ""}</a></td>
                                    <td><span class="badge ${act.get("type") != null ? act.get("type").toString().toLowerCase() : ""}">${act.get("type") != null ? act.get("type").toString() : ""}</span></td>
                                    <td>${act.get("branch") != null ? act.get("branch").toString() : "-"}</td>
                                    <td>${act.get("creator") != null ? act.get("creator").toString() : "-"}</td>
                                    <td>${act.get("totalQty") != null ? act.get("totalQty").toString() : "0"}</td>
                                    <td><span class="status ${act.get("status") != null ? act.get("status").toString().toLowerCase() : ""}">${act.get("status") != null ? act.get("status").toString() : ""}</span></td>
                                    <td>${act.get("timeAgo") != null ? act.get("timeAgo").toString() : ""}</td>
                                    <td>
                                        <a class="btn small" href="${act.get("detailUrl") != null ? act.get("detailUrl").toString() : ""}">Xem</a>
                                    </td>
                                </tr>
                            @endfor
                        @endif
                    </tbody>
                </table>
            </section>
        </main>
        
        <!-- Request Detail Modal -->
        <div id="requestDetailModal" style="display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); z-index: 1000; align-items: center; justify-content: center;">
            <div style="background: white; border-radius: 12px; width: 90%; max-width: 800px; max-height: 90vh; overflow-y: auto; box-shadow: 0 4px 12px rgba(0,0,0,0.15);">
                <div style="padding: 24px; border-bottom: 1px solid #E5E7EB; display: flex; justify-content: space-between; align-items: center;">
                    <h3 style="margin: 0; font-size: 20px; font-weight: 600; color: #111827;">Chi tiết yêu cầu</h3>
                    <button onclick="closeRequestModal()" style="background: none; border: none; cursor: pointer; font-size: 24px; color: #6B7280; padding: 4px 8px;">&times;</button>
                </div>
                <div id="requestDetailContent" style="padding: 24px;">
                    <div style="text-align: center; color: #6B7280;">Đang tải...</div>
                </div>
            </div>
        </div>
    `,
    pageScript = @`<script src="/assets/js/owner/inventory.js" defer></script>`
)

