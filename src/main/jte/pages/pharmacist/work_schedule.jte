<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Nhà Thuốc</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link rel="stylesheet" href="styles.css">
</head>
<body>
<div class="app-container">
    <!-- Sidebar -->
    <aside class="sidebar">
        <header class="sidebar-header">
            <h1 class="sidebar-title">QUẢN LÝ NHÀ THUỐC</h1>
        </header>

        <nav class="sidebar-nav">
            <ul class="nav-list">
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <span class="material-icons nav-icon">home</span>
                        <span class="nav-text">Tổng quan</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <span class="material-icons nav-icon">shopping_cart</span>
                        <span class="nav-text">Bán hàng</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <span class="material-icons nav-icon">receipt_long</span>
                        <span class="nav-text">Quản lý danh sách đơn thuốc</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <span class="material-icons nav-icon">bar_chart</span>
                        <span class="nav-text">Tổng kết ca</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <span class="material-icons nav-icon">bar_chart</span>
                        <span class="nav-text">Weekly work</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <span class="material-icons nav-icon">bar_chart</span>
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
    <div class="main-container">
        <!-- Header -->
        <header class="main-header">
            <div class="header-left">
                <button class="menu-button">
                    <span class="material-icons">menu</span>
                </button>
                <h2 class="branch-title">NHÀ THUỐC Chi nhánh DEMO</h2>
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

                <button class="play-button">
                    <span class="material-icons">play_arrow</span>
                </button>

                <div class="utilities">
                    <span class="material-icons utility-icon">lightbulb</span>
                    <span class="utility-text">Tiện ích</span>
                </div>

                <div class="user-info">
                    <span class="material-icons user-icon">account_circle</span>
                    <span class="user-name">nhan vien a</span>
                    <button class="user-dropdown">
                        <span class="material-icons">arrow_drop_down</span>
                    </button>
                </div>
            </div>
        </header>

        <!-- Main Content Area -->
        <main class="main-content">
            <div class="content-card">
                <div class="card-header">
                    <h2 class="card-title">
                        <span class="material-icons title-icon">menu</span>
                        Tổng kết Ca
                    </h2>
                </div>

                <div class="schedule-card">
                    <div class="schedule-controls">
                        <div class="control-group">
                            <label class="control-label">YEAR</label>
                            <select class="control-select">
                                <option>2025</option>
                            </select>
                        </div>

                        <div class="control-group">
                            <label class="control-label">WEEK</label>
                            <select class="control-select week-select">
                                <option>13/10 To 19/10</option>
                            </select>
                        </div>
                    </div>

                    <div class="schedule-table">
                        <div class="table-header">
                            <div class="header-cell empty-cell"></div>
                            <div class="header-cell">MON<br>13/10</div>
                            <div class="header-cell">TUE<br>14/10</div>
                            <div class="header-cell">WED<br>15/10</div>
                            <div class="header-cell">THU<br>16/10</div>
                            <div class="header-cell">FRI<br>17/10</div>
                            <div class="header-cell">SAT<br>18/10</div>
                            <div class="header-cell">SUN<br>19/10</div>
                        </div>

                        <div class="table-body">
                            <div class="table-row">
                                <div class="row-label">Ca Sáng (8:00-11:00)</div>
                                <div class="table-cell">
                                    <span class="material-icons check-icon">check_circle</span>
                                </div>
                                <div class="table-cell">
                                    <span class="material-icons check-icon">check_circle</span>
                                </div>
                                <div class="table-cell">
                                    <span class="material-icons check-icon">check_circle</span>
                                </div>
                                <div class="table-cell empty-cell"></div>
                                <div class="table-cell">
                                    <span class="material-icons check-icon">check_circle</span>
                                </div>
                                <div class="table-cell">
                                    <span class="material-icons check-icon">check_circle</span>
                                </div>
                                <div class="table-cell empty-cell"></div>
                            </div>

                            <div class="table-row">
                                <div class="row-label">Ca Chiều (13:00- 17:00)</div>
                                <div class="table-cell">
                                    <span class="material-icons check-icon">check_circle</span>
                                </div>
                                <div class="table-cell empty-cell"></div>
                                <div class="table-cell">
                                    <span class="material-icons check-icon">check_circle</span>
                                </div>
                                <div class="table-cell">
                                    <span class="material-icons check-icon">check_circle</span>
                                </div>
                                <div class="table-cell">
                                    <span class="material-icons check-icon">check_circle</span>
                                </div>
                                <div class="table-cell empty-cell"></div>
                                <div class="table-cell">
                                    <span class="material-icons check-icon">check_circle</span>
                                </div>
                            </div>

                            <div class="table-row">
                                <div class="row-label">Ca Tối (20:00-22:00)</div>
                                <div class="table-cell empty-cell"></div>
                                <div class="table-cell">
                                    <span class="material-icons check-icon">check_circle</span>
                                </div>
                                <div class="table-cell empty-cell"></div>
                                <div class="table-cell">
                                    <span class="material-icons check-icon">check_circle</span>
                                </div>
                                <div class="table-cell empty-cell"></div>
                                <div class="table-cell">
                                    <span class="material-icons check-icon">check_circle</span>
                                </div>
                                <div class="table-cell">
                                    <span class="material-icons check-icon">check_circle</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="card-footer">
                    <button class="return-button">
                        <span class="material-icons">arrow_back</span>
                        TRỞ VỀ
                    </button>
                </div>
            </div>
        </main>
    </div>
</div>

<script src="script.js"></script>
</body>
</html>
