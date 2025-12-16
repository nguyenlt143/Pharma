package vn.edu.fpt.pharma.controller.manager;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.repository.BranchRepository;
import vn.edu.fpt.pharma.repository.CategoryRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final BranchRepository branchRepository;
    private final CategoryRepository categoryRepository;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "pages/manager/dashboard";
    }

    @GetMapping("/staff")
    public String staffPage() {
        return "pages/manager/staff";
    }

    @GetMapping("/report/revenue")
    public String revenueReportPage() {
        return "pages/manager/revenue";
    }

    @GetMapping("/report/inventory")
    public String inventoryReportPage() {
        return "pages/manager/inventory";
    }

    @GetMapping("/report/import")
    public String inventoryReportImport(
            @RequestParam(required = false, defaultValue = "week") String range,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {

        // Get manager's branch
        String branchName = "Chi nhánh";
        if (userDetails != null && userDetails.getUser() != null && userDetails.getUser().getBranchId() != null) {
            Long branchId = userDetails.getUser().getBranchId();
            branchName = branchRepository.findById(branchId)
                .map(b -> b.getName())
                .orElse("Chi nhánh");
        }

        // Initial values (will be loaded via API)
        model.addAttribute("branchName", branchName);
        model.addAttribute("totalValue", "");
        model.addAttribute("valueDeltaLabel", "");
        model.addAttribute("lowStockCount", 0);
        model.addAttribute("pendingInbound", 0);
        model.addAttribute("pendingOutbound", 0);
        model.addAttribute("range", range);
        model.addAttribute("categories", new ArrayList<>());
        model.addAttribute("recentActivities", new ArrayList<>());

        return "pages/manager/import";
    }

    @GetMapping("/shift")
    public String shiftPage() {
        return "pages/manager/shift";
    }

    @GetMapping("/report/adjustments")
    public String adjustmentsReportPage() {
        return "pages/manager/adjustments";
    }

}

