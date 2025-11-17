package vn.edu.fpt.pharma.dto.manager;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShiftAssignmentResponse {
    private Long id;
    private Long userId;
    private String userFullName;
    private String roleName;
    private String status; // placeholder for workType/status mapping
    private String createdAt;
}

