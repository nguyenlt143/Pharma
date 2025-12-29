package vn.edu.fpt.pharma.dto.medicine;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.pharma.constant.DosageForm;

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
    @NotBlank(message = "Dạng bào chế không được để trống")
    private String dosageForm; // String for backward compatibility, will be converted to DosageForm enum

    @NotNull(message = "Liều dùng không được để trống")
    @NotBlank(message = "Liều dùng không được để trống")
    private String dosage;

    @NotNull(message = "Hàm lượng không được để trống")
    @NotBlank(message = "Hàm lượng không được để trống")
    private String strength;

    private String packaging;

    @NotNull(message = "Mã vạch không được để trống")
    @NotBlank(message = "Mã vạch không được để trống")
    private String barcode;

    @NotNull(message = "Số đăng ký không được để trống")
    @NotBlank(message = "Số đăng ký không được để trống")
    private String registrationNumber;

    @NotNull(message = "Điều kiện bảo quản không được để trống")
    @NotBlank(message = "Điều kiện bảo quản không được để trống")
    private String storageConditions;

    @NotNull(message = "Hướng dẫn sử dụng không được để trống")
    @NotBlank(message = "Hướng dẫn sử dụng không được để trống")
    private String instructions;

    @NotNull(message = "Cần kê đơn không được để trống")
    private Boolean prescription_require;

    private String note; // Moved from UnitConversion

    private List<UnitConversionDTO> unitConversions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UnitConversionDTO {
        private Long unitId;
        private Double multiplier;
        private Boolean isSale; // Indicates if this conversion is for sale purposes
    }
}

