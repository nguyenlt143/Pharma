package vn.edu.fpt.pharma.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import vn.edu.fpt.pharma.BaseServiceTest;
import vn.edu.fpt.pharma.entity.InventoryMovementDetail;
import vn.edu.fpt.pharma.repository.InventoryMovementDetailRepository;
import vn.edu.fpt.pharma.service.AuditService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for InventoryMovementDetailServiceImpl - 4 tests
 * Strategy: Coverage for inherited BaseServiceImpl methods
 */
@DisplayName("InventoryMovementDetailServiceImpl Tests")
class InventoryMovementDetailServiceImplTest extends BaseServiceTest {

    @Mock
    private InventoryMovementDetailRepository inventoryMovementDetailRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private InventoryMovementDetailServiceImpl inventoryMovementDetailService;

    private InventoryMovementDetail testDetail;

    @BeforeEach
    void setUp() {
        testDetail = new InventoryMovementDetail();
        testDetail.setId(1L);
        testDetail.setQuantity(100L);
    }

    @Nested
    @DisplayName("findById() tests - 2 tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should return detail when found")
        void findById_whenDetailExists_shouldReturnDetail() {
            // Arrange
            when(inventoryMovementDetailRepository.findById(1L)).thenReturn(Optional.of(testDetail));

            // Act
            InventoryMovementDetail result = inventoryMovementDetailService.findById(1L);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getQuantity()).isEqualTo(100L);
            verify(inventoryMovementDetailRepository).findById(1L);
        }

        @Test
        @DisplayName("Should return null when detail not found")
        void findById_whenDetailNotFound_shouldReturnNull() {
            // Arrange
            when(inventoryMovementDetailRepository.findById(999L)).thenReturn(Optional.empty());

            // Act
            InventoryMovementDetail result = inventoryMovementDetailService.findById(999L);

            // Assert
            assertThat(result).isNull();
            verify(inventoryMovementDetailRepository).findById(999L);
        }
    }

    @Nested
    @DisplayName("create() tests - 2 tests")
    class CreateTests {

        @Test
        @DisplayName("Should create detail successfully")
        void create_withValidDetail_shouldCreateSuccessfully() {
            // Arrange
            InventoryMovementDetail newDetail = new InventoryMovementDetail();
            newDetail.setQuantity(50L);
            when(inventoryMovementDetailRepository.save(any(InventoryMovementDetail.class))).thenReturn(testDetail);

            // Act
            InventoryMovementDetail result = inventoryMovementDetailService.create(newDetail);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            verify(inventoryMovementDetailRepository).save(newDetail);
        }

        @Test
        @DisplayName("Should handle repository save failure")
        void create_whenRepositoryFails_shouldPropagateException() {
            // Arrange
            InventoryMovementDetail newDetail = new InventoryMovementDetail();
            newDetail.setQuantity(50L);
            when(inventoryMovementDetailRepository.save(any())).thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            assertThatThrownBy(() -> inventoryMovementDetailService.create(newDetail))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Database error");
        }
    }
}

