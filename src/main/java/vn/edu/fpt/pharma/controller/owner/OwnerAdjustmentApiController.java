package vn.edu.fpt.pharma.controller.owner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequestMapping("/api/owner/adjustments")
@RequiredArgsConstructor
public class OwnerAdjustmentApiController {

    private final BranchRepository branchRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final ImportExportService importExportService;

    // -------------------- Summary --------------------
    @GetMapping("/summary")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getAdjustmentSummary(
            @RequestParam(required = false) Long branchId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        log.info("Fetching adjustment summary for branchId: {}, user: {}",
                 branchId, userDetails != null ? userDetails.getUsername() : "null");

        if (userDetails == null) {
            log.warn("Unauthorized access - userDetails is null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String role = userDetails.getRole();
        if (role == null || (!role.equalsIgnoreCase("BUSINESS_OWNER") && !role.equalsIgnoreCase("OWNER"))) {
            log.warn("Access denied - user role: {}", role);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            Map<String, Object> summary = new HashMap<>();

            // Count adjustments in last 30 days
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            List<MovementType> adjustmentTypes = List.of(MovementType.INVENTORY_ADJUSTMENT, MovementType.BR_TO_WARE2);

            log.debug("Querying movements since: {}, types: {}", thirtyDaysAgo, adjustmentTypes);

            List<InventoryMovement> movements;
        if (branchId != null) {
            movements = inventoryMovementRepository
                    .findMovementsWithDetailsSinceByBranchAndTypes(thirtyDaysAgo, branchId, adjustmentTypes);
        } else {
            // If no branch selected, we might want all branches.
            // Ideally we need a repository method for "all branches within types since
            // date"
            // For now, let's filter in memory if repository doesn't support it, or use
            // existing findAll if feasible
            // However, findMovementsWithDetailsSinceByBranchAndTypes is specific.
            // Let's assume we can fetch all and filter, or (better) just iterate all
            // branches if the dataset isn't huge.
            // Given the repo structure, let's fallback to "all movements" filtering.
            movements = inventoryMovementRepository.findAll().stream()
                    .filter(portions -> portions.getCreatedAt() != null
                            && portions.getCreatedAt().isAfter(thirtyDaysAgo))
                    .filter(portions -> adjustmentTypes.contains(portions.getMovementType()))
                    .toList();
        }

        // Calculate values by type according to business rules
        double totalExpiredValue = 0.0; // BR_TO_WARE2 (Expired returns)
        double totalShortageValue = 0.0; // INVENTORY_ADJUSTMENT with negative quantity
        double totalSurplusValue = 0.0; // INVENTORY_ADJUSTMENT with positive quantity

        for (InventoryMovement m : movements) {
            double totalMoney = Math.abs(calculateTotalMoney(m)); // Always positive

            if (m.getMovementType() == MovementType.BR_TO_WARE2) {
                // Expired returns - always loss
                totalExpiredValue += totalMoney;
            } else if (m.getMovementType() == MovementType.INVENTORY_ADJUSTMENT) {
                // Check total quantity to determine surplus/shortage
                long totalQuantity = m.getInventoryMovementDetails() != null
                        ? m.getInventoryMovementDetails().stream()
                                .mapToLong(d -> d.getQuantity() != null ? d.getQuantity() : 0L)
                                .sum()
                        : 0L;

                if (totalQuantity < 0) {
                    // Negative quantity = Shortage (loss)
                    totalShortageValue += totalMoney;
                } else if (totalQuantity > 0) {
                    // Positive quantity = Surplus (gain/offset)
                    totalSurplusValue += totalMoney;
                }
            }
        }

            // Calculate total and net values
            double totalLoss = totalExpiredValue + totalShortageValue;
            double netLoss = totalLoss - totalSurplusValue;

            summary.put("totalExpiredValue", totalExpiredValue);
            summary.put("totalShortageValue", totalShortageValue);
            summary.put("totalSurplusValue", totalSurplusValue);
            summary.put("totalLoss", totalLoss);
            summary.put("netLoss", netLoss);

            summary.put("adjustmentCount", movements.stream()
                    .filter(m -> m.getMovementType() == MovementType.INVENTORY_ADJUSTMENT)
                    .count());
            summary.put("expiredReturnCount", movements.stream()
                    .filter(m -> m.getMovementType() == MovementType.BR_TO_WARE2)
                    .count());
            summary.put("totalValue", netLoss);
            summary.put("totalValueFormatted", importExportService.formatCurrencyReadable(netLoss));

            log.info("Adjustment summary calculated - movements: {}, totalLoss: {}, netLoss: {}",
                     movements.size(), totalLoss, netLoss);

            return ResponseEntity.ok(summary);

        } catch (Exception e) {
            log.error("Error loading adjustment summary for branchId {}: {}", branchId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // -------------------- Movements for chart --------------------
    @GetMapping("/movements")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getAdjustmentMovements(
            @RequestParam(required = false, defaultValue = "week") String range,
            @RequestParam(required = false, defaultValue = "all") String type,
            @RequestParam(required = false) Long branchId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        log.info("Fetching adjustment movements - range: {}, type: {}, branchId: {}", range, type, branchId);

        if (userDetails == null) {
            log.warn("Unauthorized access - userDetails is null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String role = userDetails.getRole();
        if (role == null || (!role.equalsIgnoreCase("BUSINESS_OWNER") && !role.equalsIgnoreCase("OWNER"))) {
            log.warn("Access denied - user role: {}", role);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fromDate;
        switch (range.toLowerCase()) {
            case "month" -> fromDate = now.minusMonths(1);
            case "quarter" -> fromDate = now.minusMonths(3);
            default -> fromDate = now.minusWeeks(1);
        }

        List<MovementType> adjustmentTypes = List.of(MovementType.INVENTORY_ADJUSTMENT, MovementType.BR_TO_WARE2);
        List<InventoryMovement> allMovements;

        if (branchId != null) {
            allMovements = inventoryMovementRepository
                    .findMovementsWithDetailsSinceByBranchAndTypes(fromDate, branchId, adjustmentTypes);
        } else {
            allMovements = inventoryMovementRepository.findAll().stream()
                    .filter(m -> m.getCreatedAt() != null && m.getCreatedAt().isAfter(fromDate))
                    .filter(m -> adjustmentTypes.contains(m.getMovementType()))
                    .toList();
        }

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
                        return true;
                    }
                })
                .toList();

        Map<LocalDate, Map<String, Double>> dailyData = new LinkedHashMap<>();
        for (InventoryMovement m : movements) {
            LocalDate date = m.getCreatedAt() != null ? m.getCreatedAt().toLocalDate() : LocalDate.now();
            dailyData.putIfAbsent(date, new HashMap<>());
            Map<String, Double> dayData = dailyData.get(date);

            Double totalMoney = Math.abs(calculateTotalMoney(m));

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

        // Sorting keys if needed, TreeMap could be used or just sort now
        List<LocalDate> sortedDates = new ArrayList<>(dailyData.keySet());
        Collections.sort(sortedDates);

            for (LocalDate date : sortedDates) {
                labels.add(date.format(fmt));
                adjustments.add(dailyData.get(date).getOrDefault("adjustments", 0.0));
                expiredReturns.add(dailyData.get(date).getOrDefault("expiredReturns", 0.0));
            }

            Map<String, Object> data = new HashMap<>();
            data.put("labels", labels);
            data.put("adjustments", adjustments);
            data.put("expiredReturns", expiredReturns);

            log.info("Adjustment movements data prepared - {} data points", labels.size());

            return ResponseEntity.ok(data);

        } catch (Exception e) {
            log.error("Error loading adjustment movements for range {} and branchId {}: {}",
                      range, branchId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // -------------------- Recent activities --------------------
    @GetMapping("/activities")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Map<String, Object>>> getRecentActivities(
            @RequestParam(required = false, defaultValue = "10") int limit,
            @RequestParam(required = false, defaultValue = "all") String type,
            @RequestParam(required = false) Long branchId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String role = userDetails.getRole();
        if (role == null || (!role.equalsIgnoreCase("BUSINESS_OWNER") && !role.equalsIgnoreCase("OWNER"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        List<MovementType> adjustmentTypes = List.of(MovementType.INVENTORY_ADJUSTMENT, MovementType.BR_TO_WARE2);

        List<InventoryMovement> rawMovements;
        if (branchId != null) {
            rawMovements = inventoryMovementRepository
                    .findMovementsWithDetailsSinceByBranchAndTypes(threeMonthsAgo, branchId, adjustmentTypes);
        } else {
            rawMovements = inventoryMovementRepository.findAll().stream()
                    .filter(m -> m.getCreatedAt() != null && m.getCreatedAt().isAfter(threeMonthsAgo))
                    .filter(m -> adjustmentTypes.contains(m.getMovementType()))
                    .toList();
        }

        List<InventoryMovement> movements = rawMovements.stream()
                .filter(mv -> {
                    boolean isAdjustment = mv.getMovementType() == MovementType.INVENTORY_ADJUSTMENT;
                    boolean isExpiredReturn = mv.getMovementType() == MovementType.BR_TO_WARE2;

                    if ("INVENTORY_ADJUSTMENT".equalsIgnoreCase(type)) {
                        return isAdjustment;
                    } else if ("BR_TO_WARE2".equalsIgnoreCase(type)) {
                        return isExpiredReturn;
                    } else {
                        return true;
                    }
                })
                .sorted((a, b) -> {
                    if (a.getCreatedAt() == null && b.getCreatedAt() == null)
                        return 0;
                    if (a.getCreatedAt() == null)
                        return 1;
                    if (b.getCreatedAt() == null)
                        return -1;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                })
                .limit(limit)
                .toList();

        List<Map<String, Object>> activities = new ArrayList<>();
        for (InventoryMovement mv : movements) {
            double totalMoney = Math.abs(calculateTotalMoney(mv));

            String adjustmentType;
            if (mv.getMovementType() == MovementType.BR_TO_WARE2) {
                adjustmentType = "EXPIRED";
            } else if (mv.getMovementType() == MovementType.INVENTORY_ADJUSTMENT) {
                long totalQuantity = mv.getInventoryMovementDetails() != null
                        ? mv.getInventoryMovementDetails().stream()
                                .mapToLong(d -> d.getQuantity() != null ? d.getQuantity() : 0L)
                                .sum()
                        : 0L;
                adjustmentType = totalQuantity < 0 ? "SHORTAGE" : "SURPLUS";
            } else {
                adjustmentType = "UNKNOWN";
            }

            Map<String, Object> activity = new HashMap<>();
            activity.put("id", mv.getId());
            activity.put("code", "#MV" + String.format("%03d", mv.getId()));
            activity.put("type", mv.getMovementType().name());
            activity.put("adjustmentType", adjustmentType);
            activity.put("branchId", mv.getSourceBranchId());
            activity.put("branchName", mv.getSourceBranchId() != null
                    ? branchRepository.findById(mv.getSourceBranchId()).map(b -> b.getName()).orElse("-")
                    : "-");
            activity.put("creator", "-");
            activity.put("totalValue", totalMoney);
            activity.put("totalMoney", totalMoney);
            activity.put("totalValueFormatted", importExportService.formatCurrencyReadable(totalMoney));
            activity.put("timeAgo", importExportService.formatTimeAgo(mv.getCreatedAt()));
            activity.put("detailUrl", "/api/owner/adjustments/detail/" + mv.getId());

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

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String role = userDetails.getRole();
        if (role == null || (!role.equalsIgnoreCase("BUSINESS_OWNER") && !role.equalsIgnoreCase("OWNER"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        InventoryMovement mv = inventoryMovementRepository.findById(id).orElse(null);
        if (mv == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (mv.getMovementType() != MovementType.INVENTORY_ADJUSTMENT
                && mv.getMovementType() != MovementType.BR_TO_WARE2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Map<String, Object> detail = new HashMap<>();
        detail.put("id", mv.getId());
        detail.put("code", "#MV" + String.format("%03d", mv.getId()));
        detail.put("type", mv.getMovementType().name());

        Long branchId = mv.getSourceBranchId();
        String branchName = branchId != null ? branchRepository.findById(branchId)
                .map(b -> b.getName())
                .orElse("-") : "-";
        detail.put("branchName", branchName);

        Double tmp = inventoryMovementRepository.sumSnapCostByMovementId(mv.getId());
        double totalMoney = tmp != null ? tmp : 0.0;
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
                        ? d.getVariant().getMedicine().getName()
                        : "-");
                item.put("variantName", d.getVariant() != null ? d.getVariant().getDosage() : "-");
                item.put("batchCode", d.getBatch() != null ? d.getBatch().getBatchCode() : "-");
                item.put("quantity", d.getQuantity());
                item.put("price", d.getSnapCost());
                item.put("subtotal", (d.getQuantity() != null && d.getSnapCost() != null)
                        ? d.getQuantity() * d.getSnapCost()
                        : 0.0);
                items.add(item);
            }
        }
        detail.put("details", items);

        return ResponseEntity.ok(detail);
    }

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
