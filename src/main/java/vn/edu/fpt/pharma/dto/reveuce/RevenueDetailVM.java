package vn.edu.fpt.pharma.dto.reveuce;

public record RevenueDetailVM(
        String drugName,
        String unit,
        String batch,
        String manufacturer,
        String country,
        Long quantity,
        Double price,
        Double totalAmount
) {
}
