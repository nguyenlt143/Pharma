package vn.edu.fpt.pharma.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for QuantityValidator
 * Tests quantity validation against available stock
 * Target: 100% coverage
 */
class QuantityValidatorTest {

    private QuantityValidator validator;

    @BeforeEach
    void setUp() {
        validator = new QuantityValidator();
    }

    @Test
    void validateQuantity_withinStock_shouldReturnTrue() {
        // Arrange
        int requestedQuantity = 50;
        int availableStock = 100;

        // Act
        boolean result = validator.validate(requestedQuantity, availableStock);

        // Assert
        assertThat(result)
            .as("Quantity within stock should be valid")
            .isTrue();
    }

    @Test
    void validateQuantity_atMaxStock_shouldReturnTrue() {
        // Arrange
        int requestedQuantity = 100;
        int availableStock = 100;

        // Act
        boolean result = validator.validate(requestedQuantity, availableStock);

        // Assert
        assertThat(result)
            .as("Quantity equal to max stock should be valid")
            .isTrue();
    }

    @Test
    void validateQuantity_exceedsStock_shouldReturnFalse() {
        // Arrange
        int requestedQuantity = 101;
        int availableStock = 100;

        // Act
        boolean result = validator.validate(requestedQuantity, availableStock);

        // Assert
        assertThat(result)
            .as("Quantity exceeding stock should be invalid")
            .isFalse();
    }

    @Test
    void validateQuantity_withZero_shouldReturnFalse() {
        // Arrange
        int requestedQuantity = 0;
        int availableStock = 100;

        // Act
        boolean result = validator.validate(requestedQuantity, availableStock);

        // Assert
        assertThat(result)
            .as("Zero quantity should be invalid")
            .isFalse();
    }

    @Test
    void validateQuantity_withNegative_shouldReturnFalse() {
        // Arrange
        int requestedQuantity = -1;
        int availableStock = 100;

        // Act
        boolean result = validator.validate(requestedQuantity, availableStock);

        // Assert
        assertThat(result)
            .as("Negative quantity should be invalid")
            .isFalse();
    }

    @Test
    void validateQuantity_withOne_shouldReturnTrue() {
        // Arrange
        int requestedQuantity = 1;
        int availableStock = 100;

        // Act
        boolean result = validator.validate(requestedQuantity, availableStock);

        // Assert
        assertThat(result)
            .as("Minimum valid quantity (1) should be valid")
            .isTrue();
    }

    @Test
    void validateQuantity_withZeroStock_shouldReturnFalse() {
        // Arrange
        int requestedQuantity = 1;
        int availableStock = 0;

        // Act
        boolean result = validator.validate(requestedQuantity, availableStock);

        // Assert
        assertThat(result)
            .as("Any quantity should be invalid when stock is zero")
            .isFalse();
    }

    @Test
    void validateQuantity_withLargeQuantity_shouldReturnFalseIfExceedsStock() {
        // Arrange
        int requestedQuantity = 999999;
        int availableStock = 100;

        // Act
        boolean result = validator.validate(requestedQuantity, availableStock);

        // Assert
        assertThat(result)
            .as("Large quantity exceeding stock should be invalid")
            .isFalse();
    }

    @Test
    void validateQuantity_withLargeQuantityAndSufficientStock_shouldReturnTrue() {
        // Arrange
        int requestedQuantity = 999999;
        int availableStock = 1000000;

        // Act
        boolean result = validator.validate(requestedQuantity, availableStock);

        // Assert
        assertThat(result)
            .as("Large quantity within stock should be valid")
            .isTrue();
    }
}

/**
 * Stub QuantityValidator class for testing
 * Replace with actual implementation
 */
class QuantityValidator {

    public boolean validate(int requestedQuantity, int availableStock) {
        // Quantity must be positive and not exceed available stock
        return requestedQuantity > 0 && requestedQuantity <= availableStock;
    }
}

