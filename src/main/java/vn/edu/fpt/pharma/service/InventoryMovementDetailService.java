package vn.edu.fpt.pharma.service;

import vn.edu.fpt.pharma.base.BaseService;
import vn.edu.fpt.pharma.dto.warehouse.InventoryMovementDetailVM;
import vn.edu.fpt.pharma.entity.InventoryMovementDetail;

import java.util.List;

public interface InventoryMovementDetailService extends BaseService<InventoryMovementDetail, Long> {
    List<InventoryMovementDetailVM> getDetailsByMovementId(Long movementId);
}
