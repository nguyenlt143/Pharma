//package vn.edu.fpt.pharma.controller.manager;
//
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import vn.edu.fpt.pharma.config.CustomUserDetails;
//
//import java.nio.charset.StandardCharsets;
//import java.time.LocalDateTime;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/manager/report/inventory")
//public class InventoryApiController {
//
//    private final InventoryReportService inventoryReportService;
//
//    public InventoryApiController(InventoryReportService inventoryReportService) {
//        this.inventoryReportService = inventoryReportService;
//    }
//
//    @GetMapping
//    public ResponseEntity<Map<String, Object>> summary(@AuthenticationPrincipal CustomUserDetails userDetails) {
//        Long branchId = userDetails != null && userDetails.getUser() != null ? userDetails.getUser().getBranchId() : null;
//        Map<String, Object> summary = inventoryReportService.getInventorySummary(branchId);
//        return ResponseEntity.ok(summary);
//    }
//
//    @GetMapping("/export")
//    public ResponseEntity<byte[]> exportCsv(@RequestParam(required = false) String warehouse,
//                                            @RequestParam(required = false) String range,
//                                            @RequestParam(required = false) String category,
//                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
//        Long branchId = userDetails != null && userDetails.getUser() != null ? userDetails.getUser().getBranchId() : null;
//        String csvData = inventoryReportService.generateInventoryCsv(branchId, warehouse, range, category);
//        byte[] bytes = csvData.getBytes(StandardCharsets.UTF_8);
//        String filename = "inventory-export-" + LocalDateTime.now().toString().replace(':', '-') + ".csv";
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
//                .contentType(MediaType.parseMediaType("text/csv; charset=utf-8"))
//                .body(bytes);
//    }
//}
