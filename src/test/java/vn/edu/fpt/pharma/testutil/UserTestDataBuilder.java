package vn.edu.fpt.pharma.testutil;

import vn.edu.fpt.pharma.entity.Branch;
import vn.edu.fpt.pharma.entity.Role;
import vn.edu.fpt.pharma.entity.User;

import java.time.LocalDateTime;

/**
 * Test Data Builder for User entity
 * Provides fluent API for creating test User objects
 */
public class UserTestDataBuilder {

    private Long id = 1L;
    private String fullName = "Test User";
    private String email = "test@example.com";
    private String phoneNumber = "0123456789";
    private String password = "encodedPassword";
    private String imageUrl = null;
    private String userName = "testuser";
    private Role role;
    private Long branchId = 1L;
    private Boolean deleted = false;
    private LocalDateTime createdAt = LocalDateTime.now();
    private Long createdBy = 1L;
    private LocalDateTime updatedAt = LocalDateTime.now();
    private Long updatedBy = 1L;

    public static UserTestDataBuilder aUser() {
        return new UserTestDataBuilder();
    }

    public UserTestDataBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public UserTestDataBuilder withFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public UserTestDataBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public UserTestDataBuilder withPhone(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public UserTestDataBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public UserTestDataBuilder withImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public UserTestDataBuilder withUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public UserTestDataBuilder withRole(Role role) {
        this.role = role;
        return this;
    }

    public UserTestDataBuilder withBranch(Long branchId) {
        this.branchId = branchId;
        return this;
    }

    public UserTestDataBuilder withDeleted(Boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    public User build() {
        User user = new User();
        user.setId(id);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setPassword(password);
        user.setImageUrl(imageUrl);
        user.setUserName(userName);
        user.setRole(role);
        user.setBranchId(branchId);
        user.setDeleted(deleted);
        user.setCreatedAt(createdAt);
        user.setCreatedBy(createdBy);
        user.setUpdatedAt(updatedAt);
        user.setUpdatedBy(updatedBy);
        return user;
    }
}

