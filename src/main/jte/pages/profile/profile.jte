@param vn.edu.fpt.pharma.dto.user.ProfileVM profile

@template.layouts.pharmacist(
headContent = @`<link rel="stylesheet" href="/assets/css/profile/profile.css">`,
content = @`
    <main class="main-content">
        <div class="content-wrapper">
            <!-- Page Header -->
            <header class="page-header">
                <h1 class="page-title">Hồ sơ Nhân viên</h1>
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
                                <h2 class="profile-name">${profile.fullName()}</h2>
                            </div>
                        </div>
                    </div>

                    <!-- Basic Info Card -->
                    <div class="info-card">
                        <h3 class="card-title">Thông tin cơ bản</h3>
                        <div class="info-list">
                            <div class="info-row">
                                <div class="info-label">Chức vụ</div>
                                <div class="info-value">${profile.role()}</div>
                            </div>
                            <div class="info-row">
                                <div class="info-label">Email</div>
                                <div class="info-value">${profile.email()}</div>
                            </div>
                            <div class="info-row">
                                <div class="info-label">Số điện thoại</div>
                                <div class="info-value">${profile.phone()}</div>
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
                        </div>

                        <!-- Form Content -->
                        <div class="form-content">
                            <form class="edit-form" id="editForm" action="/pharmacist/profile/update" method="post">
                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="fullName" class="form-label">Họ và Tên</label>
                                        <input type="text" id="fullName"  name="fullName" class="form-input" value="${profile.fullName()}">
                                    </div>
                                    <div class="form-group">
                                        <label for="birthDate" class="form-label">Ngày sinh</label>
                                        <div class="date-input-container">
                                            <input type="date" id="birthDate" class="form-input date-input" value="1990-01-15">
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
                                        <input type="tel" id="phone" name="phone" class="form-input" value="${profile.phone()}">
                                    </div>

                                    <div class="form-group">
                                        <label for="password" class="form-label">Mật khẩu mới</label>
                                        <input type="password" id="password" name="password" class="form-input" placeholder="Nhập mật khẩu mới">
                                    </div>

                                    <div class="form-group">
                                        <label for="confirmPassword" class="form-label">Nhập lại mật khẩu</label>
                                        <input type="password" id="confirmPassword" name="confirmPassword" class="form-input" placeholder="Nhập lại mật khẩu">
                                    </div>

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
`,
pageScript = @``
)

