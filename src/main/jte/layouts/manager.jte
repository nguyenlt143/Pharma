@import gg.jte.Content

@param Content content
@param Content headContent
@param Content pageScript

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manager - PharmaSys</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link rel="stylesheet" href="/assets/css/owner/layout.css">
    <link rel="stylesheet" href="/assets/css/manager/common.css">
    <link rel="stylesheet" href="/assets/css/toast.css">
    <style>
        .main-content {
            padding: 24px;
            background-color: #f9fafb;
        }
        .page-header {
            margin-bottom: 24px;
        }
        .page-title {
            font-size: 24px;
            font-weight: 600;
            color: #111827;
        }
    </style>
    ${headContent}
</head>
<body>
<div class="app-container">
    <!-- Sidebar -->
    <aside class="sidebar">
        <header class="sidebar-header">
            <h1 class="sidebar-title">PharmaSys</h1>
            <p class="sidebar-subtitle">Quản lý chi nhánh</p>
        </header>

        <nav class="sidebar-nav">
            <ul class="nav-list">
                <li class="nav-item">
                    <a href="/manager/dashboard" class="nav-link">
                        <span class="material-icons nav-icon">dashboard</span>
                        <span class="nav-text">Tổng quan</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/manager/report/revenue" class="nav-link">
                        <span class="material-icons nav-icon">assessment</span>
                        <span class="nav-text">Báo cáo doanh thu</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/manager/report/inventory" class="nav-link">
                        <span class="material-icons nav-icon">inventory</span>
                        <span class="nav-text">Báo cáo kho</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/manager/report/import" class="nav-link">
                        <span class="material-icons nav-icon">swap_horiz</span>
                        <span class="nav-text">Nhập/Xuất kho</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/manager/report/adjustments" class="nav-link">
                        <span class="material-icons nav-icon">edit_note</span>
                        <span class="nav-text">Kiểm kho & Trả hàng</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/manager/shift" class="nav-link">
                        <span class="material-icons nav-icon">schedule</span>
                        <span class="nav-text">Ca làm việc</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/manager/staff" class="nav-link">
                        <span class="material-icons nav-icon">people</span>
                        <span class="nav-text">Nhân viên</span>
                    </a>
                </li>
            </ul>
        </nav>

        <div class="sidebar-footer">
            <a href="/logout" class="logout-link">
                <span class="material-icons">logout</span>
                <span>Đăng xuất</span>
            </a>
        </div>
    </aside>

    <!-- Main Content -->
    <main class="main-content">
        ${content}
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

<script src="/assets/js/utils/toast.js"></script>
${pageScript}

</body>
</html>