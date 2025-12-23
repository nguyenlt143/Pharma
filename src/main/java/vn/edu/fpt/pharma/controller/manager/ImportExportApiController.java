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
import vn.edu.fpt.pharma.repository.InventoryMovementRepository;
import vn.edu.fpt.pharma.repository.BranchRepository;
import vn.edu.fpt.pharma.repository.InventoryRepository;
import vn.edu.fpt.pharma.service.ImportExportService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/manager/import-export")
@RequiredArgsConstructor
public class ImportExportApiController {

    private final InventoryRepository inventoryRepository;
    private final BranchRepository branchRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final ImportExportService importExportService;
    private final vn.edu.fpt.pharma.repository.UnitConversionRepository unitConversionRepository;

    // Helper method to get display unit from variant's unit conversions
    private String getDisplayUnitFromVariant(vn.edu.fpt.pharma.entity.MedicineVariant variant) {
        if (variant == null) return "-";
        List<vn.edu.fpt.pharma.entity.UnitConversion> conversions = unitConversionRepository.findByVariantIdId(variant.getId());
        if (conversions.isEmpty()) return "-";

        // Strategy: Use conversion with smallest multiplier (typically the base unit)
        return conversions.stream()
                .min(Comparator.comparing(vn.edu.fpt.pharma.entity.UnitConversion::getMultiplier))
                .map(uc -> uc.getUnitId().getName())
                .orElse("-");
    }

    // -------------------- Summary --------------------
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

        // numeric total value (raw) and formatted string
        Double totalValue = importExportService.calculateTotalInventoryValue(branchId);
        summary.put("totalValue", totalValue);
        summary.put("totalValueFormatted", importExportService.formatCurrencyReadable(totalValue));

        // value delta label (keep existing logic for now)
        String valueDeltaLabel = "+0% so với tháng trước";
        summary.put("valueDeltaLabel", valueDeltaLabel);

        // Counts
        int lowStockCount = importExportService.countLowStock(branchId);
        summary.put("lowStockCount", lowStockCount);

        int pendingInbound = importExportService.countPendingInbound(branchId);
        summary.put("pendingInbound", pendingInbound);

        int pendingOutbound = importExportService.countPendingOutbound(branchId);
        summary.put("pendingOutbound", pendingOutbound);

        return ResponseEntity.ok(summary);
    }

    // -------------------- Movements for chart --------------------
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

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fromDate;
        switch (range.toLowerCase()) {
            case "month" -> fromDate = now.minusMonths(1);
            case "quarter" -> fromDate = now.minusMonths(3);
            default -> fromDate = now.minusWeeks(1);
        }

        List<InventoryMovement> movements = inventoryMovementRepository.findMovementsSinceByBranch(fromDate, branchId);

        Map<LocalDate, Map<String, Double>> dailyData = new LinkedHashMap<>();
        for (InventoryMovement m : movements) {
            LocalDate date = m.getCreatedAt() != null ? m.getCreatedAt().toLocalDate() : LocalDate.now();
            dailyData.putIfAbsent(date, new HashMap<>());
            Map<String, Double> dayData = dailyData.get(date);

            Double t = m.getTotalMoney() != null ? m.getTotalMoney() : 0.0;

            if ((m.getMovementType() == MovementType.WARE_TO_BR && Objects.equals(branchId, m.getDestinationBranchId()))
                || (m.getMovementType() == MovementType.SUP_TO_WARE)) {
                dayData.put("imports", dayData.getOrDefault("imports", 0.0) + t);
            }
            if (m.getMovementType() == MovementType.BR_TO_WARE && Objects.equals(branchId, m.getSourceBranchId())) {
                dayData.put("exports", dayData.getOrDefault("exports", 0.0) + t);
            }
        }

        List<String> labels = new ArrayList<>();
        List<Double> imports = new ArrayList<>();
        List<Double> exports = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");
        for (Map.Entry<LocalDate, Map<String, Double>> entry : dailyData.entrySet()) {
            labels.add(entry.getKey().format(fmt));
            imports.add(entry.getValue().getOrDefault("imports", 0.0));
            exports.add(entry.getValue().getOrDefault("exports", 0.0));
        }

        Map<String, Object> data = new HashMap<>();
        data.put("labels", labels);
        data.put("imports", imports);
        data.put("exports", exports);

        return ResponseEntity.ok(data);
    }

    // -------------------- Category distribution --------------------
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

        List<Object[]> stats = inventoryRepository.getCategoryStatistics(branchId);
        List<Map<String, Object>> result = new ArrayList<>();
        if (stats != null) {
            for (Object[] row : stats) {
                String name = row[0] != null ? row[0].toString() : "-";
                double value = row.length > 3 && row[3] != null ? ((Number) row[3]).doubleValue() : 0.0;
                if (value > 0) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("name", name);
                    item.put("value", value);
                    result.add(item);
                }
            }
        }

        return ResponseEntity.ok(result);
    }

    // -------------------- Recent activities (requests) --------------------
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

        // Return recent Inventory Movements involving this branch (source or destination)
        // EXCLUDE: INVENTORY_ADJUSTMENT and BR_TO_WARE2 (expired goods return)
        List<InventoryMovement> movements = inventoryMovementRepository.findAll().stream()
                .filter(mv -> mv.getMovementType() != MovementType.INVENTORY_ADJUSTMENT)
                .filter(mv -> mv.getMovementType() != MovementType.BR_TO_WARE2)
                .filter(mv -> (mv.getDestinationBranchId() != null && mv.getDestinationBranchId().equals(branchId))
                        || (mv.getSourceBranchId() != null && mv.getSourceBranchId().equals(branchId)))
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
            activity.put("type", importExportService.movementTypeLabel(mv.getMovementType()));
            activity.put("typeClass", importExportService.movementTypeClass(mv.getMovementType()));
            activity.put("creator", "-");

            long units = 0L;
            if (mv.getInventoryMovementDetails() != null) {
                for (vn.edu.fpt.pharma.entity.InventoryMovementDetail imd : mv.getInventoryMovementDetails()) {
                    units += imd.getQuantity() != null ? imd.getQuantity() : 0;
                }
            }
            activity.put("totalQty", units);

            // Use the movement-level totalMoney (precomputed) instead of recalculating
            double totalMoney = mv.getTotalMoney() != null ? mv.getTotalMoney() : 0.0;
            activity.put("totalMoney", totalMoney);
            activity.put("totalValue", totalMoney); // backwards compatibility
            activity.put("totalValueFormatted", importExportService.formatCurrencyReadable(totalMoney));

            activity.put("status", mv.getMovementStatus() != null ? mv.getMovementStatus().name() : "-");
            activity.put("timeAgo", importExportService.formatTimeAgo(mv.getCreatedAt()));
            activity.put("detailUrl", "/manager/movement/" + mv.getId());
            activities.add(activity);
        }

        return ResponseEntity.ok(activities);
    }

    // -------------------- Request/Movement detail --------------------
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
            // Load InventoryMovement by id (movement id now used here)
            Optional<InventoryMovement> mvOpt = inventoryMovementRepository.findByIdWithDetails(id);
            if (mvOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            InventoryMovement mv = mvOpt.get();

            // Permission: require that the user's branch is involved in the movement (source or destination)
            boolean allowed = (mv.getDestinationBranchId() != null && mv.getDestinationBranchId().equals(branchId))
                    || (mv.getSourceBranchId() != null && mv.getSourceBranchId().equals(branchId));
            if (!allowed) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // Map movement -> response
            Map<String, Object> response = new HashMap<>();
            response.put("id", mv.getId());
            response.put("code", "#MV" + String.format("%03d", mv.getId()));
            response.put("type", importExportService.movementTypeLabel(mv.getMovementType()));
            response.put("createdAt", mv.getCreatedAt() != null ? mv.getCreatedAt() : "-");
            // Branch name: prefer destination branch, otherwise source
            Long branchForName = mv.getDestinationBranchId() != null ? mv.getDestinationBranchId() : mv.getSourceBranchId();
            response.put("branchName", branchForName != null ? branchRepository.findById(branchForName).map(vn.edu.fpt.pharma.entity.Branch::getName).orElse("-") : "-");

            // details from movement details
            List<Map<String, Object>> detailList = new ArrayList<>();
            if (mv.getInventoryMovementDetails() != null) {
                for (vn.edu.fpt.pharma.entity.InventoryMovementDetail imd : mv.getInventoryMovementDetails()) {
                    Map<String, Object> detail = new HashMap<>();
                    detail.put("quantity", imd.getQuantity() != null ? imd.getQuantity() : 0);
                    if (imd.getVariant() != null) {
                        detail.put("variantId", imd.getVariant().getId());
                        detail.put("variantName", imd.getVariant().getDosage_form() != null ? imd.getVariant().getDosage_form() : "-");
                        detail.put("medicineName", imd.getVariant().getMedicine() != null && imd.getVariant().getMedicine().getName() != null ? imd.getVariant().getMedicine().getName() : "-");
                        detail.put("unit", getDisplayUnitFromVariant(imd.getVariant()));
                    } else {
                        detail.put("medicineName", "-");
                        detail.put("variantName", "-");
                        detail.put("unit", "-");
                    }
                    detailList.add(detail);
                }
            }

            response.put("details", detailList);

            // total quantity (sum of detail quantities) and totalValue from movement
            int totalQty = detailList.stream().mapToInt(d -> ((Number) d.getOrDefault("quantity", 0)).intValue()).sum();
            response.put("totalQty", totalQty);
            // totalValue should come from InventoryMovement.totalMoney (movement-level total), not sum of detail quantities
            // expose movement-level total as both totalMoney and totalValue
            double mvTotal = mv.getTotalMoney() != null ? mv.getTotalMoney() : 0.0;
            response.put("totalMoney", mvTotal);
            response.put("totalValue", mvTotal);
            response.put("totalValueFormatted", importExportService.formatCurrencyReadable(mvTotal));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
