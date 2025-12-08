package vn.edu.fpt.pharma.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.edu.fpt.pharma.entity.Inventory;
import vn.edu.fpt.pharma.repository.InventoryRepository;
import vn.edu.fpt.pharma.service.impl.InventoryServiceImpl;
import vn.edu.fpt.pharma.testutil.TestDataFactory;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for InventoryService - Happy Path Only
 * Strategy: 1 test per method to achieve 100% line coverage
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("InventoryService Tests - Happy Path Only")
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryServiceImpl(inventoryRepository);
    }

    @Test
    @DisplayName("reserveStock - should reserve stock successfully")
    void reserveStock_happyPath() {
        // Arrange
        Long inventoryId = 1L;
        Long quantity = 10L;
        Inventory inventory = TestDataFactory.createInventory();
        when(inventoryRepository.findById(inventoryId))
                .thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(inventory);

        // Act
        inventoryService.reserveStock(inventoryId, quantity);

        // Assert
        verify(inventoryRepository).save(argThat(inv ->
                inv.getQuantity() == 90L // 100 - 10
        ));
    }

    @Test
    @DisplayName("releaseStock - should release stock successfully")
    void releaseStock_happyPath() {
        // Arrange
        Long inventoryId = 1L;
        Long quantity = 10L;
        Inventory inventory = TestDataFactory.createInventory();
        when(inventoryRepository.findById(inventoryId))
                .thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(inventory);

        // Act
        inventoryService.releaseStock(inventoryId, quantity);

        // Assert
        verify(inventoryRepository).save(argThat(inv ->
                inv.getQuantity() == 110L // 100 + 10
        ));
    }

    @Test
    @DisplayName("checkAvailability - should return true when available")
    void checkAvailability_happyPath() {
        // Arrange
        Long inventoryId = 1L;
        Long quantity = 50L;
        Inventory inventory = TestDataFactory.createInventory();
        when(inventoryRepository.findById(inventoryId))
                .thenReturn(Optional.of(inventory));

        // Act
        boolean available = inventoryService.checkAvailability(inventoryId, quantity);

        // Assert
        assertThat(available).isTrue();
    }

    @Test
    @DisplayName("findById - should return inventory")
    void findById_happyPath() {
        // Arrange
        Long id = 1L;
        Inventory inventory = TestDataFactory.createInventory();
        when(inventoryRepository.findById(id))
                .thenReturn(Optional.of(inventory));

        // Act
        Inventory result = inventoryService.findById(id);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("getInventoryByVariant - should return inventories for variant")
    void getInventoryByVariant_happyPath() {
        // Arrange
        Long variantId = 1L;
        List<Inventory> inventories = List.of(TestDataFactory.createInventory());
        when(inventoryRepository.findByVariantId(variantId))
                .thenReturn(inventories);

        // Act
        List<Inventory> result = inventoryService.getInventoryByVariant(variantId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("updateInventoryQuantity - should update quantity")
    void updateInventoryQuantity_happyPath() {
        // Arrange
        Long id = 1L;
        Long newQuantity = 150L;
        Inventory inventory = TestDataFactory.createInventory();
        when(inventoryRepository.findById(id))
                .thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(inventory);

        // Act
        inventoryService.updateInventoryQuantity(id, newQuantity);

        // Assert
        verify(inventoryRepository).save(argThat(inv ->
                inv.getQuantity().equals(newQuantity)
        ));
    }

    @Test
    @DisplayName("getAvailableInventories - should return available inventories")
    void getAvailableInventories_happyPath() {
        // Arrange
        Long branchId = 1L;
        List<Inventory> inventories = List.of(TestDataFactory.createInventory());
        when(inventoryRepository.findAvailableByBranch(branchId))
                .thenReturn(inventories);

        // Act
        List<Inventory> result = inventoryService.getAvailableInventories(branchId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("checkExpiryStatus - should return status")
    void checkExpiryStatus_happyPath() {
        // Arrange
        Long inventoryId = 1L;
        Inventory inventory = TestDataFactory.createInventory();
        when(inventoryRepository.findById(inventoryId))
                .thenReturn(Optional.of(inventory));

        // Act
        String status = inventoryService.checkExpiryStatus(inventoryId);

        // Assert
        assertThat(status).isNotNull();
    }

    @Test
    @DisplayName("getInventoriesByBatch - should return inventories by batch")
    void getInventoriesByBatch_happyPath() {
        // Arrange
        String batchCode = "BATCH001";
        List<Inventory> inventories = List.of(TestDataFactory.createInventory());
        when(inventoryRepository.findByBatchCode(batchCode))
                .thenReturn(inventories);

        // Act
        List<Inventory> result = inventoryService.getInventoriesByBatch(batchCode);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("calculateTotalValue - should calculate total value")
    void calculateTotalValue_happyPath() {
        // Arrange
        Long branchId = 1L;
        when(inventoryRepository.calculateTotalValue(branchId))
                .thenReturn(1000000.0);

        // Act
        Double total = inventoryService.calculateTotalValue(branchId);

        // Assert
        assertThat(total).isEqualTo(1000000.0);
    }
}

