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
    ${headContent}
</head>

<body x-data="{ 'loaded': true }">

<div class="app-container">
    <!-- Sidebar -->
    <aside class="sidebar">
        <header class="sidebar-header">
            <h1 class="sidebar-title">PharmaSys</h1>
            <p class="sidebar-subtitle">Dược sĩ</p>
        </header>

        <nav class="sidebar-nav">
            <ul class="nav-list">
                <li class="nav-item">
                    <a href="/pharmacist/pos" class="nav-link">
                        <span class="material-icons nav-icon">shopping_cart</span>
                        <span class="nav-text">Bán hàng</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/pharmacist/invoices" class="nav-link">
                        <span class="material-icons nav-icon">receipt_long</span>
                        <span class="nav-text">Danh sách đơn thuốc</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/pharmacist/shifts" class="nav-link">
                        <span class="material-icons nav-icon">bar_chart</span>
                        <span class="nav-text">Tổng kết ca</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/pharmacist/work" class="nav-link">
                        <span class="material-icons nav-icon">schedule</span>
                        <span class="nav-text">Lịch làm việc</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/pharmacist/revenues" class="nav-link">
                        <span class="material-icons nav-icon">assessment</span>
                        <span class="nav-text">Báo cáo doanh thu</span>
                    </a>
                </li>
            </ul>
        </nav>

        <div class="sidebar-footer">
            <a href="/pharmacist/profile" class="logout-link">
                <span class="material-icons">person</span>
                <span>Thông tin cá nhân</span>
            </a>
            <a href="/logout" class="logout-link">
                <span class="material-icons">logout</span>
                <span>Đăng xuất</span>
            </a>
        </div>
    </aside>

    <!-- Main Content -->
    <main class="main-content">
        <!-- Content Area -->
        <div class="content-area">
            ${content}
        </div>
    </main>
</div>

${pageScript}

<script>
    // Active nav link highlighting
    document.addEventListener('DOMContentLoaded', function() {
        const currentPath = window.location.pathname;
        const navLinks = document.querySelectorAll('.nav-link');

        let bestMatch = null;
        navLinks.forEach(link => {
            link.classList.remove('active');
            const href = link.getAttribute('href');
            if (href && currentPath.startsWith(href)) {
                if (!bestMatch || href.length > bestMatch.getAttribute('href').length) {
                    bestMatch = link;
                }
            }
        });

        if (bestMatch) {
            bestMatch.classList.add('active');
        }
    });
</script>
<script src="/assets/js/pharmacist/layout.js" defer></script>
</body>
</html>
