@template.layouts.owner(
headContent = @`
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script src="https://cdn.datatables.net/1.13.7/js/jquery.dataTables.min.js"></script>
    <link rel="stylesheet" href="https://cdn.datatables.net/1.13.7/css/jquery.dataTables.min.css">
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
        .data-table-container {
            background: white;
            border-radius: 8px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
            overflow: hidden;
        }
    </style>
`,
content = @`
<div class="page-container">
    <h1 class="page-title">Báo cáo chi tiết doanh thu</h1>

    <div class="filter-section">
        <div class="filter-group">
            <label class="filter-label">Tháng</label>
            <select id="monthSelect" class="filter-select">
                <option value="01" selected>Tháng 1</option>
                <option value="02">Tháng 2</option>
                <option value="03">Tháng 3</option>
                <option value="04">Tháng 4</option>
                <option value="05">Tháng 5</option>
                <option value="06">Tháng 6</option>
                <option value="07">Tháng 7</option>
                <option value="08">Tháng 8</option>
                <option value="09">Tháng 9</option>
                <option value="10">Tháng 10</option>
                <option value="11">Tháng 11</option>
                <option value="12">Tháng 12</option>
            </select>
        </div>
        <div class="filter-group">
            <label class="filter-label">Năm</label>
            <select id="yearSelect" class="filter-select">
                <option value="2024">2024</option>
                <option value="2025">2025</option>
                <option value="2026" selected>2026</option>
                <option value="2027">2027</option>
            </select>
        </div>
        <div class="filter-group">
            <label class="filter-label">Chi nhánh</label>
            <select id="branchSelect" class="filter-select">
                <option value="">Tất cả chi nhánh</option>
            </select>
        </div>
        <button class="btn-primary" onclick="loadReport()">Tải báo cáo</button>
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

    <div class="data-table-container">
        <table id="invoiceTable" class="display" style="width:100%">
            <thead>
                <tr>
                    <th>STT</th>
                    <th>Danh mục thuốc</th>
                    <th>Doanh thu</th>
                </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    </div>
</div>
`,
pageScript = @`<script src="/assets/js/owner/report_revenue.js" defer></script>`
)

