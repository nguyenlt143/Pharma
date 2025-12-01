package vn.edu.fpt.pharma.testutil;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.entity.Role;
import vn.edu.fpt.pharma.entity.User;

import java.util.Collections;

/**
 * Helper class for mocking authentication and user details in tests
 */
public class MockUserDetailsHelper {

    /**
     * Create a CustomUserDetails for a manager with given branchId
     */
    public static CustomUserDetails createManagerUserDetails(Long userId, Long branchId) {
        Role role = TestDataBuilder.aManagerRole().build();
        User user = TestDataBuilder.aManager()
                .branchId(branchId)
                .role(role)
                .build();
        return new CustomUserDetails(user);
    }

    /**
     * Create a CustomUserDetails for a staff member
     */
    public static CustomUserDetails createStaffUserDetails(Long userId, Long branchId) {
        Role role = TestDataBuilder.aRole().build();
        User user = TestDataBuilder.aStaff()
                .branchId(branchId)
                .role(role)
                .build();
        return new CustomUserDetails(user);
    }

    /**
     * Create a CustomUserDetails for a pharmacist
     */
    public static CustomUserDetails createPharmacistUserDetails(Long userId, Long branchId) {
        Role role = TestDataBuilder.aPharmacistRole().build();
        User user = TestDataBuilder.aPharmacist()
                .branchId(branchId)
                .role(role)
                .build();
        return new CustomUserDetails(user);
    }

    /**
     * Mock authentication in SecurityContext for testing
     */
    public static void mockAuthenticationWithManager(Long userId, Long branchId) {
        CustomUserDetails userDetails = createManagerUserDetails(userId, branchId);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_BRANCH_MANAGER"))
        );
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(auth);
        SecurityContextHolder.setContext(securityContext);
    }

    /**
     * Clear authentication from SecurityContext
     */
    public static void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }
}
