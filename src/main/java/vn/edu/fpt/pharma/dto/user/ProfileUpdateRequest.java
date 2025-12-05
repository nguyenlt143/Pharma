package vn.edu.fpt.pharma.dto.user;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
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

    @Size(min = 6, max = 100, message = "Mật khẩu phải từ 6-100 ký tự")
    private String password;

    private String confirmPassword;

    @AssertTrue(message = "Mật khẩu xác nhận không khớp")
    public boolean isPasswordMatching() {
        if (password == null || password.isEmpty()) {
            return true; // Không đổi mật khẩu
        }
        return password.equals(confirmPassword);
    }
}
