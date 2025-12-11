package vn.edu.fpt.pharma.integration.manager;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.entity.Role;
import vn.edu.fpt.pharma.entity.User;

import java.util.Collections;
import java.util.List;

/**
 * Factory for creating a SecurityContext with CustomUserDetails for Manager tests.
 */
public class WithMockManagerUserSecurityContextFactory implements WithSecurityContextFactory<WithMockManagerUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockManagerUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // Create Role entity with proper name
        Role role = new Role();
        role.setId(annotation.roleId());
        role.setName("ROLE_" + annotation.role());

        // Create User entity with ALL required fields
        User user = new User();
        user.setId(annotation.userId());
        user.setUserName(annotation.username());
        user.setFullName(annotation.fullName());
        user.setBranchId(annotation.branchId());
        user.setRole(role);
        user.setPassword("test_password"); // Set password to avoid NPE
        user.setDeleted(false); // Ensure not deleted

        // Create CustomUserDetails
        CustomUserDetails principal = new CustomUserDetails(user);

        // Use CustomUserDetails.getAuthorities() instead of creating separate list
        // This ensures the authorities come from the actual implementation
        Authentication auth = new UsernamePasswordAuthenticationToken(
                principal,
                "password",
                principal.getAuthorities() // Use the authorities from CustomUserDetails
        );

        context.setAuthentication(auth);
        return context;
    }
}
