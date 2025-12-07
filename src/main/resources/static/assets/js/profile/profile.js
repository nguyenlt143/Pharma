document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('editForm');
    const fullNameField = document.getElementById('fullName');
    const emailField = document.getElementById('email');
    const phoneField = document.getElementById('phone');
    const currentPasswordField = document.getElementById('currentPassword');
    const passwordField = document.getElementById('password');
    const confirmPasswordField = document.getElementById('confirmPassword');

    // Avatar upload elements
    const avatarUpload = document.getElementById('avatarUpload');
    const avatarPreview = document.getElementById('avatarPreview');
    const avatarDataField = document.getElementById('avatarData');

    // Create toast notification function
    function showToast(message, type = 'error') {
        // Remove existing toast if any
        const existingToast = document.querySelector('.toast-notification');
        if (existingToast) {
            existingToast.remove();
        }

        // Create toast element
        const toast = document.createElement('div');
        toast.className = 'toast-notification toast-' + type;

        // Icon based on type
        const icon = type === 'error' ? '❌' : '✅';

        toast.innerHTML = `
            <div class="toast-icon">${icon}</div>
            <div class="toast-message">${message}</div>
            <button class="toast-close" onclick="this.parentElement.remove()">×</button>
        `;

        // Add to body
        document.body.appendChild(toast);

        // Trigger animation
        setTimeout(() => toast.classList.add('show'), 10);

        // Auto remove after 5 seconds
        setTimeout(() => {
            toast.classList.remove('show');
            setTimeout(() => toast.remove(), 300);
        }, 5000);
    }

    // Avatar upload handler with compression
    if (avatarUpload && avatarPreview) {
        avatarUpload.addEventListener('change', function(e) {
            const file = e.target.files[0];

            if (!file) {
                return;
            }

            // Validate file type
            const allowedTypes = ['image/jpeg', 'image/png', 'image/jpg', 'image/gif'];
            if (!allowedTypes.includes(file.type)) {
                showToast('Chỉ chấp nhận file ảnh (JPEG, PNG, JPG, GIF)', 'error');
                avatarUpload.value = '';
                return;
            }

            // Validate file size (max 2MB for upload)
            const maxSize = 2 * 1024 * 1024; // 2MB
            if (file.size > maxSize) {
                showToast('Kích thước file không được vượt quá 2MB', 'error');
                avatarUpload.value = '';
                return;
            }

            // Preview and compress image
            const reader = new FileReader();
            reader.onload = function(event) {
                const img = new Image();
                img.onload = function() {
                    // Create canvas to compress image
                    const canvas = document.createElement('canvas');
                    let width = img.width;
                    let height = img.height;

                    // Max dimensions for avatar (400x400)
                    const maxWidth = 400;
                    const maxHeight = 400;

                    // Calculate new dimensions maintaining aspect ratio
                    if (width > height) {
                        if (width > maxWidth) {
                            height *= maxWidth / width;
                            width = maxWidth;
                        }
                    } else {
                        if (height > maxHeight) {
                            width *= maxHeight / height;
                            height = maxHeight;
                        }
                    }

                    canvas.width = width;
                    canvas.height = height;

                    const ctx = canvas.getContext('2d');
                    ctx.drawImage(img, 0, 0, width, height);

                    // Convert to base64 with compression (quality 0.7 = 70%)
                    const compressedBase64 = canvas.toDataURL('image/jpeg', 0.7);

                    // Preview compressed image
                    avatarPreview.src = compressedBase64;

                    // Store compressed base64 data in hidden field
                    if (avatarDataField) {
                        avatarDataField.value = compressedBase64;
                    }

                    // Show success toast
                    showToast('Ảnh đã được nén và chọn thành công!', 'success');
                };
                img.src = event.target.result;
            };
            reader.readAsDataURL(file);
        });
    }

    // Validation rules
    const validators = {
        fullName: {
            validate: (value) => {
                if (!value || value.trim() === '') {
                    return 'Họ tên không được để trống';
                }
                if (value.length > 100) {
                    return 'Họ tên không được vượt quá 100 ký tự';
                }
                if (value.trim().length < 2) {
                    return 'Họ tên phải có ít nhất 2 ký tự';
                }
                // Check for valid characters (letters, spaces, Vietnamese)
                const nameRegex = /^[a-zA-ZÀ-ỹ\s]+$/;
                if (!nameRegex.test(value)) {
                    return 'Họ tên chỉ được chứa chữ cái và khoảng trắng';
                }
                return null;
            }
        },
        email: {
            validate: (value) => {
                if (!value || value.trim() === '') {
                    return 'Email không được để trống';
                }
                const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                if (!emailRegex.test(value)) {
                    return 'Email không hợp lệ (ví dụ: example@domain.com)';
                }
                if (value.length > 100) {
                    return 'Email không được vượt quá 100 ký tự';
                }
                return null;
            }
        },
        phone: {
            validate: (value) => {
                // Phone is optional
                if (!value || value.trim() === '') {
                    return null;
                }
                const phoneRegex = /^(0|\+84)[0-9]{9,10}$/;
                if (!phoneRegex.test(value)) {
                    return 'Số điện thoại phải bắt đầu bằng 0 hoặc +84 và có 10-11 chữ số';
                }
                return null;
            }
        },
        currentPassword: {
            validate: (value) => {
                // Current password is required only if user wants to change password
                const newPassword = passwordField ? passwordField.value : '';
                if (!newPassword || newPassword.trim() === '') {
                    // User is not changing password, current password not required
                    return null;
                }
                // User wants to change password, must provide current password
                if (!value || value.trim() === '') {
                    return 'Vui lòng nhập mật khẩu hiện tại để đổi mật khẩu';
                }
                if (value.length < 6) {
                    return 'Mật khẩu phải có ít nhất 6 ký tự';
                }
                return null;
            }
        },
        password: {
            validate: (value) => {
                // Password is optional (for change password)
                if (!value || value.trim() === '') {
                    return null;
                }
                if (value.length < 6) {
                    return 'Mật khẩu phải có ít nhất 6 ký tự';
                }
                if (value.length > 100) {
                    return 'Mật khẩu không được vượt quá 100 ký tự';
                }
                return null;
            }
        },
        confirmPassword: {
            validate: (value) => {
                const password = passwordField.value;
                // Only validate if password is provided
                if (!password || password.trim() === '') {
                    return null;
                }
                if (!value || value.trim() === '') {
                    return 'Vui lòng nhập lại mật khẩu';
                }
                if (value !== password) {
                    return 'Mật khẩu xác nhận không khớp';
                }
                return null;
            }
        }
    };

    // Create error message element if not exists
    function ensureErrorElement(field) {
        let errorDiv = document.getElementById(field.id + '-error');
        if (!errorDiv) {
            errorDiv = document.createElement('div');
            errorDiv.id = field.id + '-error';
            errorDiv.className = 'invalid-feedback';
            errorDiv.style.display = 'none';
            field.parentElement.appendChild(errorDiv);
        }
        return errorDiv;
    }

    // Show error message
    function showError(field, message) {
        const errorDiv = ensureErrorElement(field);
        errorDiv.textContent = message;
        errorDiv.style.display = 'block';
        errorDiv.style.color = '#dc3545';
        errorDiv.style.fontSize = '13px';
        errorDiv.style.marginTop = '6px';
        errorDiv.style.fontWeight = '500';

        field.classList.add('is-invalid');
        field.classList.remove('is-valid');
        field.style.borderColor = '#dc3545';
        field.style.boxShadow = '0 0 0 3px rgba(220, 53, 69, 0.1)';
    }

    // Show success (valid) state
    function showSuccess(field) {
        const errorDiv = ensureErrorElement(field);
        errorDiv.textContent = '';
        errorDiv.style.display = 'none';

        field.classList.remove('is-invalid');
        field.classList.add('is-valid');
        field.style.borderColor = '#10b981';
        field.style.boxShadow = '0 0 0 3px rgba(16, 185, 129, 0.1)';
    }

    // Clear validation state
    function clearValidation(field) {
        const errorDiv = ensureErrorElement(field);
        errorDiv.textContent = '';
        errorDiv.style.display = 'none';

        field.classList.remove('is-invalid', 'is-valid');
        field.style.borderColor = '';
        field.style.boxShadow = '';
    }

    // Validate single field
    function validateField(field) {
        const validator = validators[field.id];
        if (!validator) return true;

        const error = validator.validate(field.value);
        if (error) {
            showError(field, error);
            return false;
        } else {
            // Only show success if field has value or is required
            if (field.value.trim() !== '' || field.required) {
                showSuccess(field);
            } else {
                clearValidation(field);
            }
            return true;
        }
    }

    // Validate all fields
    function validateForm() {
        let isValid = true;

        // Validate required fields
        if (!validateField(fullNameField)) isValid = false;
        if (!validateField(emailField)) isValid = false;

        // Validate optional fields only if they have value
        if (phoneField && phoneField.value.trim() !== '') {
            if (!validateField(phoneField)) isValid = false;
        }

        // If user wants to change password, validate current password and new passwords
        if (passwordField && passwordField.value.trim() !== '') {
            if (currentPasswordField && !validateField(currentPasswordField)) isValid = false;
            if (!validateField(passwordField)) isValid = false;
            if (confirmPasswordField && !validateField(confirmPasswordField)) isValid = false;
        }

        return isValid;
    }

    // Real-time validation on input
    if (fullNameField) {
        fullNameField.addEventListener('input', function() {
            validateField(this);
        });

        fullNameField.addEventListener('blur', function() {
            validateField(this);
        });
    }

    if (emailField) {
        emailField.addEventListener('input', function() {
            validateField(this);
        });

        emailField.addEventListener('blur', function() {
            validateField(this);
        });
    }

    if (phoneField) {
        phoneField.addEventListener('input', function() {
            if (this.value.trim() !== '') {
                validateField(this);
            } else {
                clearValidation(this);
            }
        });

        phoneField.addEventListener('blur', function() {
            if (this.value.trim() !== '') {
                validateField(this);
            }
        });
    }

    if (currentPasswordField) {
        currentPasswordField.addEventListener('input', function() {
            // Validate current password if new password is provided
            if (passwordField && passwordField.value.trim() !== '') {
                validateField(this);
            } else {
                clearValidation(this);
            }
        });

        currentPasswordField.addEventListener('blur', function() {
            // Validate current password if new password is provided
            if (passwordField && passwordField.value.trim() !== '') {
                validateField(this);
            }
        });
    }

    if (passwordField) {
        passwordField.addEventListener('input', function() {
            if (this.value.trim() !== '') {
                validateField(this);
                // Also validate current password and confirm password if they have values
                if (currentPasswordField) {
                    validateField(currentPasswordField);
                }
                if (confirmPasswordField && confirmPasswordField.value.trim() !== '') {
                    validateField(confirmPasswordField);
                }
            } else {
                clearValidation(this);
                if (currentPasswordField) {
                    clearValidation(currentPasswordField);
                }
                if (confirmPasswordField) {
                    clearValidation(confirmPasswordField);
                }
            }
        });

        passwordField.addEventListener('blur', function() {
            if (this.value.trim() !== '') {
                validateField(this);
            }
        });
    }

    if (confirmPasswordField) {
        confirmPasswordField.addEventListener('input', function() {
            if (passwordField && passwordField.value.trim() !== '') {
                validateField(this);
            } else {
                clearValidation(this);
            }
        });

        confirmPasswordField.addEventListener('blur', function() {
            if (passwordField && passwordField.value.trim() !== '') {
                validateField(this);
            }
        });
    }

    // Form submission validation
    if (form) {
        form.addEventListener('submit', function(e) {
            e.preventDefault();

            // Remove was-validated class first
            form.classList.remove('was-validated');

            // Validate all fields
            if (validateForm()) {
                // Show loading state
                const submitBtn = form.querySelector('button[type="submit"]');
                if (submitBtn) {
                    const originalText = submitBtn.textContent;
                    submitBtn.disabled = true;
                    submitBtn.textContent = 'Đang lưu...';
                    submitBtn.style.opacity = '0.6';
                    submitBtn.style.cursor = 'not-allowed';
                }

                // Submit form
                form.submit();
            } else {
                // Scroll to first error
                const firstError = form.querySelector('.is-invalid');
                if (firstError) {
                    firstError.scrollIntoView({ behavior: 'smooth', block: 'center' });
                    firstError.focus();
                }
            }
        });
    }

    // Cancel button functionality
    const cancelButton = document.querySelector('.btn-secondary');
    if (cancelButton) {
        cancelButton.addEventListener('click', function(e) {
            e.preventDefault();

            // Clear all validation states
            [fullNameField, emailField, phoneField, currentPasswordField, passwordField, confirmPasswordField].forEach(field => {
                if (field) {
                    clearValidation(field);
                }
            });

            // Reset password fields
            if (currentPasswordField) currentPasswordField.value = '';
            if (passwordField) passwordField.value = '';
            if (confirmPasswordField) confirmPasswordField.value = '';
        });
    }

    // Auto-hide alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        // Add close button
        const closeBtn = document.createElement('button');
        closeBtn.innerHTML = '×';
        closeBtn.className = 'alert-close';
        closeBtn.style.cssText = `
            position: absolute;
            right: 16px;
            top: 50%;
            transform: translateY(-50%);
            background: none;
            border: none;
            font-size: 24px;
            line-height: 1;
            color: inherit;
            cursor: pointer;
            opacity: 0.5;
            transition: opacity 0.2s ease;
            padding: 0;
            width: 28px;
            height: 28px;
            display: flex;
            align-items: center;
            justify-content: center;
        `;

        closeBtn.addEventListener('click', function() {
            alert.style.transition = 'opacity 0.3s ease-in-out';
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 300);
        });

        closeBtn.addEventListener('mouseenter', function() {
            this.style.opacity = '1';
        });

        closeBtn.addEventListener('mouseleave', function() {
            this.style.opacity = '0.5';
        });

        alert.style.position = 'relative';
        alert.appendChild(closeBtn);

        // Auto hide after 5 seconds
        setTimeout(() => {
            alert.style.transition = 'opacity 0.3s ease-in-out';
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 300);
        }, 5000);
    });

    // Tab functionality
    const tabButtons = document.querySelectorAll('.tab-button');
    tabButtons.forEach(button => {
        button.addEventListener('click', function() {
            // Remove active class from all tabs
            tabButtons.forEach(tab => tab.classList.remove('active'));
            // Add active class to clicked tab
            this.classList.add('active');
        });
    });

    // Add smooth transitions to all form inputs
    const formInputs = document.querySelectorAll('.form-input, .form-select');
    formInputs.forEach(input => {
        input.style.transition = 'border-color 0.2s ease, box-shadow 0.2s ease';
    });

    // Inject toast notification styles
    const toastStyles = document.createElement('style');
    toastStyles.textContent = `
        .toast-notification {
            position: fixed;
            top: 20px;
            right: 20px;
            min-width: 300px;
            max-width: 500px;
            background: white;
            border-radius: 12px;
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15), 0 4px 12px rgba(0, 0, 0, 0.1);
            padding: 16px 20px;
            display: flex;
            align-items: center;
            gap: 12px;
            z-index: 10000;
            opacity: 0;
            transform: translateX(400px);
            transition: all 0.3s cubic-bezier(0.68, -0.55, 0.265, 1.55);
        }

        .toast-notification.show {
            opacity: 1;
            transform: translateX(0);
        }

        .toast-notification.toast-error {
            border-left: 4px solid #ef4444;
        }

        .toast-notification.toast-success {
            border-left: 4px solid #10b981;
        }

        .toast-icon {
            font-size: 24px;
            flex-shrink: 0;
        }

        .toast-message {
            flex: 1;
            font-size: 14px;
            font-weight: 500;
            color: #1f2937;
            line-height: 1.5;
        }

        .toast-close {
            background: none;
            border: none;
            font-size: 24px;
            line-height: 1;
            color: #9ca3af;
            cursor: pointer;
            padding: 0;
            width: 24px;
            height: 24px;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 4px;
            transition: all 0.2s ease;
            flex-shrink: 0;
        }

        .toast-close:hover {
            background: #f3f4f6;
            color: #1f2937;
        }

        @media (max-width: 768px) {
            .toast-notification {
                top: 10px;
                right: 10px;
                left: 10px;
                min-width: auto;
                max-width: none;
            }
        }
    `;
    document.head.appendChild(toastStyles);
});

