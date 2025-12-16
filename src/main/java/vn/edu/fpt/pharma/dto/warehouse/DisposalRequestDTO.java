package vn.edu.fpt.pharma.dto.warehouse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisposalRequestDTO {
    private String note;
    private List<DisposalItemDTO> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DisposalItemDTO {
        private Long variantId;
        private Long batchId;
        private Long inventoryId;
        private Integer quantity;
    }
}

