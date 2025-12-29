package vn.edu.fpt.pharma.controller.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.repository.*;
import vn.edu.fpt.pharma.constant.RequestStatus;
import vn.edu.fpt.pharma.constant.RequestType;
import vn.edu.fpt.pharma.constant.MovementStatus;
import vn.edu.fpt.pharma.constant.MovementType;
import vn.edu.fpt.pharma.entity.Inventory;
import vn.edu.fpt.pharma.entity.InventoryMovement;
import vn.edu.fpt.pharma.entity.Medicine;
import vn.edu.fpt.pharma.entity.MedicineVariant;
import vn.edu.fpt.pharma.entity.Category;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/owner/inventory")
@RequiredArgsConstructor
public class OwnerInventoryApiController {

    private final InventoryRepository inventoryRepository;
    private final RequestFormRepository requestFormRepository;
    private final BranchRepository branchRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final vn.edu.fpt.pharma.service.RequestFormService requestFormService;
    private final vn.edu.fpt.pharma.repository.MedicineVariantRepository medicineVariantRepository;
    private final vn.edu.fpt.pharma.repository.UnitConversionRepository unitConversionRepository;

    // Helper method to get display unit from variant's unit conversions
    private String getDisplayUnitFromVariant(MedicineVariant variant) {
        if (variant == null) return "-";
        List<vn.edu.fpt.pharma.entity.UnitConversion> conversions = unitConversionRepository.findByVariantIdId(variant.getId());
        if (conversions.isEmpty()) return "-";

        // Strategy: Use conversion with smallest multiplier (typically the base unit)
        return conversions.stream()
                .min(Comparator.comparing(vn.edu.fpt.pharma.entity.UnitConversion::getMultiplier))
                .map(uc -> uc.getUnitId().getName())
                .orElse("-");
    }

    /**
     * Get inventory summary for owner
     * GET /api/owner/inventory/summary?branchId=1
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getInventorySummary(
            @RequestParam(required = false) Long branchId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // Verify owner role - check for BUSINESS_OWNER enum name
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String role = userDetails.getRole();
        if (role == null || (!role.equalsIgnoreCase("BUSINESS_OWNER") && !role.equalsIgnoreCase("OWNER"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Map<String, Object> summary = new HashMap<>();
        
        // Calculate total inventory value (all branches or specific branch)
        Double totalValue = calculateTotalInventoryValue(branchId);
        summary.put("totalValue", formatCurrency(totalValue));
        
        // Calculate value delta (compare with previous month)
        String valueDeltaLabel = calculateValueDelta(branchId, totalValue);
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
     * Get inventory movement data for charts
     * GET /api/owner/inventory/movements?branchId=1&range=week
     */
    @GetMapping("/movements")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getInventoryMovements(
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false, defaultValue = "week") String range,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String role = userDetails.getRole();
        if (role == null || (!role.equalsIgnoreCase("BUSINESS_OWNER") && !role.equalsIgnoreCase("OWNER"))) {
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
        
        // Get movements
        List<InventoryMovement> movements = inventoryMovementRepository.findAll().stream()
            .filter(m -> m.getCreatedAt() != null && m.getCreatedAt().isAfter(fromDate))
            .filter(m -> branchId == null || 
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
            
            // Import: SUP_TO_WARE, WARE_TO_BR
            if (m.getMovementType() == MovementType.SUP_TO_WARE || 
                m.getMovementType() == MovementType.WARE_TO_BR) {
                dayData.put("imports", dayData.getOrDefault("imports", 0.0) + 
                    (m.getTotalMoney() != null ? m.getTotalMoney() : 0.0));
            }
            // Export: BR_TO_WARE, WARE_TO_SUP, DISPOSAL
            if (m.getMovementType() == MovementType.BR_TO_WARE || 
                m.getMovementType() == MovementType.WARE_TO_SUP ||
                m.getMovementType() == MovementType.DISPOSAL) {
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
     * Get category distribution
     * GET /api/owner/inventory/categories?branchId=1
     */
    @GetMapping("/categories")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Map<String, Object>>> getCategoryDistribution(
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) Long categoryId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String role = userDetails.getRole();
        if (role == null || (!role.equalsIgnoreCase("BUSINESS_OWNER") && !role.equalsIgnoreCase("OWNER"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Get categories (filter by categoryId if provided)
        List<Category> categories;
        if (categoryId != null) {
            categories = categoryRepository.findById(categoryId)
                    .map(Collections::singletonList)
                    .orElse(Collections.emptyList());
        } else {
            categories = categoryRepository.findAll();
        }
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Category cat : categories) {
            // Filter inventory by branch and category
            List<Inventory> categoryInventory = inventoryRepository.findAll().stream()
                .filter(inv -> {
                    if (branchId != null) {
                        try {
                            if (inv.getBranch() == null || !inv.getBranch().getId().equals(branchId)) {
                                return false;
                            }
                        } catch (Exception e) {
                            return false;
                        }
                    }
                    try {
                        MedicineVariant variant = inv.getVariant();
                        if (variant == null) return false;
                        Medicine medicine = variant.getMedicine();
                        if (medicine == null || medicine.getCategory() == null) return false;
                        return medicine.getCategory().getId().equals(cat.getId());
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(java.util.stream.Collectors.toList());
            
            // Calculate item count (distinct variants)
            long itemCount = categoryInventory.stream()
                .map(Inventory::getVariant)
                .filter(java.util.Objects::nonNull)
                .map(MedicineVariant::getId)
                .distinct()
                .count();
            
            // Calculate total value
            double totalValue = categoryInventory.stream()
                .mapToDouble(inv -> {
                    Long qty = inv.getQuantity() != null ? inv.getQuantity() : 0L;
                    Double cost = inv.getCostPrice() != null ? inv.getCostPrice() : 0.0;
                    return qty.doubleValue() * cost;
                })
                .sum();
            
            if (itemCount > 0 || totalValue > 0) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", cat.getName());
                item.put("value", totalValue);
                item.put("itemCount", itemCount);
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
     * Get recent activities
     * GET /api/owner/inventory/activities?branchId=1&limit=5
     */
    @GetMapping("/activities")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Map<String, Object>>> getRecentActivities(
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false, defaultValue = "5") int limit,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String role = userDetails.getRole();
        if (role == null || (!role.equalsIgnoreCase("BUSINESS_OWNER") && !role.equalsIgnoreCase("OWNER"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Map<String, Object>> activities = new ArrayList<>();
        
        // Get recent request forms
        List<vn.edu.fpt.pharma.entity.RequestForm> requestForms = requestFormRepository.findAll().stream()
            .filter(rf -> branchId == null || (rf.getBranchId() != null && rf.getBranchId().equals(branchId)))
            .sorted(Comparator.comparing(vn.edu.fpt.pharma.entity.RequestForm::getCreatedAt).reversed())
            .limit(limit)
            .collect(Collectors.toList());
        
        for (vn.edu.fpt.pharma.entity.RequestForm rf : requestForms) {
            Map<String, Object> activity = new HashMap<>();
            activity.put("code", "#RQ" + String.format("%03d", rf.getId()));
            activity.put("type", rf.getRequestType() == RequestType.IMPORT ? "Nhập kho" : "Xuất kho");
            activity.put("branch", rf.getBranchId() != null ? 
                branchRepository.findById(rf.getBranchId()).map(b -> b.getName()).orElse("-") : "-");
            activity.put("creator", "-"); // TODO: Get creator from request form
            activity.put("totalQty", 0); // TODO: Calculate from request form details
            activity.put("status", rf.getRequestStatus() != null ? getRequestStatusLabel(rf.getRequestStatus()) : "-");
            activity.put("timeAgo", formatTimeAgo(rf.getCreatedAt()));
            activity.put("detailUrl", "/owner/request/" + rf.getId());
            activities.add(activity);
        }
        
        // Get recent inventory movements
        List<InventoryMovement> movements = inventoryMovementRepository.findAll().stream()
            .filter(m -> branchId == null || 
                (m.getDestinationBranchId() != null && m.getDestinationBranchId().equals(branchId)) ||
                (m.getSourceBranchId() != null && m.getSourceBranchId().equals(branchId)))
            .sorted(Comparator.comparing(InventoryMovement::getCreatedAt).reversed())
            .limit(limit)
            .collect(Collectors.toList());
        
        for (InventoryMovement m : movements) {
            Map<String, Object> activity = new HashMap<>();
            activity.put("code", "#MV" + String.format("%03d", m.getId()));
            activity.put("type", getMovementTypeLabel(m.getMovementType()));
            activity.put("branch", m.getDestinationBranchId() != null ? 
                branchRepository.findById(m.getDestinationBranchId()).map(b -> b.getName()).orElse("-") : "-");
            activity.put("creator", "-"); // TODO: Get creator
            activity.put("totalQty", 0); // TODO: Calculate from movement details
            activity.put("status", m.getMovementStatus() != null ? getMovementStatusLabel(m.getMovementStatus()) : "-");
            activity.put("timeAgo", formatTimeAgo(m.getCreatedAt()));
            activity.put("detailUrl", "/owner/movement/" + m.getId());
            activities.add(activity);
        }
        
        // Sort by time and limit
        // Sort by created time (fallback: leave as-is if not available)
        // (currently timeAgo là chuỗi, nên giữ thứ tự đã sắp xếp từ nguồn)
        
        return ResponseEntity.ok(activities.stream().limit(limit).collect(Collectors.toList()));
    }
    
    /**
     * Get request form detail
     * GET /api/owner/inventory/request/{id}
     */
    @GetMapping("/request/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getRequestFormDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String role = userDetails.getRole();
        if (role == null || (!role.equalsIgnoreCase("BUSINESS_OWNER") && !role.equalsIgnoreCase("OWNER"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        try {
            vn.edu.fpt.pharma.dto.warehouse.RequestList request = requestFormService.getDetailById(id);
            List<vn.edu.fpt.pharma.dto.warehouse.RequestDetailVM> details = requestFormService.getDetailsOfRequest(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", request.id());
            response.put("code", "#RQ" + String.format("%03d", request.id()));
            response.put("type", request.requestType() != null ? 
                (request.requestType().equals("IMPORT") ? "Nhập kho" : "Xuất kho") : "-");
            response.put("status", request.requestStatus() != null ? request.requestStatus() : "-");
            response.put("note", request.note() != null ? request.note() : "");
            response.put("createdAt", request.createdAt() != null ? request.createdAt() : "-");
            response.put("branchId", request.branchId());
            response.put("branchName", request.branchId() != null ? 
                branchRepository.findById(request.branchId()).map(b -> b.getName()).orElse("-") : "-");
            
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
                        detail.put("variantName", variant.getDosageForm() != null ? variant.getDosageForm().getDisplayName() : "-");
                        if (variant.getMedicine() != null) {
                            detail.put("medicineName", variant.getMedicine().getName() != null ? 
                                variant.getMedicine().getName() : "-");
                        } else {
                            detail.put("medicineName", "-");
                        }
                        detail.put("unit", getDisplayUnitFromVariant(variant));
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

    /**
     * Get inventory movement detail (bao gồm INVENTORY_ADJUSTMENT)
     * GET /api/owner/inventory/movement/{id}
     */
    @GetMapping("/movement/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getInventoryMovementDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String role = userDetails.getRole();
        if (role == null || (!role.equalsIgnoreCase("BUSINESS_OWNER") && !role.equalsIgnoreCase("OWNER"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Optional<InventoryMovement> optMovement = inventoryMovementRepository.findByIdWithDetails(id);
        if (optMovement.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        InventoryMovement movement = optMovement.get();

        Map<String, Object> response = new HashMap<>();
        response.put("id", movement.getId());
        response.put("code", "#MV" + String.format("%03d", movement.getId()));
        response.put("type", getMovementTypeLabel(movement.getMovementType()));
        response.put("status", movement.getMovementStatus() != null
                ? getMovementStatusLabel(movement.getMovementStatus())
                : "-");
        response.put("createdAt", movement.getCreatedAt() != null
                ? movement.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : "-");

        Long branchId = movement.getDestinationBranchId() != null
                ? movement.getDestinationBranchId()
                : movement.getSourceBranchId();
        response.put("branchId", branchId);
        response.put("branchName", branchId != null
                ? branchRepository.findById(branchId).map(b -> b.getName()).orElse("-")
                : "-");

        // Calculate total quantity from details
        List<vn.edu.fpt.pharma.entity.InventoryMovementDetail> details =
                movement.getInventoryMovementDetails() != null
                        ? movement.getInventoryMovementDetails()
                        : Collections.emptyList();

        int totalQty = details.stream()
                .mapToInt(d -> d.getQuantity() != null ? d.getQuantity().intValue() : 0)
                .sum();
        response.put("totalQty", totalQty);

        // Format detail rows
        List<Map<String, Object>> detailList = details.stream().map(d -> {
            Map<String, Object> detail = new HashMap<>();
            detail.put("quantity", d.getQuantity() != null ? d.getQuantity() : 0);

            MedicineVariant variant = d.getVariant();
            if (variant != null) {
                detail.put("variantName", variant.getDosageForm() != null ? variant.getDosageForm().getDisplayName() : "-");
                Medicine med = variant.getMedicine();
                if (med != null) {
                    detail.put("medicineName", med.getName() != null ? med.getName() : "-");
                } else {
                    detail.put("medicineName", "-");
                }
                detail.put("unit", getDisplayUnitFromVariant(variant));
            } else {
                detail.put("medicineName", "-");
                detail.put("variantName", "-");
                detail.put("unit", "-");
            }

            return detail;
        }).collect(Collectors.toList());
        response.put("details", detailList);

        return ResponseEntity.ok(response);
    }

    private Double calculateTotalInventoryValue(Long branchId) {
        // Using stream operations (deleted filter is handled by @SQLRestriction)
        try {
            if (branchId == null) {
                double total = inventoryRepository.findAll().stream()
                    .mapToDouble(inv -> {
                        Long qty = inv.getQuantity() != null ? inv.getQuantity() : 0L;
                        Double cost = inv.getCostPrice() != null ? inv.getCostPrice() : 0.0;
                        return qty.doubleValue() * cost;
                    })
                    .sum();
                return total;
            } else {
                double total = inventoryRepository.findAll().stream()
                    .filter(inv -> inv.getBranch() != null && inv.getBranch().getId().equals(branchId))
                    .mapToDouble(inv -> {
                        Long qty = inv.getQuantity() != null ? inv.getQuantity() : 0L;
                        Double cost = inv.getCostPrice() != null ? inv.getCostPrice() : 0.0;
                        return qty.doubleValue() * cost;
                    })
                    .sum();
                return total;
            }
        } catch (Exception e) {
            // Log error and return 0
            System.err.println("Error calculating total inventory value: " + e.getMessage());
            e.printStackTrace();
            return 0.0;
        }
    }

    private String calculateValueDelta(Long branchId, Double currentValue) {
        // Compare with previous month
        // For now, return empty or placeholder
        return "+0% so với tháng trước";
    }

    private int countLowStock(Long branchId) {
        if (branchId == null) {
            // Count all branches (deleted filter is handled by @SQLRestriction)
            return (int) inventoryRepository.findAll().stream()
                .filter(inv -> inv.getQuantity() != null && 
                              inv.getMinStock() != null &&
                              inv.getQuantity() <= inv.getMinStock())
                .count();
        } else {
            return (int) inventoryRepository.findAll().stream()
                .filter(inv -> inv.getBranch() != null && 
                              inv.getBranch().getId().equals(branchId) &&
                              inv.getQuantity() != null && 
                              inv.getMinStock() != null &&
                              inv.getQuantity() <= inv.getMinStock())
                .count();
        }
    }

    private int countPendingInbound(Long branchId) {
        // deleted filter is handled by @SQLRestriction
        if (branchId == null) {
            return (int) requestFormRepository.findAll().stream()
                .filter(rf -> rf.getRequestType() == RequestType.IMPORT &&
                             (rf.getRequestStatus() == RequestStatus.REQUESTED || 
                              rf.getRequestStatus() == RequestStatus.RECEIVED))
                .count();
        } else {
            return (int) requestFormRepository.findAll().stream()
                .filter(rf -> rf.getBranchId() != null &&
                             rf.getBranchId().equals(branchId) &&
                             rf.getRequestType() == RequestType.IMPORT &&
                             (rf.getRequestStatus() == RequestStatus.REQUESTED || 
                              rf.getRequestStatus() == RequestStatus.RECEIVED))
                .count();
        }
    }

    private int countPendingOutbound(Long branchId) {
        // deleted filter is handled by @SQLRestriction
        // Outbound requests are RETURN type (xuất trả kho)
        if (branchId == null) {
            return (int) requestFormRepository.findAll().stream()
                .filter(rf -> rf.getRequestType() == RequestType.RETURN &&
                             (rf.getRequestStatus() == RequestStatus.REQUESTED || 
                              rf.getRequestStatus() == RequestStatus.RECEIVED))
                .count();
        } else {
            return (int) requestFormRepository.findAll().stream()
                .filter(rf -> rf.getBranchId() != null &&
                             rf.getBranchId().equals(branchId) &&
                             rf.getRequestType() == RequestType.RETURN &&
                             (rf.getRequestStatus() == RequestStatus.REQUESTED || 
                              rf.getRequestStatus() == RequestStatus.RECEIVED))
                .count();
        }
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
        if (days < 7) return days + " ngày trước";
        
        long weeks = days / 7;
        if (weeks < 4) return weeks + " tuần trước";
        
        return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private String getMovementTypeLabel(MovementType type) {
        if (type == null) return "-";
        switch (type) {
            case SUP_TO_WARE:
                return "Nhập từ NCC";
            case WARE_TO_BR:
                return "Cấp phát";
            case BR_TO_WARE:
                return "Trả kho";
            case WARE_TO_SUP:
                return "Trả NCC";
            case DISPOSAL:
                return "Tiêu hủy";
            default:
                return type.name();
        }
    }

    private String getRequestStatusLabel(RequestStatus status) {
        if (status == null) return "-";
        switch (status) {
            case REQUESTED:
                return "Đã yêu cầu";
            case CONFIRMED:
                return "Đã xác nhận";
            case RECEIVED:
                return "Đã nhận";
            case CANCELLED:
                return "Đã hủy";
            default:
                return status.name();
        }
    }

    private String getMovementStatusLabel(MovementStatus status) {
        if (status == null) return "-";
        switch (status) {
            case DRAFT:
                return "Nháp";
            case APPROVED:
                return "Đã duyệt";
            case SHIPPED:
                return "Đã gửi";
            case RECEIVED:
                return "Đã nhận";
            case CANCELLED:
                return "Đã hủy";
            case CLOSED:
                return "Đã đóng";
            default:
                return status.name();
        }
    }
}

