package vn.edu.fpt.pharma.controller.manager;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.constant.MovementType;
import vn.edu.fpt.pharma.constant.RequestStatus;
import vn.edu.fpt.pharma.constant.RequestType;
import vn.edu.fpt.pharma.entity.Category;
import vn.edu.fpt.pharma.entity.InventoryMovement;
import vn.edu.fpt.pharma.entity.Medicine;
import vn.edu.fpt.pharma.entity.MedicineVariant;
import vn.edu.fpt.pharma.repository.*;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/manager/import-export")
@RequiredArgsConstructor
public class ImportExportApiController {

    private final InventoryRepository inventoryRepository;
    private final RequestFormRepository requestFormRepository;
    private final BranchRepository branchRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final vn.edu.fpt.pharma.service.RequestFormService requestFormService;
    private final vn.edu.fpt.pharma.repository.MedicineVariantRepository medicineVariantRepository;

    /**
     * Get inventory summary for manager (only their branch)
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getInventorySummary(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null || userDetails.getUser() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long branchId = userDetails.getUser().getBranchId();
        if (branchId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Map<String, Object> summary = new HashMap<>();

        // Calculate total inventory value
        Double totalValue = calculateTotalInventoryValue(branchId);
        summary.put("totalValue", formatCurrency(totalValue));

        // Calculate value delta
        String valueDeltaLabel = "+0% so với tháng trước";
        summary.put("valueDeltaLabel", valueDeltaLabel);

        // Count low stock items
        int lowStockCount = countLowStock(branchId);
        summary.put("lowStockCount", lowStockCount);

        // Count pending inbound requests
        int pendingInbound = countPendingInbound(branchId);
        summary.put("pendingInbound", pendingInbound);

        // Count pending outbound requests
        int pendingOutbound = countPendingOutbound(branchId);
        summary.put("pendingOutbound", pendingOutbound);

        return ResponseEntity.ok(summary);
    }

    /**
     * Get inventory movement data for charts (manager's branch only)
     */
    @GetMapping("/movements")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getInventoryMovements(
            @RequestParam(required = false, defaultValue = "week") String range,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null || userDetails.getUser() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long branchId = userDetails.getUser().getBranchId();
        if (branchId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Calculate date range
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fromDate;
        switch (range.toLowerCase()) {
            case "month":
                fromDate = now.minusMonths(1);
                break;
            case "quarter":
                fromDate = now.minusMonths(3);
                break;
            default: // week
                fromDate = now.minusWeeks(1);
        }

        // Get movements for this branch
        List<InventoryMovement> movements = inventoryMovementRepository.findAll().stream()
            .filter(m -> m.getCreatedAt() != null && m.getCreatedAt().isAfter(fromDate))
            .filter(m ->
                (m.getDestinationBranchId() != null && m.getDestinationBranchId().equals(branchId)) ||
                (m.getSourceBranchId() != null && m.getSourceBranchId().equals(branchId)))
            .sorted(Comparator.comparing(InventoryMovement::getCreatedAt))
            .collect(Collectors.toList());

        // Group by date
        Map<LocalDate, Map<String, Double>> dailyData = new LinkedHashMap<>();
        for (InventoryMovement m : movements) {
            LocalDate date = m.getCreatedAt().toLocalDate();
            dailyData.putIfAbsent(date, new HashMap<>());
            Map<String, Double> dayData = dailyData.get(date);

            // Import: SUP_TO_WARE, WARE_TO_BR (incoming to branch)
            if ((m.getMovementType() == MovementType.WARE_TO_BR && branchId.equals(m.getDestinationBranchId())) ||
                (m.getMovementType() == MovementType.SUP_TO_WARE)) {
                dayData.put("imports", dayData.getOrDefault("imports", 0.0) +
                    (m.getTotalMoney() != null ? m.getTotalMoney() : 0.0));
            }
            // Export: BR_TO_WARE (outgoing from branch)
            if (m.getMovementType() == MovementType.BR_TO_WARE && branchId.equals(m.getSourceBranchId())) {
                dayData.put("exports", dayData.getOrDefault("exports", 0.0) +
                    (m.getTotalMoney() != null ? m.getTotalMoney() : 0.0));
            }
        }

        // Convert to lists
        List<String> labels = new ArrayList<>();
        List<Double> imports = new ArrayList<>();
        List<Double> exports = new ArrayList<>();

        for (Map.Entry<LocalDate, Map<String, Double>> entry : dailyData.entrySet()) {
            labels.add(entry.getKey().format(DateTimeFormatter.ofPattern("dd/MM")));
            imports.add(entry.getValue().getOrDefault("imports", 0.0));
            exports.add(entry.getValue().getOrDefault("exports", 0.0));
        }

        Map<String, Object> data = new HashMap<>();
        data.put("labels", labels);
        data.put("imports", imports);
        data.put("exports", exports);

        return ResponseEntity.ok(data);
    }

    /**
     * Get category distribution (manager's branch only)
     */
    @GetMapping("/categories")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Map<String, Object>>> getCategoryDistribution(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null || userDetails.getUser() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long branchId = userDetails.getUser().getBranchId();
        if (branchId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Get all categories
        List<Category> categories = categoryRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Category cat : categories) {
            double totalValue = inventoryRepository.findAll().stream()
                .filter(inv -> {
                    try {
                        if (inv.getBranch() == null || !inv.getBranch().getId().equals(branchId)) {
                            return false;
                        }
                        MedicineVariant variant = inv.getVariant();
                        if (variant == null) return false;
                        Medicine medicine = variant.getMedicine();
                        if (medicine == null || medicine.getCategory() == null) return false;
                        return medicine.getCategory().getId().equals(cat.getId());
                    } catch (Exception e) {
                        return false;
                    }
                })
                .mapToDouble(inv -> {
                    Long qty = inv.getQuantity() != null ? inv.getQuantity() : 0L;
                    Double cost = inv.getCostPrice() != null ? inv.getCostPrice() : 0.0;
                    return qty.doubleValue() * cost;
                })
                .sum();

            if (totalValue > 0) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", cat.getName());
                item.put("value", totalValue);
                result.add(item);
            }
        }

        // Sort by value descending
        result.sort((a, b) -> Double.compare(
            (Double) b.get("value"),
            (Double) a.get("value")
        ));

        return ResponseEntity.ok(result);
    }

    /**
     * Get recent activities (manager's branch only)
     */
    @GetMapping("/activities")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Map<String, Object>>> getRecentActivities(
            @RequestParam(required = false, defaultValue = "10") int limit,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null || userDetails.getUser() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long branchId = userDetails.getUser().getBranchId();
        if (branchId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Map<String, Object>> activities = new ArrayList<>();

        // Get recent request forms for this branch
        List<vn.edu.fpt.pharma.entity.RequestForm> requestForms = requestFormRepository.findAll().stream()
            .filter(rf -> rf.getBranchId() != null && rf.getBranchId().equals(branchId))
            .sorted(Comparator.comparing(vn.edu.fpt.pharma.entity.RequestForm::getCreatedAt).reversed())
            .limit(limit)
            .collect(Collectors.toList());

        for (vn.edu.fpt.pharma.entity.RequestForm rf : requestForms) {
            Map<String, Object> activity = new HashMap<>();
            activity.put("id", rf.getId());
            activity.put("code", "#RQ" + String.format("%03d", rf.getId()));

            String type = "";
            String typeClass = "";
            if (rf.getRequestType() == RequestType.IMPORT) {
                type = "Nhập kho";
                typeClass = "import";
            } else if (rf.getRequestType() == RequestType.RETURN) {
                type = "Xuất kho";
                typeClass = "export";
            }
            activity.put("type", type);
            activity.put("typeClass", typeClass);

            activity.put("creator", "-"); // TODO: Get creator from request form
            activity.put("totalQty", 0); // TODO: Calculate from request form details

            String statusLabel = "";
            String statusClass = "";
            if (rf.getRequestStatus() != null) {
                switch (rf.getRequestStatus()) {
                    case REQUESTED:
                        statusLabel = "Chờ xử lý";
                        statusClass = "pending";
                        break;
                    case CONFIRMED:
                        statusLabel = "Đã xác nhận";
                        statusClass = "confirmed";
                        break;
                    case RECEIVED:
                        statusLabel = "Đã nhận";
                        statusClass = "received";
                        break;
                    case CANCELLED:
                        statusLabel = "Đã hủy";
                        statusClass = "cancelled";
                        break;
                    default:
                        statusLabel = rf.getRequestStatus().name();
                        statusClass = "default";
                }
            }
            activity.put("statusLabel", statusLabel);
            activity.put("statusClass", statusClass);

            activity.put("timeAgo", formatTimeAgo(rf.getCreatedAt()));
            activities.add(activity);
        }

        return ResponseEntity.ok(activities);
    }

    /**
     * Get request form detail
     */
    @GetMapping("/request/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getRequestFormDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null || userDetails.getUser() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long branchId = userDetails.getUser().getBranchId();
        if (branchId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            vn.edu.fpt.pharma.dto.warehouse.RequestList request = requestFormService.getDetailById(id);

            // Verify this request belongs to manager's branch
            if (!branchId.equals(request.branchId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            List<vn.edu.fpt.pharma.dto.warehouse.RequestDetailVM> details = requestFormService.getDetailsOfRequest(id);

            Map<String, Object> response = new HashMap<>();
            response.put("id", request.id());
            response.put("code", "#RQ" + String.format("%03d", request.id()));
            response.put("type", request.requestType() != null ?
                (request.requestType().equals("IMPORT") ? "Nhập kho" : "Xuất kho") : "-");
            response.put("status", request.requestStatus() != null ? request.requestStatus() : "-");
            response.put("note", request.note() != null ? request.note() : "");
            response.put("createdAt", request.createdAt() != null ? request.createdAt() : "-");
            response.put("branchName", branchRepository.findById(branchId).map(b -> b.getName()).orElse("-"));

            // Calculate total quantity
            int totalQty = details.stream()
                .mapToInt(d -> d.quantity() != null ? d.quantity().intValue() : 0)
                .sum();
            response.put("totalQty", totalQty);

            // Format details with medicine and variant info
            List<Map<String, Object>> detailList = details.stream().map(d -> {
                Map<String, Object> detail = new HashMap<>();
                detail.put("quantity", d.quantity() != null ? d.quantity() : 0);

                // Get variant info
                if (d.variantId() != null) {
                    medicineVariantRepository.findById(d.variantId()).ifPresent(variant -> {
                        detail.put("variantName", variant.getDosage_form() != null ? variant.getDosage_form() : "-");
                        if (variant.getMedicine() != null) {
                            detail.put("medicineName", variant.getMedicine().getName() != null ?
                                variant.getMedicine().getName() : "-");
                        } else {
                            detail.put("medicineName", "-");
                        }
                        if (variant.getPackageUnitId() != null) {
                            detail.put("unit", variant.getPackageUnitId().getName() != null ?
                                variant.getPackageUnitId().getName() : "-");
                        } else {
                            detail.put("unit", "-");
                        }
                    });
                } else {
                    detail.put("medicineName", "-");
                    detail.put("variantName", "-");
                    detail.put("unit", "-");
                }

                return detail;
            }).collect(Collectors.toList());
            response.put("details", detailList);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Helper methods
    private Double calculateTotalInventoryValue(Long branchId) {
        try {
            double total = inventoryRepository.findAll().stream()
                .filter(inv -> inv.getBranch() != null && inv.getBranch().getId().equals(branchId))
                .mapToDouble(inv -> {
                    Long qty = inv.getQuantity() != null ? inv.getQuantity() : 0L;
                    Double cost = inv.getCostPrice() != null ? inv.getCostPrice() : 0.0;
                    return qty.doubleValue() * cost;
                })
                .sum();
            return total;
        } catch (Exception e) {
            System.err.println("Error calculating total inventory value: " + e.getMessage());
            return 0.0;
        }
    }

    private int countLowStock(Long branchId) {
        return (int) inventoryRepository.findAll().stream()
            .filter(inv -> inv.getBranch() != null &&
                          inv.getBranch().getId().equals(branchId) &&
                          inv.getQuantity() != null &&
                          inv.getMinStock() != null &&
                          inv.getQuantity() <= inv.getMinStock())
            .count();
    }

    private int countPendingInbound(Long branchId) {
        return (int) requestFormRepository.findAll().stream()
            .filter(rf -> rf.getBranchId() != null &&
                         rf.getBranchId().equals(branchId) &&
                         rf.getRequestType() == RequestType.IMPORT &&
                         (rf.getRequestStatus() == RequestStatus.REQUESTED ||
                          rf.getRequestStatus() == RequestStatus.RECEIVED))
            .count();
    }

    private int countPendingOutbound(Long branchId) {
        return (int) requestFormRepository.findAll().stream()
            .filter(rf -> rf.getBranchId() != null &&
                         rf.getBranchId().equals(branchId) &&
                         rf.getRequestType() == RequestType.RETURN &&
                         (rf.getRequestStatus() == RequestStatus.REQUESTED ||
                          rf.getRequestStatus() == RequestStatus.RECEIVED))
            .count();
    }

    private String formatCurrency(Double value) {
        if (value == null || value == 0) return "0 đ";
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        if (value >= 1_000_000_000) {
            return formatter.format(value / 1_000_000_000) + " Tỷ";
        } else if (value >= 1_000_000) {
            return formatter.format(value / 1_000_000) + " Triệu";
        } else if (value >= 1_000) {
            return formatter.format(value / 1_000) + " K";
        }
        return formatter.format(value) + " đ";
    }

    private String formatTimeAgo(LocalDateTime dateTime) {
        if (dateTime == null) return "-";
        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(dateTime, now).toMinutes();

        if (minutes < 1) return "Vừa xong";
        if (minutes < 60) return minutes + " phút trước";

        long hours = minutes / 60;
        if (hours < 24) return hours + " giờ trước";

        long days = hours / 24;
        if (days < 30) return days + " ngày trước";

        long months = days / 30;
        if (months < 12) return months + " tháng trước";

        long years = months / 12;
        return years + " năm trước";
    }
}

