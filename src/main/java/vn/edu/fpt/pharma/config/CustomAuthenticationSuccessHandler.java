package vn.edu.fpt.pharma.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import vn.edu.fpt.pharma.entity.User;
import vn.edu.fpt.pharma.service.UserService;

import java.io.IOException;
import java.util.Set;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

    public CustomAuthenticationSuccessHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        // 👉 Lưu thông tin user vào session
        String username = authentication.getName();
        User userEntity = userService.findByUserName(username);

        HttpSession session = request.getSession();
        session.setAttribute("user", userEntity);

        // 👉 Chuyển hướng theo role
        if (roles.contains("ROLE_ADMIN")) {
            response.sendRedirect("/admin/dashboard");
        } else if (roles.contains("ROLE_OWNER")) {
            response.sendRedirect("/owner/dashboard");
        } else if (roles.contains("ROLE_MANAGER")) {
            response.sendRedirect("/manager/dashboard");
        } else if (roles.contains("ROLE_INVENTORY")) {
            response.sendRedirect("/inventory/dashboard");
        } else if (roles.contains("ROLE_WAREHOUSE")) {
            response.sendRedirect("/warehouse/dashboard");
        } else if (roles.contains("ROLE_PHARMACIST")) {
            response.sendRedirect("/pharmacist/pos");
        } else {
            response.sendRedirect("/dashboard");
        }
    }
}