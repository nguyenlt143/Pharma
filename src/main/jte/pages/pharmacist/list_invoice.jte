<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Nhà Thuốc</title>
    <link rel="stylesheet" href="styles.css">
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
</head>
<body>
<div class="app-container">
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
                        <span>Quản lý danh sách đon thuốc</span>
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
                        <span class="material-icons">bar_chart</span>
                        <span>Báo cao danh thu</span>
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
        <header class="header">
            <div class="header-left">
                <button class="menu-button">
                    <span class="material-icons">menu</span>
                </button>
                <span class="branch-name">NHÀ THUỐC Chi nhánh DEMO</span>
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
        <main class="main-content">
            <div class="page-header">
                <h2>
                    <span class="material-icons">menu</span>
                    QUẢN LÝ HÓA ĐƠN
                </h2>
            </div>

            <!-- Filter Section -->
            <div class="filter-section">
                <div class="filter-row">
                    <div class="filter-group">
                        <label>Từ ngày</label>
                        <div class="input-container">
                            <input type="text" value="01/08/2022" readonly>
                            <span class="material-icons">calendar_today</span>
                        </div>
                    </div>

                    <div class="filter-group">
                        <label>Tới ngày</label>
                        <div class="input-container">
                            <input type="text" value="10/08/2022" readonly>
                            <span class="material-icons">calendar_today</span>
                        </div>
                    </div>

                    <div class="filter-group">
                        <label>Loại thời gian tìm kiếm</label>
                        <select>
                            <option>Ngày bán</option>
                        </select>
                    </div>

                    <div class="filter-group">
                        <label>Trạng thái</label>
                        <select>
                            <option>Tất cả</option>
                        </select>
                    </div>
                </div>

                <div class="filter-row">
                    <div class="filter-group">
                        <label>Trạng thái liên thông</label>
                        <select>
                            <option>Tất cả</option>
                        </select>
                    </div>

                    <div class="filter-group">
                        <label>Hoá đơn</label>
                        <select>
                            <option>Tất cả</option>
                        </select>
                    </div>

                    <div class="filter-group">
                        <label>Phương thức thanh toán</label>
                        <select>
                            <option>Tất cả</option>
                        </select>
                    </div>
                </div>

                <div class="filter-row">
                    <div class="filter-group full-width">
                        <label>Tìm kiếm theo hàng hoá</label>
                        <input type="text" placeholder="Nhập mã hoặc tên hàng hóa, Ân Enter">
                    </div>

                    <div class="filter-group full-width">
                        <label>Từ khóa tìm kiếm (F3)</label>
                        <div class="search-container">
                            <input type="text" placeholder="Tìm kiếm theo mã hoá đơn,">
                            <button class="search-button">
                                <span class="material-icons">search</span>
                                <span>TÌM KIẾM</span>
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Table Section -->
            <div class="table-section">
                <div class="table-header">
                    <h3>Danh sách hóa đơn (12 Hoá đơn)</h3>
                </div>

                <div class="table-container">
                    <table class="invoice-table">
                        <thead>
                        <tr>
                            <th>#</th>
                            <th>Mã hóa đơn</th>
                            <th>Khách hàng</th>
                            <th>Ngày bán hàng</th>
                            <th class="text-right">Tổng tiền</th>
                            <th class="text-right">Thanh toán</th>
                            <th>Ghi chú</th>
                            <th>Trạng thái</th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td>1</td>
                            <td>
                                <div class="invoice-code">HD0000918</div>
                                <div class="invoice-type">Không bán theo đơn</div>
                            </td>
                            <td>Trần Văn A</td>
                            <td>
                                <div class="date-time">10/08/2022 09:59</div>
                                <div class="shift-info">Ca làm việc : Ca sáng</div>
                            </td>
                            <td class="text-right">5,000.00</td>
                            <td class="text-right">5,000.00</td>
                            <td></td>
                            <td>
                                <div class="status completed">
                                    <span class="material-icons">check_circle</span>
                                    <span>Hoàn Thành</span>
                                </div>
                            </td>
                            <td>
                                <div class="dropdown-container">
                                    <button class="action-button" onclick="toggleDropdown(this)">
                                        <span>Chi tiết đơn</span>
                                        <span class="material-icons">arrow_drop_down</span>
                                    </button>
                                    <div class="dropdown-menu">
                                        <a href="#" class="dropdown-item">
                                            <span class="material-icons">visibility</span>
                                            <span>Chi tiết phiếu</span>
                                        </a>
                                        <a href="#" class="dropdown-item">
                                            <span class="material-icons">print</span>
                                            <span>In phiếu</span>
                                        </a>
                                        <a href="#" class="dropdown-item">
                                            <span class="material-icons">print</span>
                                            <span>In liều dùng</span>
                                        </a>
                                        <a href="#" class="dropdown-item">
                                            <span class="material-icons">close</span>
                                        </a>
                                        <a href="#" class="dropdown-item">
                                            <span class="material-icons">assignment_return</span>
                                            <span>Tạo phiếu khách trả</span>
                                        </a>
                                        <a href="#" class="dropdown-item">
                                            <span class="material-icons">link</span>
                                            <span>Liên thông phiếu</span>
                                        </a>
                                    </div>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td>2</td>
                            <td>
                                <div class="invoice-code">HD0000917</div>
                                <div class="invoice-type">Không bán theo đơn</div>
                            </td>
                            <td>Trần Văn A</td>
                            <td>
                                <div class="date-time">10/08/2022 09:55</div>
                                <div class="shift-info">Ca làm việc : Ca sáng</div>
                            </td>
                            <td class="text-right">315,000.00</td>
                            <td class="text-right">315,000.00</td>
                            <td></td>
                            <td>
                                <div class="status completed">
                                    <span class="material-icons">check_circle</span>
                                    <span>Hoàn Thành</span>
                                </div>
                            </td>
                            <td>
                                <div class="dropdown-container">
                                    <button class="action-button" onclick="toggleDropdown(this)">
                                        <span>Chi tiết đơn</span>
                                        <span class="material-icons">arrow_drop_down</span>
                                    </button>
                                    <div class="dropdown-menu">
                                        <a href="#" class="dropdown-item">
                                            <span class="material-icons">visibility</span>
                                            <span>Chi tiết phiếu</span>
                                        </a>
                                        <a href="#" class="dropdown-item">
                                            <span class="material-icons">print</span>
                                            <span>In phiếu</span>
                                        </a>
                                        <a href="#" class="dropdown-item">
                                            <span class="material-icons">print</span>
                                            <span>In liều dùng</span>
                                        </a>
                                        <a href="#" class="dropdown-item">
                                            <span class="material-icons">close</span>
                                        </a>
                                        <a href="#" class="dropdown-item">
                                            <span class="material-icons">assignment_return</span>
                                            <span>Tạo phiếu khách trả</span>
                                        </a>
                                        <a href="#" class="dropdown-item">
                                            <span class="material-icons">link</span>
                                            <span>Liên thông phiếu</span>
                                        </a>
                                    </div>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td>3</td>
                            <td>
                                <div class="invoice-code">HD0000916</div>
                                <div class="invoice-type">Không bán theo đơn</div>
                            </td>
                            <td>Trần Văn A</td>
                            <td>
                                <div class="date-time">10/08/2022 09:47</div>
                                <div class="shift-info">Ca làm việc : Ca sáng</div>
                            </td>
                            <td class="text-right">132,000.00</td>
                            <td class="text-right">132,000.00</td>
                            <td></td>
                            <td>
                                <div class="status completed">
                                    <span class="material-icons">check_circle</span>
                                    <span>Hoàn Thành</span>
                                </div>
                            </td>
                            <td>
                                <div class="dropdown-container">
                                    <button class="action-button" onclick="toggleDropdown(this)">
                                        <span>Chi tiết đơn</span>
                                        <span class="material-icons">arrow_drop_down</span>
                                    </button>
                                    <div class="dropdown-menu">
                                        <a href="#" class="dropdown-item">
                                            <span class="material-icons">visibility</span>
                                            <span>Chi tiết phiếu</span>
                                        </a>
                                        <a href="#" class="dropdown-item">
                                            <span class="material-icons">print</span>
                                            <span>In phiếu</span>
                                        </a>
                                        <a href="#" class="dropdown-item">
                                            <span class="material-icons">print</span>
                                            <span>In liều dùng</span>
                                        </a>
                                        <a href="#" class="dropdown-item">
                                            <span class="material-icons">close</span>
                                        </a>
                                        <a href="#" class="dropdown-item">
                                            <span class="material-icons">assignment_return</span>
                                            <span>Tạo phiếu khách trả</span>
                                        </a>
                                        <a href="#" class="dropdown-item">
                                            <span class="material-icons">link</span>
                                            <span>Liên thông phiếu</span>
                                        </a>
                                    </div>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td>4</td>
                            <td>
                                <div class="invoice-code">HD0000915</div>
                                <div class="invoice-type">Không bán theo đơn</div>
                            </td>
                            <td>Lý Chi Cao</td>
                            <td>
                                <div class="date-time">10/08/2022 09:25</div>
                                <div class="shift-info">Ca làm việc : Ca sáng</div>
                            </td>
                            <td class="text-right">100,000.00</td>
                            <td class="text-right">100,000.00</td>
                            <td></td>
                            <td>
                                <div class="status completed">
                                    <span class="material-icons">check_circle</span>
                                    <span>Hoàn Thành</span>
                                </div>
                            </td>
                            <td>
                                <div class="dropdown-container">
                                    <button class="action-button" onclick="toggleDropdown(this)">
                                        <span>Chi tiết đơn</span>
                                        <span class="material-icons">arrow_drop_down</span>
                                    </button>
                                    <div class="dropdown-menu">
                                        <a href="#" class="dropdown-item">
                                            <span class="material-icons">visibility</span>
                                            <span>Chi tiết phiếu</span>
                                        </a>
                                        <a href="#" class="dropdown-item">
                                            <span class="material-icons">print</span>
                                            <span>In phiếu</span>
                                        </a>
                                        <a href="#" class="dropdown-item">
                                            <span class="material-icons">print</span>
                                            <span>In liều dùng</span>
                                        </a>
                                        <a href="#" class="dropdown-item">
                                            <span class="material-icons">close</span>
                                        </a>
                                        <a href="#" class="dropdown-item">
                                            <span class="material-icons">assignment_return</span>
                                            <span>Tạo phiếu khách trả</span>
                                        </a>
                                        <a href="#" class="dropdown-item">
                                            <span class="material-icons">link</span>
                                            <span>Liên thông phiếu</span>
                                        </a>
                                    </div>
                                </div>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </main>
    </div>
</div>

<script src="script.js"></script>
</body>
</html>
