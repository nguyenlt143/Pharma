package vn.edu.fpt.pharma.dto.medicine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.pharma.constant.DosageForm;
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
    private String dosageForm; // Display name for frontend
    private String dosageFormEnum; // Enum name for reference
    private String dosage;
    private String strength;
    private String packaging;
    private String barcode;
    private String registrationNumber;
    private String storageConditions;
    private String instructions;
    private Boolean prescription_require;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MedicineVariantResponse fromEntity(MedicineVariant variant) {
        if (variant == null) return null;
        
        String dosageFormDisplay = null;
        String dosageFormEnumName = null;
        if (variant.getDosageForm() != null) {
            dosageFormDisplay = variant.getDosageForm().getDisplayName();
            dosageFormEnumName = variant.getDosageForm().name();
        }

        return MedicineVariantResponse.builder()
                .id(variant.getId())
                .medicineId(variant.getMedicine() != null ? variant.getMedicine().getId() : null)
                .medicineName(variant.getMedicine() != null ? variant.getMedicine().getName() : null)
                .dosageForm(dosageFormDisplay)
                .dosageFormEnum(dosageFormEnumName)
                .dosage(variant.getDosage())
                .strength(variant.getStrength())
                .packaging(variant.getPackaging())
                .barcode(variant.getBarcode())
                .registrationNumber(variant.getRegistrationNumber())
                .storageConditions(variant.getStorageConditions())
                .instructions(variant.getInstructions())
                .prescription_require(variant.isPrescription_require())
                .note(variant.getNote())
                .createdAt(variant.getCreatedAt())
                .updatedAt(variant.getUpdatedAt())
                .build();
    }
}

