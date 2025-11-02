package vn.edu.fpt.pharma.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import vn.edu.fpt.pharma.constant.UserRole;
import vn.edu.fpt.pharma.entity.User;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class CustomUserDetails  implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_" + getRole());
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
        return user.getRole().getName();
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
