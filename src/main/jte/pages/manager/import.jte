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
                        @if(recentActivities == null || recentActivities.isEmpty())
                            <tr><td colspan="6" class="muted">Không có hoạt động gần đây</td></tr>
                        @else

                            @for(java.util.Map<String, java.lang.Object> __actObj : recentActivities)
                                <tr>
                                    <td><a href="javascript:void(0)" onclick="viewRequestDetail(${__actObj.get("id") != null ? __actObj.get("id").toString() : "0"})" class="mono link">${__actObj.get("code") != null ? __actObj.get("code").toString() : ""}</a></td>
                                    <td><span class="badge badge-${__actObj.get("typeClass") != null ? __actObj.get("typeClass").toString() : ""}">${__actObj.get("type") != null ? __actObj.get("type").toString() : ""}</span></td>
                                    <td>${__actObj.get("creator") != null ? __actObj.get("creator").toString() : "-"}</td>

                                    <td>${__actObj.get("totalValueFormatted") != null ? __actObj.get("totalValueFormatted").toString() : (__actObj.get("totalValue") != null ? ( __actObj.get("totalValue") instanceof Number ? java.text.NumberFormat.getInstance(java.util.Locale.forLanguageTag("vi-VN")).format(((Number)__actObj.get("totalValue")).longValue()) + " đ" : java.text.NumberFormat.getInstance(java.util.Locale.forLanguageTag("vi-VN")).format(new java.math.BigDecimal(__actObj.get("totalValue").toString()).longValue()) + " đ") : "0 đ")}</td>
                                    <td>${__actObj.get("timeAgo") != null ? __actObj.get("timeAgo").toString() : ""}</td>
                                    <td>
                                        <button class="btn btn-small" onclick="viewRequestDetail(${__actObj.get("id") != null ? __actObj.get("id").toString() : "0"})">Xem</button>
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
