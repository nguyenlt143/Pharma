package vn.edu.fpt.pharma.dto.manager;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShiftAssignmentRequest {
    @NotNull
    private Long userId;
}

