<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý kho tổng - Nhập từ nhà cung cấp</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;700&family=Roboto:wght@400;700&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link rel="stylesheet" href="resources/static/assets/css/warehouse/receipt_create.css">
</head>
<body>
<div class="app-container">
    <!-- Sidebar -->
    <aside class="sidebar">
        <div class="sidebar-header">
            <h1 class="sidebar-title">QUẢN LÝ KHO TỔNG</h1>
        </div>
        <nav class="sidebar-nav">
            <ul class="nav-list">
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <img src="https://static.codia.ai/image/2025-10-26/S2i0irCwtR.png" alt="Icon" class="nav-icon">
                        <span>Quản lý yêu cầu</span>
                    </a>
                </li>
                <li class="nav-item active">
                    <a href="#" class="nav-link">
                        <img src="https://static.codia.ai/image/2025-10-26/spxUHNFTth.png" alt="Icon" class="nav-icon">
                        <span>Nhập kho</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <img src="https://static.codia.ai/image/2025-10-26/n7n1i7eOGD.png" alt="Icon" class="nav-icon">
                        <span>Xuất kho</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <img src="https://static.codia.ai/image/2025-10-26/qX0PRqBxm9.png" alt="Icon" class="nav-icon">
                        <span>Kiểm kho</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <img src="https://static.codia.ai/image/2025-10-26/hbt58BWhW8.png" alt="Icon" class="nav-icon">
                        <span>Báo cáo</span>
                    </a>
                </li>
            </ul>
        </nav>
    </aside>

    <!-- Main Content -->
    <main class="main-content">
        <header class="page-header">
            <h1 class="page-title">Nhập từ nhà cung cấp</h1>
        </header>

        <!-- Form Section -->
        <section class="form-section">
            <div class="form-container">
                <div class="form-row">
                    <div class="form-group">
                        <label for="supplier" class="form-label">Nhà cung cấp (F4) *</label>
                        <div class="input-wrapper">
                            <span class="material-icons input-icon">search</span>
                            <input type="text" id="supplier" class="form-input" value="Công ty dược phẩm GSK">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="receipt-code" class="form-label">Mã phiếu nhập</label>
                        <input type="text" id="receipt-code" class="form-input disabled" placeholder="Mã phiếu tự động" disabled>
                    </div>
                    <div class="form-group">
                        <label for="import-date" class="form-label">Ngày nhập *</label>
                        <input type="text" id="import-date" class="form-input" value="11/12/2023">
                    </div>
                    <div class="form-group">
                        <label for="import-time" class="form-label">Giờ nhập *</label>
                        <input type="text" id="import-time" class="form-input" value="06:46">
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group full-width">
                        <label for="notes" class="form-label">Ghi chú</label>
                        <textarea id="notes" class="form-textarea" rows="4"></textarea>
                    </div>
                </div>
            </div>
        </section>

        <!-- Product Section -->
        <section class="product-section">
            <div class="product-container">
                <div class="search-add-row">
                    <div class="search-wrapper">
                        <span class="material-icons search-icon">search</span>
                        <input type="text" class="search-input" placeholder="Tìm kiếm thuốc">
                    </div>
                    <button class="add-button">
                        <span class="material-icons">add</span>
                        <span>THÊM VÀO TỪ DANH MỤC (F7)</span>
                    </button>
                </div>

                <!-- Product Table -->
                <div class="table-container">
                    <table class="product-table">
                        <thead>
                        <tr>
                            <th>STT</th>
                            <th>Tên thuốc</th>
                            <th>ĐVT</th>
                            <th>Hàm lượng</th>
                            <th>Số lô</th>
                            <th>NSX</th>
                            <th>HSD</th>
                            <th>Số lượng</th>
                            <th>Giá nhập</th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td>1</td>
                            <td>Kaceni</td>
                            <td>Viên</td>
                            <td>500mg</td>
                            <td><input type="text" class="table-input disabled" value="123" disabled></td>
                            <td><input type="text" class="table-input disabled" value="10/10/2025" disabled></td>
                            <td><input type="text" class="table-input disabled" value="10/10/2026" disabled></td>
                            <td><input type="text" class="table-input" value="1,000"></td>
                            <td><input type="text" class="table-input" value="100,000"></td>
                            <td><button class="delete-button"><span class="material-icons">delete</span></button></td>
                        </tr>
                        <tr>
                            <td>2</td>
                            <td>Panadol</td>
                            <td>Viên</td>
                            <td>500mg</td>
                            <td><input type="text" class="table-input disabled" value="69696" disabled></td>
                            <td><input type="text" class="table-input disabled" value="10/10/2025" disabled></td>
                            <td><input type="text" class="table-input disabled" value="10/10/2025" disabled></td>
                            <td><input type="text" class="table-input" value="1,000"></td>
                            <td><input type="text" class="table-input" value="100,000"></td>
                            <td><button class="delete-button"><span class="material-icons">delete</span></button></td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </section>

        <!-- Action Buttons -->
        <div class="action-buttons">
            <button class="btn-secondary">LƯU NHÁP (F8)</button>
            <button class="btn-primary">HOÀN THÀNH (F9)</button>
        </div>
    </main>
</div>

<script src="resources/static/assets/js/warehouse/receipt_create.js"></script>
</body>
</html>