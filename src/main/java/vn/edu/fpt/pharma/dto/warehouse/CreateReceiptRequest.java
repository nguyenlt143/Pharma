package vn.edu.fpt.pharma.dto.warehouse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class CreateReceiptRequest {
    @NotBlank(message = "Loại phiếu là bắt buộc")
    private String movementType;

    @NotNull(message = "Nhà cung cấp là bắt buộc")
    private Long supplierId;

    @NotBlank(message = "Ngày chứng từ là bắt buộc")
    private String movementDate;

    private String status;

    @NotEmpty(message = "Chi tiết phiếu không được để trống")
    @Valid
    private List<ReceiptDetailRequest> details;
}
