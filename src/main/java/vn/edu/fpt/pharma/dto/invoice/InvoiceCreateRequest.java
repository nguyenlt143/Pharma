package vn.edu.fpt.pharma.dto.invoice;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class InvoiceCreateRequest {
    @Size(max = 100, message = "Tên khách hàng không được vượt quá 100 ký tự")
    private String customerName;

    @Pattern(regexp = "^$|^(0|\\+84)[0-9]{9,10}$|^Không có$",
             message = "Số điện thoại phải bắt đầu bằng 0 hoặc +84 và có 10-11 chữ số")
    private String phoneNumber;

    @NotNull(message = "Tổng tiền không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Tổng tiền phải lớn hơn 0")
    private Double totalAmount;

    @NotBlank(message = "Phương thức thanh toán không được để trống")
    @Pattern(regexp = "^(cash|transfer)$",
             message = "Phương thức thanh toán không hợp lệ (chỉ chấp nhận: cash, transfer)")
    private String paymentMethod;

    @Size(max = 500, message = "Ghi chú không được vượt quá 500 ký tự")
    private String note;

    @NotEmpty(message = "Danh sách sản phẩm không được để trống")
    @Valid
    private List<InvoiceItemRequest> items;
}
