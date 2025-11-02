package vn.edu.fpt.pharma.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private String fullName;
    private String userName;
    private String password;
    private String email;
    private String phoneNumber;
    private Long branchId;
    private String imageUrl;
}

