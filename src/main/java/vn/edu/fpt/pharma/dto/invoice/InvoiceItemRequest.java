package vn.edu.fpt.pharma.dto.invoice;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class InvoiceItemRequest {
    @NotNull(message = "ID kho không được để trống")
    @Positive(message = "ID kho phải là số dương")
    private Long inventoryId;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải ít nhất là 1")
    private Long quantity;

    @NotNull(message = "Đơn giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Đơn giá phải lớn hơn 0")
    private Double unitPrice;

    @NotNull(message = "Hệ số nhân không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Hệ số nhân phải lớn hơn 0")
    private Double selectedMultiplier;
}
