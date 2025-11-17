package vn.edu.fpt.pharma.dto.medicine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.pharma.entity.Medicine;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicineResponse {
    private Long id;
    private String medicineName;
    private Long categoryId;
    private String categoryName;
    private String activeIngredient;
    private String brandName;
    private String manufacturer;
    private String countryOfOrigin;
    private String registrationNumber;
    private String storageConditions;
    private String indications;
    private String contraindications;
    private String sideEffects;
    private String instructions;
    private Boolean prescriptionRequired;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MedicineResponse fromEntity(Medicine medicine) {
        if (medicine == null) return null;
        
        return MedicineResponse.builder()
                .id(medicine.getId())
                .medicineName(medicine.getName())
                .categoryId(medicine.getCategory() != null ? medicine.getCategory().getId() : null)
                .categoryName(medicine.getCategory() != null ? medicine.getCategory().getName() : null)
                .activeIngredient(medicine.getActiveIngredient())
                .brandName(medicine.getBrandName())
                .manufacturer(medicine.getManufacturer())
                .countryOfOrigin(medicine.getCountry())
                .createdAt(medicine.getCreatedAt())
                .updatedAt(medicine.getUpdatedAt())
                .build();
    }
}

