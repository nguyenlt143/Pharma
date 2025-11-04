
package vn.edu.fpt.pharma.dto.manager;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 30, message = "Username must be 4-30 characters")
    private String userName;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @Email(message = "Invalid email format")
    private String email;

    private Long roleId;

    // ❗ Không cho client tự gửi branchId, backend sẽ set
    private Long branchId;

    // Nếu create thì cần password, update có thể null
    private String password;
}

