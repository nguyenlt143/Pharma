<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Nhà Thuốc - Báo Cáo Doanh Thu</title>
    <link rel="stylesheet" href="styles.css">
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
</head>
<body>
<div class="container">
    <!-- Sidebar -->
    <aside class="sidebar">
        <div class="sidebar-header">
            <h1>QUẢN LÝ NHÀ THUỐC</h1>
        </div>
        <nav class="sidebar-nav">
            <ul class="nav-list">
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <span class="material-icons">home</span>
                        <span>Tổng quan</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <span class="material-icons">shopping_cart</span>
                        <span>Bán hàng</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <span class="material-icons">receipt_long</span>
                        <span>Quản lý danh sách đơn thuốc</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <span class="material-icons">bar_chart</span>
                        <span>Tổng kết ca</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <span class="material-icons">bar_chart</span>
                        <span>Weekly work</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <span class="material-icons">receipt_long</span>
                        <span>Báo cáo doanh thu</span>
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
    <div class="main-content">
        <!-- Header -->
        <header class="header">
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
                    <span class="material-icons">thermostat</span>
                    <span>0°C</span>
                    <span class="material-icons">cloud</span>
                    <span>000</span>
                </div>
                <div class="date-info">
                    <span class="material-icons">calendar_today</span>
                    <span>01/01/2200</span>
                </div>
                <button class="play-button">
                    <span class="material-icons">play_arrow</span>
                </button>
                <div class="utilities">
                    <span class="material-icons">lightbulb</span>
                    <span>Tiện ích</span>
                </div>
                <div class="user-info">
                    <span class="material-icons">account_circle</span>
                    <span>nhan vien a</span>
                    <button class="user-dropdown">
                        <span class="material-icons">arrow_drop_down</span>
                    </button>
                </div>
            </div>
        </header>

        <!-- Main Content Area -->
        <main class="main">
            <div class="report-container">
                <!-- Report Header -->
                <div class="report-header">
                    <div class="report-title">
                        <span class="material-icons">menu</span>
                        <h2>BÁO CÁO DOANH THU BÁN HÀNG</h2>
                    </div>
                </div>

                <!-- Filters -->
                <div class="filters">
                    <div class="filter-group">
                        <label>Kiểu hiển thị</label>
                        <select class="filter-select">
                            <option>Tiền</option>
                        </select>
                    </div>
                    <div class="filter-group">
                        <label>Loại thời gian</label>
                        <select class="filter-select">
                            <option>Tháng</option>
                        </select>
                    </div>
                    <div class="filter-group">
                        <label>Từ ngày</label>
                        <div class="date-input-container">
                            <input type="text" class="date-input" value="01/08/2022">
                            <span class="material-icons">calendar_today</span>
                        </div>
                    </div>
                    <div class="filter-group">
                        <label>Tới ngày</label>
                        <div class="date-input-container">
                            <input type="text" class="date-input" value="10/08/2022">
                            <span class="material-icons">calendar_today</span>
                        </div>
                    </div>
                    <div class="filter-group search-group">
                        <label>Từ khoá tìm kiếm (F3)</label>
                        <input type="text" class="search-input" placeholder="Tìm kiếm">
                    </div>
                    <button class="search-button">
                        <span class="material-icons">search</span>
                        <span>TÌM KIẾM</span>
                    </button>
                </div>

                <!-- Data Table -->
                <div class="table-container">
                    <table class="data-table">
                        <thead>
                        <tr>
                            <th>#</th>
                            <th>Thời gian</th>
                            <th>Tổng tiền hoá đơn</th>
                            <th>Tổng tiền khách hàng trả lại</th>
                            <th>Tổng giảm giá khách trả</th>
                            <th class="revenue-header">Doanh thu</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr class="total-row">
                            <td></td>
                            <td><strong>Tổng:</strong></td>
                            <td class="amount"><strong>723,000.00</strong></td>
                            <td class="amount"><strong>64,000.00</strong></td>
                            <td class="amount"><strong>0.00</strong></td>
                            <td class="amount revenue"><strong>659,000.00</strong></td>
                        </tr>
                        <tr>
                            <td>1</td>
                            <td>8/2022</td>
                            <td class="amount">552,000.00</td>
                            <td class="amount">4,000.00</td>
                            <td class="amount">0.00</td>
                            <td class="amount">548,000.00</td>
                        </tr>
                        <tr class="alternate">
                            <td>2</td>
                            <td>7/2022</td>
                            <td class="amount">151,000.00</td>
                            <td class="amount">60,000.00</td>
                            <td class="amount">0.00</td>
                            <td class="amount">91,000.00</td>
                        </tr>
                        <tr>
                            <td>3</td>
                            <td>6/2022</td>
                            <td class="amount">20,000.00</td>
                            <td class="amount">0.00</td>
                            <td class="amount">0.00</td>
                            <td class="amount">20,000.00</td>
                        </tr>
                        </tbody>
                    </table>
                </div>

                <!-- Pagination and Controls -->
                <div class="table-footer">
                    <div class="pagination">
                        <button class="page-button disabled">
                            <span class="material-icons">first_page</span>
                        </button>
                        <button class="page-button disabled">
                            <span class="material-icons">chevron_left</span>
                        </button>
                        <input type="text" class="page-input" value="1">
                        <button class="page-button">
                            <span class="material-icons">chevron_right</span>
                        </button>
                        <button class="page-button">
                            <span class="material-icons">last_page</span>
                        </button>
                    </div>
                    <div class="records-info">
                        <select class="records-select">
                            <option>10</option>
                        </select>
                        <span>Bản ghi hiển thị</span>
                    </div>
                </div>

                <!-- Back Button -->
                <div class="action-buttons">
                    <button class="back-action-button">
                        <span class="material-icons">arrow_back</span>
                        <span>TRỞ VỀ</span>
                    </button>
                </div>
            </div>
        </main>
    </div>
</div>

<script src="script.js"></script>
</body>
</html>
