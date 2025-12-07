package vn.edu.fpt.pharma.dto.user;

import vn.edu.fpt.pharma.entity.User;

public record ProfileVM(
        String fullName,
        String phone,
        String email,
        String password,
        String confirmPassword,
        String role,
        String avatarUrl
) {
    public ProfileVM(User user) {
        this(
                user.getFullName(),
                user.getPhoneNumber(),
                user.getEmail(),
                "",
                "",
                user.getRole() != null ? user.getRole().getName() : null,
                user.getImageUrl()
        );
    }
}
