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
    private Long packageUnitId;
    private String packageUnitName;
    private Long baseUnitId;
    private String baseUnitName;
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
                .packageUnitId(variant.getPackageUnitId() != null ? variant.getPackageUnitId().getId() : null)
                .packageUnitName(variant.getPackageUnitId() != null ? variant.getPackageUnitId().getName() : null)
                .baseUnitId(variant.getBaseUnitId() != null ? variant.getBaseUnitId().getId() : null)
                .baseUnitName(variant.getBaseUnitId() != null ? variant.getBaseUnitId().getName() : null)
                .quantityPerPackage(variant.getQuantityPerPackage())
                .barcode(variant.getBarcode())
                .registrationNumber(variant.getRegistrationNumber())
                .storageConditions(variant.getStorageConditions())
                .indications(variant.getIndications())
                .contraindications(variant.getContraindications())
                .sideEffects(variant.getSideEffects())
                .instructions(variant.getInstructions())
                .prescription_require(variant.isPrescription_require())
                .uses(variant.getUses())
                .createdAt(variant.getCreatedAt())
                .updatedAt(variant.getUpdatedAt())
                .build();
    }
}

