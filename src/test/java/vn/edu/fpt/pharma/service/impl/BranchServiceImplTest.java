package vn.edu.fpt.pharma.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import vn.edu.fpt.pharma.BaseServiceTest;
import vn.edu.fpt.pharma.entity.Branch;
import vn.edu.fpt.pharma.repository.BranchRepository;
import vn.edu.fpt.pharma.service.AuditService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BranchServiceImpl - 4 tests
 * Strategy: Coverage for inherited BaseServiceImpl methods
 */
@DisplayName("BranchServiceImpl Tests")
class BranchServiceImplTest extends BaseServiceTest {

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private BranchServiceImpl branchService;

    private Branch testBranch;

    @BeforeEach
    void setUp() {
        testBranch = new Branch();
        testBranch.setId(1L);
        testBranch.setName("Chi nhánh Quận 1");
        testBranch.setAddress("123 Nguyễn Huệ, Q1, TP.HCM");
    }

    @Nested
    @DisplayName("findById() tests - 2 tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should return branch when found")
        void findById_whenBranchExists_shouldReturnBranch() {
            // Arrange
            when(branchRepository.findById(1L)).thenReturn(Optional.of(testBranch));

            // Act
            Branch result = branchService.findById(1L);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("Chi nhánh Quận 1");
            verify(branchRepository).findById(1L);
        }

        @Test
        @DisplayName("Should return null when branch not found")
        void findById_whenBranchNotFound_shouldReturnNull() {
            // Arrange
            when(branchRepository.findById(999L)).thenReturn(Optional.empty());

            // Act
            Branch result = branchService.findById(999L);

            // Assert
            assertThat(result).isNull();
            verify(branchRepository).findById(999L);
        }
    }

    @Nested
    @DisplayName("create() tests - 2 tests")
    class CreateTests {

        @Test
        @DisplayName("Should create branch successfully")
        void create_withValidBranch_shouldCreateSuccessfully() {
            // Arrange
            Branch newBranch = new Branch();
            newBranch.setName("New Branch");
            when(branchRepository.save(any(Branch.class))).thenReturn(testBranch);

            // Act
            Branch result = branchService.create(newBranch);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            verify(branchRepository).save(newBranch);
        }

        @Test
        @DisplayName("Should handle repository save failure")
        void create_whenRepositoryFails_shouldPropagateException() {
            // Arrange
            Branch newBranch = new Branch();
            newBranch.setName("New Branch");
            when(branchRepository.save(any())).thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            assertThatThrownBy(() -> branchService.create(newBranch))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Database error");
        }
    }
}
