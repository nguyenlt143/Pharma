package vn.edu.fpt.pharma.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnRequestDTO {
    private Long branchId;
    private String note;
    private List<ReturnItemDTO> items;
    // ID of created RequestForm (set after creation)
    private Long requestId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReturnItemDTO {
        private Long variantId;
        private Long batchId;
        private Long inventoryId;
        private Integer quantity;
    }
}
