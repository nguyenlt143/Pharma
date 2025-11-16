@param java.util.List<vn.edu.fpt.pharma.dto.reveuce.RevenueDetailVM> revenueDetailVMS

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
                    <h1 class="page-title">Doanh thu tháng 8</h1>
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
                        <th class="table-cell header-cell">SỐ LƯỢNG</th>
                        <th class="table-cell header-cell">KIỂU ĐÓNG GÓI</th>
                        <th class="table-cell header-cell">HOẠT CHẤT</th>
                        <th class="table-cell header-cell">HÃNG SẢN XUẤT</th>
                        <th class="table-cell header-cell">NƯỚC SẢN XUẤT</th>
                        <th class="table-cell header-cell">GIÁ BÁN</th>
                        <th class="table-cell header-cell">THÀNH TIỀN</th>
                    </tr>
                    </thead>
                    <tbody>
                    @for(int i = 0; i < revenueDetailVMS.size(); i++)
                        <tr class="table-row">
                            <td class="col-stt">${i + 1}</td>
                            <td class="col-medicine">${revenueDetailVMS.get(i).drugName()}</td>
                            <td class="col-unit">${revenueDetailVMS.get(i).unit()}</td>
                            <td class="col-quantity">${revenueDetailVMS.get(i).quantity()}</td>
                            <td class="col-price">${String.format("%,.0f", revenueDetailVMS.get(i).price())}</td>
                            <td class="col-total">${String.format("%,.0f", revenueDetailVMS.get(i).totalAmount())}</td>
                        </tr>
                    @endfor
                    </tbody>
                </table>
            </div>
        </div>
    </div>
`,
pageScript = @` <script src="/assets/js/pharmacist/revenue_detail.js" defer></script>`
)
