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
    <!-- Header -->
    <header class="header">
        <div class="header-container">
            <div class="logo-container">
                <img src="https://static.codia.ai/image/2025-10-26/z6is336KkE.png" alt="PharmaSys Logo" class="logo-icon">
                <h1 class="logo-text">PharmaSys</h1>
            </div>

            <nav class="nav">
                <a href="/manager/dashboard" class="nav-link active">Tổng quan</a>
                <a href="/manager/sale" class="nav-link">Hóa đơn</a>
                <a href="/manager/shift" class="nav-link">Ca làm việc</a>
                <a href="/manager/report" class="nav-link">Báo cáo</a>
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
</body>
</html>
