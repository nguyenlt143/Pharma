package vn.edu.fpt.pharma.dto.medicine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.pharma.entity.MedicineVariant;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicineVariantResponse {
    private Long id;
    private Long medicineId;
    private String medicineName;
    private String dosageForm;
    private String dosage;
    private String strength;
    private String barcode;
    private String registrationNumber;
    private String storageConditions;
    private String instructions;
    private Boolean prescription_require;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MedicineVariantResponse fromEntity(MedicineVariant variant) {
        if (variant == null) return null;
        
        return MedicineVariantResponse.builder()
                .id(variant.getId())
                .medicineId(variant.getMedicine() != null ? variant.getMedicine().getId() : null)
                .medicineName(variant.getMedicine() != null ? variant.getMedicine().getName() : null)
                .dosageForm(variant.getDosage_form())
                .dosage(variant.getDosage())
                .strength(variant.getStrength())
                .barcode(variant.getBarcode())
                .registrationNumber(variant.getRegistrationNumber())
                .storageConditions(variant.getStorageConditions())
                .instructions(variant.getInstructions())
                .prescription_require(variant.isPrescription_require())
                .createdAt(variant.getCreatedAt())
                .updatedAt(variant.getUpdatedAt())
                .build();
    }
}

