package vn.edu.fpt.pharma.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException {

        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        // Kiểm tra role và chuyển hướng
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
            // fallback nếu không khớp role nào
            response.sendRedirect("/dashboard");
        }
    }
}
