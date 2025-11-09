package vn.edu.fpt.pharma.dto.reveuce;

public record RevenueShiftVM(
        String period,
        Long totalInvoice,
        Long totalCast,
        Long totalTransfer,
        Double totalRevenue
) {
}
