package vn.edu.fpt.pharma.dto.unit;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnitRequest {
    @NotBlank(message = "Unit name is required")
    private String name;

    private String description;

    @Builder.Default
    private Boolean isBase = false;

    private String listUnitAvailable; // Comma-separated unit IDs
}
