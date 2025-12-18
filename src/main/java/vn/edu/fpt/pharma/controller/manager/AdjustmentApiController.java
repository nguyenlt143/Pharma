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
    @Transactional(readOnly = true)
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

        // Count adjustments in last 30 days using optimized query
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<MovementType> adjustmentTypes = List.of(MovementType.INVENTORY_ADJUSTMENT, MovementType.BR_TO_WARE2);
        List<InventoryMovement> movements = inventoryMovementRepository
                .findMovementsWithDetailsSinceByBranchAndTypes(thirtyDaysAgo, branchId, adjustmentTypes);

        // Separate by type
        List<InventoryMovement> adjustments = movements.stream()
                .filter(m -> m.getMovementType() == MovementType.INVENTORY_ADJUSTMENT)
                .toList();

        List<InventoryMovement> expiredReturns = movements.stream()
                .filter(m -> m.getMovementType() == MovementType.BR_TO_WARE2)
                .toList();

        // Calculate total value from snapCost * quantity
        double totalValue = 0.0;
        for (InventoryMovement m : movements) {
            totalValue += calculateTotalMoney(m);
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

        // Use optimized query with eager loading
        List<MovementType> adjustmentTypes = List.of(MovementType.INVENTORY_ADJUSTMENT, MovementType.BR_TO_WARE2);
        List<InventoryMovement> allMovements = inventoryMovementRepository
                .findMovementsWithDetailsSinceByBranchAndTypes(fromDate, branchId, adjustmentTypes);

        // Filter by optional type parameter
        List<InventoryMovement> movements = allMovements.stream()
                .filter(m -> {
                    boolean isAdjustment = m.getMovementType() == MovementType.INVENTORY_ADJUSTMENT;
                    boolean isExpiredReturn = m.getMovementType() == MovementType.BR_TO_WARE2;

                    if ("adjustment".equalsIgnoreCase(type)) {
                        return isAdjustment;
                    } else if ("expired_return".equalsIgnoreCase(type)) {
                        return isExpiredReturn;
                    } else {
                        return true; // Already filtered by query
                    }
                })
                .toList();

        Map<LocalDate, Map<String, Double>> dailyData = new LinkedHashMap<>();
        for (InventoryMovement m : movements) {
            LocalDate date = m.getCreatedAt() != null ? m.getCreatedAt().toLocalDate() : LocalDate.now();
            dailyData.putIfAbsent(date, new HashMap<>());
            Map<String, Double> dayData = dailyData.get(date);

            // Calculate total from snapCost * quantity
            Double totalMoney = calculateTotalMoney(m);

            if (m.getMovementType() == MovementType.INVENTORY_ADJUSTMENT) {
                dayData.put("adjustments", dayData.getOrDefault("adjustments", 0.0) + totalMoney);
            } else if (m.getMovementType() == MovementType.BR_TO_WARE2) {
                dayData.put("expiredReturns", dayData.getOrDefault("expiredReturns", 0.0) + totalMoney);
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
        // Use a reasonable time range for performance (e.g., last 3 months)
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        List<MovementType> adjustmentTypes = List.of(MovementType.INVENTORY_ADJUSTMENT, MovementType.BR_TO_WARE2);

        List<InventoryMovement> movements = inventoryMovementRepository
                .findMovementsWithDetailsSinceByBranchAndTypes(threeMonthsAgo, branchId, adjustmentTypes)
                .stream()
                .filter(mv -> {
                    boolean isAdjustment = mv.getMovementType() == MovementType.INVENTORY_ADJUSTMENT;
                    boolean isExpiredReturn = mv.getMovementType() == MovementType.BR_TO_WARE2;

                    if ("INVENTORY_ADJUSTMENT".equalsIgnoreCase(type)) {
                        return isAdjustment;
                    } else if ("BR_TO_WARE2".equalsIgnoreCase(type)) {
                        return isExpiredReturn;
                    } else {
                        return true; // Already filtered by query
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
            // Calculate total from snapCost * quantity
            double totalMoney = calculateTotalMoney(mv);

            Map<String, Object> activity = new HashMap<>();
            activity.put("id", mv.getId());
            activity.put("code", "#MV" + String.format("%03d", mv.getId()));
            activity.put("type", mv.getMovementType().name()); // Return type code for frontend mapping
            activity.put("creator", "-");
            activity.put("totalMoney", totalMoney);
            activity.put("totalValueFormatted", importExportService.formatCurrencyReadable(totalMoney));
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

        // Calculate total from snapCost * quantity
        double totalMoney = calculateTotalMoney(mv);
        detail.put("totalMoney", totalMoney);
        detail.put("totalValueFormatted", importExportService.formatCurrencyReadable(totalMoney));

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

    // -------------------- Helper Methods --------------------

    /**
     * Calculate total money from inventory movement details using snapCost * quantity
     * @param movement InventoryMovement to calculate total for
     * @return Total money calculated from details
     */
    private double calculateTotalMoney(InventoryMovement movement) {
        if (movement == null || movement.getInventoryMovementDetails() == null) {
            return 0.0;
        }

        return movement.getInventoryMovementDetails().stream()
                .filter(d -> d.getQuantity() != null && d.getSnapCost() != null)
                .mapToDouble(d -> d.getQuantity() * d.getSnapCost())
                .sum();
    }
}

