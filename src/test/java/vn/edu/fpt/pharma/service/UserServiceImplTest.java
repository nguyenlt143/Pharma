package vn.edu.fpt.pharma.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.dto.manager.UserDto;
import vn.edu.fpt.pharma.dto.manager.UserRequest;
import vn.edu.fpt.pharma.entity.Role;
import vn.edu.fpt.pharma.entity.User;
import vn.edu.fpt.pharma.repository.BranchRepository;
import vn.edu.fpt.pharma.repository.RoleRepository;
import vn.edu.fpt.pharma.repository.ShiftAssignmentRepository;
import vn.edu.fpt.pharma.repository.UserRepository;
import vn.edu.fpt.pharma.service.impl.UserServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("UserServiceImpl Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private BranchRepository branchRepository;

    @Mock
    private ShiftAssignmentRepository shiftAssignmentRepository;

    @Mock
    private AuditService auditService;

    private UserServiceImpl userService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        // Manually construct - both repository and userRepository point to same mock
        userService = new UserServiceImpl(userRepository, auditService, userRepository, roleRepository, passwordEncoder, shiftAssignmentRepository, branchRepository);

        // Setup default mocks that are commonly used
        lenient().when(branchRepository.findById(anyLong())).thenReturn(Optional.empty());
    }

    @Nested
    @DisplayName("getStaffs Tests")
    class GetStaffsTests {
        @Test
        void getStaffs_returnsAllIncludingDeleted() {
            Long branchId = 1L;
            List<User> users = Arrays.asList(createUser(1L, "john", false), createUser(2L, "jane", true));
            when(userRepository.findStaffInBranchIdIncludingDeleted(branchId)).thenReturn(users);

            List<UserDto> result = userService.getStaffs(branchId);

            assertThat(result).hasSize(2);
            verify(userRepository).findStaffInBranchIdIncludingDeleted(branchId);
        }
    }

    @Nested
    @DisplayName("getStaffsActive Tests")
    class GetStaffsActiveTests {
        @Test
        void getStaffsActive_returnsOnlyActive() {
            Long branchId = 1L;
            List<User> users = Arrays.asList(createUser(1L, "john", false), createUser(2L, "jane", false));
            when(userRepository.findStaffInBranchId(branchId)).thenReturn(users);

            List<UserDto> result = userService.getStaffsActive(branchId);

            assertThat(result).hasSize(2);
            assertThat(result).allMatch(dto -> !dto.isDeleted());
            verify(userRepository).findStaffInBranchId(branchId);
        }
    }

    @Nested
    @DisplayName("getPharmacists Tests")
    class GetPharmacistsTests {
        @Test
        void getPharmacists_returnsOnlyPharmacists() {
            Long branchId = 1L;
            List<User> users = Arrays.asList(createUser(3L, "alice", false), createUser(4L, "bob", false));
            when(userRepository.findPharmacistsInBranchId(branchId)).thenReturn(users);

            List<UserDto> result = userService.getPharmacists(branchId);

            assertThat(result).hasSize(2);
            verify(userRepository).findPharmacistsInBranchId(branchId);
        }
    }

    @Nested
    @DisplayName("getById Tests")
    class GetByIdTests {

        @Test
        void getById_found_returnsUserDto() {
            User user = createUser(1L, "john", false);
            user.setBranchId(1L);
            user.setEmail("john@example.com");
            user.setPhoneNumber("1234567890");
            user.setFullName("John Doe");

            doReturn(Optional.of(user)).when(userRepository).findById(1L);

            UserDto result = userService.getById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getUserName()).isEqualTo("john");
            verify(userRepository).findById(1L);
        }

        @Test
        void getById_notFound_throwsException() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getById(999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Nhân viên không tồn tại");
        }
    }

    @Nested
    @DisplayName("create Tests")
    class CreateTests {
        @Test
        void create_validData_success() {
            UserRequest request = createUserRequest("newuser", "new@example.com", "1234567890", 1L, 1L);
            Role role = createRole(1L, "STAFF");
            User savedUser = createUser(5L, "newuser", false);

            when(userRepository.existsByUserNameIgnoreCase("newuser")).thenReturn(false);
            when(userRepository.existsByEmailIgnoreCase("new@example.com")).thenReturn(false);
            when(userRepository.existsByPhoneNumber("1234567890")).thenReturn(false);
            when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
            when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
            when(userRepository.save(any(User.class))).thenReturn(savedUser);

            UserDto result = userService.create(request);

            assertThat(result).isNotNull();
            assertThat(result.getUserName()).isEqualTo("newuser");
            verify(passwordEncoder).encode(request.getPassword());
            verify(userRepository).save(any(User.class));
        }

        @Test
        void create_duplicateUsername_throwsException() {
            UserRequest request = createUserRequest("duplicate", "test@example.com", "1234567890", 1L, 1L);
            when(userRepository.existsByUserNameIgnoreCase("duplicate")).thenReturn(true);

            assertThatThrownBy(() -> userService.create(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Tên đăng nhập đã tồn tại");

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void create_duplicateEmail_throwsException() {
            UserRequest request = createUserRequest("newuser", "duplicate@example.com", "1234567890", 1L, 1L);
            when(userRepository.existsByUserNameIgnoreCase("newuser")).thenReturn(false);
            when(userRepository.existsByEmailIgnoreCase("duplicate@example.com")).thenReturn(true);

            assertThatThrownBy(() -> userService.create(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Email đã tồn tại");

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void create_roleNotFound_throwsException() {
            UserRequest request = createUserRequest("newuser", "new@example.com", "1234567890", 999L, 1L);
            when(userRepository.existsByUserNameIgnoreCase("newuser")).thenReturn(false);
            when(userRepository.existsByEmailIgnoreCase("new@example.com")).thenReturn(false);
            when(userRepository.existsByPhoneNumber("1234567890")).thenReturn(false);
            when(roleRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.create(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Vai trò không tồn tại");
        }
    }

    @Nested
    @DisplayName("update Tests")
    class UpdateTests {

        @Test
        void update_validData_success() {
            User existingUser = createUser(1L, "john", false);
            existingUser.setBranchId(1L);
            existingUser.setEmail("old@example.com");
            existingUser.setPhoneNumber("1234567890");
            UserRequest request = createUserRequest("john", "newemail@example.com", "9876543210", 1L, 1L);

            doReturn(Optional.of(existingUser)).when(userRepository).findById(1L);
            doReturn(false).when(userRepository).existsByEmailIgnoreCaseAndIdNot("newemail@example.com", 1L);
            doReturn(false).when(userRepository).existsByPhoneNumberAndIdNot("9876543210", 1L);
            doReturn("encoded_new_password").when(passwordEncoder).encode(anyString());
            doAnswer(invocation -> invocation.getArgument(0)).when(userRepository).save(any(User.class));

            UserDto result = userService.update(1L, request);

            assertThat(result).isNotNull();
            verify(userRepository).save(any(User.class));
        }

        @Test
        void update_duplicateUsername_throwsException() {
            User existingUser = createUser(1L, "john", false);
            existingUser.setBranchId(1L);
            existingUser.setEmail("john@example.com");
            existingUser.setPhoneNumber("1234567890");
            UserRequest request = createUserRequest("jane", "john@example.com", "1234567890", 1L, 1L);

            doReturn(Optional.of(existingUser)).when(userRepository).findById(1L);
            doReturn(true).when(userRepository).existsByUserNameIgnoreCase("jane");

            assertThatThrownBy(() -> userService.update(1L, request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Tên đăng nhập đã tồn tại");

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void update_preservesBranchId() {
            User existingUser = createUser(1L, "john", false);
            existingUser.setBranchId(1L);
            existingUser.setEmail("john@example.com");
            existingUser.setPhoneNumber("1234567890");
            UserRequest request = createUserRequest("john", "john@example.com", "1234567890", 1L, 2L); // Try to change branchId to 2L

            doReturn(Optional.of(existingUser)).when(userRepository).findById(1L);
            doReturn(false).when(userRepository).existsByEmailIgnoreCaseAndIdNot(anyString(), anyLong());
            doReturn(false).when(userRepository).existsByPhoneNumberAndIdNot(anyString(), anyLong());
            doAnswer(invocation -> invocation.getArgument(0)).when(userRepository).save(any(User.class));

            userService.update(1L, request);

            // Should preserve original branchId (1L), not use request's branchId (2L)
            verify(userRepository).save(argThat(user -> user.getBranchId().equals(1L)));
        }
    }

    @Nested
    @DisplayName("delete Tests")
    class DeleteTests {
        @Test
        void delete_noActiveAssignment_success() {
            when(shiftAssignmentRepository.existsByUserIdAndDeletedFalse(1L)).thenReturn(false);
            doNothing().when(userRepository).deleteById(1L);

            userService.delete(1L);

            verify(shiftAssignmentRepository).existsByUserIdAndDeletedFalse(1L);
            verify(userRepository).deleteById(1L);
        }

        @Test
        void delete_hasActiveAssignment_throwsException() {
            when(shiftAssignmentRepository.existsByUserIdAndDeletedFalse(1L)).thenReturn(true);

            assertThatThrownBy(() -> userService.delete(1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Nhân viên đang trong một ca làm việc, không thể xóa");

            verify(userRepository, never()).deleteById(anyLong());
        }
    }

    @Nested
    @DisplayName("restore Tests")
    class RestoreTests {
        @Test
        void restore_success() {
            doNothing().when(userRepository).restoreById(1L);

            userService.restore(1L);

            verify(userRepository).restoreById(1L);
        }
    }

    @Nested
    @DisplayName("loadUserByUsername Tests")
    class LoadUserByUsernameTests {
        @Test
        void loadUserByUsername_found_returnsCustomUserDetails() {
            User user = createUser(1L, "john", false);
            when(userRepository.findByUserNameIgnoreCase("john")).thenReturn(Optional.of(user));

            CustomUserDetails result = (CustomUserDetails) userService.loadUserByUsername("john");

            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo("john");
            verify(userRepository).findByUserNameIgnoreCase("john");
        }

        @Test
        void loadUserByUsername_caseInsensitive_success() {
            User user = createUser(1L, "john", false);
            when(userRepository.findByUserNameIgnoreCase("JOHN")).thenReturn(Optional.of(user));

            CustomUserDetails result = (CustomUserDetails) userService.loadUserByUsername("JOHN");

            assertThat(result).isNotNull();
            verify(userRepository).findByUserNameIgnoreCase("JOHN");
        }

        @Test
        void loadUserByUsername_notFound_throwsException() {
            when(userRepository.findByUserNameIgnoreCase("nonexistent")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.loadUserByUsername("nonexistent"))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessageContaining("User not found with username: nonexistent");
        }

        @Test
        void loadUserByUsername_noRole_throwsException() {
            User user = createUser(1L, "john", false);
            user.setRole(null);
            when(userRepository.findByUserNameIgnoreCase("john")).thenReturn(Optional.of(user));

            assertThatThrownBy(() -> userService.loadUserByUsername("john"))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessageContaining("does not have a role assigned");
        }
    }

    // Helper methods
    private User createUser(Long id, String username, boolean deleted) {
        User user = new User();
        user.setId(id);
        user.setUserName(username);
        user.setDeleted(deleted);
        user.setRole(createRole(1L, "STAFF"));
        return user;
    }

    private Role createRole(Long id, String name) {
        Role role = new Role();
        role.setId(id);
        role.setName(name);
        return role;
    }

    private UserRequest createUserRequest(String username, String email, String phone, Long roleId, Long branchId) {
        UserRequest request = new UserRequest();
        request.setUserName(username);
        request.setEmail(email);
        request.setPhoneNumber(phone);
        request.setRoleId(roleId);
        request.setBranchId(branchId);
        request.setPassword("password");
        return request;
    }
}

