package vn.edu.fpt.pharma.dto.warehouse;

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
    private List<ExportDetailItem> details;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExportDetailItem {
        private Long inventoryId;
        private Long batchId;
        private Long variantId;
        private Long quantity;
        private Double price;
    }
}

