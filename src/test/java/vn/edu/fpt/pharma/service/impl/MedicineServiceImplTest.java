package vn.edu.fpt.pharma.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import vn.edu.fpt.pharma.BaseServiceTest;
import vn.edu.fpt.pharma.dto.medicine.MedicineRequest;
import vn.edu.fpt.pharma.dto.medicine.MedicineResponse;
import vn.edu.fpt.pharma.entity.Category;
import vn.edu.fpt.pharma.entity.Medicine;
import vn.edu.fpt.pharma.exception.EntityInUseException;
import vn.edu.fpt.pharma.repository.CategoryRepository;
import vn.edu.fpt.pharma.repository.MedicineRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MedicineServiceImpl - 15 tests
 * Strategy: Full coverage for create/update/delete with validation rules
 */
@DisplayName("MedicineServiceImpl Tests")
class MedicineServiceImplTest extends BaseServiceTest {

    @Mock
    private MedicineRepository medicineRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private MedicineServiceImpl medicineService;

    private Category testCategory;
    private Medicine testMedicine;
    private MedicineRequest validRequest;

    @BeforeEach
    void setUp() {
        testCategory = Category.builder()
                .name("Thuốc giảm đau")
                .build();
        testCategory.setId(1L);

        testMedicine = Medicine.builder()
                .name("Paracetamol 500mg")
                .category(testCategory)
                .activeIngredient("Paracetamol")
                .brandName("Acephen")
                .manufacturer("DHG Pharma")
                .country("Vietnam")
                .build();
        testMedicine.setId(1L);

        validRequest = MedicineRequest.builder()
                .medicineName("Paracetamol 500mg")
                .categoryId(1L)
                .activeIngredient("Paracetamol")
                .brandName("Acephen")
                .manufacturer("DHG Pharma")
                .countryOfOrigin("Vietnam")
                .build();
    }

    @Nested
    @DisplayName("createMedicine() tests - 5 tests")
    class CreateMedicineTests {

        @Test
        @DisplayName("Should create medicine with valid request")
        void createMedicine_withValidRequest_shouldCreateMedicine() {
            // Arrange
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
            when(medicineRepository.save(any(Medicine.class))).thenAnswer(invocation -> {
                Medicine medicine = invocation.getArgument(0);
                medicine.setId(1L);
                return medicine;
            });

            // Act
            MedicineResponse result = medicineService.createMedicine(validRequest);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getMedicineName()).isEqualTo("Paracetamol 500mg");
            assertThat(result.getCategoryName()).isEqualTo("Thuốc giảm đau");

            verify(categoryRepository).findById(1L);
            verify(medicineRepository).save(argThat(medicine ->
                medicine.getName().equals("Paracetamol 500mg") &&
                medicine.getCategory().getId().equals(1L) &&
                medicine.getActiveIngredient().equals("Paracetamol")
            ));
        }

        @Test
        @DisplayName("Should throw exception when category not found")
        void createMedicine_whenCategoryNotFound_shouldThrowException() {
            // Arrange
            when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> medicineService.createMedicine(validRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Category not found");

            verify(categoryRepository).findById(1L);
            verify(medicineRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should create medicine without category when categoryId is null")
        void createMedicine_withNullCategoryId_shouldCreateWithoutCategory() {
            // Arrange
            validRequest.setCategoryId(null);
            when(medicineRepository.save(any(Medicine.class))).thenAnswer(invocation -> {
                Medicine medicine = invocation.getArgument(0);
                medicine.setId(1L);
                return medicine;
            });

            // Act
            MedicineResponse result = medicineService.createMedicine(validRequest);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getMedicineName()).isEqualTo("Paracetamol 500mg");

            verify(categoryRepository, never()).findById(any());
            verify(medicineRepository).save(argThat(medicine ->
                medicine.getCategory() == null
            ));
        }

        @Test
        @DisplayName("Should create medicine with all optional fields")
        void createMedicine_withAllOptionalFields_shouldCreateSuccessfully() {
            // Arrange
            validRequest.setActiveIngredient("Active Ingredient");
            validRequest.setBrandName("Brand Name");
            validRequest.setManufacturer("Manufacturer Name");
            validRequest.setCountryOfOrigin("Country");

            when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
            when(medicineRepository.save(any(Medicine.class))).thenAnswer(invocation -> {
                Medicine medicine = invocation.getArgument(0);
                medicine.setId(1L);
                return medicine;
            });

            // Act
            MedicineResponse result = medicineService.createMedicine(validRequest);

            // Assert
            assertThat(result).isNotNull();
            verify(medicineRepository).save(argThat(medicine ->
                medicine.getActiveIngredient().equals("Active Ingredient") &&
                medicine.getBrandName().equals("Brand Name") &&
                medicine.getManufacturer().equals("Manufacturer Name") &&
                medicine.getCountry().equals("Country")
            ));
        }

        @Test
        @DisplayName("Should handle repository save failure")
        void createMedicine_whenRepositorySaveFails_shouldPropagateException() {
            // Arrange
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
            when(medicineRepository.save(any())).thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            assertThatThrownBy(() -> medicineService.createMedicine(validRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Database error");
        }
    }

    @Nested
    @DisplayName("updateMedicine() tests - 5 tests")
    class UpdateMedicineTests {

        @Test
        @DisplayName("Should update medicine with valid request")
        void updateMedicine_withValidRequest_shouldUpdateMedicine() {
            // Arrange
            validRequest.setMedicineName("Updated Name");
            validRequest.setCategoryId(2L);

            Category newCategory = Category.builder()
                    .name("Kháng sinh")
                    .build();
            newCategory.setId(2L);

            when(medicineRepository.findById(1L)).thenReturn(Optional.of(testMedicine));
            when(categoryRepository.findById(2L)).thenReturn(Optional.of(newCategory));
            when(medicineRepository.save(any(Medicine.class))).thenReturn(testMedicine);

            // Act
            MedicineResponse result = medicineService.updateMedicine(1L, validRequest);

            // Assert
            assertThat(result).isNotNull();
            verify(medicineRepository).findById(1L);
            verify(categoryRepository).findById(2L);
            verify(medicineRepository).save(testMedicine);
        }

        @Test
        @DisplayName("Should throw exception when medicine not found")
        void updateMedicine_whenMedicineNotFound_shouldThrowException() {
            // Arrange
            when(medicineRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> medicineService.updateMedicine(1L, validRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Medicine not found");

            verify(medicineRepository).findById(1L);
            verify(medicineRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when new category not found")
        void updateMedicine_whenCategoryNotFound_shouldThrowException() {
            // Arrange
            validRequest.setCategoryId(999L);
            when(medicineRepository.findById(1L)).thenReturn(Optional.of(testMedicine));
            when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> medicineService.updateMedicine(1L, validRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Category not found");

            verify(medicineRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should update only non-null fields")
        void updateMedicine_withPartialData_shouldUpdateOnlyNonNullFields() {
            // Arrange
            MedicineRequest partialRequest = MedicineRequest.builder()
                    .medicineName("New Name Only")
                    .build(); // Only name, others null

            when(medicineRepository.findById(1L)).thenReturn(Optional.of(testMedicine));
            when(medicineRepository.save(any(Medicine.class))).thenReturn(testMedicine);

            // Act
            MedicineResponse result = medicineService.updateMedicine(1L, partialRequest);

            // Assert
            assertThat(result).isNotNull();
            verify(medicineRepository).save(argThat(medicine ->
                medicine.getName().equals("New Name Only") &&
                medicine.getCategory() != null // Category should remain unchanged
            ));
        }

        @Test
        @DisplayName("Should not update category when categoryId is null")
        void updateMedicine_withNullCategoryId_shouldNotUpdateCategory() {
            // Arrange
            validRequest.setCategoryId(null);
            validRequest.setMedicineName("Updated Name");

            when(medicineRepository.findById(1L)).thenReturn(Optional.of(testMedicine));
            when(medicineRepository.save(any(Medicine.class))).thenReturn(testMedicine);

            // Act
            MedicineResponse result = medicineService.updateMedicine(1L, validRequest);

            // Assert
            assertThat(result).isNotNull();
            verify(categoryRepository, never()).findById(any());
            verify(medicineRepository).save(testMedicine);
        }
    }

    @Nested
    @DisplayName("deleteById() tests - 3 tests")
    class DeleteByIdTests {

        @Test
        @DisplayName("Should delete medicine when no variants exist")
        void deleteById_whenNoVariantsExist_shouldDeleteSuccessfully() {
            // Arrange
            when(medicineRepository.countVariantsByMedicineId(1L)).thenReturn(0L);

            // Act
            medicineService.deleteById(1L);

            // Assert
            verify(medicineRepository).countVariantsByMedicineId(1L);
        }

        @Test
        @DisplayName("Should throw exception when medicine has variants")
        void deleteById_whenMedicineHasVariants_shouldThrowException() {
            // Arrange
            when(medicineRepository.countVariantsByMedicineId(1L)).thenReturn(3L);

            // Act & Assert
            assertThatThrownBy(() -> medicineService.deleteById(1L))
                    .isInstanceOf(EntityInUseException.class)
                    .hasMessageContaining("Thuốc")
                    .hasMessageContaining("3 biến thể");

            verify(medicineRepository).countVariantsByMedicineId(1L);
            verify(medicineRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("Should include variant count in exception message")
        void deleteById_withMultipleVariants_shouldShowCorrectCount() {
            // Arrange
            when(medicineRepository.countVariantsByMedicineId(1L)).thenReturn(10L);

            // Act & Assert
            assertThatThrownBy(() -> medicineService.deleteById(1L))
                    .isInstanceOf(EntityInUseException.class)
                    .hasMessageContaining("10 biến thể");

            verify(medicineRepository).countVariantsByMedicineId(1L);
        }
    }

    @Nested
    @DisplayName("getMedicineById() tests - 2 tests")
    class GetMedicineByIdTests {

        @Test
        @DisplayName("Should return medicine when found")
        void getMedicineById_whenMedicineExists_shouldReturnMedicine() {
            // Arrange
            when(medicineRepository.findById(1L)).thenReturn(Optional.of(testMedicine));

            // Act
            MedicineResponse result = medicineService.getMedicineById(1L);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getMedicineName()).isEqualTo("Paracetamol 500mg");
            verify(medicineRepository).findById(1L);
        }

        @Test
        @DisplayName("Should throw exception when medicine not found")
        void getMedicineById_whenMedicineNotFound_shouldThrowException() {
            // Arrange
            when(medicineRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> medicineService.getMedicineById(1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Medicine not found");

            verify(medicineRepository).findById(1L);
        }
    }
}

