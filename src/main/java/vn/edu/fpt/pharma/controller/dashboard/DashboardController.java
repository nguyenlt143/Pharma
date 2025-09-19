package vn.edu.fpt.pharma.controller.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.pharma.util.SecurityUtils;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    @GetMapping
    public String index(Model model) {
        model.addAttribute("role", SecurityUtils.getUserRole().get());
        return "pages/dashboard/index";
    }
}
