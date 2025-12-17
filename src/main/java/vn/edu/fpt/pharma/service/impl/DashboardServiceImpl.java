package vn.edu.fpt.pharma.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.constant.MovementStatus;
import vn.edu.fpt.pharma.constant.MovementType;
import vn.edu.fpt.pharma.repository.*;
import vn.edu.fpt.pharma.service.DashboardService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final RequestFormRepository requestFormRepository;
    private final StockAdjustmentRepository stockAdjustmentRepository;
    private final BatchRepository batchRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository inventoryMovementRepository;

    @Override
    public Map<String, Object> getDashboardData(Long branchId) {
        Map<String, Object> result = new HashMap<>();

        // Waiting orders: count movements WARE_TO_BR with SHIPPED (waiting for branch to confirm)
        long waitingOrders = inventoryMovementRepository.countByBranchAndTypeAndStatus(
                branchId, MovementType.WARE_TO_BR, MovementStatus.SHIPPED);

        LocalDateTime lastInventoryCheck = stockAdjustmentRepository.findLastInventoryCheckByBranch(branchId);
        int nearlyExpiredCount = batchRepository.countNearlyExpiredByBranch(branchId);
        int lowStockCount = inventoryRepository.countLowStockByBranch(branchId);

        result.put("waitingOrders", (int) waitingOrders);
        result.put("lastInventoryCheck",
                lastInventoryCheck != null ? lastInventoryCheck.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Chưa kiểm kho");
        result.put("nearlyExpiredCount", nearlyExpiredCount);
        result.put("lowStockCount", lowStockCount);

        return result;
    }

    @Override
    public Map<String, Object> getWarehouseDashboardData() {
        Map<String, Object> result = new HashMap<>();
        Long warehouseBranchId = 1L; // Warehouse branch ID

        // 1. Count pending requests (REQUESTED status) that need warehouse confirmation
        int pendingRequests = requestFormRepository.countPendingRequestsForWarehouse();

        // 2. Count shipped orders from warehouse (SHIPPED status, source = warehouse)
        long shippedOrders = inventoryMovementRepository.countShippedFromWarehouse(
                warehouseBranchId, MovementStatus.SHIPPED);

        // 3. Count nearly expired medicines in warehouse (within 90 days)
        int nearlyExpiredCount = batchRepository.countNearlyExpiredByBranch(warehouseBranchId);

        // 4. Count already expired medicines in warehouse (past expiry date)
        int expiredCount = batchRepository.countExpiredByBranch(warehouseBranchId);

        result.put("pendingRequests", pendingRequests);
        result.put("shippedOrders", (int) shippedOrders);
        result.put("nearlyExpiredCount", nearlyExpiredCount);
        result.put("expiredCount", expiredCount);

        return result;
    }
}
