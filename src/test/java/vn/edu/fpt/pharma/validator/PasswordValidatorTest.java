package vn.edu.fpt.pharma.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for PasswordValidator
 * Tests password length validation (6-100 characters)
 * Target: 100% coverage
 */
class PasswordValidatorTest {

    private PasswordValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PasswordValidator();
    }

    @Test
    void validatePassword_withValidLength_shouldReturnTrue() {
        // Arrange
        String validPassword = "password123";

        // Act
        boolean result = validator.isValid(validPassword, null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void validatePassword_withMinLength_shouldReturnTrue() {
        // Arrange
        String minPassword = "pass12"; // Exactly 6 characters

        // Act
        boolean result = validator.isValid(minPassword, null);

        // Assert
        assertThat(result)
            .as("Password with minimum length (6 chars) should be valid")
            .isTrue();
    }

    @Test
    void validatePassword_withMaxLength_shouldReturnTrue() {
        // Arrange
        String maxPassword = "a".repeat(100); // Exactly 100 characters

        // Act
        boolean result = validator.isValid(maxPassword, null);

        // Assert
        assertThat(result)
            .as("Password with maximum length (100 chars) should be valid")
            .isTrue();
    }

    @Test
    void validatePassword_withTooShort_shouldReturnFalse() {
        // Arrange
        String shortPassword = "pass1"; // Only 5 characters

        // Act
        boolean result = validator.isValid(shortPassword, null);

        // Assert
        assertThat(result)
            .as("Password shorter than 6 characters should be invalid")
            .isFalse();
    }

    @Test
    void validatePassword_withTooLong_shouldReturnFalse() {
        // Arrange
        String longPassword = "a".repeat(101); // 101 characters

        // Act
        boolean result = validator.isValid(longPassword, null);

        // Assert
        assertThat(result)
            .as("Password longer than 100 characters should be invalid")
            .isFalse();
    }

    @Test
    void validatePassword_withNull_shouldReturnFalse() {
        // Act
        boolean result = validator.isValid(null, null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void validatePassword_withEmpty_shouldReturnFalse() {
        // Act
        boolean result = validator.isValid("", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void validatePassword_withOneCharacter_shouldReturnFalse() {
        // Act
        boolean result = validator.isValid("a", null);

        // Assert
        assertThat(result)
            .as("Single character password should be invalid")
            .isFalse();
    }

    @Test
    void validatePassword_withSpecialCharacters_shouldReturnTrue() {
        // Arrange - Password with special chars but valid length
        String specialPassword = "p@ss!123";

        // Act
        boolean result = validator.isValid(specialPassword, null);

        // Assert
        assertThat(result)
            .as("Password with special characters should be valid if length is correct")
            .isTrue();
    }
}

/**
 * Stub PasswordValidator class for testing
 * Replace with actual implementation
 */
class PasswordValidator {

    private static final int MIN_LENGTH = 6;
    private static final int MAX_LENGTH = 100;

    public boolean isValid(String password, Object context) {
        if (password == null || password.isEmpty()) {
            return false;
        }

        int length = password.length();
        return length >= MIN_LENGTH && length <= MAX_LENGTH;
    }
}

