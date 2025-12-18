package vn.edu.fpt.pharma.dto.medicine;

import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "Dạng bào chế không được để trống")
    private String dosageForm;

    @NotNull(message = "Liều dùng không được để trống")
    @NotBlank(message = "Liều dùng không được để trống")
    private String dosage;

    @NotNull(message = "Hàm lượng không được để trống")
    @NotBlank(message = "Hàm lượng không được để trống")
    private String strength;

    @NotNull(message = "Đơn vị đóng gói không được để trống")
    private Long packageUnitId;

    @NotNull(message = "Đơn vị cơ bản không được để trống")
    private Long baseUnitId;

    @NotNull(message = "Số lượng trong mỗi đơn vị đóng gói là bắt buộc")
    @Positive(message = "Số lượng trong mỗi đơn vị đóng gói phải lớn hơn 0")
    private Double quantityPerPackage;

    @NotNull(message = "Mã vạch không được để trống")
    @NotBlank(message = "Mã vạch không được để trống")
    private String barcode;

    @NotNull(message = "Số đăng ký không được để trống")
    @NotBlank(message = "Số đăng ký không được để trống")
    private String registrationNumber;

    @NotNull(message = "Điều kiện bảo quản không được để trống")
    @NotBlank(message = "Điều kiện bảo quản không được để trống")
    private String storageConditions;

    @NotNull(message = "Chỉ định không được để trống")
    @NotBlank(message = "Chỉ định không được để trống")
    private String indications;

    @NotNull(message = "Chống chỉ định không được để trống")
    @NotBlank(message = "Chống chỉ định không được để trống")
    private String contraindications;

    @NotNull(message = "Tác dụng phụ không được để trống")
    @NotBlank(message = "Tác dụng phụ không được để trống")
    private String sideEffects;

    @NotNull(message = "Hướng dẫn sử dụng không được để trống")
    @NotBlank(message = "Hướng dẫn sử dụng không được để trống")
    private String instructions;

    @NotNull(message = "Cần kê đơn không được để trống")
    private Boolean prescription_require;

    @NotNull(message = "Công dụng không được để trống")
    @NotBlank(message = "Công dụng không được để trống")
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

