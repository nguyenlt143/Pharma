package vn.edu.fpt.pharma.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for EmailValidator - Comprehensive Tests
 * Strategy: Test all email formats and edge cases (20 tests)
 */
@DisplayName("EmailValidator - Comprehensive Tests")
class EmailValidatorTest {

    private EmailValidator validator;

    @BeforeEach
    void setUp() {
        validator = new EmailValidator();
    }

    @ParameterizedTest(name = "Valid email: {0}")
    @ValueSource(strings = {
            "test@example.com",
            "user.name@example.com",
            "user+tag@example.com",
            "user_name@example.com",
            "user-name@example.com",
            "123@example.com",
            "test@sub.example.com",
            "test@example.co.uk",
            "test@example-domain.com",
            "a@b.c"
    })
    void validateEmail_withValidFormats_shouldReturnTrue(String email) {
        assertThat(validator.validate(email)).isTrue();
    }

    @ParameterizedTest(name = "Invalid email: {0}")
    @ValueSource(strings = {
            "plaintext",
            "@example.com",
            "test@",
            "test@@example.com",
            "test @example.com",
            "test@example",
            "test@.com",
            "test..name@example.com",
            "test@example..com",
            "test@-example.com",
            "test@example-.com"
    })
    void validateEmail_withInvalidFormats_shouldReturnFalse(String email) {
        assertThat(validator.validate(email)).isFalse();
    }

    @Test
    @DisplayName("Should reject null email")
    void validateEmail_withNull_shouldReturnFalse() {
        assertThat(validator.validate(null)).isFalse();
    }

    @Test
    @DisplayName("Should reject empty email")
    void validateEmail_withEmpty_shouldReturnFalse() {
        assertThat(validator.validate("")).isFalse();
    }

    @Test
    @DisplayName("Should reject blank email")
    void validateEmail_withBlank_shouldReturnFalse() {
        assertThat(validator.validate("   ")).isFalse();
    }
}

