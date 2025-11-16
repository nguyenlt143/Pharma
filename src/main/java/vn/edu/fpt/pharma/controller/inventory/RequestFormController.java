package vn.edu.fpt.pharma.controller.inventory;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import vn.edu.fpt.pharma.entity.User;
import vn.edu.fpt.pharma.service.RequestFormService;

@Controller
@RequiredArgsConstructor
public class RequestFormController {

    private final RequestFormService requestFormService;

    @GetMapping("/inventory/import_list")
    public String getRequestForms(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        Long branchId = user.getBranchId();

        model.addAttribute("requestForms", requestFormService.getRequestFormsByBranch(branchId));
        return "inventory/import_list";
    }
}
