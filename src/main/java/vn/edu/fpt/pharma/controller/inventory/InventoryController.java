package vn.edu.fpt.pharma.controller.inventory;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

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

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/inventory")
public class InventoryController {

    private final DashboardService dashboardService;
    private final RequestFormService requestFormService;
    private final StockAdjustmentService stockAdjustmentService;
    private final InventoryService inventoryService;

    // -------------------- DASHBOARD --------------------
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        var data = dashboardService.getDashboardData();
        model.addAllAttributes(data);
        return "pages/inventory/dashboard";
    }

    // -------------------- IMPORT --------------------
    @GetMapping("/import/list")
    public String importList(
            @RequestParam(required = false) String code,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdAt,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        Long branchId = userDetails.getUser().getBranchId();

        List<RequestFormVM> imports = requestFormService.searchImportForms(branchId, code, createdAt);
        model.addAttribute("imports", imports);

        return "pages/inventory/import_list";
    }


    @GetMapping("/import/create")
    public String importCreate() {
        return "pages/inventory/import_create";
    }

    @GetMapping("/import/detail/{id}")
    public String importDetail(@PathVariable Long id) {
        return "pages/inventory/import_detail";
    }

    @GetMapping("/api/medicines/search")
    @ResponseBody
    public List<vn.edu.fpt.pharma.dto.inventory.MedicineSearchDTO> searchMedicines(
            @RequestParam String query
    ) {
        return inventoryService.searchMedicinesInWarehouse(query);
    }

    @PostMapping("/import/submit")
    @ResponseBody
    public String submitImportRequest(
            @RequestBody vn.edu.fpt.pharma.dto.inventory.ImportRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        // TODO: Implement logic to save import request
        return "success";
    }

    @GetMapping("/import/success/{code}")
    public String importSuccess(@PathVariable String code, Model model) {
        model.addAttribute("code", code);
        return "pages/inventory/import_success";
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
}
