package vn.edu.fpt.pharma.dto.warehouse;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import vn.edu.fpt.pharma.validation.ValidDateRange;

@Data
@ValidDateRange
public class ReceiptDetailRequest {
    @NotNull(message = "Thuốc là bắt buộc")
    private Long variantId;

    @NotBlank(message = "Mã lô là bắt buộc")
    private String batchCode;

    @NotBlank(message = "Ngày sản xuất là bắt buộc")
    private String manufactureDate;

    @NotBlank(message = "Hạn sử dụng là bắt buộc")
    private String expiryDate;

    @NotNull(message = "Số lượng là bắt buộc")
    @Positive(message = "Số lượng phải lớn hơn 0")
    private Long quantity;

    @NotNull(message = "Giá là bắt buộc")
    @DecimalMin(value = "0", message = "Giá không được âm")
    private Double price;

    @DecimalMin(value = "0", message = "Chi phí không được âm")
    private Double snapCost;
}
