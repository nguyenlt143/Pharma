package vn.edu.fpt.pharma.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.util.SecurityUtils;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {
        String error = request.getParameter("error");
        String logout = request.getParameter("logout");
        if (error != null) model.addAttribute("error", "Invalid username or password");
        if (logout != null) model.addAttribute("logout", "You have been logged out");
//        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
//        if (csrf != null) {
//            model.addAttribute("csrf", csrf);
//        }
        return "pages/home/login";
    }

    @GetMapping("/me")
    @ResponseBody
    public CustomUserDetails me() {
        return SecurityUtils.getUserDetail().orElse(null);
    }


}
