@template.layouts.pharmacist(
headContent = @`<link rel="stylesheet" href="/assets/css/pharmacist/revenue.css">`,
content = @`
    <!-- Main Content Area -->
    <main class="main">
        <div class="report-container">
            <!-- Report Header -->
            <div class="report-header">
                <div class="report-title">
                    <span class="material-icons">menu</span>
                    <h2>Tổng kết Ca</h2>
                </div>
            </div>

            <!-- Data Table -->
            <div class="table-container">
                <table class="data-table">
                    <thead>
                    <tr>
                        <th>Ca</th>
                        <th>Thanh toán</th>
                        <th>Tiền mặt</th>
                        <th>Chuyển khoản</th>
                        <th>Doanh thu</th>
                        <th class="revenue-header">Doanh thu</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr class="total-row">
                        <td></td>
                        <td><strong>Tổng:</strong></td>
                        <td class="amount"><strong>723,000.00</strong></td>
                        <td class="amount"><strong>64,000.00</strong></td>
                        <td class="amount"><strong>0.00</strong></td>
                        <td class="amount revenue"><strong>659,000.00</strong></td>
                    </tr>
                    <tr>
                        <td>1</td>
                        <td>8/2022</td>
                        <td class="amount">552,000.00</td>
                        <td class="amount">4,000.00</td>
                        <td class="amount">0.00</td>
                        <td class="amount">548,000.00</td>
                    </tr>
                    <tr class="alternate">
                        <td>2</td>
                        <td>7/2022</td>
                        <td class="amount">151,000.00</td>
                        <td class="amount">60,000.00</td>
                        <td class="amount">0.00</td>
                        <td class="amount">91,000.00</td>
                    </tr>
                    <tr>
                        <td>3</td>
                        <td>6/2022</td>
                        <td class="amount">20,000.00</td>
                        <td class="amount">0.00</td>
                        <td class="amount">0.00</td>
                        <td class="amount">20,000.00</td>
                    </tr>
                    </tbody>
                </table>
            </div>

            <!-- Pagination and Controls -->
            <div class="table-footer">
                <div class="pagination">
                    <button class="page-button disabled">
                        <span class="material-icons">first_page</span>
                    </button>
                    <button class="page-button disabled">
                        <span class="material-icons">chevron_left</span>
                    </button>
                    <input type="text" class="page-input" value="1">
                    <button class="page-button">
                        <span class="material-icons">chevron_right</span>
                    </button>
                    <button class="page-button">
                        <span class="material-icons">last_page</span>
                    </button>
                </div>
                <div class="records-info">
                    <select class="records-select">
                        <option>10</option>
                    </select>
                    <span>Bản ghi hiển thị</span>
                </div>
            </div>

            <!-- Back Button -->
            <div class="action-buttons">
                <button class="back-action-button">
                    <span class="material-icons">arrow_back</span>
                    <span>TRỞ VỀ</span>
                </button>
            </div>
        </div>
    </main>
`,
pageScript = @` <script src="/assets/js/pharmacist/revenue.js" defer></script>`
)
