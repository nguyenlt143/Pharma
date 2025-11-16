package vn.edu.fpt.pharma.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.repository.*;
import vn.edu.fpt.pharma.service.DashboardService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service

@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final RequestFormRepository requestFormRepository;
    private final StockAdjustmentRepository stockAdjustmentRepository;
    private final BatchRepository batchRepository;
    private final InventoryRepository inventoryRepository;

    @Override
    public Map<String, Object> getDashboardData() {
        Map<String, Object> result = new HashMap<>();

        int waitingOrders = requestFormRepository.countWaitingOrders();
        LocalDateTime lastInventoryCheck = stockAdjustmentRepository.findLastInventoryCheck();
        int nearlyExpiredCount = batchRepository.countNearlyExpired();
        int lowStockCount = inventoryRepository.countLowStock();

        result.put("waitingOrders", waitingOrders);
        result.put("lastInventoryCheck",
                lastInventoryCheck != null ? lastInventoryCheck.toString() : "Chưa kiểm kho");
        result.put("nearlyExpiredCount", nearlyExpiredCount);
        result.put("lowStockCount", lowStockCount);

        return result;
    }
}
