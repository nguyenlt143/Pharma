
@import vn.edu.fpt.pharma.dto.warehouse.ReceiptListItem
@import vn.edu.fpt.pharma.entity.Branch
@import java.util.List

@param List<ReceiptListItem> receipts
@param List<Branch> branches

@template.layouts.warehouse(
headContent = @`
    <title>Danh sách phiếu</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="/assets/css/warehouse/receipt_list.css">
`,
content = @`


<div class="container">
    <div class="main-content">

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
            </div>
        </section>


        <section class="filter-section">
            <div class="filter-container">
                <div class="filter-dropdown">
                    <button class="filter-button" id="branch-filter">
                        <span class="filter-text">Chi Nhánh</span>
                        <img src="https://static.codia.ai/image/2025-11-05/EVQukAnihp.png" alt="Dropdown" class="filter-icon">
                    </button>
                    <div class="dropdown-menu" id="branch-menu">
                        <div class="dropdown-item" data-value="">Tất cả</div>
                        @for(Branch branch : branches)
                            <div class="dropdown-item" data-value="${branch.getId()}">${branch.getName()}</div>
                        @endfor
                    </div>
                </div>

                <div class="filter-dropdown">
                    <button class="filter-button" id="date-filter">
                        <span class="filter-text">Ngày</span>
                        <img src="https://static.codia.ai/image/2025-11-05/qVXHNi7hnA.png" alt="Dropdown" class="filter-icon">
                    </button>
                    <input type="date" id="date-input" class="date-input" style="display:none;">
                </div>

                <div class="filter-dropdown">
                    <button class="filter-button" id="status-filter">
                        <span class="filter-text">Trạng thái</span>
                        <img src="https://static.codia.ai/image/2025-11-05/Vh0ukYYmjG.png" alt="Dropdown" class="filter-icon">
                    </button>
                    <div class="dropdown-menu" id="status-menu">
                        <div class="dropdown-item" data-value="">Tất cả</div>
                        <div class="dropdown-item" data-value="DRAFT">Đang duyệt</div>
                        <div class="dropdown-item" data-value="APPROVED">Chấp nhận</div>
                        <div class="dropdown-item" data-value="CANCELLED">Từ chối</div>
                        <div class="dropdown-item" data-value="RECEIVED">Đã nhận</div>
                        <div class="dropdown-item" data-value="CLOSED">Hoàn tất</div>
                    </div>
                </div>
            </div>
        </section>


        <section class="table-section">
            <div class="table-container">
                <table class="data-table">
                    <thead>
                    <tr class="table-header">
                        <th class="table-cell header-cell">Chi Nhánh</th>
                        <th class="table-cell header-cell">Ngày tạo</th>
                        <th class="table-cell header-cell">Loại Yêu Cầu</th>
                        <th class="table-cell header-cell">Trạng Thái</th>
                        <th class="table-cell header-cell"></th>
                    </tr>
                    </thead>
                    <tbody id="receipt-tbody">
                    @if(receipts.isEmpty())
                        <tr class="table-row">
                            <td class="table-cell data-cell" colspan="5" style="text-align: center;">Không có dữ liệu</td>
                        </tr>
                    @else
                        @for(ReceiptListItem receipt : receipts)
                            <tr class="table-row">
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
        </section>
    </div>
</div>
`,
pageScript = @`
    <script src="/assets/js/warehouse/receipt_list.js" defer></script>
`
)

