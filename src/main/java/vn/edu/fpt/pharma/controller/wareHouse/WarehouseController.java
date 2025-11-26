package vn.edu.fpt.pharma.controller.warehouse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import vn.edu.fpt.pharma.constant.MovementType;
import vn.edu.fpt.pharma.dto.warehouse.ExportCreateDTO;
import vn.edu.fpt.pharma.dto.warehouse.InventoryMovementDetailVM;
import vn.edu.fpt.pharma.dto.warehouse.InventoryMovementVM;
import vn.edu.fpt.pharma.dto.warehouse.ReceiptDetailVM;
import vn.edu.fpt.pharma.dto.warehouse.ReceiptInfo;
import vn.edu.fpt.pharma.dto.warehouse.ReceiptListItem;
import vn.edu.fpt.pharma.entity.Branch;
import vn.edu.fpt.pharma.service.BranchService;
import vn.edu.fpt.pharma.service.InventoryMovementService;
import vn.edu.fpt.pharma.service.RequestFormService;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final InventoryMovementService inventoryMovementService;
    private final BranchService branchService;
    private final RequestFormService requestFormService;
    @GetMapping("/receipt/create")
    public String receiptCreate(Model model) {
        // Tạo ViewModel rỗng cho form mới
        InventoryMovementVM inventoryMovementVM = new InventoryMovementVM(
                null, null, null, null, null, null, null, null, 0.0, null
        );
        List<InventoryMovementDetailVM> inventoryMovementDetails = new ArrayList<>();

        model.addAttribute("inventoryMovementVM", inventoryMovementVM);
        model.addAttribute("inventoryMovementDetails", inventoryMovementDetails);

        return "pages/warehouse/receipt_create";
    }

    @GetMapping("/receipt-list")
    public String receiptList(Model model) {
        // Get all receipts initially
        List<ReceiptListItem> receipts = inventoryMovementService.getReceiptList(null, null, null);
        List<Branch> branches = branchService.findAll();

        model.addAttribute("receipts", receipts);
        model.addAttribute("branches", branches);

        return "pages/warehouse/receipt_list";
    }

    // Alias for backward compatibility
    @GetMapping("/receipt/list")
    public String receiptListAlias(Model model) {
        return receiptList(model);
    }

    @GetMapping("/receipt-detail/{id}")
    public String receiptDetail(@PathVariable Long id, Model model) {
        ReceiptInfo receipt = inventoryMovementService.getReceiptInfo(id);
        List<ReceiptDetailVM> details = inventoryMovementService.getReceiptDetails(id);

        model.addAttribute("receipt", receipt);
        model.addAttribute("details", details);

        return "pages/warehouse/receipt_detail";
    }

    @PostMapping("/receipts/{id}/approve")
    @ResponseBody
    public void approveReceipt(@PathVariable Long id) {
        inventoryMovementService.approveReceipt(id);
    }

    @PostMapping("/receipts/{id}/ship")
    @ResponseBody
    public void shipReceipt(@PathVariable Long id) {
        inventoryMovementService.shipReceipt(id);
    }

    @PostMapping("/receipts/{id}/receive")
    @ResponseBody
    public void receiveReceipt(@PathVariable Long id) {
        inventoryMovementService.receiveReceipt(id);
    }

    @PostMapping("/receipts/{id}/close")
    @ResponseBody
    public void closeReceipt(@PathVariable Long id) {
        inventoryMovementService.closeReceipt(id);
    }

    @PostMapping("/receipts/{id}/cancel")
    @ResponseBody
    public void cancelReceipt(@PathVariable Long id) {
        inventoryMovementService.cancelReceipt(id);
    }

    @GetMapping("/inventory")
    public String warehouseInventory(Model model) {

        return "pages/warehouse/warehouse_manage";
    }

    @GetMapping("/export/create")
    public String exportCreate(@RequestParam(required = false) Long requestId, Model model) {
        if (requestId != null) {
            // Load data from request
            ExportCreateDTO exportData = requestFormService.prepareExportCreation(requestId);
            model.addAttribute("exportData", exportData);
        } else {
            // Create empty export form
            model.addAttribute("exportData", null);
        }
        return "pages/warehouse/export_create";
    }

    @PostMapping("/export/create")
    @ResponseBody
    public java.util.Map<String, Object> createExportMovement(@org.springframework.web.bind.annotation.RequestBody vn.edu.fpt.pharma.dto.warehouse.ExportSubmitDTO dto) {
        try {
            Long movementId = inventoryMovementService.createExportMovement(dto);
            return java.util.Map.of(
                "success", true,
                "movementId", movementId,
                "message", "Tạo phiếu xuất thành công!"
            );
        } catch (Exception e) {
            return java.util.Map.of(
                "success", false,
                "message", "Lỗi: " + e.getMessage()
            );
        }
    }

    @GetMapping("/receipt-list/filter")
    @ResponseBody
    public List<ReceiptListItem> filterReceipts(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) String status
    ) {
        MovementType movementType = null;
        if (type != null && !type.isEmpty()) {
            try {
                movementType = MovementType.valueOf(type);
            } catch (IllegalArgumentException e) {
                // Invalid type, ignore
            }
        }

        return inventoryMovementService.getReceiptList(movementType, branchId, status);
    }

//    @GetMapping("/request/detail")
//    public String requestDetail(Model model) {
//
//        return "pages/warehouse/request_detail";
//    }

}
