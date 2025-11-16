package vn.edu.fpt.pharma.dto.reveuce;

public record RevenueDetailVM(
        String drugName,
        Long quantity,
        String unit,
        String activeIngredient,
        String manufacturer,
        String country,
        Double price,
        Double totalAmount
) {
}
