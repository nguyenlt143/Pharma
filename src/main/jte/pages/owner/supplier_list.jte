<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý nhà cung cấp</title>
    <link rel="stylesheet" href="/assets/css/owner/layout.css">
</head>
<body>
<div class="layout">
    <!-- Sidebar -->
    <aside class="sidebar">
        <div class="sidebar-header">
            <h2>PharmaSys</h2>
        </div>
        <ul class="sidebar-menu">
            <li><a href="#">Tổng quan</a></li>
            <li><a href="#">Quản lý thuốc</a></li>
            <li><a href="#">Khách hàng</a></li>
            <li class="active"><a href="#">Nhà cung cấp</a></li>
            <li><a href="#">Quản lý danh mục</a></li>
            <li><a href="#">Báo cáo</a></li>
        </ul>
        <div class="sidebar-footer">
            <a href="#">Đăng xuất</a>
        </div>
    </aside>

    <!-- Main -->
    <main class="main">
        <div class="header">
            <h1>Quản Lý Nhà Cung Cấp</h1>
        </div>

        <div class="actions">
            <input type="text" placeholder="Tìm kiếm nhà cung cấp..." class="search">
            <button class="add-btn">+ Thêm Nhà Cung Cấp</button>
        </div>

        <table class="table">
            <thead>
            <tr>
                <th>Mã NCC</th>
                <th>Tên nhà cung cấp</th>
                <th>Địa chỉ</th>
                <th>Số điện thoại</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <tr>
                <td>NCC001</td>
                <td>Imexpharm</td>
                <td>Đồng Tháp</td>
                <td>0277 388 9999</td>
                <td><a href="#">Xem chi tiết</a> | <a href="#">Cập nhật</a></td>
            </tr>
            <tr>
                <td>NCC002</td>
                <td>Traphaco</td>
                <td>Hà Nội</td>
                <td>024 3751 7151</td>
                <td><a href="#">Xem chi tiết</a> | <a href="#">Cập nhật</a></td>
            </tr>
            <tr>
                <td>NCC003</td>
                <td>United Pharma</td>
                <td>TP.HCM</td>
                <td>028 3940 8888</td>
                <td><a href="#">Xem chi tiết</a> | <a href="#">Cập nhật</a></td>
            </tr>
            </tbody>
        </table>
    </main>
</div>
</body>
</html>
