package vn.edu.fpt.pharma.dto.reveuce;

public record RevenueShiftVM(
        String shiftName,
        Long orderCount,
        Double cashTotal,
        Double transferTotal,
        Double totalRevenue
) {
}
