package vn.edu.fpt.pharma.dto.medicine;

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
public class MedicineRequest {
    @NotBlank(message = "Tên thuốc không được để trống")
    private String medicineName;
    
    @NotNull(message = "Danh mục thuốc là bắt buộc")
    private Long categoryId;
    
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
    private Boolean prescriptionRequired = false;
    private Integer status = 1;
}

