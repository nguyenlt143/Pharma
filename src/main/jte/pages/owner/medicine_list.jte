<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý thuốc</title>
    <link rel="stylesheet" href="/assets/css/owner/layout.css">
</head>
<body>
<div class="layout">
    <!-- Sidebar -->
    <aside class="sidebar">
        <div class="sidebar-header">
            <h2>Pharmacy</h2>
        </div>
        <ul class="sidebar-menu">
            <li><a href="#">Tổng quan</a></li>
            <li class="active"><a href="#">Quản lý thuốc</a></li>
            <li><a href="#">Khách hàng</a></li>
            <li><a href="#">Quản lý nhà cung cấp</a></li>
            <li><a href="#">Quản lý danh mục</a></li>
            <li><a href="#">Báo cáo</a></li>
        </ul>
        <div class="sidebar-footer">
            <a href="#">Đăng xuất</a>
        </div>
    </aside>

    <!-- Main content -->
    <main class="main">
        <div class="header">
            <h1>Quản Lý Thuốc</h1>
            <p>Quản lý danh sách các loại thuốc trong hệ thống.</p>
        </div>

        <div class="actions">
            <input type="text" placeholder="Tìm kiếm thuốc..." class="search">
            <button class="add-btn">+ Thêm Thuốc Mới</button>
        </div>

        <table class="table">
            <thead>
            <tr>
                <th>Mã hàng hóa</th>
                <th>Tên hàng hóa</th>
                <th>Tên danh mục</th>
                <th>Thương hiệu</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <tr>
                <td>PNC001</td>
                <td>Paracetamol 500mg</td>
                <td>Thuốc giảm đau, hạ sốt</td>
                <td>Hapacol</td>
                <td>
                    <a href="#">Xem chi tiết</a> |
                    <a href="#">Cập nhật</a>
                </td>
            </tr>
            <tr>
                <td>AMCO02</td>
                <td>Amoxicilin 250mg</td>
                <td>Thuốc kháng sinh</td>
                <td>Imexpharm</td>
                <td>
                    <a href="#">Xem chi tiết</a> |
                    <a href="#">Cập nhật</a>
                </td>
            </tr>
            <tr>
                <td>BRH003</td>
                <td>Berberin 100mg</td>
                <td>Thuốc tiêu hóa</td>
                <td>Traphaco</td>
                <td>
                    <a href="#">Xem chi tiết</a> |
                    <a href="#">Cập nhật</a>
                </td>
            </tr>
            <tr>
                <td>DCL004</td>
                <td>Decolgen Forte</td>
                <td>Thuốc trị cảm cúm</td>
                <td>United Pharma</td>
                <td>
                    <a href="#">Xem chi tiết</a> |
                    <a href="#">Cập nhật</a>
                </td>
            </tr>
            </tbody>
        </table>

        <div class="pagination">
            <p>Hiển thị 1 đến 4 của 20 kết quả</p>
            <div class="pages">
                <button>&lt;</button>
                <button class="active">1</button>
                <button>2</button>
                <button>3</button>
                <button>&gt;</button>
            </div>
        </div>
    </main>
</div>
</body>
</html>
