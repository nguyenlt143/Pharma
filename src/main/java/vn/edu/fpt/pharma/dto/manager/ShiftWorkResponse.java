package vn.edu.fpt.pharma.dto.manager;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShiftWorkResponse {
    private Long id;
    private Long userId;
    private String userFullName;
    private String userName;
    private String phoneNumber;
    private String workType;
    private String createdAt; // ISO string
}

