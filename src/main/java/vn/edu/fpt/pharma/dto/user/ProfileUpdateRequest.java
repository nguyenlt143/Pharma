package vn.edu.fpt.pharma.dto.user;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {
    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên không được vượt quá 100 ký tự")
    private String fullName;

    @Pattern(regexp = "^(0|\\+84)[0-9]{9,10}$", message = "Số điện thoại không hợp lệ")
    private String phone;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Size(max = 100, message = "Email không được vượt quá 100 ký tự")
    private String email;

    // Current password (optional - only required when changing password)
    private String currentPassword;

    // New password (optional - only if changing password)
    // Note: No @Size validation here because we want to allow empty string when user doesn't change password
    // Frontend JS validates length when user actually enters a password
    private String password;

    private String confirmPassword;

    // Avatar data as base64 string
    private String avatarData;

    @AssertTrue(message = "Mật khẩu xác nhận không khớp")
    public boolean isPasswordMatching() {
        if (password == null || password.isEmpty()) {
            return true; // Không đổi mật khẩu
        }
        return password.equals(confirmPassword);
    }
}
