package vn.edu.fpt.pharma.controller.branchInventory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import vn.edu.fpt.pharma.service.DashboardService;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/inventory/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("waitingOrders", dashboardService.countWaitingOrders());
        model.addAttribute("lastInventoryCheck", dashboardService.getLastInventoryCheck());
        model.addAttribute("nearlyExpiredCount", dashboardService.countNearlyExpiredMedicines());
        model.addAttribute("lowStockCount", dashboardService.countLowStockItems());
        return "inventory/dashboard";
    }
}
