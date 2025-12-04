@import gg.jte.Content

@param Content content
@param Content headContent
@param Content pageScript

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Nhà Thuốc</title>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;600;700&family=Inter:wght@400&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link rel="stylesheet" href="/assets/css/pharmacist/layout.css">
    <link rel="stylesheet" href="https://cdn.datatables.net/2.3.4/css/dataTables.bootstrap5.css" />
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.13.1/font/bootstrap-icons.min.css">
    <script src="https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4"></script>
    <script src="https://cdn.jsdelivr.net/npm/dayjs@1/dayjs.min.js"></script>
    <script defer src="https://cdn.jsdelivr.net/npm/alpinejs@3.x.x/dist/cdn.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script src="https://cdn.datatables.net/2.3.4/js/dataTables.min.js"></script>
    <script src="https://cdn.datatables.net/2.3.4/js/dataTables.bootstrap5.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
    <link rel="stylesheet" href="/assets/css/pharmacist/layout.css">
    ${headContent}
</head>

<body x-data="{ 'loaded': true }">

<div class="app-container">
    <!-- Sidebar -->
    <aside class="sidebar">
        <header class="sidebar-header">
            <h1 class="sidebar-title">PharmSystem</h1>
        </header>

        <nav class="sidebar-nav">
            <ul class="nav-list">
                <li class="nav-item">
                    <a href="/pharmacist/pos" class="nav-link">
                        <span class="nav-icon material-icons">shopping_cart</span>
                        <span class="nav-text">Bán hàng</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/pharmacist/invoices" class="nav-link">
                        <span class="nav-icon material-icons">receipt_long</span>
                        <span class="nav-text">Quản lý danh sách đơn thuốc</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/pharmacist/shifts" class="nav-link">
                        <span class="nav-icon material-icons">bar_chart</span>
                        <span class="nav-text">Tổng kết ca</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/pharmacist/work" class="nav-link">
                        <span class="nav-icon material-icons">summarize</span>
                        <span class="nav-text">Lịch làm việc</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/pharmacist/revenues" class="nav-link">
                        <span class="nav-icon material-icons">event_note</span>
                        <span class="nav-text">Báo cao danh thu</span>
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

    <!-- Main Content -->
    <main class="main-content">
        <!-- Header -->
        <header class="main-header">
            <div class="header-left">
                <h2 class="branch-name">NHÀ THUỐC HÀ NỘI</h2>
            </div>

            <div class="header-right">
                <div class="date-info">
                    <span class="date-text">01/01/2200</span>
                </div>

                <div class="user-info">
                    <span class="material-icons user-icon">account_circle</span>
                    <span class="user-name">nhan vien a</span>
                    <button class="user-dropdown" type="button">
                        <span class="material-icons">arrow_drop_down</span>
                    </button>

                    <div class="dropdown-menu">
                        <a href="/pharmacist/profile">Thông tin cá nhân</a>
                        <a href="/logout">Đăng xuất</a>
                    </div>
                </div>
            </div>
        </header>

        <!-- Content Area -->
        <div class="content-area">
            ${content}
        </div>
    </main>

</div>

@template.template.preloader()
<script src="/assets/js/pharmacist/layout.js" defer></script>
${pageScript}
</body>
</html>

