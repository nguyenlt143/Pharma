package vn.edu.fpt.pharma.dto.manager;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffDto {
    public Long id;
    public String userName;
    public String fullName;
    public Long roleId;
    public String roleName;
    public Long branchId;
    public String phoneNumber;
    public String email;
    public String imageUrl;
    public String accountStatus;
}
