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
    <link rel="stylesheet" href="/assets/css/pharmacist/pos.css">
    ${headContent}

</head>
<body>
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
                    <a href="/pharmacist/invoice" class="nav-link">
                        <span class="nav-icon material-icons">receipt_long</span>
                        <span class="nav-text">Quản lý danh sách đơn thuốc</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/pharmacist/shift" class="nav-link">
                        <span class="nav-icon material-icons">bar_chart</span>
                        <span class="nav-text">Tổng kết ca</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/pharmacist/work" class="nav-link">
                        <span class="nav-icon material-icons">summarize</span>
                        <span class="nav-text">Weekly work</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/pharmacist/revenue" class="nav-link">
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
                <button class="menu-button">
                    <span class="material-icons">menu</span>
                </button>
                <h2 class="branch-name">NHÀ THUỐC Chi nhánh DEMO</h2>
                <button class="dropdown-button">
                    <span class="material-icons">arrow_drop_down</span>
                </button>
            </div>

            <div class="header-right">
                <div class="weather-info">
                    <span class="material-icons weather-icon">thermostat</span>
                    <span class="temperature">0°C</span>
                    <span class="material-icons cloud-icon">cloud</span>
                    <span class="weather-code">000</span>
                </div>

                <div class="date-info">
                    <span class="material-icons date-icon">calendar_today</span>
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

<script src="/assets/js/pharmacist/pos.js" defer></script>
${pageScript}
</body>
</html>
