D@template.layouts.owner(
headContent = @`
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        .page-title {
            font-size: 24px;
            font-weight: 700;
            color: #111827;
            margin-bottom: 24px;
        }
        .filter-section {
            background: white;
            padding: 16px;
            border-radius: 8px;
            margin-bottom: 24px;
            display: flex;
            gap: 16px;
            align-items: end;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }
        .filter-group {
            flex: 1;
        }
        .filter-label {
            display: block;
            margin-bottom: 6px;
            font-weight: 500;
            color: #374151;
            font-size: 14px;
        }
        .filter-input, .filter-select {
            width: 100%;
            padding: 8px;
            border: 1px solid #D1D5DB;
            border-radius: 6px;
            box-sizing: border-box;
        }
        .btn-primary {
            background-color: #2563EB;
            color: white;
            padding: 10px 20px;
            border-radius: 8px;
            border: none;
            font-weight: 500;
            cursor: pointer;
            height: fit-content;
        }
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-bottom: 24px;
        }
        .stat-card {
            background: white;
            padding: 24px;
            border-radius: 8px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }
        .stat-label {
            font-size: 14px;
            color: #6B7280;
            margin-bottom: 8px;
        }
        .stat-value {
            font-size: 28px;
            font-weight: 700;
            color: #111827;
        }
        .stat-value.revenue {
            color: #059669;
        }
        .stat-value.profit {
            color: #2563EB;
        }
        .stat-value.orders {
            color: #7C3AED;
        }
        .chart-container {
            background: white;
            padding: 24px;
            border-radius: 8px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
            margin-bottom: 24px;
        }
        .chart-title {
            font-size: 18px;
            font-weight: 600;
            margin-bottom: 16px;
            color: #111827;
        }
        .product-stats {
            background: white;
            padding: 24px;
            border-radius: 8px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }
        .product-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 12px 0;
            border-bottom: 1px solid #E5E7EB;
        }
        .product-item:last-child {
            border-bottom: none;
        }
        .product-name {
            font-weight: 500;
            color: #374151;
        }
        .product-percent {
            font-size: 14px;
            color: #6B7280;
        }
        .product-bar {
            height: 8px;
            background: #E5E7EB;
            border-radius: 4px;
            margin: 8px 0;
            overflow: hidden;
        }
        .product-bar-fill {
            height: 100%;
            border-radius: 4px;
            transition: width 0.3s;
        }
    </style>
`,
content = @`
<div class="page-container">
    <h1 class="page-title">Dashboard - Tổng quan doanh thu & lợi nhuận</h1>

    <div class="filter-section">
        <div class="filter-group">
            <label class="filter-label">Tháng</label>
            <input type="month" id="periodInput" class="filter-input" value="${java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"))}">
        </div>
        <div class="filter-group">
            <label class="filter-label">Chi nhánh</label>
            <select id="branchSelect" class="filter-select">
                <option value="">Tất cả chi nhánh</option>
            </select>
        </div>
        <button class="btn-primary" onclick="loadDashboard('revenue')">Xem Doanh thu</button>
        <button class="btn-primary" onclick="loadDashboard('profit')">Xem Lợi nhuận</button>
    </div>

    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-label">Tổng doanh thu</div>
            <div class="stat-value revenue" id="totalRevenue">0 đ</div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Tổng lợi nhuận</div>
            <div class="stat-value profit" id="totalProfit">0 đ</div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Tổng số đơn</div>
            <div class="stat-value orders" id="totalOrders">0</div>
        </div>
    </div>

    <div class="chart-container">
        <div class="chart-title">Biểu đồ doanh thu</div>
        <canvas id="revenueChart" height="60"></canvas>
    </div>

    <div class="product-stats">
        <div class="chart-title">Thống kê sản phẩm theo danh mục</div>
        <div id="productStatsList"></div>
    </div>
</div>
`,
pageScript = @`<script src="/assets/js/owner/dashboard.js" defer></script>`
)

