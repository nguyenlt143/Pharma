package vn.edu.fpt.pharma.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "pages/admin/dashboard";
    }

    @GetMapping("/accounts")
    public String accountsPage() {
        return "pages/admin/accounts";
    }

    @GetMapping("/branches")
    public String branchesPage() {
        return "pages/admin/branches";
    }
}

