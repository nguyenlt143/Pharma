package vn.edu.fpt.pharma.dto.invoice;

public record InvoiceDetailDTO(
        Long inventoryId,
        Long variantId,
        String name,
        String batch,
        Long quantity,
        Double price
) {
}
