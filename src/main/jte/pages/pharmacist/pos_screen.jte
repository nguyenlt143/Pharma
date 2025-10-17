<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Nhà Thuốc</title>
    <link rel="stylesheet" href="styles.css">
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;600;700&family=Inter:wght@400&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
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
                        <span class="nav-icon material-icons">home</span>
                        <span class="nav-text">Tổng quan</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <span class="nav-icon material-icons">shopping_cart</span>
                        <span class="nav-text">Bán hàng</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <span class="nav-icon material-icons">receipt_long</span>
                        <span class="nav-text">Quản lý danh sách đon thuốc</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <span class="nav-icon material-icons">bar_chart</span>
                        <span class="nav-text">Tổng kết ca</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <span class="nav-icon material-icons">bar_chart</span>
                        <span class="nav-text">Weekly work</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <span class="nav-icon material-icons">bar_chart</span>
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

        <!-- Content Area -->
        <div class="content-area">
            <!-- Search Section -->
            <section class="search-section">
                <div class="search-container">
                    <div class="search-input-wrapper">
                        <span class="material-icons search-icon">search</span>
                        <input type="text" class="search-input" placeholder="Search for medication...">
                        <button class="search-button">Search</button>
                    </div>
                </div>
            </section>

            <!-- Right Panel -->
            <section class="right-panel">
                <!-- Prescription Section -->
                <div class="prescription-section">
                    <div class="section-header">
                        <h3 class="section-title">Đơn Thuốc</h3>
                    </div>

                    <div class="prescription-table">
                        <table class="table">
                            <thead>
                            <tr class="table-header">
                                <th class="table-cell">#</th>
                                <th class="table-cell">Tên thuốc</th>
                                <th class="table-cell">Kiểu đóng gói</th>
                                <th class="table-cell">Số lượng</th>
                                <th class="table-cell text-right">Giá bán</th>
                                <th class="table-cell text-right">Thành tiền</th>
                            </tr>
                            </thead>
                            <tbody>
                            <!-- Empty table body for now -->
                            </tbody>
                            <tfoot>
                            <tr class="table-footer">
                                <td colspan="5" class="table-cell text-right">Tổng tiền</td>
                                <td class="table-cell text-right">
                                    <div class="total-amount"></div>
                                    <div class="vat-info">VAT: 0 (0%)</div>
                                </td>
                            </tr>
                            </tfoot>
                        </table>
                    </div>
                </div>

                <!-- Payment Section -->
                <div class="payment-section">
                    <div class="payment-header">
                        <h3 class="section-title">Thanh toán</h3>
                        <span class="payment-time">10/08/2022 09:55</span>
                    </div>

                    <div class="payment-content">
                        <div class="customer-info">
                            <div class="form-group">
                                <label class="form-label">Tên khách hàng</label>
                                <div class="input-wrapper">
                                    <input type="text" class="form-input">
                                    <button class="clear-button">
                                        <span class="material-icons">close</span>
                                    </button>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="form-label">Số điện thoại</label>
                                <div class="input-wrapper">
                                    <input type="text" class="form-input">
                                    <button class="clear-button">
                                        <span class="material-icons">close</span>
                                    </button>
                                </div>
                            </div>
                        </div>

                        <div class="payment-details">
                            <div class="payment-row">
                                <span class="payment-label">Tổng tiền hàng</span>
                                <span class="payment-value"></span>
                            </div>

                            <div class="payment-row">
                                <span class="payment-label">Khách cần trả</span>
                                <span class="payment-value"></span>
                            </div>

                            <div class="form-group">
                                <label class="form-label">Khách thanh toán (F8)</label>
                                <input type="text" class="form-input">
                            </div>

                            <div class="form-group">
                                <label class="form-label">Phương thức thanh toán</label>
                                <select class="form-select">
                                    <option>Tiền mặt</option>
                                </select>
                            </div>

                            <div class="payment-row">
                                <span class="payment-label">Tiền thừa trả khách</span>
                                <span class="payment-value">0.00</span>
                            </div>

                            <div class="form-group">
                                <label class="form-label">Ghi chú</label>
                                <textarea class="form-textarea"></textarea>
                            </div>

                            <button class="payment-button">Thanh Toán</button>
                        </div>
                    </div>
                </div>
            </section>
        </div>
    </main>
</div>

<script src="script.js"></script>
</body>
</html>
