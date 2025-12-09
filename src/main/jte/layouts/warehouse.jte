@import gg.jte.Content

@param Content content
@param Content headContent
@param Content pageScript

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Warehouse - PharmaSys</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link rel="stylesheet" href="/assets/css/owner/layout.css">
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
            <p class="sidebar-subtitle">Quản lý kho tổng</p>
        </header>

        <nav class="sidebar-nav">
            <ul class="nav-list">
                <li class="nav-item">
                    <a href="/warehouse/receipt/create" class="nav-link">
                        <span class="material-icons nav-icon">add_box</span>
                        <span class="nav-text">Tạo phiếu nhập</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/warehouse/receipt-list" class="nav-link">
                        <span class="material-icons nav-icon">receipt_long</span>
                        <span class="nav-text">Danh sách phiếu</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/warehouse/request/list" class="nav-link">
                        <span class="material-icons nav-icon">list_alt</span>
                        <span class="nav-text">Danh sách yêu cầu</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/warehouse/check" class="nav-link">
                        <span class="material-icons nav-icon">fact_check</span>
                        <span class="nav-text">Kiểm kho</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/warehouse/inventory" class="nav-link">
                        <span class="material-icons nav-icon">inventory_2</span>
                        <span class="nav-text">Thuốc tồn kho</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/warehouse/category" class="nav-link">
                        <span class="material-icons nav-icon">category</span>
                        <span class="nav-text">Quản lý danh mục</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/warehouse/medicine" class="nav-link">
                        <span class="material-icons nav-icon">medication</span>
                        <span class="nav-text">Quản lý thuốc</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/warehouse/price" class="nav-link">
                        <span class="material-icons nav-icon">attach_money</span>
                        <span class="nav-text">Quản lý giá</span>
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

</body>
</html>
