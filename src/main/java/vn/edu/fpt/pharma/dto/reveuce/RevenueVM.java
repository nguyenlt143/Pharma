package vn.edu.fpt.pharma.dto.reveuce;

public record RevenueVM(
        String period,
        Long totalInvoice,
        Long totalCustomer,
        Double totalRevenue
){
}
