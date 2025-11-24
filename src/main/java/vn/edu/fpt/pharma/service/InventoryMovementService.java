package vn.edu.fpt.pharma.service;

import vn.edu.fpt.pharma.base.BaseService;
import vn.edu.fpt.pharma.dto.warehouse.InventoryMovementVM;
import vn.edu.fpt.pharma.entity.InventoryMovement;

import java.util.List;

public interface InventoryMovementService extends BaseService<InventoryMovement, Long> {
    List<InventoryMovementVM> getAllMovements();
    InventoryMovementVM getMovementById(Long id);
}
