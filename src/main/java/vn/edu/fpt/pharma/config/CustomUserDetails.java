package vn.edu.fpt.pharma.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import vn.edu.fpt.pharma.entity.User;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class CustomUserDetails  implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = getRole();
        final String authority = role != null && role.startsWith("ROLE_") ? role : ("ROLE_" + role);
        return List.of(() -> authority);
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }

    public String getRole() {
        return user.getRole() != null ? user.getRole().getName() : null;
    }

    public Long getId() {
        return user.getId();
    }
    public User getUser() {
        return user;
    }

    public String getFullName() {
        return user.getFullName();
    }
}
