   @template.layouts.pharmacist(
headContent = @`<link rel="stylesheet" href="/assets/css/pharmacist/revenue_detail.css">`,
content = @`
    <!-- Content Area -->
    <div class="content-area">
        <div class="content-container">
            <!-- Page Header -->
            <div class="page-header">
                <div class="page-title-section">
                    <span class="material-icons page-icon">menu</span>
                    <h1 class="page-title">Ca sáng</h1>
                </div>

                <div class="date-filter">
                    <div class="date-input-container">
                        <span class="material-icons date-input-icon">calendar_today</span>
                        <input type="text" class="date-input" value="1/1/2200" readonly>
                    </div>
                </div>
            </div>

            <!-- Revenue Table -->
            <div class="table-container">
                <table class="revenue-table">
                    <thead>
                    <tr class="table-header">
                        <th class="table-cell header-cell">TÊN THUỐC</th>
                        <th class="table-cell header-cell">SỐ<br>LƯỢNG</th>
                        <th class="table-cell header-cell">KIỂU ĐÓNG<br>GÓI</th>
                        <th class="table-cell header-cell">HOẠT CHẤT</th>
                        <th class="table-cell header-cell">HÃNG SẢN XUẤT</th>
                        <th class="table-cell header-cell">NƯỚC SẢN XUẤT</th>
                        <th class="table-cell header-cell">GIÁ<br>BÁN</th>
                        <th class="table-cell header-cell">THÀNH<br>TIỀN</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr class="table-row">
                        <td class="table-cell data-cell drug-name">Parasorb</td>
                        <td class="table-cell data-cell">2</td>
                        <td class="table-cell data-cell">Hộp</td>
                        <td class="table-cell data-cell">Paracetamol 500<br>mg -</td>
                        <td class="table-cell data-cell">Hộp 10 vỉ, 20 vỉ x 10 viên nén dài</td>
                        <td class="table-cell data-cell">Việt Nam</td>
                        <td class="table-cell data-cell">25,000</td>
                        <td class="table-cell data-cell">50,000</td>
                    </tr>
                    <tr class="table-row">
                        <td class="table-cell data-cell drug-name">paradol</td>
                        <td class="table-cell data-cell">2</td>
                        <td class="table-cell data-cell">Hộp</td>
                        <td class="table-cell data-cell">Paracetamol -<br>250mg</td>
                        <td class="table-cell data-cell">Hộp 10 vỉ * 10 viên</td>
                        <td class="table-cell data-cell">Việt Nam</td>
                        <td class="table-cell data-cell">20,000</td>
                        <td class="table-cell data-cell">40,000</td>
                    </tr>
                    <tr class="table-row">
                        <td class="table-cell data-cell drug-name">Paracetamol 500<br>mg</td>
                        <td class="table-cell data-cell">2</td>
                        <td class="table-cell data-cell">Hộp</td>
                        <td class="table-cell data-cell">Paracetamol 500<br>mg -</td>
                        <td class="table-cell data-cell">Hộp 10 vỉ x 10 viên, Chai 100 viên, 200 viên,<br>500 viên</td>
                        <td class="table-cell data-cell">Việt Nam</td>
                        <td class="table-cell data-cell">18,000</td>
                        <td class="table-cell data-cell">36,000</td>
                    </tr>
                    <tr class="table-row">
                        <td class="table-cell data-cell drug-name">Parasorb</td>
                        <td class="table-cell data-cell">2</td>
                        <td class="table-cell data-cell">Vỉ</td>
                        <td class="table-cell data-cell">Paracetamol 500<br>mg -</td>
                        <td class="table-cell data-cell">Hộp 10 vỉ, 20 vỉ x 10 viên nén dài</td>
                        <td class="table-cell data-cell">Việt Nam</td>
                        <td class="table-cell data-cell">2,500</td>
                        <td class="table-cell data-cell">5,000</td>
                    </tr>
                    <tr class="table-row">
                        <td class="table-cell data-cell drug-name">paradol</td>
                        <td class="table-cell data-cell">2</td>
                        <td class="table-cell data-cell">Vỉ</td>
                        <td class="table-cell data-cell">Paracetamol -<br>250mg</td>
                        <td class="table-cell data-cell">Hộp 10 vỉ * 10 viên</td>
                        <td class="table-cell data-cell">Việt Nam</td>
                        <td class="table-cell data-cell">2,000</td>
                        <td class="table-cell data-cell">4,000</td>
                    </tr>
                    <tr class="table-row total-row">
                        <td class="table-cell data-cell drug-name">Doanh thu</td>
                        <td class="table-cell data-cell"></td>
                        <td class="table-cell data-cell"></td>
                        <td class="table-cell data-cell"></td>
                        <td class="table-cell data-cell"></td>
                        <td class="table-cell data-cell"></td>
                        <td class="table-cell data-cell"></td>
                        <td class="table-cell data-cell">135,000</td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
`,
pageScript = @` <script src="/assets/js/pharmacist/revenue_detail.js" defer></script>`
)
