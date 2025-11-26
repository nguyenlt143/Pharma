package vn.edu.fpt.pharma.dto.medicine;

import java.time.LocalDate;

public record InventoryDetailDTO(
        Long id,
        String batchNumber,
        LocalDate expiryDate,
        Long quantity,
        Double costPrice,
        String supplierName,
        Double salePrice
) {
}

