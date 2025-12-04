@param vn.edu.fpt.pharma.dto.user.ProfileVM profile
@param vn.edu.fpt.pharma.dto.user.ProfileUpdateRequest profileUpdateRequest
@param String success = null
@param String error = null
@param String displayFullName
@param String displayEmail
@param String displayPhone

@template.layouts.pharmacist(
headContent = @`<link rel="stylesheet" href="/assets/css/profile/profile.css">`,
content = @`
    <main class="main-content">
        <div class="content-wrapper">
            <!-- Page Header -->
            <header class="page-header">
                <h1 class="page-title">Hồ sơ Nhân viên</h1>
            </header>

            <!-- Alert Messages -->
            @if(success != null)
                <div class="alert alert-success" role="alert">
                    ${success}
                </div>
            @endif
            @if(error != null)
                <div class="alert alert-danger" role="alert">
                    ${error}
                </div>
            @endif

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
                            <form class="edit-form" id="editForm" action="/pharmacist/profile/update" method="post" novalidate>
                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="fullName" class="form-label">
                                            Họ và Tên <span class="required">*</span>
                                        </label>
                                        <input type="text"
                                               id="fullName"
                                               name="fullName"
                                               class="form-input"
                                               value="${displayFullName}"
                                               required
                                               maxlength="100">
                                        <div class="invalid-feedback" id="fullName-error"></div>
                                    </div>
                                    <div class="form-group">
                                        <label for="email" class="form-label">
                                            Email <span class="required">*</span>
                                        </label>
                                        <input type="email"
                                               id="email"
                                               name="email"
                                               class="form-input"
                                               value="${displayEmail}"
                                               required
                                               maxlength="100">
                                        <div class="invalid-feedback" id="email-error"></div>
                                    </div>
                                </div>

                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="phone" class="form-label">Số điện thoại</label>
                                        <input type="tel"
                                               id="phone"
                                               name="phone"
                                               class="form-input"
                                               value="${displayPhone}"
                                               pattern="^(0|\+84)[0-9]{9,10}$"
                                               title="Số điện thoại phải bắt đầu bằng 0 hoặc +84 và có 10-11 chữ số">
                                        <div class="invalid-feedback" id="phone-error"></div>
                                    </div>
                                </div>

                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="password" class="form-label">Mật khẩu mới</label>
                                        <input type="password"
                                               id="password"
                                               name="password"
                                               class="form-input"
                                               placeholder="Để trống nếu không muốn đổi"
                                               minlength="6"
                                               maxlength="100">
                                        <div class="invalid-feedback" id="password-error"></div>
                                    </div>

                                    <div class="form-group">
                                        <label for="confirmPassword" class="form-label">Nhập lại mật khẩu</label>
                                        <input type="password"
                                               id="confirmPassword"
                                               name="confirmPassword"
                                               class="form-input"
                                               placeholder="Nhập lại mật khẩu mới">
                                        <div class="invalid-feedback" id="confirmPassword-error"></div>
                                    </div>
                                </div>

                                <!-- Form Actions -->
                                <div class="form-actions">
                                    <button type="button" class="btn btn-secondary" onclick="window.location.reload()">Hủy</button>
                                    <button type="submit" class="btn btn-primary">Lưu thay đổi</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </main>
`,
pageScript = @`
<script>
document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('editForm');
    const passwordField = document.getElementById('password');
    const confirmPasswordField = document.getElementById('confirmPassword');

    // Validate password confirmation on input
    function validatePasswordMatch() {
        if (passwordField.value && confirmPasswordField.value) {
            if (passwordField.value !== confirmPasswordField.value) {
                confirmPasswordField.setCustomValidity('Mật khẩu xác nhận không khớp');
            } else {
                confirmPasswordField.setCustomValidity('');
            }
        }
    }

    passwordField.addEventListener('input', validatePasswordMatch);
    confirmPasswordField.addEventListener('input', validatePasswordMatch);

    // Form submission validation
    form.addEventListener('submit', function(e) {
        if (!form.checkValidity()) {
            e.preventDefault();
            e.stopPropagation();
        }
        form.classList.add('was-validated');
    });

    // Auto-hide alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 300);
        }, 5000);
    });
});
</script>
<style>
.required { color: #dc3545; }
.is-invalid { border-color: #dc3545; }
.invalid-feedback {
    display: block;
    color: #dc3545;
    font-size: 0.875rem;
    margin-top: 0.25rem;
}
.alert {
    padding: 0.75rem 1.25rem;
    margin-bottom: 1rem;
    border: 1px solid transparent;
    border-radius: 0.375rem;
    transition: opacity 0.3s ease-in-out;
}
.alert-success {
    color: #155724;
    background-color: #d4edda;
    border-color: #c3e6cb;
}
.alert-danger {
    color: #721c24;
    background-color: #f8d7da;
    border-color: #f5c6cb;
}
</style>
`
)

