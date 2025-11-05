package vn.edu.fpt.pharma.controller.branchInventory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import vn.edu.fpt.pharma.service.DashboardServicev2;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardServicev2 dashboardService;

    @GetMapping("/inventory/dashboard")
    public String dashboard(Model model) {
        Map<String, Object> data = dashboardService.getDashboardData();
        model.addAllAttributes(data);
        return "inventory/dashboard";
    }
}
