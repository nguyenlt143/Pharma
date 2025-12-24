package vn.edu.fpt.pharma.dto.medicine;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @Size(max = 255, message = "Tên thuốc không được vượt quá 255 ký tự")
    private String medicineName;

    @NotNull(message = "Danh mục thuốc là bắt buộc")
    @Min(value = 1, message = "ID danh mục phải lớn hơn 0")
    private Long categoryId;

    @NotBlank(message = "Thành phần hoạt chất là bắt buộc")
    @Size(max = 500, message = "Thành phần hoạt chất không được vượt quá 500 ký tự")
    private String activeIngredient;

    @NotBlank(message = "Tên thương hiệu là bắt buộc")
    @Size(max = 255, message = "Tên thương hiệu không được vượt quá 255 ký tự")
    private String brandName;

    @NotBlank(message = "Nhà sản xuất là bắt buộc")
    @Size(max = 255, message = "Nhà sản xuất không được vượt quá 255 ký tự")
    private String manufacturer;

    @NotBlank(message = "Quốc gia sản xuất là bắt buộc")
    @Size(max = 100, message = "Quốc gia sản xuất không được vượt quá 100 ký tự")
    private String countryOfOrigin;

    @Size(max = 1000, message = "Chỉ định không được vượt quá 1000 ký tự")
    private String indications;

    @Size(max = 1000, message = "Chống chỉ định không được vượt quá 1000 ký tự")
    private String contraindications;

    @Size(max = 1000, message = "Tác dụng phụ không được vượt quá 1000 ký tự")
    private String sideEffects;

    @Size(max = 1000, message = "Công dụng không được vượt quá 1000 ký tự")
    private String uses;

    @Size(max = 100, message = "Số đăng ký không được vượt quá 100 ký tự")
    private String registrationNumber;

    @Size(max = 500, message = "Điều kiện bảo quản không được vượt quá 500 ký tự")
    private String storageConditions;

    @Size(max = 1000, message = "Hướng dẫn sử dụng không được vượt quá 1000 ký tự")
    private String instructions;

    @NotNull(message = "Trạng thái kê đơn là bắt buộc")
    @Builder.Default
    private Boolean prescriptionRequired = false;

    @NotNull(message = "Trạng thái là bắt buộc")
    @Min(value = 0, message = "Trạng thái phải lớn hơn hoặc bằng 0")
    @Builder.Default
    private Integer status = 1;
}