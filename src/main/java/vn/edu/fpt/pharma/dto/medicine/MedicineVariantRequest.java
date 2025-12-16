package vn.edu.fpt.pharma.dto.medicine;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicineVariantRequest {
    @NotNull(message = "Medicine ID is required")
    private Long medicineId;
    
    @NotNull(message = "Dạng bào chế không được để trống")
    private String dosageForm;
    @NotNull(message = "Hàm lượng không được để trống")
    private String dosage;
    @NotNull(message = "Nồng độ/strength không được để trống")
    private String strength;
    @NotNull(message = "Đơn vị đóng gói không được để trống")
    private Long packageUnitId;
    @NotNull(message = "Đơn vị cơ bản không được để trống")
    private Long baseUnitId;
    @NotNull(message = "Số lượng trong mỗi đơn vị đóng gói là bắt buộc")
    @Positive(message = "Số lượng trong mỗi đơn vị đóng gói phải lớn hơn 0")
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
    private List<UnitConversionDTO> unitConversions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UnitConversionDTO {
        private Long unitId;
        private Double multiplier;
    }
}

