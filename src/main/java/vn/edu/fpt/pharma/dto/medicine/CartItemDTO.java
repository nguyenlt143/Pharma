package vn.edu.fpt.pharma.dto.medicine;

public record CartItemDTO(
        Long inventoryId,
        Long variantId,
        String name,
        String batchCode,
        Long quantity,
        Double price
) {
}
