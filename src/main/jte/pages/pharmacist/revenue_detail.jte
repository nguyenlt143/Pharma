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
        <h1 class="sidebar-title">QUẢN LÝ NHÀ THUỐC</h1>
      </div>

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
              <span class="nav-text">Quản lý danh sách đơn thuốc</span>
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
        <div class="content-container">
          <!-- Page Header -->
          <div class="page-header">
            <div class="page-title-section">
              <span class="material-icons page-icon">menu</span>
              <h1 class="page-title">Doanh thu tháng 8</h1>
            </div>

            <div class="date-filter">
              <div class="date-input-container">
                <span class="material-icons date-input-icon">calendar_today</span>
                <input type="text" class="date-input" value="1/1/2200" readonly>
              </div>
            </div>
          </div>

          <!-- Revenue Table -->
          <div class="table-container">
            <table class="revenue-table">
              <thead>
                <tr class="table-header">
                  <th class="table-cell header-cell">TÊN THUỐC</th>
                  <th class="table-cell header-cell">SỐ<br>LƯỢNG</th>
                  <th class="table-cell header-cell">KIỂU ĐÓNG<br>GÓI</th>
                  <th class="table-cell header-cell">HOẠT CHẤT</th>
                  <th class="table-cell header-cell">HÃNG SẢN XUẤT</th>
                  <th class="table-cell header-cell">NƯỚC SẢN XUẤT</th>
                  <th class="table-cell header-cell">GIÁ<br>BÁN</th>
                  <th class="table-cell header-cell">THÀNH<br>TIỀN</th>
                </tr>
              </thead>
              <tbody>
                <tr class="table-row">
                  <td class="table-cell data-cell drug-name">Parasorb</td>
                  <td class="table-cell data-cell">2</td>
                  <td class="table-cell data-cell">Hộp</td>
                  <td class="table-cell data-cell">Paracetamol 500<br>mg -</td>
                  <td class="table-cell data-cell">Hộp 10 vỉ, 20 vỉ x 10 viên nén dài</td>
                  <td class="table-cell data-cell">Việt Nam</td>
                  <td class="table-cell data-cell">25,000</td>
                  <td class="table-cell data-cell">50,000</td>
                </tr>
                <tr class="table-row">
                  <td class="table-cell data-cell drug-name">paradol</td>
                  <td class="table-cell data-cell">2</td>
                  <td class="table-cell data-cell">Hộp</td>
                  <td class="table-cell data-cell">Paracetamol -<br>250mg</td>
                  <td class="table-cell data-cell">Hộp 10 vỉ * 10 viên</td>
                  <td class="table-cell data-cell">Việt Nam</td>
                  <td class="table-cell data-cell">20,000</td>
                  <td class="table-cell data-cell">40,000</td>
                </tr>
                <tr class="table-row">
                  <td class="table-cell data-cell drug-name">Paracetamol 500<br>mg</td>
                  <td class="table-cell data-cell">2</td>
                  <td class="table-cell data-cell">Hộp</td>
                  <td class="table-cell data-cell">Paracetamol 500<br>mg -</td>
                  <td class="table-cell data-cell">Hộp 10 vỉ x 10 viên, Chai 100 viên, 200 viên,<br>500 viên</td>
                  <td class="table-cell data-cell">Việt Nam</td>
                  <td class="table-cell data-cell">18,000</td>
                  <td class="table-cell data-cell">36,000</td>
                </tr>
                <tr class="table-row">
                  <td class="table-cell data-cell drug-name">Parasorb</td>
                  <td class="table-cell data-cell">2</td>
                  <td class="table-cell data-cell">Vỉ</td>
                  <td class="table-cell data-cell">Paracetamol 500<br>mg -</td>
                  <td class="table-cell data-cell">Hộp 10 vỉ, 20 vỉ x 10 viên nén dài</td>
                  <td class="table-cell data-cell">Việt Nam</td>
                  <td class="table-cell data-cell">2,500</td>
                  <td class="table-cell data-cell">5,000</td>
                </tr>
                <tr class="table-row">
                  <td class="table-cell data-cell drug-name">paradol</td>
                  <td class="table-cell data-cell">2</td>
                  <td class="table-cell data-cell">Vỉ</td>
                  <td class="table-cell data-cell">Paracetamol -<br>250mg</td>
                  <td class="table-cell data-cell">Hộp 10 vỉ * 10 viên</td>
                  <td class="table-cell data-cell">Việt Nam</td>
                  <td class="table-cell data-cell">2,000</td>
                  <td class="table-cell data-cell">4,000</td>
                </tr>
                <tr class="table-row total-row">
                  <td class="table-cell data-cell drug-name">Doanh thu</td>
                  <td class="table-cell data-cell"></td>
                  <td class="table-cell data-cell"></td>
                  <td class="table-cell data-cell"></td>
                  <td class="table-cell data-cell"></td>
                  <td class="table-cell data-cell"></td>
                  <td class="table-cell data-cell"></td>
                  <td class="table-cell data-cell">135,000</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </main>

    <!-- Floating Control Panel -->
    <div class="floating-controls">
      <button class="control-button pause-button">
        <span class="material-icons">pause</span>
      </button>
      <div class="control-divider"></div>
      <button class="control-button stop-button">
        <span class="material-icons">stop</span>
      </button>
    </div>
  </div>

  <script src="script.js"></script>
</body>
</html>
