package vn.edu.fpt.pharma.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import vn.edu.fpt.pharma.BaseServiceTest;
import vn.edu.fpt.pharma.entity.Batch;
import vn.edu.fpt.pharma.repository.BatchRepository;
import vn.edu.fpt.pharma.service.AuditService;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BatchServiceImpl - 4 tests
 * Strategy: Coverage for inherited BaseServiceImpl methods
 */
@DisplayName("BatchServiceImpl Tests")
class BatchServiceImplTest extends BaseServiceTest {

    @Mock
    private BatchRepository batchRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private BatchServiceImpl batchService;

    private Batch testBatch;

    @BeforeEach
    void setUp() {
        testBatch = new Batch();
        testBatch.setId(1L);
        testBatch.setBatchCode("BATCH-001");
        testBatch.setExpiryDate(LocalDate.now().plusYears(2));
    }

    @Nested
    @DisplayName("findById() tests - 2 tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should return batch when found")
        void findById_whenBatchExists_shouldReturnBatch() {
            // Arrange
            when(batchRepository.findById(1L)).thenReturn(Optional.of(testBatch));

            // Act
            Batch result = batchService.findById(1L);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getBatchCode()).isEqualTo("BATCH-001");
            verify(batchRepository).findById(1L);
        }

        @Test
        @DisplayName("Should return null when batch not found")
        void findById_whenBatchNotFound_shouldReturnNull() {
            // Arrange
            when(batchRepository.findById(999L)).thenReturn(Optional.empty());

            // Act
            Batch result = batchService.findById(999L);

            // Assert
            assertThat(result).isNull();
            verify(batchRepository).findById(999L);
        }
    }

    @Nested
    @DisplayName("create() tests - 2 tests")
    class CreateTests {

        @Test
        @DisplayName("Should create batch successfully")
        void create_withValidBatch_shouldCreateSuccessfully() {
            // Arrange
            Batch newBatch = new Batch();
            newBatch.setBatchCode("NEW-BATCH");
            when(batchRepository.save(any(Batch.class))).thenReturn(testBatch);

            // Act
            Batch result = batchService.create(newBatch);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            verify(batchRepository).save(newBatch);
        }

        @Test
        @DisplayName("Should handle repository save failure")
        void create_whenRepositoryFails_shouldPropagateException() {
            // Arrange
            Batch newBatch = new Batch();
            newBatch.setBatchCode("NEW-BATCH");
            when(batchRepository.save(any())).thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            assertThatThrownBy(() -> batchService.create(newBatch))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Database error");
        }
    }
}
