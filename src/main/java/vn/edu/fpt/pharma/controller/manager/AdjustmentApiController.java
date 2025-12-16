package vn.edu.fpt.pharma.controller.manager;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.constant.MovementType;
import vn.edu.fpt.pharma.entity.InventoryMovement;
import vn.edu.fpt.pharma.entity.InventoryMovementDetail;
import vn.edu.fpt.pharma.repository.InventoryMovementRepository;
import vn.edu.fpt.pharma.repository.BranchRepository;
import vn.edu.fpt.pharma.service.ImportExportService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/manager/adjustments")
@RequiredArgsConstructor
public class AdjustmentApiController {

    private final BranchRepository branchRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final ImportExportService importExportService;

    // -------------------- Summary --------------------
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getAdjustmentSummary(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null || userDetails.getUser() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long branchId = userDetails.getUser().getBranchId();
        if (branchId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Map<String, Object> summary = new HashMap<>();

        // Count adjustments in last 30 days
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<InventoryMovement> adjustments = inventoryMovementRepository.findAll().stream()
                .filter(m -> m.getMovementType() == MovementType.INVENTORY_ADJUSTMENT
                          && m.getSourceBranchId() != null
                          && m.getSourceBranchId().equals(branchId)
                          && m.getCreatedAt() != null
                          && m.getCreatedAt().isAfter(thirtyDaysAgo))
                .toList();

        // Count expired returns in last 30 days
        List<InventoryMovement> expiredReturns = inventoryMovementRepository.findAll().stream()
                .filter(m -> m.getMovementType() == MovementType.BR_TO_WARE2
                          && m.getSourceBranchId() != null
                          && m.getSourceBranchId().equals(branchId)
                          && m.getCreatedAt() != null
                          && m.getCreatedAt().isAfter(thirtyDaysAgo))
                .toList();

        // Calculate total value
        double totalValue = 0.0;
        for (InventoryMovement m : adjustments) {
            if (m.getTotalMoney() != null) {
                totalValue += m.getTotalMoney();
            }
        }
        for (InventoryMovement m : expiredReturns) {
            if (m.getTotalMoney() != null) {
                totalValue += m.getTotalMoney();
            }
        }

        summary.put("adjustmentCount", adjustments.size());
        summary.put("expiredReturnCount", expiredReturns.size());
        summary.put("totalValue", totalValue);
        summary.put("totalValueFormatted", importExportService.formatCurrencyReadable(totalValue));
        return ResponseEntity.ok(summary);
    }

    // -------------------- Movements for chart --------------------
    @GetMapping("/movements")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getAdjustmentMovements(
            @RequestParam(required = false, defaultValue = "week") String range,
            @RequestParam(required = false, defaultValue = "all") String type,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null || userDetails.getUser() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long branchId = userDetails.getUser().getBranchId();
        if (branchId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fromDate;
        switch (range.toLowerCase()) {
            case "month" -> fromDate = now.minusMonths(1);
            case "quarter" -> fromDate = now.minusMonths(3);
            default -> fromDate = now.minusWeeks(1);
        }

        List<InventoryMovement> movements = inventoryMovementRepository.findMovementsSinceByBranch(fromDate, branchId);

        // Filter by adjustment types and optional type parameter
        movements = movements.stream()
                .filter(m -> {
                    boolean isAdjustment = m.getMovementType() == MovementType.INVENTORY_ADJUSTMENT;
                    boolean isExpiredReturn = m.getMovementType() == MovementType.BR_TO_WARE2;

                    if ("adjustment".equalsIgnoreCase(type)) {
                        return isAdjustment;
                    } else if ("expired_return".equalsIgnoreCase(type)) {
                        return isExpiredReturn;
                    } else {
                        return isAdjustment || isExpiredReturn;
                    }
                })
                .toList();

        Map<LocalDate, Map<String, Double>> dailyData = new LinkedHashMap<>();
        for (InventoryMovement m : movements) {
            LocalDate date = m.getCreatedAt() != null ? m.getCreatedAt().toLocalDate() : LocalDate.now();
            dailyData.putIfAbsent(date, new HashMap<>());
            Map<String, Double> dayData = dailyData.get(date);

            Double t = m.getTotalMoney() != null ? m.getTotalMoney() : 0.0;

            if (m.getMovementType() == MovementType.INVENTORY_ADJUSTMENT) {
                dayData.put("adjustments", dayData.getOrDefault("adjustments", 0.0) + t);
            } else if (m.getMovementType() == MovementType.BR_TO_WARE2) {
                dayData.put("expiredReturns", dayData.getOrDefault("expiredReturns", 0.0) + t);
            }
        }

        List<String> labels = new ArrayList<>();
        List<Double> adjustments = new ArrayList<>();
        List<Double> expiredReturns = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");
        for (Map.Entry<LocalDate, Map<String, Double>> entry : dailyData.entrySet()) {
            labels.add(entry.getKey().format(fmt));
            adjustments.add(entry.getValue().getOrDefault("adjustments", 0.0));
            expiredReturns.add(entry.getValue().getOrDefault("expiredReturns", 0.0));
        }

        Map<String, Object> data = new HashMap<>();
        data.put("labels", labels);
        data.put("adjustments", adjustments);
        data.put("expiredReturns", expiredReturns);

        return ResponseEntity.ok(data);
    }

    // -------------------- Recent activities --------------------
    @GetMapping("/activities")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Map<String, Object>>> getRecentActivities(
            @RequestParam(required = false, defaultValue = "10") int limit,
            @RequestParam(required = false, defaultValue = "all") String type,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null || userDetails.getUser() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long branchId = userDetails.getUser().getBranchId();
        if (branchId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Return recent adjustments and expired returns from this branch
        List<InventoryMovement> movements = inventoryMovementRepository.findAll().stream()
                .filter(mv -> mv.getSourceBranchId() != null && mv.getSourceBranchId().equals(branchId))
                .filter(mv -> {
                    boolean isAdjustment = mv.getMovementType() == MovementType.INVENTORY_ADJUSTMENT;
                    boolean isExpiredReturn = mv.getMovementType() == MovementType.BR_TO_WARE2;

                    if ("INVENTORY_ADJUSTMENT".equalsIgnoreCase(type)) {
                        return isAdjustment;
                    } else if ("BR_TO_WARE2".equalsIgnoreCase(type)) {
                        return isExpiredReturn;
                    } else {
                        return isAdjustment || isExpiredReturn;
                    }
                })
                .sorted((a, b) -> {
                    if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
                    if (a.getCreatedAt() == null) return 1;
                    if (b.getCreatedAt() == null) return -1;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                })
                .limit(limit)
                .toList();

        List<Map<String, Object>> activities = new ArrayList<>();
        for (InventoryMovement mv : movements) {
            Map<String, Object> activity = new HashMap<>();
            activity.put("id", mv.getId());
            activity.put("code", "#MV" + String.format("%03d", mv.getId()));
            activity.put("type", mv.getMovementType().name()); // Return type code for frontend mapping
            activity.put("creator", "-");
            activity.put("totalMoney", mv.getTotalMoney());
            activity.put("totalValueFormatted", importExportService.formatCurrencyReadable(mv.getTotalMoney()));
            activity.put("timeAgo", importExportService.formatTimeAgo(mv.getCreatedAt()));
            activity.put("detailUrl", "/api/manager/adjustments/detail/" + mv.getId());

            activities.add(activity);
        }

        return ResponseEntity.ok(activities);
    }

    // -------------------- Detail --------------------
    @GetMapping("/detail/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null || userDetails.getUser() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long branchId = userDetails.getUser().getBranchId();
        if (branchId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        InventoryMovement mv = inventoryMovementRepository.findById(id).orElse(null);
        if (mv == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Verify this movement belongs to user's branch
        if (mv.getSourceBranchId() == null || !mv.getSourceBranchId().equals(branchId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Verify it's an adjustment or expired return
        if (mv.getMovementType() != MovementType.INVENTORY_ADJUSTMENT
                && mv.getMovementType() != MovementType.BR_TO_WARE2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Map<String, Object> detail = new HashMap<>();
        detail.put("id", mv.getId());
        detail.put("code", "#MV" + String.format("%03d", mv.getId()));
        detail.put("type", mv.getMovementType().name()); // Return type code for frontend mapping

        String branchName = branchRepository.findById(branchId)
                .map(b -> b.getName())
                .orElse("-");
        detail.put("branchName", branchName);
        detail.put("totalMoney", mv.getTotalMoney());
        detail.put("totalValueFormatted", importExportService.formatCurrencyReadable(mv.getTotalMoney()));

        List<InventoryMovementDetail> details = mv.getInventoryMovementDetails();
        long totalQty = details != null ? details.stream()
                .filter(d -> d.getQuantity() != null)
                .mapToLong(InventoryMovementDetail::getQuantity)
                .sum() : 0L;
        detail.put("totalQty", totalQty);

        List<Map<String, Object>> items = new ArrayList<>();
        if (details != null) {
            for (InventoryMovementDetail d : details) {
                Map<String, Object> item = new HashMap<>();
                item.put("medicineName", d.getVariant() != null && d.getVariant().getMedicine() != null
                        ? d.getVariant().getMedicine().getName() : "-");
                item.put("variantName", d.getVariant() != null ? d.getVariant().getDosage() : "-");
                item.put("batchCode", d.getBatch() != null ? d.getBatch().getBatchCode() : "-");
                item.put("quantity", d.getQuantity());
                item.put("price", d.getSnapCost());
                item.put("subtotal", (d.getQuantity() != null && d.getSnapCost() != null)
                        ? d.getQuantity() * d.getSnapCost() : 0.0);
                items.add(item);
            }
        }
        detail.put("details", items);

        return ResponseEntity.ok(detail);
    }
}

