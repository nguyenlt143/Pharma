package vn.edu.fpt.pharma.dto.manager;

import lombok.*;

import java.time.LocalDate;

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
    private Long remainingDays; // Số ngày làm việc còn lại
    private LocalDate lastWorkDate; // Ngày làm việc cuối cùng
}

