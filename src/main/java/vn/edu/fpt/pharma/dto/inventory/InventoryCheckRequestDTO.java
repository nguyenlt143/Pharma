package vn.edu.fpt.pharma.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryCheckRequestDTO {
    private String note;
    private List<Item> items;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item {
        private Long inventoryId;
        private Long variantId;
        private Long batchId;
        private Long countedQuantity; // Số lượng thực tế kiểm
    }
}

