package vn.edu.fpt.pharma.service;

import vn.edu.fpt.pharma.base.BaseService;
import vn.edu.fpt.pharma.dto.inventorycheck.InventoryCheckHistoryVM;
import vn.edu.fpt.pharma.dto.inventorycheck.StockAdjustmentDetailVM;
import vn.edu.fpt.pharma.entity.StockAdjustment;

import java.util.List;

public interface StockAdjustmentService extends BaseService<StockAdjustment, Long> {

    List<InventoryCheckHistoryVM> getInventoryCheckHistory(Long branchId);

    List<StockAdjustmentDetailVM> getInventoryCheckDetails(Long branchId, String checkDate);

    void performInventoryCheck(Long branchId, Long userId, vn.edu.fpt.pharma.dto.inventory.InventoryCheckRequestDTO request);
}