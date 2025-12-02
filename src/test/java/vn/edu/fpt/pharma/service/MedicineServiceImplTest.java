package vn.edu.fpt.pharma.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import vn.edu.fpt.pharma.dto.DataTableRequest;
import vn.edu.fpt.pharma.dto.DataTableResponse;
import vn.edu.fpt.pharma.dto.medicine.MedicineRequest;
import vn.edu.fpt.pharma.dto.medicine.MedicineResponse;
import vn.edu.fpt.pharma.dto.medicine.MedicineSearchDTO;
import vn.edu.fpt.pharma.entity.Category;
import vn.edu.fpt.pharma.entity.Medicine;
import vn.edu.fpt.pharma.exception.EntityInUseException;
import vn.edu.fpt.pharma.repository.CategoryRepository;
import vn.edu.fpt.pharma.repository.MedicineRepository;
import vn.edu.fpt.pharma.service.impl.MedicineServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MedicineServiceImpl Tests")
class MedicineServiceImplTest {

    @Mock
    private MedicineRepository medicineRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private MedicineServiceImpl medicineService;

    private Category category;
    private Medicine medicine;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .name("Kháng sinh")
                .build();
        category.setId(1L);

        medicine = Medicine.builder()
                .name("Amoxicillin")
                .brandName("Amoxil")
                .activeIngredient("Amoxicillin trihydrate")
                .manufacturer("ABC Pharma")
                .country("Vietnam")
                .category(category)
                .build();
        medicine.setId(1L);
    }

    @Nested
    @DisplayName("Get Medicines Tests")
    class GetMedicinesTests {

        @Test
        @DisplayName("Should get medicines with pagination")
        void shouldGetMedicinesWithPagination() {
            // Arrange
            DataTableRequest request = new DataTableRequest(0, 0, 10, null, null, "asc");

            List<Medicine> medicines = List.of(medicine);
            Page<Medicine> page = new PageImpl<>(medicines);

            when(medicineRepository.findAll(any(Pageable.class))).thenReturn(page);
            when(medicineRepository.count()).thenReturn(1L);

            // Act
            DataTableResponse<MedicineResponse> response = medicineService.getMedicines(request, null);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.data()).hasSize(1);
            assertThat(response.data().get(0).getMedicineName()).isEqualTo("Amoxicillin");
            verify(medicineRepository).findAll(any(Pageable.class));
        }

        @Test
        @DisplayName("Should filter medicines by status")
        void shouldFilterMedicinesByStatus() {
            // Arrange
            DataTableRequest request = new DataTableRequest(0, 0, 10, null, null, "asc");

            List<Medicine> medicines = List.of(medicine);
            Page<Medicine> page = new PageImpl<>(medicines);

            when(medicineRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
            when(medicineRepository.count()).thenReturn(1L);

            // Act
            DataTableResponse<MedicineResponse> response = medicineService.getMedicines(request, 1);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.data()).hasSize(1);
            verify(medicineRepository).findAll(any(Specification.class), any(Pageable.class));
        }

        @Test
        @DisplayName("Should search medicines with keyword")
        void shouldSearchMedicinesWithKeyword() {
            // Arrange
            DataTableRequest request = new DataTableRequest(0, 0, 10, "Amox", null, "asc");

            List<Medicine> medicines = List.of(medicine);
            Page<Medicine> page = new PageImpl<>(medicines);

            when(medicineRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
            when(medicineRepository.count()).thenReturn(1L);

            // Act
            DataTableResponse<MedicineResponse> response = medicineService.getMedicines(request, null);

            // Assert
            assertThat(response).isNotNull();
            verify(medicineRepository).findAll(any(Specification.class), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("Create Medicine Tests")
    class CreateMedicineTests {

        @Test
        @DisplayName("Should create medicine successfully")
        void shouldCreateMedicineSuccessfully() {
            // Arrange
            MedicineRequest request = new MedicineRequest();
            request.setMedicineName("New Medicine");
            request.setActiveIngredient("Active Ingredient");
            request.setBrandName("Brand");
            request.setManufacturer("Manufacturer");
            request.setCountryOfOrigin("Vietnam");
            request.setCategoryId(1L);

            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(medicineRepository.save(any(Medicine.class))).thenReturn(medicine);

            // Act
            MedicineResponse response = medicineService.createMedicine(request);

            // Assert
            assertThat(response).isNotNull();
            verify(categoryRepository).findById(1L);
            verify(medicineRepository).save(any(Medicine.class));
        }

        @Test
        @DisplayName("Should create medicine without category")
        void shouldCreateMedicineWithoutCategory() {
            // Arrange
            MedicineRequest request = new MedicineRequest();
            request.setMedicineName("New Medicine");
            request.setActiveIngredient("Active Ingredient");
            request.setCategoryId(null);

            when(medicineRepository.save(any(Medicine.class))).thenReturn(medicine);

            // Act
            MedicineResponse response = medicineService.createMedicine(request);

            // Assert
            assertThat(response).isNotNull();
            verify(categoryRepository, never()).findById(anyLong());
            verify(medicineRepository).save(any(Medicine.class));
        }

        @Test
        @DisplayName("Should throw exception when category not found")
        void shouldThrowExceptionWhenCategoryNotFound() {
            // Arrange
            MedicineRequest request = new MedicineRequest();
            request.setMedicineName("New Medicine");
            request.setCategoryId(999L);

            when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> medicineService.createMedicine(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Category not found");
        }
    }

    @Nested
    @DisplayName("Update Medicine Tests")
    class UpdateMedicineTests {

        @Test
        @DisplayName("Should update medicine successfully")
        void shouldUpdateMedicineSuccessfully() {
            // Arrange
            MedicineRequest request = new MedicineRequest();
            request.setMedicineName("Updated Medicine");
            request.setActiveIngredient("Updated Ingredient");
            request.setCategoryId(1L);

            when(medicineRepository.findById(1L)).thenReturn(Optional.of(medicine));
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(medicineRepository.save(any(Medicine.class))).thenReturn(medicine);

            // Act
            MedicineResponse response = medicineService.updateMedicine(1L, request);

            // Assert
            assertThat(response).isNotNull();
            verify(medicineRepository).findById(1L);
            verify(medicineRepository).save(any(Medicine.class));
        }

        @Test
        @DisplayName("Should throw exception when medicine not found")
        void shouldThrowExceptionWhenMedicineNotFound() {
            // Arrange
            MedicineRequest request = new MedicineRequest();
            when(medicineRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> medicineService.updateMedicine(999L, request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Medicine not found");
        }

        @Test
        @DisplayName("Should update only provided fields")
        void shouldUpdateOnlyProvidedFields() {
            // Arrange
            MedicineRequest request = new MedicineRequest();
            request.setMedicineName("Updated Name");
            // Other fields are null

            when(medicineRepository.findById(1L)).thenReturn(Optional.of(medicine));
            when(medicineRepository.save(any(Medicine.class))).thenReturn(medicine);

            // Act
            MedicineResponse response = medicineService.updateMedicine(1L, request);

            // Assert
            assertThat(response).isNotNull();
            verify(medicineRepository).save(any(Medicine.class));
        }
    }

    @Nested
    @DisplayName("Get Medicine By Id Tests")
    class GetMedicineByIdTests {

        @Test
        @DisplayName("Should get medicine by id successfully")
        void shouldGetMedicineByIdSuccessfully() {
            // Arrange
            when(medicineRepository.findById(1L)).thenReturn(Optional.of(medicine));

            // Act
            MedicineResponse response = medicineService.getMedicineById(1L);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getMedicineName()).isEqualTo("Amoxicillin");
            verify(medicineRepository).findById(1L);
        }

        @Test
        @DisplayName("Should throw exception when medicine not found")
        void shouldThrowExceptionWhenMedicineNotFound() {
            // Arrange
            when(medicineRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> medicineService.getMedicineById(999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Medicine not found");
        }
    }

    @Nested
    @DisplayName("Delete Medicine Tests")
    class DeleteMedicineTests {

        @Test
        @DisplayName("Should delete medicine successfully when no variants exist")
        void shouldDeleteMedicineSuccessfully() {
            // Arrange
            when(medicineRepository.countVariantsByMedicineId(1L)).thenReturn(0L);
            doNothing().when(medicineRepository).deleteById(1L);

            // Act
            medicineService.deleteById(1L);

            // Assert
            verify(medicineRepository).countVariantsByMedicineId(1L);
            verify(medicineRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw EntityInUseException when variants exist")
        void shouldThrowExceptionWhenVariantsExist() {
            // Arrange
            when(medicineRepository.countVariantsByMedicineId(1L)).thenReturn(3L);

            // Act & Assert
            assertThatThrownBy(() -> medicineService.deleteById(1L))
                    .isInstanceOf(EntityInUseException.class)
                    .hasMessageContaining("Thuốc")
                    .hasMessageContaining("3 biến thể");
        }
    }

    @Nested
    @DisplayName("Search Medicines Tests")
    class SearchMedicinesTests {

        @Test
        @DisplayName("Should search medicines by keyword")
        void shouldSearchMedicinesByKeyword() {
            // Arrange
            Object[] row = new Object[]{1L, "Amoxicillin", "Amoxicillin trihydrate", "Amoxil", "ABC Pharma", "500mg"};
            List<Object[]> rows = java.util.Collections.singletonList(row);

            when(medicineRepository.searchMedicinesByKeyword("Amox")).thenReturn(rows);

            // Act
            List<MedicineSearchDTO> results = medicineService.searchMedicinesByKeyword("Amox");

            // Assert
            assertThat(results).hasSize(1);
            assertThat(results.get(0).id()).isEqualTo(1L);
            assertThat(results.get(0).name()).isEqualTo("Amoxicillin");
            verify(medicineRepository).searchMedicinesByKeyword("Amox");
        }

        @Test
        @DisplayName("Should return empty list when no matches found")
        void shouldReturnEmptyListWhenNoMatches() {
            // Arrange
            when(medicineRepository.searchMedicinesByKeyword("NonExistent")).thenReturn(List.of());

            // Act
            List<MedicineSearchDTO> results = medicineService.searchMedicinesByKeyword("NonExistent");

            // Assert
            assertThat(results).isEmpty();
            verify(medicineRepository).searchMedicinesByKeyword("NonExistent");
        }
    }
}
