package vn.edu.fpt.pharma.dto.manager;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShiftWorkAssignRequest {
    @NotNull
    private Long userId;

    /**
     * date in "YYYY-MM-DD"
     */
    @NotNull
    private String workDate;

    /**
     * WorkType as string: NOT_STARTED|IN_WORK|DONE
     */
    private String workType;
}

