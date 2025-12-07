package vn.edu.fpt.pharma.testbuilder;

import vn.edu.fpt.pharma.dto.manager.UserDto;
import vn.edu.fpt.pharma.dto.manager.UserRequest;
import vn.edu.fpt.pharma.entity.Role;
import vn.edu.fpt.pharma.entity.User;

/**
 * Test data builder for User entities and DTOs
 */
public class UserTestBuilder {

    private Long id;
    private String userName = "testuser";
    private String fullName = "Test User";
    private String email = "test@example.com";
    private String phoneNumber = "0123456789";
    private String password = "password123";
    private Long roleId = 4L; // Default: Pharmacist
    private Long branchId = 1L;
    private boolean deleted = false;

    public static UserTestBuilder create() {
        return new UserTestBuilder();
    }

    public static UserTestBuilder defaultUser() {
        return new UserTestBuilder();
    }

    public UserTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public UserTestBuilder withUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public UserTestBuilder withFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public UserTestBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public UserTestBuilder withPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public UserTestBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public UserTestBuilder withRoleId(Long roleId) {
        this.roleId = roleId;
        return this;
    }

    public UserTestBuilder withBranchId(Long branchId) {
        this.branchId = branchId;
        return this;
    }

    public UserTestBuilder withDeleted(boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    public UserTestBuilder asManager() {
        this.roleId = 3L;
        return this;
    }

    public UserTestBuilder asPharmacist() {
        this.roleId = 4L;
        return this;
    }

    public UserRequest buildRequest() {
        UserRequest request = new UserRequest();
        request.setUserName(userName);
        request.setFullName(fullName);
        request.setEmail(email);
        request.setPhoneNumber(phoneNumber);
        request.setPassword(password);
        request.setRoleId(roleId);
        request.setBranchId(branchId);
        return request;
    }

    public User buildEntity() {
        User user = new User();
        user.setId(id);
        user.setUserName(userName);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setPassword(password);
        user.setBranchId(branchId);
        user.setDeleted(deleted);

        Role role = new Role();
        role.setId(roleId);
        role.setName(roleId == 3L ? "MANAGER" : "PHARMACIST");
        user.setRole(role);

        return user;
    }

    public UserDto buildDto() {
        return UserDto.builder()
                .id(id)
                .userName(userName)
                .fullName(fullName)
                .email(email)
                .phoneNumber(phoneNumber)
                .roleName(roleId == 3L ? "MANAGER" : "PHARMACIST")
                .branchId(branchId)
                .deleted(deleted)
                .build();
    }
}

