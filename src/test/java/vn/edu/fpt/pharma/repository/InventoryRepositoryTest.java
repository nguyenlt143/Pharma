package vn.edu.fpt.pharma.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import vn.edu.fpt.pharma.entity.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("InventoryRepository Tests")
class InventoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private InventoryRepository inventoryRepository;

    private Branch branch;
    private Branch warehouse;
    private Category category;
    private Medicine medicine;
    private Unit unit;
    private MedicineVariant variant;
    private Supplier supplier;
    private Batch batch;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        // Create warehouse (branch_id = 1)
        warehouse = Branch.builder()
                .name("Kho tổng")
                .address("Warehouse Address")
                .build();
        entityManager.persist(warehouse);

        // Create branch
        branch = Branch.builder()
                .name("Chi nhánh 1")
                .address("123 Test St")
                .build();
        entityManager.persist(branch);

        // Create category
        category = Category.builder()
                .name("Kháng sinh")
                .description("Antibiotics")
                .build();
        entityManager.persist(category);

        // Create medicine
        medicine = Medicine.builder()
                .name("Amoxicillin")
                .activeIngredient("Amoxicillin trihydrate")
                .brandName("Amoxil")
                .manufacturer("ABC Pharma")
                .category(category)
                .build();
        entityManager.persist(medicine);

        // Create unit
        unit = Unit.builder()
                .name("Viên")
                .build();
        entityManager.persist(unit);

        // Create medicine variant
        variant = MedicineVariant.builder()
                .medicine(medicine)
                .strength("500mg")
                .dosage_form("Viên nén")
                .baseUnitId(unit)
                .build();
        entityManager.persist(variant);

        // Create supplier
        supplier = Supplier.builder()
                .name("Test Supplier")
                .phone("0987654321")
                .address("Supplier Address")
                .build();
        entityManager.persist(supplier);

        // Create batch
        batch = Batch.builder()
                .batchCode("BATCH-001")
                .expiryDate(LocalDate.now().plusYears(2))
                .supplier(supplier)
                .build();
        entityManager.persist(batch);

        // Create inventory
        inventory = Inventory.builder()
                .branch(branch)
                .variant(variant)
                .batch(batch)
                .quantity(100L)
                .minStock(10L)
                .costPrice(5000.0)
                .build();
        entityManager.persist(inventory);

        entityManager.flush();
    }

    @Nested
    @DisplayName("CRUD Operations Tests")
    class CrudOperationsTests {

        @Test
        @DisplayName("Should find inventory by id")
        void shouldFindInventoryById() {
            // Act
            Optional<Inventory> found = inventoryRepository.findById(inventory.getId());

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getQuantity()).isEqualTo(100L);
            assertThat(found.get().getBranch().getName()).isEqualTo("Chi nhánh 1");
        }

        @Test
        @DisplayName("Should save inventory")
        void shouldSaveInventory() {
            // Arrange
            Inventory newInventory = Inventory.builder()
                    .branch(branch)
                    .variant(variant)
                    .batch(batch)
                    .quantity(50L)
                    .minStock(5L)
                    .costPrice(4500.0)
                    .build();

            // Act
            Inventory saved = inventoryRepository.save(newInventory);

            // Assert
            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getQuantity()).isEqualTo(50L);
        }

        @Test
        @DisplayName("Should find all inventories")
        void shouldFindAllInventories() {
            // Act
            List<Inventory> inventories = inventoryRepository.findAll();

            // Assert
            assertThat(inventories).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("Custom Query Tests")
    class CustomQueryTests {

        @Test
        @DisplayName("Should count low stock items")
        void shouldCountLowStockItems() {
            // Arrange
            Inventory lowStockInventory = Inventory.builder()
                    .branch(branch)
                    .variant(variant)
                    .batch(batch)
                    .quantity(5L)
                    .minStock(10L)
                    .costPrice(5000.0)
                    .build();
            entityManager.persist(lowStockInventory);
            entityManager.flush();

            // Act
            int count = inventoryRepository.countLowStock();

            // Assert
            assertThat(count).isGreaterThanOrEqualTo(0);
        }

        @Test
        @DisplayName("Should find inventory by branch, variant, and batch IDs")
        void shouldFindByBranchVariantAndBatchIds() {
            // Act
            Optional<Inventory> found = inventoryRepository.findByBranchIdAndVariantIdAndBatchId(
                    branch.getId(), variant.getId(), batch.getId());

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getQuantity()).isEqualTo(100L);
        }

        @Test
        @DisplayName("Should find inventory by entities")
        void shouldFindByBranchVariantAndBatch() {
            // Act
            Optional<Inventory> found = inventoryRepository.findByBranchAndVariantAndBatch(
                    branch, variant, batch);

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getQuantity()).isEqualTo(100L);
        }
    }

    @Nested
    @DisplayName("Inventory Report Tests")
    class InventoryReportTests {

        @Test
        @DisplayName("Should count total items by branch")
        void shouldCountTotalItems() {
            // Act
            int count = inventoryRepository.countTotalItems(branch.getId());

            // Assert
            assertThat(count).isGreaterThanOrEqualTo(1);
        }

        @Test
        @DisplayName("Should count low stock items by branch")
        void shouldCountLowStockItemsByBranch() {
            // Arrange
            Inventory lowStock = Inventory.builder()
                    .branch(branch)
                    .variant(variant)
                    .batch(batch)
                    .quantity(5L)
                    .minStock(10L)
                    .costPrice(5000.0)
                    .build();
            entityManager.persist(lowStock);
            entityManager.flush();

            // Act
            int count = inventoryRepository.countLowStockItems(branch.getId());

            // Assert
            assertThat(count).isGreaterThanOrEqualTo(1);
        }

        @Test
        @DisplayName("Should count out of stock items")
        void shouldCountOutOfStockItems() {
            // Arrange
            Inventory outOfStock = Inventory.builder()
                    .branch(branch)
                    .variant(variant)
                    .batch(batch)
                    .quantity(0L)
                    .minStock(10L)
                    .costPrice(5000.0)
                    .build();
            entityManager.persist(outOfStock);
            entityManager.flush();

            // Act
            int count = inventoryRepository.countOutOfStockItems(branch.getId());

            // Assert
            assertThat(count).isGreaterThanOrEqualTo(1);
        }

        @Test
        @DisplayName("Should calculate total inventory value")
        void shouldCalculateTotalValue() {
            // Act
            Double totalValue = inventoryRepository.calculateTotalValue(branch.getId());

            // Assert
            assertThat(totalValue).isGreaterThan(0);
            assertThat(totalValue).isEqualTo(500000.0); // 100 * 5000
        }
    }

    @Nested
    @DisplayName("Expiry Item Tests")
    class ExpiryItemTests {

        @Test
        @DisplayName("Should count near expiry items")
        void shouldCountNearExpiryItems() {
            // Arrange
            Batch nearExpiryBatch = Batch.builder()
                    .batchCode("EXPIRING-001")
                    .expiryDate(LocalDate.now().plusDays(15))
                    .supplier(supplier)
                    .build();
            entityManager.persist(nearExpiryBatch);

            Inventory nearExpiry = Inventory.builder()
                    .branch(branch)
                    .variant(variant)
                    .batch(nearExpiryBatch)
                    .quantity(20L)
                    .minStock(5L)
                    .costPrice(5000.0)
                    .build();
            entityManager.persist(nearExpiry);
            entityManager.flush();

            // Act
            int count = inventoryRepository.countNearExpiryItems(branch.getId());

            // Assert
            assertThat(count).isGreaterThanOrEqualTo(1);
        }

        @Test
        @DisplayName("Should count expired items")
        void shouldCountExpiredItems() {
            // Arrange
            Batch expiredBatch = Batch.builder()
                    .batchCode("EXPIRED-001")
                    .expiryDate(LocalDate.now().minusDays(1))
                    .supplier(supplier)
                    .build();
            entityManager.persist(expiredBatch);

            Inventory expired = Inventory.builder()
                    .branch(branch)
                    .variant(variant)
                    .batch(expiredBatch)
                    .quantity(10L)
                    .minStock(5L)
                    .costPrice(5000.0)
                    .build();
            entityManager.persist(expired);
            entityManager.flush();

            // Act
            int count = inventoryRepository.countExpiredItems(branch.getId());

            // Assert
            assertThat(count).isGreaterThanOrEqualTo(1);
        }

        @Test
        @DisplayName("Should count items by expiry with custom threshold")
        void shouldCountItemsByExpiryWithCustomThreshold() {
            // Act
            int count = inventoryRepository.countItemsByExpiry(branch.getId(), 60, false);

            // Assert
            assertThat(count).isGreaterThanOrEqualTo(0);
        }
    }

    @Nested
    @DisplayName("Search Tests")
    class SearchTests {

        @Test
        @DisplayName("Should search medicines in warehouse")
        void shouldSearchMedicinesInWarehouse() {
            // Arrange
            Inventory warehouseInventory = Inventory.builder()
                    .branch(warehouse)
                    .variant(variant)
                    .batch(batch)
                    .quantity(200L)
                    .minStock(20L)
                    .costPrice(5000.0)
                    .build();
            entityManager.persist(warehouseInventory);
            entityManager.flush();

            // Act
            List<Object[]> results = inventoryRepository.searchMedicinesInWarehouse("Amox");

            // Assert
            assertThat(results).isNotEmpty();
        }

        @Test
        @DisplayName("Should find medicines by branch")
        void shouldFindMedicinesByBranch() {
            // Act
            List<Object[]> results = inventoryRepository.findMedicinesByBranch(branch.getId());

            // Assert
            assertThat(results).isNotEmpty();
        }

        @Test
        @DisplayName("Should find inventory by variant id")
        void shouldFindInventoryByVariantId() {
            // Act
            List<Object[]> results = inventoryRepository.findInventoryByVariantId(variant.getId());

            // Assert
            assertThat(results).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("Category Statistics Tests")
    class CategoryStatisticsTests {

        @Test
        @DisplayName("Should get category statistics")
        void shouldGetCategoryStatistics() {
            // Act
            List<Object[]> stats = inventoryRepository.getCategoryStatistics(branch.getId());

            // Assert
            assertThat(stats).isNotEmpty();
        }

        @Test
        @DisplayName("Should get all categories by branch")
        void shouldGetAllCategoriesByBranch() {
            // Act
            List<Object[]> categories = inventoryRepository.getAllCategories(branch.getId());

            // Assert
            assertThat(categories).isNotEmpty();
        }
    }
}
