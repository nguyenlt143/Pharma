<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PharmSystem - Hồ sơ Nhân viên</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;900&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="/assets/css/profile/profile.css">
</head>
<body>
<div class="container">
    <!-- Sidebar -->
    <aside class="sidebar">
        <div class="sidebar-content">
            <!-- Logo Section -->
            <div class="logo-section">
                <div class="logo-container">
                    <img src="https://static.codia.ai/image/2025-10-28/J3aPit5AYj.png" alt="PharmSystem Logo" class="logo-icon">
                    <h1 class="logo-text">PharmSystem</h1>
                </div>
            </div>

            <!-- Navigation Menu -->
            <nav class="nav-menu">
                <div class="nav-item">
                    <img src="https://static.codia.ai/image/2025-10-28/cjtgQPk09i.png" alt="Tổng quan" class="nav-icon">
                    <span class="nav-text">Tổng quan</span>
                </div>
                <div class="nav-item">
                    <img src="https://static.codia.ai/image/2025-10-28/Ge9S7NpA8L.png" alt="Đơn hàng" class="nav-icon">
                    <span class="nav-text">Đơn hàng</span>
                </div>
                <div class="nav-item">
                    <img src="https://static.codia.ai/image/2025-10-28/Q2LxpyHgz1.png" alt="Sản phẩm" class="nav-icon">
                    <span class="nav-text">Sản phẩm</span>
                </div>
                <div class="nav-item active">
                    <img src="https://static.codia.ai/image/2025-10-28/vv5XkZVLTk.png" alt="Nhân viên" class="nav-icon">
                    <span class="nav-text">Nhân viên</span>
                </div>
                <div class="nav-item">
                    <img src="https://static.codia.ai/image/2025-10-28/fuv6hzJ8ok.png" alt="Báo cáo" class="nav-icon">
                    <span class="nav-text">Báo cáo</span>
                </div>
            </nav>
        </div>

        <!-- User Profile Section -->
        <div class="user-profile-section">
            <div class="user-profile">
                <div class="user-info">
                    <img src="https://static.codia.ai/image/2025-10-28/G5xs1LE1eD.png" alt="User Avatar" class="user-avatar">
                    <div class="user-details">
                        <div class="user-name">Nguyễn Văn An</div>
                        <div class="user-role">Quản trị viên</div>
                    </div>
                </div>
            </div>

            <div class="nav-item">
                <img src="https://static.codia.ai/image/2025-10-28/cOM2E9qboT.png" alt="Cài đặt" class="nav-icon">
                <span class="nav-text">Cài đặt</span>
            </div>
            <div class="nav-item">
                <img src="https://static.codia.ai/image/2025-10-28/H4XUWFjZbQ.png" alt="Đăng xuất" class="nav-icon">
                <span class="nav-text">Đăng xuất</span>
            </div>
        </div>
    </aside>

    <!-- Main Content -->
    <main class="main-content">
        <div class="content-wrapper">
            <!-- Page Header -->
            <header class="page-header">
                <h1 class="page-title">Hồ sơ Nhân viên</h1>
                <p class="page-description">Quản lý thông tin cá nhân và hoạt động của bạn.</p>
            </header>

            <!-- Content Grid -->
            <div class="content-grid">
                <!-- Left Column - Profile Card -->
                <div class="left-column">
                    <!-- Profile Card -->
                    <div class="profile-card">
                        <div class="profile-avatar-section">
                            <div class="profile-avatar">
                                <img src="https://static.codia.ai/image/2025-10-28/Ot4EUiUOos.png" alt="Profile Avatar" class="avatar-image">
                            </div>
                            <div class="profile-info">
                                <h2 class="profile-name">Nguyễn Văn An</h2>
                                <p class="profile-id">Mã NV: NV12345</p>
                            </div>
                        </div>
                    </div>

                    <!-- Basic Info Card -->
                    <div class="info-card">
                        <h3 class="card-title">Thông tin cơ bản</h3>
                        <div class="info-list">
                            <div class="info-row">
                                <div class="info-label">Chức vụ</div>
                                <div class="info-value">Dược sĩ</div>
                            </div>
                            <div class="info-row">
                                <div class="info-label">Phòng ban</div>
                                <div class="info-value">Phòng Kinh Doanh</div>
                            </div>
                            <div class="info-row">
                                <div class="info-label">Email</div>
                                <div class="info-value">nguyenvana@example.com</div>
                            </div>
                            <div class="info-row">
                                <div class="info-label">Số điện thoại</div>
                                <div class="info-value">0901234567</div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Right Column - Edit Form -->
                <div class="right-column">
                    <div class="form-card">
                        <!-- Tab Navigation -->
                        <div class="tab-navigation">
                            <button class="tab-button active">Chỉnh sửa thông tin</button>
                            <button class="tab-button">Thay đổi mật khẩu</button>
                            <button class="tab-button">Lịch sử hoạt động</button>
                        </div>

                        <!-- Form Content -->
                        <div class="form-content">
                            <form class="edit-form" id="editForm">
                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="fullName" class="form-label">Họ và Tên</label>
                                        <input type="text" id="fullName" class="form-input" value="Nguyễn Văn An">
                                    </div>
                                    <div class="form-group">
                                        <label for="birthDate" class="form-label">Ngày sinh</label>
                                        <div class="date-input-container">
                                            <input type="date" id="birthDate" class="form-input date-input" value="1990-01-15">
                                            <img src="https://static.codia.ai/image/2025-10-28/pTv4pPanpz.png" alt="Calendar" class="date-icon">
                                        </div>
                                    </div>
                                </div>

                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="gender" class="form-label">Giới tính</label>
                                        <select id="gender" class="form-select">
                                            <option value="male" selected>Nam</option>
                                            <option value="female">Nữ</option>
                                            <option value="other">Khác</option>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label for="phone" class="form-label">Số điện thoại</label>
                                        <input type="tel" id="phone" class="form-input" value="0901234567">
                                    </div>
                                </div>

                                <div class="form-group full-width">
                                    <label for="address" class="form-label">Địa chỉ</label>
                                    <input type="text" id="address" class="form-input" value="123 Đường ABC, Phường X, Quận Y, TP. HCM">
                                </div>

                                <!-- Form Actions -->
                                <div class="form-actions">
                                    <button type="button" class="btn btn-secondary">Hủy</button>
                                    <button type="submit" class="btn btn-primary">Lưu thay đổi</button>
                                </div>
                            </form>

                            <!-- Success Message -->
                            <div class="success-message" id="successMessage">
                                <img src="https://static.codia.ai/image/2025-10-28/S5oPzCHKJA.png" alt="Success" class="success-icon">
                                <span class="success-text">Cập nhật thông tin thành công!</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </main>
</div>

<script src="/assets/js/profile/profile.js"></script>
</body>
</html>
