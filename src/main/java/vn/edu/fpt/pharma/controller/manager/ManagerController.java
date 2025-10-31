package vn.edu.fpt.pharma.controller.manager;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "manager/dashboard.jte";
    }

    @GetMapping("/staff")
    public String staffPage() {
        return "manager/staff.jte";
    }

    @GetMapping("/report")
    public String reportsPage() {
        return "manager/report.jte";
    }

    @GetMapping("/shift")
    public String shiftPage() {
        return "manager/shift.jte";
    }

    @GetMapping("/sale")
    public String saleReportPage() {
        return "manager/sale.jte";
    }
}

