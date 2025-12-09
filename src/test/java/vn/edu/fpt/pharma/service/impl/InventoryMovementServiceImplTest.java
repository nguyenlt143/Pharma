package vn.edu.fpt.pharma.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import vn.edu.fpt.pharma.BaseServiceTest;
import vn.edu.fpt.pharma.constant.MovementStatus;
import vn.edu.fpt.pharma.constant.MovementType;
import vn.edu.fpt.pharma.entity.InventoryMovement;
import vn.edu.fpt.pharma.repository.InventoryMovementRepository;
import vn.edu.fpt.pharma.service.AuditService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for InventoryMovementServiceImpl - 4 tests
 * Strategy: Coverage for inherited BaseServiceImpl methods
 */
@DisplayName("InventoryMovementServiceImpl Tests")
class InventoryMovementServiceImplTest extends BaseServiceTest {

    @Mock
    private InventoryMovementRepository inventoryMovementRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private InventoryMovementServiceImpl inventoryMovementService;

    private InventoryMovement testMovement;

    @BeforeEach
    void setUp() {
        testMovement = new InventoryMovement();
        testMovement.setId(1L);
        testMovement.setMovementType(MovementType.SUP_TO_WARE);
        testMovement.setMovementStatus(MovementStatus.RECEIVED);
    }

    @Nested
    @DisplayName("findById() tests - 2 tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should return movement when found")
        void findById_whenMovementExists_shouldReturnMovement() {
            // Arrange
            when(inventoryMovementRepository.findById(1L)).thenReturn(Optional.of(testMovement));

            // Act
            InventoryMovement result = inventoryMovementService.findById(1L);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getMovementType()).isEqualTo(MovementType.SUP_TO_WARE);
            verify(inventoryMovementRepository).findById(1L);
        }

        @Test
        @DisplayName("Should return null when movement not found")
        void findById_whenMovementNotFound_shouldReturnNull() {
            // Arrange
            when(inventoryMovementRepository.findById(999L)).thenReturn(Optional.empty());

            // Act
            InventoryMovement result = inventoryMovementService.findById(999L);

            // Assert
            assertThat(result).isNull();
            verify(inventoryMovementRepository).findById(999L);
        }
    }

    @Nested
    @DisplayName("create() tests - 2 tests")
    class CreateTests {

        @Test
        @DisplayName("Should create movement successfully")
        void create_withValidMovement_shouldCreateSuccessfully() {
            // Arrange
            InventoryMovement newMovement = new InventoryMovement();
            newMovement.setMovementType(MovementType.SUP_TO_WARE);
            when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenReturn(testMovement);

            // Act
            InventoryMovement result = inventoryMovementService.create(newMovement);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            verify(inventoryMovementRepository).save(newMovement);
        }

        @Test
        @DisplayName("Should handle repository save failure")
        void create_whenRepositoryFails_shouldPropagateException() {
            // Arrange
            InventoryMovement newMovement = new InventoryMovement();
            newMovement.setMovementType(MovementType.SUP_TO_WARE);
            when(inventoryMovementRepository.save(any())).thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            assertThatThrownBy(() -> inventoryMovementService.create(newMovement))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Database error");
        }
    }
}

