
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


        <section class="search-section">
            <div class="search-container">
                <div class="search-icon">
                    <img src="https://static.codia.ai/image/2025-11-05/wS6ZqNm37v.png" alt="Search">
                </div>
                <div class="search-input">
                    <img src="https://static.codia.ai/image/2025-11-05/KB8ok8AS1S.png" alt="Search Input">
                </div>
            </div>
        </section>


        <section class="tab-section">
            <div class="tab-container">
                <div class="tab active">
                    <span class="tab-text">Tất cả yêu cầu</span>
                </div>
                <div class="tab">
                    <span class="tab-text">Yêu cầu nhập hàng</span>
                </div>
                <div class="tab">
                    <span class="tab-text">Yêu cầu trả hàng</span>
                </div>
            </div>
        </section>


        <section class="filter-section">
            <div class="filter-container">
                <button class="filter-button">
                    <span class="filter-text">Chi Nhánh</span>
                    <img src="https://static.codia.ai/image/2025-11-05/EVQukAnihp.png" alt="Dropdown" class="filter-icon">
                </button>
                <button class="filter-button">
                    <span class="filter-text">Ngày</span>
                    <img src="https://static.codia.ai/image/2025-11-05/qVXHNi7hnA.png" alt="Dropdown" class="filter-icon">
                </button>
                <button class="filter-button">
                    <span class="filter-text">Trạng thái</span>
                    <img src="https://static.codia.ai/image/2025-11-05/Vh0ukYYmjG.png" alt="Dropdown" class="filter-icon">
                </button>
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
                    <tbody>
                    <tr class="table-row">
                        <td class="table-cell data-cell">Hoàng Mai</td>
                        <td class="table-cell data-cell date-cell">2025-09-15</td>
                        <td class="table-cell data-cell type-cell">Nhập Hàng</td>
                        <td class="table-cell data-cell">
                            <div class="status-button pending">Đang duyệt</div>
                        </td>
                        <td class="table-cell data-cell">
                            <a href="#" class="detail-link">Xem Chi Tiết</a>
                        </td>
                    </tr>
                    <tr class="table-row">
                        <td class="table-cell data-cell">Mỹ Đình</td>
                        <td class="table-cell data-cell date-cell">2025-09-10</td>
                        <td class="table-cell data-cell type-cell">Nhập Hàng</td>
                        <td class="table-cell data-cell">
                            <div class="status-button approved">Chấp nhận</div>
                        </td>
                        <td class="table-cell data-cell">
                            <a href="#" class="detail-link">Xem Chi Tiết</a>
                        </td>
                    </tr>
                    <tr class="table-row">
                        <td class="table-cell data-cell">Hòa Lạc</td>
                        <td class="table-cell data-cell date-cell">2025-09-05</td>
                        <td class="table-cell data-cell type-cell">Trả Hàng</td>
                        <td class="table-cell data-cell">
                            <div class="status-button rejected">Từ chối</div>
                        </td>
                        <td class="table-cell data-cell">
                            <a href="#" class="detail-link">Xem Chi Tiết</a>
                        </td>
                    </tr>
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

