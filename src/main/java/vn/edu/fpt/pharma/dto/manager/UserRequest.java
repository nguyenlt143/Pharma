package vn.edu.fpt.pharma.dto.manager;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {

    @NotBlank(message = "Tên đăng nhập là bắt buộc")
    @Size(min = 4, max = 30, message = "Tên đăng nhập phải từ 4-30 ký tự")
    private String userName;

    @NotBlank(message = "Họ và tên là bắt buộc")
    private String fullName;

    @Pattern(
            regexp = "^(?:\\+?84|0)(?:3|5|7|8|9)\\d{8}$",
            message = "Số điện thoại không hợp lệ"
    )
    private String phoneNumber;

    @Email(message = "Email không hợp lệ")
    private String email;

    @NotNull(message = "Vai trò là bắt buộc")
    private Long roleId;

    private Long branchId;

    // Nếu tạo mới cần password, cập nhật có thể để trống
    @Size(min = 6, max = 100, message = "Mật khẩu phải từ 6 ký tự trở lên")
    private String password;
}
