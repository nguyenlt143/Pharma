package vn.edu.fpt.pharma.dto.manager;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShiftRequest {
    private Long id;

    @NotBlank
    private String name;

    /**
     * Expect format "HH:mm" or "HH:mm:ss" (LocalTime ISO)
     */
    @NotBlank
    private String startTime;

    @NotBlank
    private String endTime;

    private String note;
}
