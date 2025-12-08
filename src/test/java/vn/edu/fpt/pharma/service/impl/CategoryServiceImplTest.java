package vn.edu.fpt.pharma.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import vn.edu.fpt.pharma.BaseServiceTest;
import vn.edu.fpt.pharma.dto.category.CategoryRequest;
import vn.edu.fpt.pharma.dto.category.CategoryResponse;
import vn.edu.fpt.pharma.entity.Category;
import vn.edu.fpt.pharma.exception.EntityInUseException;
import vn.edu.fpt.pharma.repository.CategoryRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CategoryServiceImpl - 12 tests
 * Strategy: Full coverage for create/update/delete with validation rules
 */
@DisplayName("CategoryServiceImpl Tests")
class CategoryServiceImplTest extends BaseServiceTest {

    @Mock
    private CategoryRepository categoryRepository;


    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category testCategory;
    private CategoryRequest validRequest;

    @BeforeEach
    void setUp() {
        testCategory = Category.builder()
                .name("Thuốc giảm đau")
                .description("Các loại thuốc giảm đau, hạ sốt")
                .build();
        testCategory.setId(1L);

        validRequest = CategoryRequest.builder()
                .categoryName("Thuốc giảm đau")
                .description("Các loại thuốc giảm đau, hạ sốt")
                .build();
    }

    @Nested
    @DisplayName("createCategory() tests - 4 tests")
    class CreateCategoryTests {

        @Test
        @DisplayName("Should create category with valid request")
        void createCategory_withValidRequest_shouldCreateCategory() {
            // Arrange
            when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
                Category category = invocation.getArgument(0);
                category.setId(1L);
                return category;
            });

            // Act
            CategoryResponse result = categoryService.createCategory(validRequest);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCategoryName()).isEqualTo("Thuốc giảm đau");
            assertThat(result.getDescription()).isEqualTo("Các loại thuốc giảm đau, hạ sốt");

            verify(categoryRepository).save(argThat(category ->
                category.getName().equals("Thuốc giảm đau") &&
                category.getDescription().equals("Các loại thuốc giảm đau, hạ sốt")
            ));
        }

        @Test
        @DisplayName("Should create category with only required fields")
        void createCategory_withOnlyRequiredFields_shouldCreateSuccessfully() {
            // Arrange
            CategoryRequest minimalRequest = CategoryRequest.builder()
                    .categoryName("Kháng sinh")
                    .build();

            when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
                Category category = invocation.getArgument(0);
                category.setId(1L);
                return category;
            });

            // Act
            CategoryResponse result = categoryService.createCategory(minimalRequest);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCategoryName()).isEqualTo("Kháng sinh");

            verify(categoryRepository).save(argThat(category ->
                category.getName().equals("Kháng sinh") &&
                category.getDescription() == null
            ));
        }

        @Test
        @DisplayName("Should create category with description null")
        void createCategory_withNullDescription_shouldCreateSuccessfully() {
            // Arrange
            validRequest.setDescription(null);

            when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
                Category category = invocation.getArgument(0);
                category.setId(1L);
                return category;
            });

            // Act
            CategoryResponse result = categoryService.createCategory(validRequest);

            // Assert
            assertThat(result).isNotNull();
            verify(categoryRepository).save(argThat(category ->
                category.getDescription() == null
            ));
        }

        @Test
        @DisplayName("Should handle repository save failure")
        void createCategory_whenRepositorySaveFails_shouldPropagateException() {
            // Arrange
            when(categoryRepository.save(any())).thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            assertThatThrownBy(() -> categoryService.createCategory(validRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Database error");
        }
    }

    @Nested
    @DisplayName("updateCategory() tests - 4 tests")
    class UpdateCategoryTests {

        @Test
        @DisplayName("Should update category with valid request")
        void updateCategory_withValidRequest_shouldUpdateCategory() {
            // Arrange
            validRequest.setCategoryName("Updated Category Name");
            validRequest.setDescription("Updated description");

            when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
            when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

            // Act
            CategoryResponse result = categoryService.updateCategory(1L, validRequest);

            // Assert
            assertThat(result).isNotNull();
            verify(categoryRepository).findById(1L);
            verify(categoryRepository).save(testCategory);
            assertThat(testCategory.getName()).isEqualTo("Updated Category Name");
            assertThat(testCategory.getDescription()).isEqualTo("Updated description");
        }

        @Test
        @DisplayName("Should throw exception when category not found")
        void updateCategory_whenCategoryNotFound_shouldThrowException() {
            // Arrange
            when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> categoryService.updateCategory(1L, validRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Category not found");

            verify(categoryRepository).findById(1L);
            verify(categoryRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should update only non-null fields")
        void updateCategory_withPartialData_shouldUpdateOnlyNonNullFields() {
            // Arrange
            CategoryRequest partialRequest = CategoryRequest.builder()
                    .categoryName("New Name Only")
                    .build(); // description is null

            String originalDescription = testCategory.getDescription();

            when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
            when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

            // Act
            CategoryResponse result = categoryService.updateCategory(1L, partialRequest);

            // Assert
            assertThat(result).isNotNull();
            assertThat(testCategory.getName()).isEqualTo("New Name Only");
            // Description should remain unchanged since request had null description
            verify(categoryRepository).save(testCategory);
        }

        @Test
        @DisplayName("Should not update name when name is null")
        void updateCategory_withNullName_shouldNotUpdateName() {
            // Arrange
            CategoryRequest partialRequest = CategoryRequest.builder()
                    .description("New description only")
                    .build(); // categoryName is null

            when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
            when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

            // Act
            CategoryResponse result = categoryService.updateCategory(1L, partialRequest);

            // Assert
            assertThat(result).isNotNull();
            assertThat(testCategory.getDescription()).isEqualTo("New description only");
            verify(categoryRepository).save(testCategory);
        }
    }

    @Nested
    @DisplayName("deleteById() tests - 3 tests")
    class DeleteByIdTests {

        @Test
        @DisplayName("Should delete category when no medicines exist")
        void deleteById_whenNoMedicinesExist_shouldDeleteSuccessfully() {
            // Arrange
            when(categoryRepository.countMedicinesByCategoryId(1L)).thenReturn(0L);

            // Act
            categoryService.deleteById(1L);

            // Assert
            verify(categoryRepository).countMedicinesByCategoryId(1L);
        }

        @Test
        @DisplayName("Should throw exception when category has medicines")
        void deleteById_whenCategoryHasMedicines_shouldThrowException() {
            // Arrange
            when(categoryRepository.countMedicinesByCategoryId(1L)).thenReturn(5L);

            // Act & Assert
            assertThatThrownBy(() -> categoryService.deleteById(1L))
                    .isInstanceOf(EntityInUseException.class)
                    .hasMessageContaining("Danh mục")
                    .hasMessageContaining("5 thuốc");

            verify(categoryRepository).countMedicinesByCategoryId(1L);
            verify(categoryRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("Should include medicine count in exception message")
        void deleteById_withMultipleMedicines_shouldShowCorrectCount() {
            // Arrange
            when(categoryRepository.countMedicinesByCategoryId(1L)).thenReturn(25L);

            // Act & Assert
            assertThatThrownBy(() -> categoryService.deleteById(1L))
                    .isInstanceOf(EntityInUseException.class)
                    .hasMessageContaining("25 thuốc");

            verify(categoryRepository).countMedicinesByCategoryId(1L);
        }
    }

    @Nested
    @DisplayName("getCategoryById() tests - 2 tests")
    class GetCategoryByIdTests {

        @Test
        @DisplayName("Should return category when found")
        void getCategoryById_whenCategoryExists_shouldReturnCategory() {
            // Arrange
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

            // Act
            CategoryResponse result = categoryService.getCategoryById(1L);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCategoryName()).isEqualTo("Thuốc giảm đau");
            assertThat(result.getDescription()).isEqualTo("Các loại thuốc giảm đau, hạ sốt");
            verify(categoryRepository).findById(1L);
        }

        @Test
        @DisplayName("Should throw exception when category not found")
        void getCategoryById_whenCategoryNotFound_shouldThrowException() {
            // Arrange
            when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> categoryService.getCategoryById(1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Category not found");

            verify(categoryRepository).findById(1L);
        }
    }
}

