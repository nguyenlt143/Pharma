package vn.edu.fpt.pharma.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import vn.edu.fpt.pharma.entity.Category;
import vn.edu.fpt.pharma.entity.Medicine;
import vn.edu.fpt.pharma.entity.MedicineVariant;
import vn.edu.fpt.pharma.entity.Unit;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("MedicineRepository Tests")
class MedicineRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MedicineRepository medicineRepository;

    private Category category;
    private Medicine medicine;
    private Unit unit;
    private MedicineVariant variant;

    @BeforeEach
    void setUp() {
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
                .country("Vietnam")
                .category(category)
                .build();
        entityManager.persist(medicine);

        // Create unit for variant
        unit = Unit.builder()
                .name("Viên")
                .build();
        entityManager.persist(unit);

        // Create variant
        variant = MedicineVariant.builder()
                .medicine(medicine)
                .strength("500mg")
                .dosage_form("Viên nén")
                .baseUnitId(unit)
                .build();
        entityManager.persist(variant);

        entityManager.flush();
    }

    @Nested
    @DisplayName("CRUD Operations Tests")
    class CrudOperationsTests {

        @Test
        @DisplayName("Should find medicine by id")
        void shouldFindMedicineById() {
            // Act
            Optional<Medicine> found = medicineRepository.findById(medicine.getId());

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getName()).isEqualTo("Amoxicillin");
            assertThat(found.get().getCategory().getName()).isEqualTo("Kháng sinh");
        }

        @Test
        @DisplayName("Should save medicine")
        void shouldSaveMedicine() {
            // Arrange
            Medicine newMedicine = Medicine.builder()
                    .name("Paracetamol")
                    .activeIngredient("Paracetamol")
                    .brandName("Tylenol")
                    .manufacturer("XYZ Pharma")
                    .country("USA")
                    .category(category)
                    .build();

            // Act
            Medicine saved = medicineRepository.save(newMedicine);

            // Assert
            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getName()).isEqualTo("Paracetamol");
        }

        @Test
        @DisplayName("Should find all medicines")
        void shouldFindAllMedicines() {
            // Act
            List<Medicine> medicines = medicineRepository.findAll();

            // Assert
            assertThat(medicines).isNotEmpty();
            assertThat(medicines).hasSize(1);
        }

        @Test
        @DisplayName("Should update medicine")
        void shouldUpdateMedicine() {
            // Arrange
            medicine.setManufacturer("Updated Pharma");

            // Act
            Medicine updated = medicineRepository.save(medicine);

            // Assert
            assertThat(updated.getManufacturer()).isEqualTo("Updated Pharma");
        }

        @Test
        @DisplayName("Should delete medicine")
        void shouldDeleteMedicine() {
            // Arrange
            Long medicineId = medicine.getId();

            // Act
            medicineRepository.deleteById(medicineId);
            entityManager.flush();
            entityManager.clear();

            // Assert
            Optional<Medicine> deleted = medicineRepository.findById(medicineId);
            assertThat(deleted).isEmpty();
        }
    }

    @Nested
    @DisplayName("Custom Query Tests")
    class CustomQueryTests {

        @Test
        @DisplayName("Should count variants by medicine id")
        void shouldCountVariantsByMedicineId() {
            // Act
            long count = medicineRepository.countVariantsByMedicineId(medicine.getId());

            // Assert
            assertThat(count).isEqualTo(1);
        }

        @Test
        @DisplayName("Should return zero when no variants exist")
        void shouldReturnZeroWhenNoVariantsExist() {
            // Arrange
            Medicine newMedicine = Medicine.builder()
                    .name("Test Medicine")
                    .category(category)
                    .build();
            entityManager.persist(newMedicine);
            entityManager.flush();

            // Act
            long count = medicineRepository.countVariantsByMedicineId(newMedicine.getId());

            // Assert
            assertThat(count).isZero();
        }

        @Test
        @DisplayName("Should search medicines by keyword")
        void shouldSearchMedicinesByKeyword() {
            // Act
            List<Object[]> results = medicineRepository.searchMedicinesByKeyword("Amox");

            // Assert
            assertThat(results).isNotEmpty();
            // Verify the medicine is in results
            boolean found = results.stream()
                    .anyMatch(row -> "Amoxicillin".equals(row[1]));
            assertThat(found).isTrue();
        }

        @Test
        @DisplayName("Should search by active ingredient")
        void shouldSearchByActiveIngredient() {
            // Act
            List<Object[]> results = medicineRepository.searchMedicinesByKeyword("trihydrate");

            // Assert
            assertThat(results).isNotEmpty();
        }

        @Test
        @DisplayName("Should search by brand name")
        void shouldSearchByBrandName() {
            // Act
            List<Object[]> results = medicineRepository.searchMedicinesByKeyword("Amoxil");

            // Assert
            assertThat(results).isNotEmpty();
        }

        @Test
        @DisplayName("Should return empty list for non-matching keyword")
        void shouldReturnEmptyListForNonMatchingKeyword() {
            // Act
            List<Object[]> results = medicineRepository.searchMedicinesByKeyword("NonExistentMedicine");

            // Assert
            assertThat(results).isEmpty();
        }

        @Test
        @DisplayName("Should handle case-insensitive search")
        void shouldHandleCaseInsensitiveSearch() {
            // Act
            List<Object[]> results = medicineRepository.searchMedicinesByKeyword("AMOXICILLIN");

            // Assert
            assertThat(results).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("Relationship Tests")
    class RelationshipTests {

        @Test
        @DisplayName("Should load medicine with category")
        void shouldLoadMedicineWithCategory() {
            // Act
            Medicine found = medicineRepository.findById(medicine.getId()).orElseThrow();

            // Assert
            assertThat(found.getCategory()).isNotNull();
            assertThat(found.getCategory().getName()).isEqualTo("Kháng sinh");
        }

        @Test
        @DisplayName("Should cascade updates to medicine")
        void shouldCascadeUpdatesToMedicine() {
            // Arrange
            medicine.setName("Updated Amoxicillin");

            // Act
            medicineRepository.save(medicine);
            entityManager.flush();
            entityManager.clear();

            // Assert
            Medicine updated = medicineRepository.findById(medicine.getId()).orElseThrow();
            assertThat(updated.getName()).isEqualTo("Updated Amoxicillin");
        }
    }

    @Nested
    @DisplayName("Filtering Tests")
    class FilteringTests {

        @Test
        @DisplayName("Should filter medicines by category")
        void shouldFilterMedicinesByCategory() {
            // Arrange
            Medicine medicine2 = Medicine.builder()
                    .name("Paracetamol")
                    .category(category)
                    .build();
            entityManager.persist(medicine2);
            entityManager.flush();

            // Act
            List<Medicine> medicines = medicineRepository.findAll();

            // Assert
            assertThat(medicines).hasSize(2);
            assertThat(medicines).allMatch(m -> m.getCategory().equals(category));
        }

        @Test
        @DisplayName("Should handle null category")
        void shouldHandleNullCategory() {
            // Arrange
            Medicine medicineWithoutCategory = Medicine.builder()
                    .name("Generic Medicine")
                    .build();
            entityManager.persist(medicineWithoutCategory);
            entityManager.flush();

            // Act
            Optional<Medicine> found = medicineRepository.findById(medicineWithoutCategory.getId());

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getCategory()).isNull();
        }
    }

    @Nested
    @DisplayName("Data Validation Tests")
    class DataValidationTests {

        @Test
        @DisplayName("Should store all medicine fields correctly")
        void shouldStoreAllMedicineFieldsCorrectly() {
            // Act
            Medicine found = medicineRepository.findById(medicine.getId()).orElseThrow();

            // Assert
            assertThat(found.getName()).isEqualTo("Amoxicillin");
            assertThat(found.getActiveIngredient()).isEqualTo("Amoxicillin trihydrate");
            assertThat(found.getBrandName()).isEqualTo("Amoxil");
            assertThat(found.getManufacturer()).isEqualTo("ABC Pharma");
            assertThat(found.getCountry()).isEqualTo("Vietnam");
        }

        @Test
        @DisplayName("Should handle long text fields")
        void shouldHandleLongTextFields() {
            // Arrange
            String longDescription = "A".repeat(250); // Use 250 to stay within VARCHAR(255) default
            medicine.setActiveIngredient(longDescription);

            // Act
            medicineRepository.save(medicine);
            entityManager.flush();
            entityManager.clear();

            // Assert
            Medicine found = medicineRepository.findById(medicine.getId()).orElseThrow();
            assertThat(found.getActiveIngredient()).hasSize(250);
        }
    }
}

