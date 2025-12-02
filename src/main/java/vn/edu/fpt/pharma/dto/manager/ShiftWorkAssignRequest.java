package vn.edu.fpt.pharma.dto.manager;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShiftWorkAssignRequest {
    @NotNull(message = "ID nhân viên là bắt buộc")
    private Long userId;

    /**
     * date in "YYYY-MM-DD"
     */
    @NotNull(message = "Ngày làm việc là bắt buộc")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Ngày làm việc phải có định dạng YYYY-MM-DD")
    private String workDate;

    /**
     * WorkType as string: NOT_STARTED|IN_WORK|DONE
     */
    private String workType;
}

