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

    @NotNull(message = "Danh mục thuốc là bắt buộc")
    private String activeIngredient;

    @NotNull(message = "Danh mục thuốc là bắt buộc")
    private String brandName;

    @NotNull(message = "Danh mục thuốc là bắt buộc")
    private String manufacturer;

    @NotNull(message = "Danh mục thuốc là bắt buộc")
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