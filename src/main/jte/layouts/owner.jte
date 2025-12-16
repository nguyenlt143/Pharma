@import gg.jte.Content

@param Content content
@param Content headContent
@param Content pageScript

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Owner Management - PharmaSys</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link rel="stylesheet" href="/assets/css/owner/layout.css">
    ${headContent}
</head>
<body>
<div class="app-container">
    <!-- Sidebar -->
    <aside class="sidebar">
        <header class="sidebar-header">
            <h1 class="sidebar-title">PharmaSys</h1>
            <p class="sidebar-subtitle">Quản trị viên</p>
        </header>

        <nav class="sidebar-nav">
            <ul class="nav-list">
                <li class="nav-item">
                    <a href="/owner/dashboard" class="nav-link">
                        <span class="material-icons nav-icon">dashboard</span>
                        <span class="nav-text">Dashboard</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/owner/supplier/list" class="nav-link">
                        <span class="material-icons nav-icon">local_shipping</span>
                        <span class="nav-text">Nhà cung cấp</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/owner/report/revenue" class="nav-link">
                        <span class="material-icons nav-icon">assessment</span>
                        <span class="nav-text">Báo cáo</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/owner/report/inventory" class="nav-link">
                        <span class="material-icons nav-icon">inventory</span>
                        <span class="nav-text">Báo cáo kho</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/owner/inventory/current" class="nav-link">
                        <span class="material-icons nav-icon">medication</span>
                        <span class="nav-text">Kho hiện tại</span>
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

<script>
    // Active nav link highlighting
    document.addEventListener('DOMContentLoaded', function() {
        const currentPath = window.location.pathname;
        const navLinks = document.querySelectorAll('.nav-link');
        
        navLinks.forEach(link => {
            link.classList.remove('active');
            const href = link.getAttribute('href');
            if (href && currentPath.startsWith(href)) {
                link.classList.add('active');
            }
        });
    });
</script>

${pageScript}
</body>
</html>

