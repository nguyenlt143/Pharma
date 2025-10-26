package vn.edu.fpt.pharma.controller.branchInventory;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("dashboardControllerBranch")
@RequestMapping("/branch-manager")
public class DashboardController {
    public String dashboard(Model model) {
        model.addAttribute("title", "Tổng quan chi nhánh");
        return "branch_manager/dashboard";
    }
    }
