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
    private Long packageUnitId;
    private Long baseUnitId;
    private Double quantityPerPackage;
    private String barcode;
    private String registrationNumber;
    private String storageConditions;
    private String indications;
    private String contraindications;
    private String sideEffects;
    private String instructions;
    private Boolean prescription_require;
    private String uses;
}

