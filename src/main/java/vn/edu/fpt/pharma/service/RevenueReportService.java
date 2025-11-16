package vn.edu.fpt.pharma.service;

import java.util.Map;

public interface RevenueReportService {
    /**
     * Build revenue report data for manager dashboard.
     * @param branchId current branch id (nullable)
     * @param date single day in yyyy-MM-dd; optional
     * @param mode day|week|month; defaults to day
     * @param period week (yyyy-"W"ww) or month (yyyy-MM) string depending on mode; optional
     * @param shift optional shift id filter
     * @param employeeId optional employee id filter
     * @return a Map containing: totalInvoices, totalRevenue, totalProfit, invoices, productStats, totalProducts
     */
    Map<String, Object> getRevenueReport(Long branchId,
                                         String date,
                                         String mode,
                                         String period,
                                         Long shift,
                                         Long employeeId);
}

