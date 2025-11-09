package vn.edu.fpt.pharma.dto.user;

import vn.edu.fpt.pharma.entity.User;

public record ProfileVM(
        String userName,
        String phone,
        String email,
        String password,
        String confirmPassword,
        String role
) {
    public ProfileVM(User user) {
        this(
                user.getUserName(),
                user.getPhoneNumber(),
                user.getEmail(),
                "",
                "",
                user.getRole() != null ? user.getRole().getName() : null
        );
    }
}
