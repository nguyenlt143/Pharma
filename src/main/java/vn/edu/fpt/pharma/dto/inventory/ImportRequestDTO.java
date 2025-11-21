package vn.edu.fpt.pharma.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportRequestDTO {
    private String note;
    private List<ImportItemDTO> items;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ImportItemDTO {
        private Long variantId;
        private Long batchId;
        private Integer quantity;
    }
}

