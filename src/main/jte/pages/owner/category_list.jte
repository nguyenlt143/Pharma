<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý danh mục hàng hóa</title>
    <link rel="stylesheet" href="/assets/css/owner/layout.css">
</head>
<body>
<div class="layout">
    <!-- Sidebar -->
    <aside class="sidebar">
        <div class="sidebar-header">
            <h2>Pharmacy</h2>
            <p>Quản trị viên</p>
        </div>
        <ul class="sidebar-menu">
            <li><a href="#">Tổng quan</a></li>
            <li><a href="#">Quản lý thuốc</a></li>
            <li><a href="#">Khách hàng</a></li>
            <li><a href="#">Nhà cung cấp</a></li>
            <li class="active"><a href="#">Quản lý danh mục</a></li>
            <li><a href="#">Báo cáo</a></li>
        </ul>
        <div class="sidebar-footer">
            <a href="#">Đăng xuất</a>
        </div>
    </aside>

    <!-- Main -->
    <main class="main">
        <div class="header">
            <h1>Quản Lý Danh Mục Hàng Hóa</h1>
        </div>

        <div class="actions">
            <input type="text" placeholder="Tìm kiếm danh mục..." class="search">
            <button class="add-btn">+ Thêm Danh Mục</button>
        </div>

        <table class="table">
            <thead>
            <tr>
                <th>Mã danh mục</th>
                <th>Tên danh mục</th>
                <th>Mô tả</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <tr>
                <td>DM001</td>
                <td>Thuốc giảm đau</td>
                <td>Dành cho các loại thuốc giảm đau, hạ sốt</td>
                <td><a href="#">Xem chi tiết</a> | <a href="#">Cập nhật</a></td>
            </tr>
            <tr>
                <td>DM002</td>
                <td>Thuốc kháng sinh</td>
                <td>Nhóm thuốc trị nhiễm khuẩn, viêm</td>
                <td><a href="#">Xem chi tiết</a> | <a href="#">Cập nhật</a></td>
            </tr>
            <tr>
                <td>DM003</td>
                <td>Thuốc tiêu hóa</td>
                <td>Hỗ trợ hệ tiêu hóa, giảm đau bụng</td>
                <td><a href="#">Xem chi tiết</a> | <a href="#">Cập nhật</a></td>
            </tr>
            </tbody>
        </table>
    </main>
</div>
</body>
</html>
