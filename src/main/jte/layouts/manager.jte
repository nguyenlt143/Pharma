@import gg.jte.Content

@param Content content
@param Content headContent
@param Content pageScript

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PharmaSys - Bảng điều khiển</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700@display=swap" rel="stylesheet">
    <link rel="stylesheet" href="/assets/css/manager/header.css">
    ${headContent}
</head>
<body>

<div class="dashboard">
    <header class="header">
        <div class="header-container">
            <div class="logo-container">
                <img src="https://static.codia.ai/image/2025-10-26/z6is336KkE.png" alt="PharmaSys Logo" class="logo-icon">
                <h1 class="logo-text">PharmaSys</h1>
            </div>

            <nav class="nav">
                <a href="/manager/dashboard" class="nav-link">Tổng quan</a>
                <!-- Dropdown menu cho Báo cáo -->
                <div class="nav-dropdown">
                    <a href="#" class="nav-link">Báo cáo ▾</a>
                    <div class="dropdown-content">
                        <a href="/manager/report/revenue">Doanh thu</a>
                        <a href="/manager/report/inventory">Báo cáo kho</a>
                    </div>
                </div>
                <a href="/manager/shift" class="nav-link">Ca làm việc</a>
                <a href="/manager/staff" class="nav-link">Nhân viên</a>
            </nav>

            <div class="user-section">
                <img src="https://static.codia.ai/image/2025-10-26/XV9PGtTDvv.png" alt="Notifications" class="notification-icon">
                <img src="https://static.codia.ai/image/2025-10-26/QXRwDp0GJT.png" alt="User Avatar" class="user-avatar">
            </div>
        </div>
    </header>

    ${content}

</div>

<script src="script.js"></script>
${pageScript}

<script>
    document.addEventListener('DOMContentLoaded', function() {
        const navLinks = document.querySelectorAll('.nav-link');
        const currentPath = window.location.pathname;

        // --- RESET mọi trạng thái active ---
        navLinks.forEach(link => link.classList.remove('active'));

        // --- Kích hoạt link trùng URL ---
        navLinks.forEach(link => {
            const href = link.getAttribute('href');
            if (href && href !== '#' && currentPath === new URL(link.href).pathname) {
                link.classList.add('active');
            }
        });

        // --- Kích hoạt riêng menu Báo cáo khi đang ở trang con ---
        if (currentPath.startsWith('/manager/report')) {
            const reportMenu = document.querySelector('.nav-dropdown > .nav-link');
            if (reportMenu) reportMenu.classList.add('active');
        }

        // --- Dropdown hover behavior ---
        const dropdown = document.querySelector('.nav-dropdown');
        if (dropdown) {
            const dropdownContent = dropdown.querySelector('.dropdown-content');

            // Hiện khi hover vào
            dropdown.addEventListener('mouseenter', () => {
                dropdownContent.style.display = 'flex';
            });

            // Ẩn khi rời chuột
            dropdown.addEventListener('mouseleave', () => {
                dropdownContent.style.display = 'none';
            });
        }
    });
</script>

</body>
</html>