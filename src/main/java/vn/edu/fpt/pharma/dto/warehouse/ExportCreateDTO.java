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
public class ExportCreateDTO {
    private Long requestId;
    private Long branchId;
    private String branchName;
    private LocalDate createdDate;
    private String note;
    private List<MedicineWithBatches> medicines;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MedicineWithBatches {
        private Long variantId;
        private String medicineName;
        private String unit;
        private String concentration;
        private Long requestedQuantity;
        private List<BatchInfo> batches;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BatchInfo {
        private Long inventoryId;
        private Long batchId;
        private String batchCode;
        private Long availableQuantity;
        private Double branchPrice;
        private Long quantityToSend;
    }
}

