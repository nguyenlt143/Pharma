package vn.edu.fpt.pharma.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String fullName;
    private String userName;
    private String email;
    private String phoneNumber;
    private String roleName;
    private Long branchId;
}


