package vn.edu.fpt.pharma.controller.manager;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "pages/manager/dashboard";
    }

    @GetMapping("/staff")
    public String staffPage() {
        return "pages/manager/staff";
    }

    @GetMapping("/report")
    public String reportsPage() {
        return "pages/manager/report";
    }

    @GetMapping("/shift")
    public String shiftPage() {
        return "pages/manager/shift";
    }

    @GetMapping("/sale")
    public String saleReportPage() {
        return "pages/manager/sale";
    }
}

