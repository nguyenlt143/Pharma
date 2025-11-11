@import gg.jte.Content

@param Content content
@param Content headContent
@param Content pageScript

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Warehouse Management</title>

    <!-- Chỉ CSS chung cho layout -->
    <link rel="stylesheet" href="/assets/css/warehouse/sidebar.css">

    ${headContent} <!-- Trang con tự thêm CSS riêng -->
</head>
<body>
<div class="app-container">
    <!-- Sidebar -->
    <aside class="sidebar">
        <header class="sidebar-header">
            <h1 class="sidebar-title">WAREHOUSE MANAGEMENT</h1>
        </header>

        <nav class="sidebar-nav">
            <ul class="nav-list">
                <li class="nav-item">
                    <a href="/warehouse/receipt/create" class="nav-link">
                        <img src="/assets/icons/import.svg" class="nav-icon" alt="Import">
                        <span class="nav-text">Tạo phiếu nhập</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/warehouse/receipt/list" class="nav-link">
                        <img src="/assets/icons/list.svg" class="nav-icon" alt="List">
                        <span class="nav-text">Danh sách phiếu</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/warehouse/request/list" class="nav-link">
                        <img src="/assets/icons/list.svg" class="nav-icon" alt="List">
                        <span class="nav-text">Danh sách yêu cầu</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/warehouse/inventory" class="nav-link">
                        <img src="/assets/icons/audit.svg" class="nav-icon" alt="Audit">
                        <span class="nav-text">Kiểm kê tồn kho</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/warehouse/expiry" class="nav-link">
                        <img src="/assets/icons/expiry.svg" class="nav-icon" alt="Expiry">
                        <span class="nav-text">Hàng sắp hết hạn</span>
                    </a>
                </li>
            </ul>
        </nav>

        <div class="sidebar-footer">
            <button class="back-button">
                <span class="material-icons">arrow_back</span>
            </button>
        </div>
    </aside>

    <!-- Nội dung trang con -->
    <main class="main-content">
        ${content}
    </main>
</div>

${pageScript}
</body>
</html>
