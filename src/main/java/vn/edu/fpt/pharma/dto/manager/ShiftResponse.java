package vn.edu.fpt.pharma.dto.manager;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShiftResponse {
    private Long id;
    private String name;
    /**
     * Return LocalTime ISO (e.g. "08:30:00")
     */
    private String startTime;
    private String endTime;
    private String note;
    private boolean deleted;
}

