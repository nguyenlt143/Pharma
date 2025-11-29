package vn.edu.fpt.pharma.controller.inventory;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.dto.inventorycheck.InventoryCheckHistoryVM;
import vn.edu.fpt.pharma.dto.inventorycheck.StockAdjustmentDetailVM;
import vn.edu.fpt.pharma.dto.requestform.RequestFormVM;
import vn.edu.fpt.pharma.dto.inventory.InventoryMedicineVM;
import vn.edu.fpt.pharma.dto.inventory.InventoryCheckRequestDTO;
import vn.edu.fpt.pharma.service.DashboardService;
import vn.edu.fpt.pharma.service.RequestFormService;
import vn.edu.fpt.pharma.service.StockAdjustmentService;
import vn.edu.fpt.pharma.service.InventoryService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/inventory")
public class InventoryController {

    private final DashboardService dashboardService;
    private final RequestFormService requestFormService;
    private final StockAdjustmentService stockAdjustmentService;
    private final InventoryService inventoryService;
    private final vn.edu.fpt.pharma.service.InventoryMovementService inventoryMovementService;

    // -------------------- DASHBOARD --------------------
    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long branchId = userDetails.getUser().getBranchId();
        var data = dashboardService.getDashboardData(branchId);
        model.addAllAttributes(data);
        return "pages/inventory/dashboard";
    }

    // -------------------- IMPORT --------------------
    @GetMapping("/import/list")
    public String importList(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String q,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdAt,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        Long branchId = userDetails.getUser().getBranchId();

        List<RequestFormVM> imports = requestFormService.searchImportForms(branchId, code, createdAt);
        // apply optional text filter 'q' against code and note
        if (q != null && !q.trim().isEmpty()) {
            String term = q.trim().toLowerCase();
            imports = imports.stream()
                    .filter(it -> (it.getCode() != null && it.getCode().toLowerCase().contains(term))
                               || (it.getNote() != null && it.getNote().toLowerCase().contains(term)))
                    .toList();
        }
        model.addAttribute("imports", imports);
        model.addAttribute("q", q);

        return "pages/inventory/import_list";
    }


    @GetMapping("/import/create")
    public String importCreate(Model model) {
        // Load medicines from warehouse (branch_id = 1)
        List<InventoryMedicineVM> medicines = inventoryService.getInventoryMedicinesByBranch(1L);
        model.addAttribute("medicines", medicines);
        return "pages/inventory/import_create";
    }

    @GetMapping("/import/detail/{id}")
    public String importDetail(@PathVariable Long id, Model model) {
        var request = requestFormService.getDetailById(id);
        var details = requestFormService.getDetailsOfRequest(id);
        model.addAttribute("request", request);
        model.addAttribute("details", details);
        return "pages/inventory/import_detail";
    }

    @GetMapping("/api/medicines/search")
    @ResponseBody
    public List<vn.edu.fpt.pharma.dto.inventory.InventoryMedicineSearchDTO> searchMedicines(
            @RequestParam String query
    ) {
        return inventoryService.searchMedicinesInWarehouse(query);
    }

    @PostMapping("/import/submit")
    @ResponseBody
    public ResponseEntity<?> submitImportRequest(
            @RequestBody vn.edu.fpt.pharma.dto.inventory.ImportRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        try {
            Long branchId = userDetails.getUser().getBranchId();
            String requestCode = requestFormService.createImportRequest(branchId, request);
            return ResponseEntity.ok(java.util.Map.of("success", true, "code", requestCode));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/import/success/{code}")
    public String importSuccess(@PathVariable String code, Model model) {
        model.addAttribute("code", code);
        return "pages/inventory/import_success";
    }

    // -------------------- CONFIRM IMPORT FROM WAREHOUSE --------------------
    @GetMapping("/confirm/list")
    public String confirmImportList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        Long branchId = userDetails.getUser().getBranchId();
        List<vn.edu.fpt.pharma.dto.inventory.ConfirmImportVM> confirmImports =
            inventoryMovementService.getConfirmImportList(branchId);
        model.addAttribute("confirmImports", confirmImports);
        return "pages/inventory/confirm_import_list";
    }

    @GetMapping("/confirm/detail/{id}")
    public String confirmImportDetail(@PathVariable Long id, Model model) {
        var confirmImport = inventoryMovementService.getConfirmImportDetail(id);
        var details = ((vn.edu.fpt.pharma.service.impl.InventoryMovementServiceImpl) inventoryMovementService)
                .getReceiptDetailsForBranch(id);
        model.addAttribute("confirmImport", confirmImport);
        model.addAttribute("details", details);
        return "pages/inventory/confirm_import_detail";
    }

    @PostMapping("/confirm/{id}")
    @ResponseBody
    public ResponseEntity<?> confirmImport(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        try {
            Long branchId = userDetails.getUser().getBranchId();
            inventoryMovementService.confirmImportReceipt(id, branchId);
            return ResponseEntity.ok(java.util.Map.of("success", true, "message", "Đã xác nhận nhập hàng thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("success", false, "message", e.getMessage()));
        }
    }

    // -------------------- EXPORT --------------------
    @GetMapping("/export/list")
    public String exportList(
            @RequestParam(required = false) String code,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdAt,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        Long branchId = userDetails.getUser().getBranchId();

        List<RequestFormVM> exports = requestFormService.searchExportForms(branchId, code, createdAt);
        model.addAttribute("exports", exports);

        return "pages/inventory/export_list";
    }

    @GetMapping("/export/detail/{id}")
    public String exportDetail(@PathVariable Long id) {
        return "pages/inventory/export_detail";
    }


    // -------------------- CHECK INVENTORY --------------------
    @GetMapping("/check")
    public String checkList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        Long branchId = userDetails.getUser().getBranchId();

        List<InventoryCheckHistoryVM> inventoryChecks = stockAdjustmentService.getInventoryCheckHistory(branchId);

        model.addAttribute("inventoryChecks", inventoryChecks);
        model.addAttribute("branchName", "Kho chi nhánh " + branchId);

        return "pages/inventory/check_list";
    }

    @GetMapping("/check/detail")
    public String checkDetail(
            @RequestParam String checkDate,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        Long branchId = userDetails.getUser().getBranchId();

        List<StockAdjustmentDetailVM> details = stockAdjustmentService.getInventoryCheckDetails(branchId, checkDate);

        model.addAttribute("checkDate", checkDate);
        model.addAttribute("details", details);

        return "pages/inventory/inventory_check_detail";
    }

    @GetMapping("/check/create")
    public String checkCreate(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long branchId = userDetails.getUser().getBranchId();
        List<InventoryMedicineVM> medicines = inventoryService.getInventoryMedicinesByBranch(branchId);
        model.addAttribute("medicines", medicines);
        model.addAttribute("branchId", branchId);
        return "pages/inventory/inventory_check_create";
    }

    @PostMapping("/check/submit")
    @ResponseBody
    public ResponseEntity<?> submitInventoryCheck(@RequestBody InventoryCheckRequestDTO request,
                                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long branchId = userDetails.getUser().getBranchId();
        Long userId = userDetails.getUser().getId();
        stockAdjustmentService.performInventoryCheck(branchId, userId, request);
        return ResponseEntity.ok().build();
    }

    // -------------------- MEDICINE LIST --------------------
    @GetMapping("/medicine/list")
    public String medicineList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        Long branchId = userDetails.getUser().getBranchId();

        List<InventoryMedicineVM> medicines = inventoryService.getInventoryMedicinesByBranch(branchId);

        model.addAttribute("medicines", medicines);
        model.addAttribute("branchId", branchId);

        return "pages/inventory/medicine_list";
    }

    @PostMapping("/medicine/delete-out-of-stock")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteOutOfStockMedicines(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        try {
            Long branchId = userDetails.getUser().getBranchId();
            int deletedCount = inventoryService.deleteOutOfStockFromBranch(branchId);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đã xóa " + deletedCount + " thuốc hết hàng khỏi kho",
                "deletedCount", deletedCount
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Lỗi: " + e.getMessage()
            ));
        }
    }

    // -------------------- CHECK INVENTORY --------------------
    @GetMapping("/expiring_medicine")
    public String expireMedicine() {
        return "pages/inventory/expiring_medicine";
    }

    // -------------------- REPORT --------------------
    @GetMapping("/report")
    public String reportPage() {
        return "pages/inventory/report_overview";
    }

    // -------------------- RETURN REQUEST LIST/DETAIL --------------------
    @GetMapping("/return/list")
    public String returnRequestList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        Long branchId = userDetails.getUser().getBranchId();
        List<vn.edu.fpt.pharma.dto.inventory.ReturnRequestVM> returnRequests =
            requestFormService.getReturnRequestsForBranch(branchId);
        model.addAttribute("returnRequests", returnRequests);
        return "pages/inventory/return_request_list";
    }

    @GetMapping("/return/create")
    public String returnCreate(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        Long branchId = userDetails.getUser().getBranchId();
        List<InventoryMedicineVM> medicines = inventoryService.getInventoryMedicinesByBranch(branchId);
        model.addAttribute("medicines", medicines);
        model.addAttribute("branchId", branchId);
        return "pages/inventory/return_create";
    }

    @PostMapping("/return/create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createReturn(
            @RequestBody vn.edu.fpt.pharma.dto.inventory.ReturnRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        try {
            Long branchId = userDetails.getUser().getBranchId();
            request.setBranchId(branchId);

            // 1) Create RETURN request form to show in return list
            String requestCode = requestFormService.createReturnRequest(branchId, request);

            // 2) Create movement BR_TO_WARE and link to request via requestId inside service
            Long movementId = inventoryMovementService.createReturnMovement(request);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Tạo phiếu trả hàng thành công",
                "code", requestCode,
                "movement", "#MV" + String.format("%03d", movementId)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/return/success")
    public String returnSuccess(@RequestParam String code, Model model) {
        model.addAttribute("code", code);
        return "pages/inventory/return_success";
    }

    @GetMapping("/return/detail/{id}")
    public String returnDetail(@PathVariable Long id, Model model) {
        var request = requestFormService.getReturnRequestDetail(id);
        var details = requestFormService.getDetailsOfRequest(id);
        model.addAttribute("request", request);
        model.addAttribute("details", details);
        return "pages/inventory/return_request_detail";
    }
}
