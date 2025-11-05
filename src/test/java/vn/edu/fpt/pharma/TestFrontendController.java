package vn.edu.fpt.pharma;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.Map;

@Controller
public class TestFrontendController {

    @GetMapping("/test/dashboard")
    public String testDashboard(Model model) {
        model.addAllAttributes(Map.of(
                "waitingOrders", 5,
                "lastInventoryCheck", "2025-10-15 14:20",
                "nearlyExpiredCount", 12,
                "lowStockCount", 7
        ));
        return "inventory/dashboard";
    }

    @GetMapping("/test/requests")
    public String testRequestList(Model model) {
        model.addAttribute("requestForms", List.of(
                Map.of("id", 1, "code", "#RQ001", "createdAt", "2025-10-12", "note", "Nhập hàng test", "requestStatus", "RECEIVED"),
                Map.of("id", 2, "code", "#RQ002", "createdAt", "2025-10-13", "note", "Thử nghiệm hiển thị", "requestStatus", "REQUESTED"),
                Map.of("id", 3, "code", "#RQ003", "createdAt", "2025-10-14", "note", "Phiếu hủy", "requestStatus", "CANCELLED")
        ));
        return "inventory/request_list";
    }
}
