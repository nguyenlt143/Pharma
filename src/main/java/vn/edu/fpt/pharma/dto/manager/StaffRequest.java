package vn.edu.fpt.pharma.dto.manager;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffRequest {
    public String userName;
    public String password;
    public String fullName;
    public Long roleId;
    public Long branchId;
    public String phoneNumber;
    public String email;
    public String imageUrl;
}
