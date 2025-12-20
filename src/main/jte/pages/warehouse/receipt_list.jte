
@import vn.edu.fpt.pharma.dto.warehouse.ReceiptListItem
@import vn.edu.fpt.pharma.entity.Branch
@import vn.edu.fpt.pharma.dto.common.PageResponse
@import java.util.List

@param List<ReceiptListItem> receipts
@param List<Branch> branches
@param PageResponse<ReceiptListItem> pagination

@template.layouts.warehouse(
headContent = @`
    <title>Danh sách phiếu</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="/assets/css/warehouse/receipt_list.css">
    <link rel="stylesheet" href="/assets/css/warehouse/pagination.css">
`,
content = @`
<header class="header">
    <div class="header-content">
        <h1 class="page-title">Danh sách phiếu</h1>
        <p class="page-description">Quản lý danh sách phiếu.</p>
    </div>
</header>


<%--        <section class="search-section">--%>
<%--            <div class="search-container">--%>
<%--                <div class="search-icon">--%>
<%--                    <img src="https://static.codia.ai/image/2025-11-05/wS6ZqNm37v.png" alt="Search">--%>
<%--                </div>--%>
<%--                <div class="search-input">--%>
<%--                    <img src="https://static.codia.ai/image/2025-11-05/KB8ok8AS1S.png" alt="Search Input">--%>
<%--                </div>--%>
<%--            </div>--%>
<%--        </section>--%>


        <section class="tab-section">
            <div class="tab-container">
                <div class="tab active" data-type="">
                    <span class="tab-text">Tất cả </span>
                </div>
                <div class="tab" data-type="SUP_TO_WARE">
                    <span class="tab-text">Phiếu Nhập</span>
                </div>
                <div class="tab" data-type="WARE_TO_BR">
                    <span class="tab-text">Phiếu Xuất</span>
                </div>
                <div class="tab" data-type="BR_TO_WARE">
                    <span class="tab-text">Chuyển Về Kho Tổng</span>
                </div>
                <div class="tab" data-type="BR_TO_WARE2">
                    <span class="tab-text">Hàng Hết Hạn</span>
                </div>
                <div class="tab" data-type="DISPOSAL">
                    <span class="tab-text">Xuất Hủy</span>
                </div>
                <div class="tab" data-type="INVENTORY_ADJUSTMENT">
                    <span class="tab-text">Kiểm Kho</span>
                </div>
            </div>
        </section>



        <section class="table-section">
            <div class="table-container">
                <table class="data-table">
                    <thead>
                    <tr class="table-header">
                        <th class="table-cell header-cell" style="width: 50px; text-align: center;">STT</th>
                        <th class="table-cell header-cell">Chi Nhánh</th>
                        <th class="table-cell header-cell">Ngày tạo</th>
                        <th class="table-cell header-cell">Loại Phiếu</th>
                        <th class="table-cell header-cell">Trạng Thái</th>
                        <th class="table-cell header-cell"></th>
                    </tr>
                    </thead>
                    <tbody id="receipt-tbody">
                    @if(receipts.isEmpty())
                        <tr class="table-row">
                            <td class="table-cell data-cell" colspan="6" style="text-align: center;">Không có dữ liệu</td>
                        </tr>
                    @else
                        @for(var i = 0; i < receipts.size(); i++)
                            !{var receipt = receipts.get(i);}
                            <tr class="table-row">
                                <td class="table-cell data-cell" style="text-align: center;">${i + 1}</td>
                                <td class="table-cell data-cell">${receipt.branchName()}</td>
                                <td class="table-cell data-cell date-cell">${receipt.createdDate()}</td>
                                <td class="table-cell data-cell type-cell">${receipt.requestType()}</td>
                                <td class="table-cell data-cell">
                                    <div class="status-button ${receipt.statusClass()}">${receipt.status()}</div>
                                </td>
                                <td class="table-cell data-cell">
                                    <a href="/warehouse/receipt-detail/${receipt.id()}" class="detail-link">Xem Chi Tiết</a>
                                </td>
                            </tr>
                        @endfor
                    @endif
                    </tbody>
                </table>
        </div>

        @if(pagination != null)
            @template.template.pagination(pagination = pagination, baseUrl = "/warehouse/receipt-list")
        @endif
    </section>
`,
pageScript = @`
    <script src="/assets/js/warehouse/receipt_list.js" defer></script>
`
)

