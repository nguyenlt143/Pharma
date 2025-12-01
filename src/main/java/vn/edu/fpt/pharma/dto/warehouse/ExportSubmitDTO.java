package vn.edu.fpt.pharma.dto.warehouse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExportSubmitDTO {
    private Long requestId;
    private Long branchId;
    private LocalDate createdDate;
    private String note;
    @NotEmpty(message = "Chi tiết xuất kho không được để trống")
    @Valid
    private List<ExportDetailItem> details;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExportDetailItem {
        @NotNull(message = "Kho hàng không hợp lệ")
        private Long inventoryId;
        @NotNull(message = "Lô hàng không hợp lệ")
        private Long batchId;
        @NotNull(message = "Sản phẩm không hợp lệ")
        private Long variantId;
        @NotNull(message = "Số lượng là bắt buộc")
        @Positive(message = "Số lượng phải lớn hơn 0")
        private Long quantity;
        @NotNull(message = "Giá là bắt buộc")
        @DecimalMin(value = "0", message = "Giá không được âm")
        private Double price;
    }
}
