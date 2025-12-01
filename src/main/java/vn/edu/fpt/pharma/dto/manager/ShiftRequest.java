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

    @NotBlank(message = "Tên ca là bắt buộc")
    private String name;

    /**
     * Expect format "HH:mm" or "HH:mm:ss" (LocalTime ISO)
     */
    @NotBlank(message = "Giờ bắt đầu là bắt buộc")
    private String startTime;

    @NotBlank(message = "Giờ kết thúc là bắt buộc")
    private String endTime;

    private String note;
}
