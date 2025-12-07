@import vn.edu.fpt.pharma.dto.warehouse.RequestList
@import vn.edu.fpt.pharma.entity.Branch
@import java.util.List

@param List<RequestList> requests
@param List<Branch> branches

@template.layouts.warehouse(
headContent = @`
    <title>Yêu Cầu Từ Chi Nhánh</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="/assets/css/warehouse/request_list.css">
`,
content = @`


<div class="container">
    <div class="main-content">

        <header class="header">
            <div class="header-content">
                <h1 class="page-title">Yêu Cầu Từ Chi Nhánh</h1>
                <p class="page-description">Quản lý các yêu cầu từ chi nhánh.</p>
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
                <div class="tab">
                    <a href="/warehouse/request/list" class="tab-link">
                        <span class="tab-text">Tất cả yêu cầu</span>
                    </a>
                </div>
                <div class="tab">
                    <a href="/warehouse/request/list/import" class="tab-link">
                        <span class="tab-text">Yêu cầu nhập hàng</span>
                    </a>
                </div>
                <div class="tab">
                    <a href="/warehouse/request/list/return" class="tab-link">
                        <span class="tab-text">Yêu cầu trả hàng</span>
                    </a>
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
                    <input type="date" id="date-input" class="date-input">
                </div>

                <div class="filter-dropdown">
                    <button class="filter-button" id="status-filter">
                        <span class="filter-text">Trạng thái</span>
                        <img src="https://static.codia.ai/image/2025-11-05/Vh0ukYYmjG.png" alt="Dropdown" class="filter-icon">
                    </button>
                    <div class="dropdown-menu" id="status-menu">
                        <div class="dropdown-item" data-value="">Tất cả</div>
                        <div class="dropdown-item" data-value="REQUESTED">Đang duyệt</div>
                        <div class="dropdown-item" data-value="CONFIRMED">Chấp nhận</div>
                        <div class="dropdown-item" data-value="CANCELLED">Từ chối</div>
                    </div>
                </div>

                <button class="clear-filters-button" id="clear-filters">
                    <span>Xóa bộ lọc</span>
                </button>
            </div>
        </section>


        <section class="table-section">
            <div class="table-container">
                <table class="data-table">
                    <thead>
                    <tr class="table-header">
                        <th>Chi Nhánh</th>
                        <th>Ngày tạo</th>
                        <th>Loại Yêu Cầu</th>
                        <th>Trạng Thái</th>
                        <th></th>
                    </tr>
                    </thead>
                    <tbody id="request-tbody">
                    @for(RequestList request : requests)
                        <tr>
                            <td>${request.branchName()}</td>
                            <td>${request.createdAt()}</td>
                            <td>
                                @if(request.requestType().equals("IMPORT"))
                                    Nhập hàng
                                @elseif(request.requestType().equals("RETURN"))
                                    Trả hàng
                                @else
                                    Không xác định
                                @endif
                            </td>
                            <td>
                                @if(request.requestStatus().equals("REQUESTED"))
                                    <div class="status-button pending">Đang duyệt</div>
                                @elseif(request.requestStatus().equals("CONFIRMED"))
                                    <div class="status-button approved">Chấp nhận</div>
                                @elseif(request.requestStatus().equals("CANCELLED"))
                                    <div class="status-button rejected">Từ chối</div>
                                @else
                                    <div class="status-button undefined">Không xác định</div>
                                @endif
                            </td>
                            <td>
                                <a href="/warehouse/request/detail?id=${request.id()}" class="detail-link">Xem Chi Tiết</a>
                            </td>
                        </tr>
                    @endfor
                    </tbody>
                </table>
            </div>
        </section>
    </div>
</div>
`,
pageScript = @`
    <script src="/assets/js/warehouse/request_list.js" defer></script>
`
)

