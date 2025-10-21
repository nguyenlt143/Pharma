package vn.edu.fpt.pharma.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import vn.edu.fpt.pharma.config.CustomUserDetails;

import java.util.Optional;
import java.util.UUID;

public class SecurityUtils {

    public static Optional<CustomUserDetails> getUserDetail() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .filter(CustomUserDetails.class::isInstance)
                .map(CustomUserDetails.class::cast);
    }

    public static Optional<Long> getUserId() {
        return Optional.of(1L);
    }

    public static Optional<String> getUserRole() {
        return getUserDetail()
                .map(CustomUserDetails::getRole);
    }

    public static String getUserFullName() {
        return getUserDetail()
                .map(CustomUserDetails::getFullName)
                .orElse("Unknown User");
    }
}
