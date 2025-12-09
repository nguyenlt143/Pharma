package vn.edu.fpt.pharma.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.constant.MovementStatus;
import vn.edu.fpt.pharma.constant.MovementType;
import vn.edu.fpt.pharma.repository.*;
import vn.edu.fpt.pharma.service.DashboardService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    // private final RequestFormRepository requestFormRepository; // unused
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
                lastInventoryCheck != null ? lastInventoryCheck.toString() : "Chưa kiểm kho");
        result.put("nearlyExpiredCount", nearlyExpiredCount);
        result.put("lowStockCount", lowStockCount);

        return result;
    }
}
