package vn.edu.fpt.pharma.controller.wareHouse;

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
import vn.edu.fpt.pharma.dto.warehouse.DisposalRequestDTO;
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
import vn.edu.fpt.pharma.service.InventoryService;
import vn.edu.fpt.pharma.service.StockAdjustmentService;
import vn.edu.fpt.pharma.dto.inventory.InventoryMedicineVM;
import vn.edu.fpt.pharma.dto.inventory.InventoryCheckRequestDTO;
import vn.edu.fpt.pharma.dto.inventorycheck.InventoryCheckHistoryVM;
import vn.edu.fpt.pharma.dto.inventorycheck.StockAdjustmentDetailVM;
import vn.edu.fpt.pharma.entity.Inventory;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final InventoryMovementService inventoryMovementService;
    private final BranchService branchService;
    private final RequestFormService requestFormService;
    private final InventoryService inventoryService;
    private final StockAdjustmentService stockAdjustmentService;
    private final vn.edu.fpt.pharma.service.DashboardService dashboardService;

    // -------------------- DASHBOARD --------------------
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        var data = dashboardService.getWarehouseDashboardData();
        model.addAllAttributes(data);
        return "pages/warehouse/dashboard";
    }

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
    public String receiptList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        // Get paginated receipts initially
        vn.edu.fpt.pharma.dto.common.PageResponse<ReceiptListItem> pageResponse =
            inventoryMovementService.getReceiptListPaginated(null, null, null, page, size);
        List<Branch> branches = branchService.findAll();

        model.addAttribute("receipts", pageResponse.content());
        model.addAttribute("branches", branches);
        model.addAttribute("pagination", pageResponse);

        return "pages/warehouse/receipt_list";
    }

    // Alias for backward compatibility
    @GetMapping("/receipt/list")
    public String receiptListAlias(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        return receiptList(page, size, model);
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
        // Load medicines from central warehouse (branchId = 1)
        List<InventoryMedicineVM> medicines = inventoryService.getInventoryMedicinesByBranch(1L);

        model.addAttribute("medicines", medicines);
        model.addAttribute("branchId", 1L);
        model.addAttribute("branchName", "Kho Tổng");

        return "pages/warehouse/warehouse_inventory";
    }

    @PostMapping("/api/inventory/{inventoryId}/min-stock")
    @ResponseBody
    public ResponseEntity<?> updateMinStock(@PathVariable Long inventoryId, @org.springframework.web.bind.annotation.RequestBody Map<String, Object> body) {
        try {
            if (!body.containsKey("minStock")) return ResponseEntity.badRequest().body(Map.of("success", false, "message", "minStock is required"));
            Long minStock = body.get("minStock") == null ? null : Long.valueOf(body.get("minStock").toString());
            inventoryService.updateMinStock(inventoryId, minStock);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
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
    public vn.edu.fpt.pharma.dto.common.PageResponse<ReceiptListItem> filterReceipts(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        MovementType movementType = null;
        if (type != null && !type.isEmpty()) {
            try {
                movementType = MovementType.valueOf(type);
            } catch (IllegalArgumentException e) {
                // Invalid type, ignore
            }
        }

        return inventoryMovementService.getReceiptListPaginated(movementType, branchId, status, page, size);
    }

    // -------------------- CHECK INVENTORY --------------------
    @GetMapping("/check")
    public String checkList(Model model) {
        Long branchId = 1L; // Warehouse branch ID
        List<InventoryCheckHistoryVM> inventoryChecks =
            stockAdjustmentService.getInventoryCheckHistory(branchId);

        model.addAttribute("inventoryChecks", inventoryChecks);
        model.addAttribute("branchName", "Kho Tổng");

        return "pages/warehouse/warehouse_check_list";
    }

    @GetMapping("/check/detail")
    public String checkDetail(
            @RequestParam String checkDate,
            Model model
    ) {
        Long branchId = 1L;
        List<StockAdjustmentDetailVM> details =
            stockAdjustmentService.getInventoryCheckDetails(branchId, checkDate);

        model.addAttribute("checkDate", checkDate);
        model.addAttribute("details", details);

        return "pages/warehouse/warehouse_check_detail";
    }

    @GetMapping("/check/create")
    public String checkCreate(Model model) {
        Long branchId = 1L;
        List<InventoryMedicineVM> medicines =
            inventoryService.getInventoryMedicinesByBranch(branchId);

        model.addAttribute("medicines", medicines);
        model.addAttribute("branchId", branchId);

        return "pages/warehouse/warehouse_check_create";
    }

    @PostMapping("/check/submit")
    @ResponseBody
    public ResponseEntity<?> submitInventoryCheck(
            @org.springframework.web.bind.annotation.RequestBody InventoryCheckRequestDTO request,
            @org.springframework.security.core.annotation.AuthenticationPrincipal vn.edu.fpt.pharma.config.CustomUserDetails userDetails
    ) {
        try {
            Long branchId = 1L;
            Long userId = userDetails.getUser().getId(); // ⭐ Get from authenticated user

            // Kiểm tra shortage
            List<Map<String, Object>> shortageItems = new ArrayList<>();
            for (var item : request.getItems()) {
                Inventory inv = inventoryService.findById(item.getInventoryId());
                if (inv != null) {
                    Long systemQty = inv.getQuantity() != null ? inv.getQuantity() : 0L;
                    Long countedQty = item.getCountedQuantity() != null ? item.getCountedQuantity() : 0L;

                    if (countedQty < systemQty) {
                        Map<String, Object> shortage = new HashMap<>();
                        shortage.put("inventoryId", item.getInventoryId());
                        shortage.put("variantId", item.getVariantId());
                        shortage.put("shortage", systemQty - countedQty);
                        shortageItems.add(shortage);
                    }
                }
            }

            stockAdjustmentService.performInventoryCheck(branchId, userId, request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("hasShortage", !shortageItems.isEmpty());
            response.put("shortageItems", shortageItems);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

//    @GetMapping("/request/detail")
//    public String requestDetail(Model model) {
//
//        return "pages/warehouse/request_detail";
//    }

    // -------------------- CATEGORY MANAGEMENT --------------------
    @GetMapping("/category")
    public String categoryList(Model model) {
        return "pages/warehouse/category_list";
    }

    // -------------------- PRICE MANAGEMENT --------------------
    @GetMapping("/price")
    public String priceList(Model model) {
        return "pages/warehouse/price_list";
    }

    // -------------------- DISPOSAL MANAGEMENT --------------------
    @GetMapping("/disposal/create")
    public String disposalCreate(Model model) {
        // Load medicines from warehouse (branch_id = 1)
        List<InventoryMedicineVM> medicines = inventoryService.getInventoryMedicinesByBranch(1L);
        model.addAttribute("medicines", medicines);
        return "pages/warehouse/medicine_disposal";
    }

    @PostMapping("/disposal/create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createDisposal(
            @org.springframework.web.bind.annotation.RequestBody vn.edu.fpt.pharma.dto.warehouse.DisposalRequestDTO request
    ) {
        try {
            // Create disposal movement
            Long movementId = inventoryMovementService.createDisposalMovement(request);
            String code = "DISPOSAL-" + String.format("%06d", movementId);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Tạo phiếu xuất hủy thành công",
                "code", code,
                "movementId", movementId
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/disposal/success")
    public String disposalSuccess(@RequestParam String code, Model model) {
        model.addAttribute("code", code);
        return "pages/warehouse/disposal_success";
    }

    // -------------------- MEDICINE MANAGEMENT --------------------
    @GetMapping("/medicine")
    public String medicineList(Model model) {
        return "pages/warehouse/medicine_list";
    }

}
