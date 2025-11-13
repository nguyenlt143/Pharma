package vn.edu.fpt.pharma.controller.inventory;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.dto.requestform.RequestFormVM;
import vn.edu.fpt.pharma.service.DashboardService;
import vn.edu.fpt.pharma.service.RequestFormService;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/inventory")
public class InventoryController {

    private final DashboardService dashboardService;
    private final RequestFormService requestFormService;

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
        Long branchId = userDetails.getUser().getBranchId(); // ✅ lấy từ user đang đăng nhập

        List<RequestFormVM> imports = requestFormService.searchRequestForms(branchId, code, createdAt);
        model.addAttribute("imports", imports);

        return "pages/inventory/import_list";
    }


    @GetMapping("/import/detail/{id}")
    public String importDetail(@PathVariable Long id) {
        return "pages/inventory/import_detail";
    }

    // -------------------- EXPORT --------------------
    @GetMapping("/export/list")
    public String exportList() {
        return "pages/inventory/export_list";
    }

    @GetMapping("/export/detail/{id}")
    public String exportDetail(@PathVariable Long id) {
        return "pages/inventory/export_detail";
    }

    // -------------------- CHECK INVENTORY --------------------
    @GetMapping("/check")
    public String checkList() {
        return "pages/inventory/check_list";
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
