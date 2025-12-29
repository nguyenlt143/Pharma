package vn.edu.fpt.pharma.dto.dosageform;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DosageFormRequest {

    @NotBlank(message = "Tên dạng bào chế không được để trống")
    private String displayName;

    @NotNull(message = "Đơn vị cơ bản không được để trống")
    private Long baseUnitId;

    private String description;

    private Boolean active;

    private Integer displayOrder;
}

