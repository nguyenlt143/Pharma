@import gg.jte.Content

@param Content content
@param Content headContent
@param Content pageScript

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Warehouse Management</title>

    <!-- Chỉ CSS chung cho layout -->
    <link rel="stylesheet" href="/assets/css/warehouse/sidebar.css">

    ${headContent} <!-- Trang con tự thêm CSS riêng -->
</head>
<body>
<div class="app-container">
    <!-- Sidebar -->
    <aside class="sidebar">
        <header class="sidebar-header">
            <h1 class="sidebar-title">WAREHOUSE MANAGEMENT</h1>
        </header>

        <nav class="sidebar-nav">
            <ul class="nav-list">
                <li class="nav-item">
                    <a href="/warehouse/receipt/create" class="nav-link">
                        <span class="nav-text">Tạo phiếu nhập</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/warehouse/receipt-list" class="nav-link">
                        <span class="nav-text">Danh sách phiếu</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/warehouse/request/list" class="nav-link">
                        <span class="nav-text">Danh sách yêu cầu</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/warehouse/inventory" class="nav-link">
                        <span class="nav-text">Kiểm kê tồn kho</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/warehouse/expiry" class="nav-link">
                        <span class="nav-text">Hàng sắp hết hạn</span>
                    </a>
                </li>
            </ul>
        </nav>

        <div class="sidebar-footer">
            <a href="/logout" class="logout-button">
<%--                <span class="material-icons">logout</span>--%>
                <span class="logout-text">Đăng xuất</span>
            </a>

            <style>
                .logout-button {
                    display: flex;
                    align-items: center;
                    justify-content: flex-start;
                    gap: 6px;
                    background: #e53935;
                    color: white;
                    padding: 8px 10px;
                    border-radius: 5px;
                    text-decoration: none;
                    font-size: 14px;
                    font-weight: 600;
                    transition: 0.2s ease;
                    width: fit-content;
                }
                .logout-button:hover {
                    background: #c62828;
                }
                .logout-text {
                    margin-left: 2px;
                }
            </style>
        </div>
    </aside>

    <!-- Nội dung trang con -->
    <main class="main-content">
        ${content}
    </main>
</div>

${pageScript}
</body>
</html>
