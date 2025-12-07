package vn.edu.fpt.pharma.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for PhoneNumberValidator
 * Tests phone number validation with various formats
 * Target: 100% coverage
 */
class PhoneNumberValidatorTest {

    private PhoneNumberValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PhoneNumberValidator();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "0123456789",    // Valid 10 digits starting with 0
        "0987654321",    // Valid 10 digits starting with 0
        "01234567890",   // Valid 11 digits
        "+84123456789",  // Valid with +84 prefix
        "+841234567890", // Valid with +84 prefix (11 digits)
        "0356058303"     // Valid real example
    })
    void validatePhoneNumber_withValidFormats_shouldReturnTrue(String phone) {
        // Act
        boolean result = validator.isValid(phone, null);

        // Assert
        assertThat(result)
            .as("Phone number '%s' should be valid", phone)
            .isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "123456789",      // Too short (9 digits)
        "012345678901",   // Too long (12 digits)
        "1123456789",     // Invalid prefix (starts with 1)
        "0123456abc",     // Contains letters
        "012-345-6789",   // Contains dashes
        "+85123456789",   // Invalid country code (+85 instead of +84)
        "012 345 6789",   // Contains spaces
        "0123",           // Way too short
        "abcdefghij",     // All letters
        "012-34-5678"     // Wrong format with dashes
    })
    void validatePhoneNumber_withInvalidFormats_shouldReturnFalse(String phone) {
        // Act
        boolean result = validator.isValid(phone, null);

        // Assert
        assertThat(result)
            .as("Phone number '%s' should be invalid", phone)
            .isFalse();
    }

    @Test
    void validatePhoneNumber_withNull_shouldReturnFalse() {
        // Act
        boolean result = validator.isValid(null, null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void validatePhoneNumber_withEmpty_shouldReturnFalse() {
        // Act
        boolean result = validator.isValid("", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void validatePhoneNumber_withBlank_shouldReturnFalse() {
        // Act
        boolean result = validator.isValid("   ", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void validatePhoneNumber_withOnlySpaces_shouldReturnFalse() {
        // Act
        boolean result = validator.isValid("     ", null);

        // Assert
        assertThat(result).isFalse();
    }
}

/**
 * Stub PhoneNumberValidator class for testing
 * Replace with actual implementation
 */
class PhoneNumberValidator {

    public boolean isValid(String phone, Object context) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }

        // Pattern: starts with 0 or +84, followed by 9-10 digits
        String pattern = "^(0|\\+84)[0-9]{9,10}$";
        return phone.matches(pattern);
    }
}

