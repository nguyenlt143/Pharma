@template.layouts.owner(
headContent = @`
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
        .stat-value.profit {
            color: #2563EB;
        }
        .stat-value.revenue {
            color: #059669;
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
    <h1 class="page-title">Báo cáo chi tiết lợi nhuận</h1>

    <div class="filter-section">
        <div class="filter-group">
            <label class="filter-label">Từ ngày</label>
            <input type="date" id="fromDate" class="filter-input" value="${java.time.LocalDate.now().minusDays(30).toString()}">
        </div>
        <div class="filter-group">
            <label class="filter-label">Đến ngày</label>
            <input type="date" id="toDate" class="filter-input" value="${java.time.LocalDate.now().toString()}">
        </div>
        <div class="filter-group">
            <label class="filter-label">Ca làm việc</label>
            <select id="shiftSelect" class="filter-select">
                <option value="">Tất cả</option>
            </select>
        </div>
        <div class="filter-group">
            <label class="filter-label">Nhân viên</label>
            <select id="employeeSelect" class="filter-select">
                <option value="">Tất cả</option>
            </select>
        </div>
        <button class="btn-primary" onclick="loadReport()">Tải báo cáo</button>
    </div>

    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-label">Tổng lợi nhuận</div>
            <div class="stat-value profit" id="totalProfit">0 đ</div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Tổng doanh thu</div>
            <div class="stat-value revenue" id="totalRevenue">0 đ</div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Tổng số đơn</div>
            <div class="stat-value orders" id="totalOrders">0</div>
        </div>
    </div>

    <div class="data-table-container">
        <table id="profitTable" class="display" style="width:100%">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Mã hóa đơn</th>
                    <th>Thời gian</th>
                    <th>Doanh thu</th>
                    <th>Lợi nhuận</th>
                </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    </div>
</div>
`,
pageScript = @`<script src="/assets/js/owner/report_profit.js" defer></script>`
)

