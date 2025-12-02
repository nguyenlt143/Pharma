package vn.edu.fpt.pharma.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import vn.edu.fpt.pharma.entity.Role;
import vn.edu.fpt.pharma.entity.User;
import vn.edu.fpt.pharma.testutil.BaseDataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DisplayName("UserRepository Tests")
class UserRepositoryTest extends BaseDataJpaTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void findByUserNameIgnoreCase_existingUser_returnsUser() {
        Role role = createAndSaveRole("STAFF");
        User user = createAndSaveUser("john", "john@test.com", role, 1L);

        Optional<User> found = userRepository.findByUserNameIgnoreCase("john");

        assertThat(found).isPresent();
        assertThat(found.get().getUserName()).isEqualTo("john");
    }

    @Test
    void findByUserNameIgnoreCase_caseInsensitive_returnsUser() {
        Role role = createAndSaveRole("STAFF");
        User user = createAndSaveUser("john", "john@test.com", role, 1L);

        Optional<User> found = userRepository.findByUserNameIgnoreCase("JOHN");

        assertThat(found).isPresent();
        assertThat(found.get().getUserName()).isEqualTo("john");
    }

    @Test
    void existsByUserNameIgnoreCase_existingUser_returnsTrue() {
        Role role = createAndSaveRole("STAFF");
        createAndSaveUser("jane", "jane@test.com", role, 1L);

        boolean exists = userRepository.existsByUserNameIgnoreCase("JANE");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByUserNameIgnoreCase_nonExisting_returnsFalse() {
        boolean exists = userRepository.existsByUserNameIgnoreCase("nonexistent");

        assertThat(exists).isFalse();
    }

    @Test
    void findStaffInBranchId_returnsOnlyActiveStaff() {
        Role role = createAndSaveRoleWithId(4L, "STAFF");
        User user1 = createAndSaveUser("active1", "active1@test.com", role, 1L);
        User user2 = createAndSaveUser("active2", "active2@test.com", role, 1L);
        User deletedUser = createAndSaveUser("deleted", "deleted@test.com", role, 1L);
        userRepository.flush();
        userRepository.deleteById(deletedUser.getId());
        userRepository.flush();

        List<User> staffs = userRepository.findStaffInBranchId(1L);

        assertThat(staffs).hasSize(2);
    }

    @Test
    void findStaffInBranchIdIncludingDeleted_returnsAll() {
        Role role = createAndSaveRoleWithId(4L, "STAFF");
        User user1 = createAndSaveUser("active", "active@test.com", role, 1L);
        User deletedUser = createAndSaveUser("deleted", "deleted@test.com", role, 1L);
        userRepository.deleteById(deletedUser.getId());

        List<User> staffs = userRepository.findStaffInBranchIdIncludingDeleted(1L);

        assertThat(staffs).hasSize(2);
    }

    @Test
    void deleteById_softDeletesUser() {
        Role role = createAndSaveRole("STAFF");
        User user = createAndSaveUser("todelete", "delete@test.com", role, 1L);
        Long userId = user.getId();

        userRepository.deleteById(userId);
        userRepository.flush();

        Optional<User> found = userRepository.findById(userId);
        assertThat(found).isEmpty(); // Soft deleted users not returned by default
    }

    @Test
    void restoreById_restoresDeletedUser() {
        Role role = createAndSaveRole("STAFF");
        User user = createAndSaveUser("restore", "restore@test.com", role, 1L);
        Long userId = user.getId();

        userRepository.deleteById(userId);
        userRepository.flush();
        userRepository.restoreById(userId);
        userRepository.flush();

        Optional<User> found = userRepository.findById(userId);
        assertThat(found).isPresent();
    }

    // Helper methods
    private Role createAndSaveRole(String name) {
        Role role = new Role();
        role.setName(name);
        return roleRepository.save(role);
    }

    private Role createAndSaveRoleWithId(Long id, String name) {
        Role role = new Role();
        role.setId(id);
        role.setName(name);
        return roleRepository.save(role);
    }

    private User createAndSaveUser(String username, String email, Role role, Long branchId) {
        User user = new User();
        user.setUserName(username);
        user.setEmail(email);
        user.setRole(role);
        user.setBranchId(branchId);
        user.setPassword("password");
        return userRepository.save(user);
    }
}
