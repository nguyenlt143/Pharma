package vn.edu.fpt.pharma.service;

import vn.edu.fpt.pharma.base.BaseService;
import vn.edu.fpt.pharma.constant.MovementType;
import vn.edu.fpt.pharma.dto.warehouse.ExportSubmitDTO;
import vn.edu.fpt.pharma.dto.warehouse.InventoryMovementVM;
import vn.edu.fpt.pharma.dto.warehouse.ReceiptDetailVM;
import vn.edu.fpt.pharma.dto.warehouse.ReceiptInfo;
import vn.edu.fpt.pharma.dto.warehouse.ReceiptListItem;
import vn.edu.fpt.pharma.entity.InventoryMovement;

import java.util.List;

public interface InventoryMovementService extends BaseService<InventoryMovement, Long> {
    List<InventoryMovementVM> getAllMovements();
    InventoryMovementVM getMovementById(Long id);
    List<ReceiptListItem> getReceiptList(MovementType movementType, Long branchId, String status);
    ReceiptInfo getReceiptInfo(Long id);
    List<ReceiptDetailVM> getReceiptDetails(Long id);
    void approveReceipt(Long id);
    void shipReceipt(Long id);
    void receiveReceipt(Long id);
    void closeReceipt(Long id);
    void cancelReceipt(Long id);
    Long createExportMovement(ExportSubmitDTO dto);

    // For Inventory role - confirm import from warehouse
    List<vn.edu.fpt.pharma.dto.inventory.ConfirmImportVM> getConfirmImportList(Long branchId);
    vn.edu.fpt.pharma.dto.inventory.ConfirmImportVM getConfirmImportDetail(Long id);
    void confirmImportReceipt(Long id, Long branchId);

    // For Inventory role - create return to warehouse
    Long createReturnMovement(vn.edu.fpt.pharma.dto.inventory.ReturnRequestDTO dto);

    // For Warehouse role - create disposal movement
    Long createDisposalMovement(vn.edu.fpt.pharma.dto.warehouse.DisposalRequestDTO dto);
}
