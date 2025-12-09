package vn.edu.fpt.pharma.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import vn.edu.fpt.pharma.BaseServiceTest;
import vn.edu.fpt.pharma.entity.StockAdjustment;
import vn.edu.fpt.pharma.repository.StockAdjustmentRepository;
import vn.edu.fpt.pharma.service.AuditService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for StockAdjustmentServiceImpl - 4 tests
 * Strategy: Coverage for inherited BaseServiceImpl methods
 */
@DisplayName("StockAdjustmentServiceImpl Tests")
class StockAdjustmentServiceImplTest extends BaseServiceTest {

    @Mock
    private StockAdjustmentRepository stockAdjustmentRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private StockAdjustmentServiceImpl stockAdjustmentService;

    private StockAdjustment testAdjustment;

    @BeforeEach
    void setUp() {
        testAdjustment = new StockAdjustment();
        testAdjustment.setId(1L);
        testAdjustment.setBrandId(1L);
        testAdjustment.setVariantId(1L);
        testAdjustment.setBeforeQuantity(100L);
        testAdjustment.setAfterQuantity(90L);
        testAdjustment.setDifferenceQuantity(-10L);
        testAdjustment.setReason("Test adjustment");
    }

    @Nested
    @DisplayName("findById() tests - 2 tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should return stock adjustment when found")
        void findById_whenAdjustmentExists_shouldReturnAdjustment() {
            // Arrange
            when(stockAdjustmentRepository.findById(1L)).thenReturn(Optional.of(testAdjustment));

            // Act
            StockAdjustment result = stockAdjustmentService.findById(1L);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getReason()).isEqualTo("Test adjustment");
            verify(stockAdjustmentRepository).findById(1L);
        }

        @Test
        @DisplayName("Should return null when adjustment not found")
        void findById_whenAdjustmentNotFound_shouldReturnNull() {
            // Arrange
            when(stockAdjustmentRepository.findById(999L)).thenReturn(Optional.empty());

            // Act
            StockAdjustment result = stockAdjustmentService.findById(999L);

            // Assert
            assertThat(result).isNull();
            verify(stockAdjustmentRepository).findById(999L);
        }
    }

    @Nested
    @DisplayName("create() tests - 2 tests")
    class CreateTests {

        @Test
        @DisplayName("Should create stock adjustment successfully")
        void create_withValidAdjustment_shouldCreateSuccessfully() {
            // Arrange
            StockAdjustment newAdjustment = new StockAdjustment();
            newAdjustment.setBrandId(1L);
            newAdjustment.setReason("New adjustment");
            when(stockAdjustmentRepository.save(any(StockAdjustment.class))).thenReturn(testAdjustment);

            // Act
            StockAdjustment result = stockAdjustmentService.create(newAdjustment);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            verify(stockAdjustmentRepository).save(newAdjustment);
        }

        @Test
        @DisplayName("Should handle repository save failure")
        void create_whenRepositoryFails_shouldPropagateException() {
            // Arrange
            StockAdjustment newAdjustment = new StockAdjustment();
            newAdjustment.setReason("New adjustment");
            when(stockAdjustmentRepository.save(any())).thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            assertThatThrownBy(() -> stockAdjustmentService.create(newAdjustment))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Database error");
        }
    }
}

