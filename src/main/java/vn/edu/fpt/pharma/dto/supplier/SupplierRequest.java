package vn.edu.fpt.pharma.dto.supplier;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierRequest {
    @NotBlank(message = "Tên nhà cung cấp không được để trống")
    private String supplierName;

    @Pattern(
            regexp = "^$|^(?:\\+?84|0)(?:3|5|7|8|9)\\d{8}$",
            message = "Số điện thoại không hợp lệ"
    )
    private String phone;
    private String address;
}