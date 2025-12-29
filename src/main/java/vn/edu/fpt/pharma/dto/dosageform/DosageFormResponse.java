package vn.edu.fpt.pharma.dto.dosageform;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.pharma.entity.DosageForm;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DosageFormResponse {

    private Long id;
    private String displayName;
    private Long baseUnitId;
    private String baseUnitName;
    private String description;
    private Boolean active;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static DosageFormResponse fromEntity(DosageForm entity) {
        return DosageFormResponse.builder()
                .id(entity.getId())
                .displayName(entity.getDisplayName())
                .baseUnitId(entity.getBaseUnit() != null ? entity.getBaseUnit().getId() : null)
                .baseUnitName(entity.getBaseUnit() != null ? entity.getBaseUnit().getName() : null)
                .description(entity.getDescription())
                .active(entity.getActive())
                .displayOrder(entity.getDisplayOrder())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

