package vn.edu.fpt.pharma.controller.inventory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.pharma.service.DashboardService;
import vn.edu.fpt.pharma.service.RequestFormService;
import vn.edu.fpt.pharma.dto.requestform.RequestFormVM;

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
    public String importList(Model model) {
        Long branchId = 2L;
        List<RequestFormVM> requests = requestFormService.getRequestFormsByBranch(branchId);
        model.addAttribute("requestForms", requestFormService.getRequestFormsByBranch(branchId));
        return "pages/inventory/import_list";
    }

    //@GetMapping("/import/detail/{id}")
    //public String importDetail(@PathVariable Long id, Model model) {
        //RequestFormVM form = requestFormService.getDetailById(id);
        //model.addAttribute("requestForm", form);
        //return "pages/inventory/import_detail";
    //}

    // -------------------- EXPORT --------------------
    //@GetMapping("/export/list")
    //public String exportList(Model model) {
        //Long branchId = 2L;
        //List<RequestFormVM> exports = requestFormService.getExportRequestsByBranch(branchId);
        //model.addAttribute("requestForms", exports);
        //return "pages/inventory/export_list";
    //}

    //@GetMapping("/export/detail/{id}")
    //public String exportDetail(@PathVariable Long id, Model model) {
        //RequestFormVM form = requestFormService.getDetailById(id);
        //model.addAttribute("requestForm", form);
        //return "pages/inventory/export_detail";
   // }

    // -------------------- CHECK INVENTORY --------------------
    @GetMapping("/check")
    public String checkList(Model model) {
        return "pages/inventory/check_list";
    }

    // -------------------- REPORT --------------------
    @GetMapping("/report")
    public String reportPage(Model model) {
        return "pages/inventory/report_overview";
    }
}
