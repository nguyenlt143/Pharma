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
     * GET /api/owner/report/revenue?fromDate=2024-01-01&toDate=2024-01-31&shift=1&employeeId=1
     */
    @GetMapping("/revenue")
    public ResponseEntity<Map<String, Object>> getDetailRevenue(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) Long shift,
            @RequestParam(required = false) Long employeeId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // Verify owner role
        if (userDetails == null || !"OWNER".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        // Owner can view all branches, so branchId is null
        Long branchId = null;

        LocalDateTime from = parseDateOrToday(fromDate).atStartOfDay();
        LocalDateTime to = parseDateOrToday(toDate != null ? toDate : fromDate).plusDays(1).atStartOfDay();

        KpiData kpi = invoiceRepository.sumRevenue(branchId, from, to, shift, employeeId);
        List<InvoiceSummary> invoices = invoiceRepository.findInvoicesForReport(branchId, from, to, shift, employeeId);

        Map<String, Object> response = new HashMap<>();
        response.put("totalRevenue", kpi.getRevenue());
        response.put("totalProfit", kpi.getProfit());
        response.put("totalOrders", kpi.getOrderCount());
        response.put("invoices", invoices);

        return ResponseEntity.ok(response);
    }

    /**
     * View detail profit – Report
     * GET /api/owner/report/profit?fromDate=2024-01-01&toDate=2024-01-31&shift=1&employeeId=1
     */
    @GetMapping("/profit")
    public ResponseEntity<Map<String, Object>> getDetailProfit(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) Long shift,
            @RequestParam(required = false) Long employeeId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // Verify owner role
        if (userDetails == null || !"OWNER".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        // Owner can view all branches, so branchId is null
        Long branchId = null;

        LocalDateTime from = parseDateOrToday(fromDate).atStartOfDay();
        LocalDateTime to = parseDateOrToday(toDate != null ? toDate : fromDate).plusDays(1).atStartOfDay();

        KpiData kpi = invoiceRepository.sumRevenue(branchId, from, to, shift, employeeId);
        List<InvoiceSummary> invoices = invoiceRepository.findInvoicesForReport(branchId, from, to, shift, employeeId);

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

    private LocalDate parseDateOrToday(String dateStr) {
        try {
            if (dateStr == null || dateStr.isBlank()) return LocalDate.now();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(dateStr, fmt);
        } catch (Exception e) {
            return LocalDate.now();
        }
    }
}

