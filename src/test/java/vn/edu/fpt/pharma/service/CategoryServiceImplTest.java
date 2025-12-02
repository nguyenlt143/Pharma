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
import vn.edu.fpt.pharma.dto.category.CategoryRequest;
import vn.edu.fpt.pharma.dto.category.CategoryResponse;
import vn.edu.fpt.pharma.entity.Category;
import vn.edu.fpt.pharma.exception.EntityInUseException;
import vn.edu.fpt.pharma.repository.CategoryRepository;
import vn.edu.fpt.pharma.service.impl.CategoryServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryServiceImpl Tests")
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .name("Kháng sinh")
                .description("Nhóm thuốc kháng sinh")
                .build();
        category.setId(1L);
    }

    @Nested
    @DisplayName("Get Categories Tests")
    class GetCategoriesTests {

        @Test
        @DisplayName("Should get categories with pagination")
        void shouldGetCategoriesWithPagination() {
            // Arrange
            DataTableRequest request = new DataTableRequest(0, 0, 10, null, null, "asc");

            List<Category> categories = List.of(category);
            Page<Category> page = new PageImpl<>(categories);

            when(categoryRepository.findAll(any(Pageable.class))).thenReturn(page);
            when(categoryRepository.count()).thenReturn(1L);

            // Act
            DataTableResponse<CategoryResponse> response = categoryService.getCategories(request);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.data()).hasSize(1);
            assertThat(response.data().get(0).getCategoryName()).isEqualTo("Kháng sinh");
            verify(categoryRepository).findAll(any(Pageable.class));
        }

        @Test
        @DisplayName("Should search categories with keyword")
        void shouldSearchCategoriesWithKeyword() {
            // Arrange
            DataTableRequest request = new DataTableRequest(0, 0, 10, "Kháng", null, "asc");

            List<Category> categories = List.of(category);
            Page<Category> page = new PageImpl<>(categories);

            when(categoryRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
            when(categoryRepository.count()).thenReturn(1L);

            // Act
            DataTableResponse<CategoryResponse> response = categoryService.getCategories(request);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.data()).hasSize(1);
            verify(categoryRepository).findAll(any(Specification.class), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("Create Category Tests")
    class CreateCategoryTests {

        @Test
        @DisplayName("Should create category successfully")
        void shouldCreateCategorySuccessfully() {
            // Arrange
            CategoryRequest request = new CategoryRequest();
            request.setCategoryName("Thuốc giảm đau");
            request.setDescription("Nhóm thuốc giảm đau");

            when(categoryRepository.save(any(Category.class))).thenReturn(category);

            // Act
            CategoryResponse response = categoryService.createCategory(request);

            // Assert
            assertThat(response).isNotNull();
            verify(categoryRepository).save(any(Category.class));
        }

        @Test
        @DisplayName("Should create category without description")
        void shouldCreateCategoryWithoutDescription() {
            // Arrange
            CategoryRequest request = new CategoryRequest();
            request.setCategoryName("New Category");
            request.setDescription(null);

            when(categoryRepository.save(any(Category.class))).thenReturn(category);

            // Act
            CategoryResponse response = categoryService.createCategory(request);

            // Assert
            assertThat(response).isNotNull();
            verify(categoryRepository).save(any(Category.class));
        }
    }

    @Nested
    @DisplayName("Update Category Tests")
    class UpdateCategoryTests {

        @Test
        @DisplayName("Should update category successfully")
        void shouldUpdateCategorySuccessfully() {
            // Arrange
            CategoryRequest request = new CategoryRequest();
            request.setCategoryName("Updated Category");
            request.setDescription("Updated Description");

            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(categoryRepository.save(any(Category.class))).thenReturn(category);

            // Act
            CategoryResponse response = categoryService.updateCategory(1L, request);

            // Assert
            assertThat(response).isNotNull();
            verify(categoryRepository).findById(1L);
            verify(categoryRepository).save(any(Category.class));
        }

        @Test
        @DisplayName("Should throw exception when category not found")
        void shouldThrowExceptionWhenCategoryNotFound() {
            // Arrange
            CategoryRequest request = new CategoryRequest();
            when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> categoryService.updateCategory(999L, request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Category not found");
        }

        @Test
        @DisplayName("Should update only provided fields")
        void shouldUpdateOnlyProvidedFields() {
            // Arrange
            CategoryRequest request = new CategoryRequest();
            request.setCategoryName("Updated Name");
            request.setDescription(null);

            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(categoryRepository.save(any(Category.class))).thenReturn(category);

            // Act
            CategoryResponse response = categoryService.updateCategory(1L, request);

            // Assert
            assertThat(response).isNotNull();
            verify(categoryRepository).save(any(Category.class));
        }
    }

    @Nested
    @DisplayName("Get Category By Id Tests")
    class GetCategoryByIdTests {

        @Test
        @DisplayName("Should get category by id successfully")
        void shouldGetCategoryByIdSuccessfully() {
            // Arrange
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

            // Act
            CategoryResponse response = categoryService.getCategoryById(1L);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getCategoryName()).isEqualTo("Kháng sinh");
            verify(categoryRepository).findById(1L);
        }

        @Test
        @DisplayName("Should throw exception when category not found")
        void shouldThrowExceptionWhenCategoryNotFound() {
            // Arrange
            when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> categoryService.getCategoryById(999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Category not found");
        }
    }

    @Nested
    @DisplayName("Delete Category Tests")
    class DeleteCategoryTests {

        @Test
        @DisplayName("Should delete category successfully when no medicines exist")
        void shouldDeleteCategorySuccessfully() {
            // Arrange
            when(categoryRepository.countMedicinesByCategoryId(1L)).thenReturn(0L);
            doNothing().when(categoryRepository).deleteById(1L);

            // Act
            categoryService.deleteById(1L);

            // Assert
            verify(categoryRepository).countMedicinesByCategoryId(1L);
            verify(categoryRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw EntityInUseException when medicines exist")
        void shouldThrowExceptionWhenMedicinesExist() {
            // Arrange
            when(categoryRepository.countMedicinesByCategoryId(1L)).thenReturn(5L);

            // Act & Assert
            assertThatThrownBy(() -> categoryService.deleteById(1L))
                    .isInstanceOf(EntityInUseException.class)
                    .hasMessageContaining("Danh mục")
                    .hasMessageContaining("5 thuốc");
        }
    }
}
