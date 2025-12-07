package vn.edu.fpt.pharma.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vn.edu.fpt.pharma.dto.user.ProfileUpdateRequest;
import vn.edu.fpt.pharma.entity.User;
import vn.edu.fpt.pharma.service.impl.UserServiceImpl;
import vn.edu.fpt.pharma.testutil.BaseServiceTest;
import vn.edu.fpt.pharma.testutil.UserTestDataBuilder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService.updateProfile() method
 * Tests validation logic, business rules, and error handling
 */
class UserServiceTest extends BaseServiceTest {

    private UserServiceImpl userService;

    @BeforeEach
    void setUpService() {
        // Manual injection to ensure all dependencies are provided
        userService = new UserServiceImpl(
            userRepository,      // repository (BaseService)
            auditService,        // auditService (BaseService)
            userRepository,      // userRepository (duplicate parameter)
            roleRepository,      // roleRepository
            passwordEncoder,     // passwordEncoder
            shiftAssignmentRepository,  // shiftAssignmentRepository
            branchRepository     // branchRepository
        );
    }

    // ========================================
    // HAPPY PATH TESTS (5 tests)
    // ========================================

    @Test
    void updateProfile_withValidData_shouldUpdateSuccessfully() {
        // Arrange
        Long userId = 1L;
        User existingUser = UserTestDataBuilder.aUser()
            .withId(userId)
            .withEmail("old@example.com")
            .withPhone("0123456789")
            .build();

        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
            .fullName("New Name")
            .email("new@example.com")
            .phone("0987654321")
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailIgnoreCaseAndIdNot(request.getEmail(), userId))
            .thenReturn(false);
        when(userRepository.existsByPhoneNumberAndIdNot(request.getPhone(), userId))
            .thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // Act
        userService.updateProfile(userId, request);

        // Assert
        verify(userRepository).save(argThat(user ->
            user.getFullName().equals("New Name") &&
            user.getEmail().equals("new@example.com") &&
            user.getPhoneNumber().equals("0987654321")
        ));
    }

    @Test
    void updateProfile_withOnlyNameChange_shouldNotUpdatePassword() {
        // Arrange
        Long userId = 1L;
        User existingUser = UserTestDataBuilder.aUser()
            .withId(userId)
            .withPassword("encodedOldPassword")
            .build();

        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
            .fullName("New Name")
            .email(existingUser.getEmail())
            .phone(existingUser.getPhoneNumber())
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailIgnoreCaseAndIdNot(anyString(), anyLong()))
            .thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // Act
        userService.updateProfile(userId, request);

        // Assert
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository).save(argThat(user ->
            user.getPassword().equals("encodedOldPassword")
        ));
    }

    @Test
    void updateProfile_withPasswordChange_shouldEncodeAndUpdate() {
        // Arrange
        Long userId = 1L;
        String oldPassword = "oldPass123";
        String newPassword = "newPass456";
        String encodedOld = "encodedOldPassword";
        String encodedNew = "encodedNewPassword";

        User existingUser = UserTestDataBuilder.aUser()
            .withId(userId)
            .withPassword(encodedOld)
            .build();

        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
            .fullName(existingUser.getFullName())
            .email(existingUser.getEmail())
            .phone(existingUser.getPhoneNumber())
            .currentPassword(oldPassword)
            .password(newPassword)
            .confirmPassword(newPassword)
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailIgnoreCaseAndIdNot(anyString(), anyLong()))
            .thenReturn(false);
        when(passwordEncoder.matches(oldPassword, encodedOld)).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedNew);
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // Act
        userService.updateProfile(userId, request);

        // Assert
        verify(passwordEncoder).matches(oldPassword, encodedOld);
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(argThat(user ->
            user.getPassword().equals(encodedNew)
        ));
    }

    @Test
    void updateProfile_withEmptyPhone_shouldAcceptAndStoreEmpty() {
        // Arrange
        Long userId = 1L;
        User existingUser = UserTestDataBuilder.aUser()
            .withId(userId)
            .withPhone("0123456789")
            .build();

        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
            .fullName("Test User")
            .email("test@example.com")
            .phone("") // Empty phone
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailIgnoreCaseAndIdNot(anyString(), anyLong()))
            .thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // Act
        userService.updateProfile(userId, request);

        // Assert
        verify(userRepository).save(argThat(user ->
            user.getPhoneNumber() != null && user.getPhoneNumber().isEmpty()
        ));
    }

    @Test
    void updateProfile_withAvatarData_shouldUpdateImageUrl() {
        // Arrange
        Long userId = 1L;
        String base64Avatar = "data:image/png;base64,iVBORw0KGgoAAAANS";

        User existingUser = UserTestDataBuilder.aUser()
            .withId(userId)
            .withImageUrl(null)
            .build();

        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
            .fullName("Test User")
            .email("test@example.com")
            .avatarData(base64Avatar)
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailIgnoreCaseAndIdNot(anyString(), anyLong()))
            .thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // Act
        userService.updateProfile(userId, request);

        // Assert
        verify(userRepository).save(argThat(user ->
            user.getImageUrl() != null && user.getImageUrl().equals(base64Avatar)
        ));
    }

    // ========================================
    // BOUNDARY CASES (5 tests)
    // ========================================

    @Test
    void updateProfile_withMinLengthName_shouldAccept() {
        // Arrange
        Long userId = 1L;
        String minLengthName = "Name12"; // 6 characters

        User existingUser = UserTestDataBuilder.aUser()
            .withId(userId)
            .build();

        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
            .fullName(minLengthName)
            .email("test@example.com")
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailIgnoreCaseAndIdNot(anyString(), anyLong()))
            .thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // Act
        userService.updateProfile(userId, request);

        // Assert
        verify(userRepository).save(argThat(user ->
            user.getFullName().equals(minLengthName)
        ));
    }

    @Test
    void updateProfile_withMaxLengthName_shouldAccept() {
        // Arrange
        Long userId = 1L;
        String maxLengthName = "A".repeat(100); // 100 characters

        User existingUser = UserTestDataBuilder.aUser()
            .withId(userId)
            .build();

        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
            .fullName(maxLengthName)
            .email("test@example.com")
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailIgnoreCaseAndIdNot(anyString(), anyLong()))
            .thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // Act
        userService.updateProfile(userId, request);

        // Assert
        verify(userRepository).save(argThat(user ->
            user.getFullName().equals(maxLengthName)
        ));
    }

    @Test
    void updateProfile_withMinPasswordLength_shouldAccept() {
        // Arrange
        Long userId = 1L;
        String minPassword = "pass12"; // 6 characters
        String encodedPassword = "encoded_pass12";

        User existingUser = UserTestDataBuilder.aUser()
            .withId(userId)
            .withPassword("oldEncoded")
            .build();

        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
            .fullName("Test")
            .email("test@example.com")
            .currentPassword("oldPass")
            .password(minPassword)
            .confirmPassword(minPassword)
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailIgnoreCaseAndIdNot(anyString(), anyLong()))
            .thenReturn(false);
        when(passwordEncoder.matches("oldPass", "oldEncoded")).thenReturn(true);
        when(passwordEncoder.encode(minPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // Act
        userService.updateProfile(userId, request);

        // Assert
        verify(passwordEncoder).encode(minPassword);
    }

    @Test
    void updateProfile_withMaxPasswordLength_shouldAccept() {
        // Arrange
        Long userId = 1L;
        String maxPassword = "p".repeat(100); // 100 characters
        String encodedPassword = "encoded_max";

        User existingUser = UserTestDataBuilder.aUser()
            .withId(userId)
            .withPassword("oldEncoded")
            .build();

        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
            .fullName("Test")
            .email("test@example.com")
            .currentPassword("oldPass")
            .password(maxPassword)
            .confirmPassword(maxPassword)
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailIgnoreCaseAndIdNot(anyString(), anyLong()))
            .thenReturn(false);
        when(passwordEncoder.matches("oldPass", "oldEncoded")).thenReturn(true);
        when(passwordEncoder.encode(maxPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // Act
        userService.updateProfile(userId, request);

        // Assert
        verify(passwordEncoder).encode(maxPassword);
    }

    @Test
    void updateProfile_withExactlyMaxPhoneLength_shouldAccept() {
        // Arrange
        Long userId = 1L;
        String maxPhone = "+84123456789"; // 11 digits with +84

        User existingUser = UserTestDataBuilder.aUser()
            .withId(userId)
            .build();

        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
            .fullName("Test")
            .email("test@example.com")
            .phone(maxPhone)
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailIgnoreCaseAndIdNot(anyString(), anyLong()))
            .thenReturn(false);
        when(userRepository.existsByPhoneNumberAndIdNot(maxPhone, userId))
            .thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // Act
        userService.updateProfile(userId, request);

        // Assert
        verify(userRepository).save(argThat(user ->
            user.getPhoneNumber().equals(maxPhone)
        ));
    }

    // ========================================
    // ABNORMAL/ERROR CASES (10 tests)
    // ========================================

    @Test
    void updateProfile_withNullUserId_shouldThrowException() {
        // Arrange
        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
            .fullName("Test")
            .email("test@example.com")
            .build();

        when(userRepository.findById(null)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.updateProfile(null, request))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("User not found");
    }

    @Test
    void updateProfile_whenUserNotFound_shouldThrowException() {
        // Arrange
        Long userId = 999L;
        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
            .fullName("Test")
            .email("test@example.com")
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.updateProfile(userId, request))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("User not found");
    }

    @Test
    void updateProfile_withDuplicateEmail_shouldThrowException() {
        // Arrange
        Long userId = 1L;
        User existingUser = UserTestDataBuilder.aUser().withId(userId).build();

        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
            .fullName("Test")
            .email("duplicate@example.com")
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailIgnoreCaseAndIdNot(
            "duplicate@example.com", userId)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.updateProfile(userId, request))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Email");
    }

    @Test
    void updateProfile_withDuplicatePhone_shouldThrowException() {
        // Arrange
        Long userId = 1L;
        User existingUser = UserTestDataBuilder.aUser().withId(userId).build();

        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
            .fullName("Test")
            .email("test@example.com")
            .phone("0123456789")
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailIgnoreCaseAndIdNot(anyString(), anyLong()))
            .thenReturn(false);
        when(userRepository.existsByPhoneNumberAndIdNot("0123456789", userId))
            .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.updateProfile(userId, request))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("điện thoại");
    }

    @Test
    void updateProfile_withWrongCurrentPassword_shouldThrowException() {
        // Arrange
        Long userId = 1L;
        String wrongPassword = "wrongPass";
        String newPassword = "newPass456";
        String encodedOld = "encodedOldPassword";

        User existingUser = UserTestDataBuilder.aUser()
            .withId(userId)
            .withPassword(encodedOld)
            .build();

        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
            .fullName("Test")
            .email("test@example.com")
            .currentPassword(wrongPassword)
            .password(newPassword)
            .confirmPassword(newPassword)
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        // Note: No need to mock existsByEmailIgnoreCaseAndIdNot because
        // password verification fails first and throws exception immediately
        when(passwordEncoder.matches(wrongPassword, encodedOld)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> userService.updateProfile(userId, request))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Mật khẩu hiện tại không đúng");
    }

    @Test
    void updateProfile_withNullPassword_shouldNotChangePassword() {
        // Arrange
        Long userId = 1L;
        String originalPassword = "originalEncoded";

        User existingUser = UserTestDataBuilder.aUser()
            .withId(userId)
            .withPassword(originalPassword)
            .build();

        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
            .fullName("Test")
            .email("test@example.com")
            .password(null) // Null password
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailIgnoreCaseAndIdNot(anyString(), anyLong()))
            .thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // Act
        userService.updateProfile(userId, request);

        // Assert
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository).save(argThat(user ->
            user.getPassword().equals(originalPassword)
        ));
    }

    @Test
    void updateProfile_withEmptyPassword_shouldNotChangePassword() {
        // Arrange
        Long userId = 1L;
        String originalPassword = "originalEncoded";

        User existingUser = UserTestDataBuilder.aUser()
            .withId(userId)
            .withPassword(originalPassword)
            .build();

        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
            .fullName("Test")
            .email("test@example.com")
            .password("") // Empty password
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailIgnoreCaseAndIdNot(anyString(), anyLong()))
            .thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // Act
        userService.updateProfile(userId, request);

        // Assert
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void updateProfile_withBlankPassword_shouldNotChangePassword() {
        // Arrange
        Long userId = 1L;
        String originalPassword = "originalEncoded";

        User existingUser = UserTestDataBuilder.aUser()
            .withId(userId)
            .withPassword(originalPassword)
            .build();

        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
            .fullName("Test")
            .email("test@example.com")
            .password("   ") // Blank password
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailIgnoreCaseAndIdNot(anyString(), anyLong()))
            .thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // Act
        userService.updateProfile(userId, request);

        // Assert
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void updateProfile_whenRepositorySaveFails_shouldThrowException() {
        // Arrange
        Long userId = 1L;
        User existingUser = UserTestDataBuilder.aUser().withId(userId).build();
        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
            .fullName("Test")
            .email("test@example.com")
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailIgnoreCaseAndIdNot(anyString(), anyLong()))
            .thenReturn(false);
        when(userRepository.save(any(User.class)))
            .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThatThrownBy(() -> userService.updateProfile(userId, request))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Database error");

        // Verify save was attempted
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateProfile_whenPasswordEncoderFails_shouldThrowException() {
        // Arrange
        Long userId = 1L;
        User existingUser = UserTestDataBuilder.aUser()
            .withId(userId)
            .withPassword("oldEncoded")
            .build();

        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
            .fullName("Test")
            .email("test@example.com")
            .currentPassword("oldPass")
            .password("newPass")
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailIgnoreCaseAndIdNot(anyString(), anyLong()))
            .thenReturn(false);
        when(passwordEncoder.matches("oldPass", "oldEncoded")).thenReturn(true);
        when(passwordEncoder.encode("newPass"))
            .thenThrow(new RuntimeException("Encoding failed"));

        // Act & Assert
        assertThatThrownBy(() -> userService.updateProfile(userId, request))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Encoding failed");
    }
}

