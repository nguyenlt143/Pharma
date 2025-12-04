package vn.edu.fpt.pharma.dto.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ProfileUpdateRequestValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidProfileUpdateRequest() {
        // Given
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setFullName("Nguyen Van A");
        request.setPhone("0123456789");
        request.setEmail("test@example.com");
        request.setPassword("newpassword");
        request.setConfirmPassword("newpassword");

        // When
        Set<ConstraintViolation<ProfileUpdateRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void testValidProfileUpdateRequest_WithoutPasswordChange() {
        // Given
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setFullName("Nguyen Van A");
        request.setPhone("0123456789");
        request.setEmail("test@example.com");
        // No password fields set

        // When
        Set<ConstraintViolation<ProfileUpdateRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void testInvalidFullName_Blank() {
        // Given
        ProfileUpdateRequest request = createValidRequest();
        request.setFullName("");

        // When
        Set<ConstraintViolation<ProfileUpdateRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Họ tên không được để trống");
    }

    @Test
    void testInvalidFullName_TooLong() {
        // Given
        ProfileUpdateRequest request = createValidRequest();
        request.setFullName("A".repeat(101)); // 101 characters

        // When
        Set<ConstraintViolation<ProfileUpdateRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Họ tên không được vượt quá 100 ký tự");
    }

    @Test
    void testInvalidPhone() {
        // Given
        ProfileUpdateRequest request = createValidRequest();
        request.setPhone("invalid-phone");

        // When
        Set<ConstraintViolation<ProfileUpdateRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Số điện thoại không hợp lệ");
    }

    @Test
    void testValidPhoneNumbers() {
        // Test valid phone number formats
        String[] validPhones = {"0123456789", "0987654321", "+84123456789", "+84987654321"};

        for (String phone : validPhones) {
            ProfileUpdateRequest request = createValidRequest();
            request.setPhone(phone);

            Set<ConstraintViolation<ProfileUpdateRequest>> violations = validator.validate(request);

            assertThat(violations).isEmpty();
        }
    }

    @Test
    void testInvalidEmail_Blank() {
        // Given
        ProfileUpdateRequest request = createValidRequest();
        request.setEmail("");

        // When
        Set<ConstraintViolation<ProfileUpdateRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Email không được để trống");
    }

    @Test
    void testInvalidEmail_Format() {
        // Given
        ProfileUpdateRequest request = createValidRequest();
        request.setEmail("invalid-email");

        // When
        Set<ConstraintViolation<ProfileUpdateRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Email không hợp lệ");
    }

    @Test
    void testInvalidEmail_TooLong() {
        // Given
        ProfileUpdateRequest request = createValidRequest();
        request.setEmail("a".repeat(90) + "@test.com"); // Over 100 characters

        // When
        Set<ConstraintViolation<ProfileUpdateRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Email không được vượt quá 100 ký tự");
    }

    @Test
    void testInvalidPassword_TooShort() {
        // Given
        ProfileUpdateRequest request = createValidRequest();
        request.setPassword("12345"); // 5 characters
        request.setConfirmPassword("12345");

        // When
        Set<ConstraintViolation<ProfileUpdateRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Mật khẩu phải từ 6-100 ký tự");
    }

    @Test
    void testInvalidPassword_TooLong() {
        // Given
        ProfileUpdateRequest request = createValidRequest();
        String longPassword = "a".repeat(101);
        request.setPassword(longPassword);
        request.setConfirmPassword(longPassword);

        // When
        Set<ConstraintViolation<ProfileUpdateRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Mật khẩu phải từ 6-100 ký tự");
    }

    @Test
    void testPasswordMismatch() {
        // Given
        ProfileUpdateRequest request = createValidRequest();
        request.setPassword("password1");
        request.setConfirmPassword("password2");

        // When
        Set<ConstraintViolation<ProfileUpdateRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Mật khẩu xác nhận không khớp");
    }

    @Test
    void testPasswordMatch() {
        // Given
        ProfileUpdateRequest request = createValidRequest();
        request.setPassword("password123");
        request.setConfirmPassword("password123");

        // When
        Set<ConstraintViolation<ProfileUpdateRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    private ProfileUpdateRequest createValidRequest() {
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setFullName("Nguyen Van A");
        request.setPhone("0123456789");
        request.setEmail("test@example.com");
        return request;
    }
}
