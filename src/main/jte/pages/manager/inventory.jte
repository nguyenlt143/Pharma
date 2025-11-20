@param Boolean userCanExport
@param String totalValue
@param String valueDeltaLabel
@param Integer lowStockCount
@param Integer pendingInbound
@param Integer pendingOutbound
@param java.util.List<java.util.Map<String, Object>> warehouses
@param String range
@param java.util.List<java.util.Map<String, Object>> categories
@param java.util.List<java.util.Map<String, Object>> recentActivities

@template.layouts.manager(
    headContent = @`<link rel="stylesheet" href="/assets/css/manager/inventory.css">`,
    content = @`
        <main class="main">
            <div class="breadcrumb">
                <span>Báo Cáo</span>
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none"><path d="M9 18L15 12L9 6" stroke="currentColor" stroke-width="2"/></svg>
                <span>Kho - Báo cáo quản lý</span>
            </div>

            <div class="page-header">
                <h2 class="page-title">Quản lý kho - Báo cáo</h2>
                <div class="page-actions">
                    <a class="btn btn-outline" href="/manager/inventory/create-po">Tạo phiếu đặt hàng</a>
                    @if(userCanExport != null && userCanExport)
                        <button id="exportBtn" class="btn btn-primary" aria-label="Xuất báo cáo">Xuất (CSV)</button>
                    @endif
                </div>
            </div>

            <section class="kpi-row">
                <div class="kpi-card">
                    <div class="kpi-title">Tổng giá trị tồn kho</div>
                    <div class="kpi-value" id="totalValue">${totalValue != null ? totalValue : ""}</div>
                    <div class="kpi-sub">${valueDeltaLabel != null ? valueDeltaLabel : ""}</div>
                    <button class="btn btn-link" onclick="location.href='/manager/inventory/valuation'">Xem chi tiết</button>
                </div>

                <div class="kpi-card warn">
                    <div class="kpi-title">Sắp hết hàng</div>
                    <div class="kpi-value" id="lowStockCount">${lowStockCount != null ? lowStockCount : 0}</div>
                    <div class="kpi-sub">Sản phẩm cần nhập</div>
                    <div class="kpi-cta"><a href="/manager/inventory/low-stock" class="link">Xem danh sách</a></div>
                </div>

                <div class="kpi-card">
                    <div class="kpi-title">Đơn nhập chờ xử lý</div>
                    <div class="kpi-value" id="pendingInbound">${pendingInbound != null ? pendingInbound : 0}</div>
                    <div class="kpi-sub">Phiếu yêu cầu</div>
                    <button class="btn btn-link" onclick="location.href='/manager/inbound?status=pending'">Xử lý</button>
                </div>

                <div class="kpi-card">
                    <div class="kpi-title">Đơn xuất chờ đi</div>
                    <div class="kpi-value" id="pendingOutbound">${pendingOutbound != null ? pendingOutbound : 0}</div>
                    <div class="kpi-sub">Đơn hàng</div>
                    <button class="btn btn-link" onclick="location.href='/manager/outbound?status=pending'">Xử lý</button>
                </div>
            </section>

            <section class="filters">
                <form id="filterForm" method="get" action="/manager/inventory">
                    <label>Kho:
                        <select name="warehouse" aria-label="Chọn kho">
                            <option value="">Tất cả</option>
                            @for(var wh : warehouses)
                                @if(wh.get("selected") != null && Boolean.TRUE.equals(wh.get("selected")))
                                    <option value="${wh.get("id") != null ? wh.get("id").toString() : ""}" selected>${wh.get("name") != null ? wh.get("name").toString() : ""}</option>
                                @else
                                    <option value="${wh.get("id") != null ? wh.get("id").toString() : ""}">${wh.get("name") != null ? wh.get("name").toString() : ""}</option>
                                @endif
                            @endfor
                        </select>
                    </label>
                    <label>Thời gian:
                        <select name="range" aria-label="Chọn khoảng thời gian">
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
                        <select name="category" aria-label="Chọn danh mục">
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
                        <button type="submit" class="btn">Áp dụng</button>
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
                    <div class="chart-header"><h3>Tỷ trọng danh mục</h3></div>
                    <canvas id="categoryDonut" aria-label="Tỷ trọng danh mục" role="img"></canvas>
                    <ul id="donutLegend" class="donut-legend"></ul>
                </div>
            </section>

            <section class="activity">
                <div class="activity-header">
                    <h3>Hoạt động gần đây</h3>
                    <a href="/manager/inventory/activities" class="link">Xem tất cả</a>
                </div>

                <table class="activity-table">
                    <thead>
                    <tr>
                        <th>Mã đơn</th>
                        <th>Loại</th>
                        <th>Kho</th>
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
                                    <td>${act.get("warehouse") != null ? act.get("warehouse").toString() : "-"}</td>
                                    <td>${act.get("creator") != null ? act.get("creator").toString() : "-"}</td>
                                    <td>${act.get("totalQty") != null ? act.get("totalQty").toString() : "0"}</td>
                                    <td><span class="status ${act.get("status") != null ? act.get("status").toString().toLowerCase() : ""}">${act.get("status") != null ? act.get("status").toString() : ""}</span></td>
                                    <td>${act.get("timeAgo") != null ? act.get("timeAgo").toString() : ""}</td>
                                    <td>
                                        <a class="btn small" href="${act.get("detailUrl") != null ? act.get("detailUrl").toString() : ""}">Xem</a>
                                        @if(act.get("canQuickAction") != null && Boolean.TRUE.equals(act.get("canQuickAction")))
                                            <form method="post" action="${act.get("actionUrl") != null ? act.get("actionUrl").toString() : ""}" class="inline-form" onsubmit="return confirm('Bạn chắc chắn?')">
                                                <button class="btn small ghost" type="submit">Xác nhận</button>
                                            </form>
                                        @endif
                                    </td>
                                </tr>
                            @endfor
                        @endif
                    </tbody>
                </table>
            </section>
        </main>
    `,
    pageScript = @`<script src="/assets/js/manager/inventory.js" defer></script>`
)
