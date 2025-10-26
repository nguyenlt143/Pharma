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
        if (roles.contains("ROLE_SYSTEM_ADMIN")) {
            response.sendRedirect("/system-admin/dashboard");
        } else if (roles.contains("ROLE_BUSINESS_OWNER")) {
            response.sendRedirect("/owner/dashboard");
        } else if (roles.contains("ROLE_BRANCH_MANAGER")) {
            response.sendRedirect("/branch/manager/dashboard");
        } else if (roles.contains("ROLE_BRANCH_WAREHOUSE")) {
            response.sendRedirect("/branch/warehouse/dashboard");
        } else if (roles.contains("ROLE_GENERAL_WAREHOUSE")) {
            response.sendRedirect("/warehouse/general/dashboard");
        } else if (roles.contains("ROLE_PHARMACIST")) {
            response.sendRedirect("/pharmacist/dashboard");
        } else {
            // fallback nếu không khớp role nào
            response.sendRedirect("/dashboard");
        }
    }
}
