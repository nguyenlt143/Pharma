package vn.edu.fpt.pharma.controller.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.repository.InvoiceRepository;
import vn.edu.fpt.pharma.dto.manager.InvoiceSummary;
import vn.edu.fpt.pharma.dto.manager.KpiData;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/owner/report")
@RequiredArgsConstructor
public class ReportApiController {

    private final InvoiceRepository invoiceRepository;

    /**
     * View detail revenue – Report
     * GET /api/owner/report/revenue?period=2024-01&branchId=1
     * Only supports monthly revenue by branch
     */
    @GetMapping("/revenue")
    public ResponseEntity<Map<String, Object>> getDetailRevenue(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) Long branchId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // Verify owner role
        if (userDetails == null || !"OWNER".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Only calculate monthly revenue
        LocalDate firstOfMonth = parseMonthStart(period);
        LocalDateTime from = firstOfMonth.atStartOfDay();
        LocalDateTime to = firstOfMonth.plusMonths(1).atStartOfDay();

        KpiData kpi = invoiceRepository.sumRevenue(branchId, from, to, null, null);
        List<InvoiceSummary> invoices = invoiceRepository.findInvoicesForReport(branchId, from, to, null, null);

        Map<String, Object> response = new HashMap<>();
        response.put("totalRevenue", kpi.getRevenue());
        response.put("totalProfit", kpi.getProfit());
        response.put("totalOrders", kpi.getOrderCount());
        response.put("invoices", invoices);

        return ResponseEntity.ok(response);
    }

    /**
     * View detail profit – Report
     * GET /api/owner/report/profit?period=2024-01&branchId=1
     * Only supports monthly profit by branch
     */
    @GetMapping("/profit")
    public ResponseEntity<Map<String, Object>> getDetailProfit(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) Long branchId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // Verify owner role
        if (userDetails == null || !"OWNER".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Only calculate monthly profit
        LocalDate firstOfMonth = parseMonthStart(period);
        LocalDateTime from = firstOfMonth.atStartOfDay();
        LocalDateTime to = firstOfMonth.plusMonths(1).atStartOfDay();

        KpiData kpi = invoiceRepository.sumRevenue(branchId, from, to, null, null);
        List<InvoiceSummary> invoices = invoiceRepository.findInvoicesForReport(branchId, from, to, null, null);

        Map<String, Object> response = new HashMap<>();
        response.put("totalProfit", kpi.getProfit());
        response.put("totalRevenue", kpi.getRevenue());
        response.put("totalOrders", kpi.getOrderCount());
        response.put("invoices", invoices);
        
        // Calculate profit details
        List<Map<String, Object>> profitDetails = new ArrayList<>();
        for (InvoiceSummary invoice : invoices) {
            Map<String, Object> detail = new HashMap<>();
            detail.put("invoiceId", invoice.getId());
            detail.put("invoiceCode", invoice.getCode());
            detail.put("date", invoice.getCreatedAt());
            detail.put("revenue", invoice.getTotalAmount());
            detail.put("profit", invoice.getProfit());
            profitDetails.add(detail);
        }
        response.put("profitDetails", profitDetails);

        return ResponseEntity.ok(response);
    }

    private LocalDate parseMonthStart(String ym) {
        try {
            if (ym == null || ym.isBlank()) return LocalDate.now().withDayOfMonth(1);
            // Support both yyyy-MM and yyyy-MM-dd formats
            if (ym.length() == 7) {
                // yyyy-MM format
                return LocalDate.parse(ym + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } else {
                // yyyy-MM-dd format, extract month
                LocalDate date = LocalDate.parse(ym, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                return date.withDayOfMonth(1);
            }
        } catch (Exception e) {
            return LocalDate.now().withDayOfMonth(1);
        }
    }
}

