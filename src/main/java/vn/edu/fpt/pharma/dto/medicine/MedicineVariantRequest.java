package vn.edu.fpt.pharma.dto.medicine;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicineVariantRequest {
    @NotNull(message = "Medicine ID is required")
    private Long medicineId;
    
    private String dosageForm;
    private String dosage;
    private String strength;
    private Long baseUnitId;
    private String barcode;
}

