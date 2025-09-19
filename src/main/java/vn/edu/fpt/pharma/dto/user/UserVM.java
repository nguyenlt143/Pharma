package vn.edu.fpt.pharma.dto.user;

import vn.edu.fpt.pharma.entity.User;

import java.time.LocalDateTime;

public record UserVM(
        Long id,
        String email,
        String fullName,
        String storeCode,
        String storeName,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String createdByName,
        String updatedByName
) {
    public UserVM(User user, String storeName) {
        this (
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getStoreCode(),
                storeName,
                user.isActive(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getCreatedByName(),
                user.getUpdatedByName()
        );
    }

    public UserVM(User user) {
        this (
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getStoreCode(),
                null,
                user.isActive(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getCreatedByName(),
                user.getUpdatedByName()
        );
    }
}
