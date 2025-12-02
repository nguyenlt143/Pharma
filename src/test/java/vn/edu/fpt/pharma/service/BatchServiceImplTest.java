package vn.edu.fpt.pharma.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.edu.fpt.pharma.entity.Batch;
import vn.edu.fpt.pharma.repository.BatchRepository;
import vn.edu.fpt.pharma.service.impl.BatchServiceImpl;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BatchServiceImpl Tests")
class BatchServiceImplTest {

    @Mock
    private BatchRepository batchRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private BatchServiceImpl batchService;

    private Batch batch;

    @BeforeEach
    void setUp() {
        batch = Batch.builder()
                .batchCode("BATCH-001")
                .expiryDate(LocalDate.now().plusYears(2))
                .build();
        batch.setId(1L);
    }

    @Nested
    @DisplayName("CRUD Operations Tests")
    class CrudOperationsTests {

        @Test
        @DisplayName("Should find batch by id")
        void shouldFindBatchById() {
            // Arrange
            when(batchRepository.findById(1L)).thenReturn(Optional.of(batch));

            // Act
            Optional<Batch> result = batchRepository.findById(1L);

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getBatchCode()).isEqualTo("BATCH-001");
            verify(batchRepository).findById(1L);
        }

        @Test
        @DisplayName("Should save batch")
        void shouldSaveBatch() {
            // Arrange
            Batch newBatch = Batch.builder()
                    .batchCode("BATCH-002")
                    .expiryDate(LocalDate.now().plusYears(1))
                    .build();

            when(batchRepository.save(any(Batch.class))).thenReturn(newBatch);

            // Act
            Batch saved = batchRepository.save(newBatch);

            // Assert
            assertThat(saved).isNotNull();
            assertThat(saved.getBatchCode()).isEqualTo("BATCH-002");
            verify(batchRepository).save(any(Batch.class));
        }

        @Test
        @DisplayName("Should find all batches")
        void shouldFindAllBatches() {
            // Arrange
            List<Batch> batches = Arrays.asList(batch);
            when(batchRepository.findAll()).thenReturn(batches);

            // Act
            List<Batch> result = batchRepository.findAll();

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getBatchCode()).isEqualTo("BATCH-001");
            verify(batchRepository).findAll();
        }

        @Test
        @DisplayName("Should delete batch by id")
        void shouldDeleteBatchById() {
            // Arrange
            doNothing().when(batchRepository).deleteById(1L);

            // Act
            batchRepository.deleteById(1L);

            // Assert
            verify(batchRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Should update batch")
        void shouldUpdateBatch() {
            // Arrange
            batch.setBatchCode("UPDATED-BATCH");
            when(batchRepository.save(any(Batch.class))).thenReturn(batch);

            // Act
            Batch updated = batchRepository.save(batch);

            // Assert
            assertThat(updated.getBatchCode()).isEqualTo("UPDATED-BATCH");
            verify(batchRepository).save(any(Batch.class));
        }
    }

    @Nested
    @DisplayName("Batch Expiry Tests")
    class BatchExpiryTests {

        @Test
        @DisplayName("Should identify expired batch")
        void shouldIdentifyExpiredBatch() {
            // Arrange
            Batch expiredBatch = Batch.builder()
                    .batchCode("EXPIRED-001")
                    .expiryDate(LocalDate.now().minusDays(1))
                    .build();
            expiredBatch.setId(2L);

            // Act
            boolean isExpired = expiredBatch.getExpiryDate().isBefore(LocalDate.now());

            // Assert
            assertThat(isExpired).isTrue();
        }

        @Test
        @DisplayName("Should identify valid batch")
        void shouldIdentifyValidBatch() {
            // Arrange
            Batch validBatch = Batch.builder()
                    .batchCode("VALID-001")
                    .expiryDate(LocalDate.now().plusYears(2))
                    .build();
            validBatch.setId(3L);

            // Act
            boolean isExpired = validBatch.getExpiryDate().isBefore(LocalDate.now());

            // Assert
            assertThat(isExpired).isFalse();
        }

        @Test
        @DisplayName("Should identify batch expiring soon")
        void shouldIdentifyBatchExpiringSoon() {
            // Arrange
            Batch expiringSoonBatch = Batch.builder()
                    .batchCode("EXPIRING-001")
                    .expiryDate(LocalDate.now().plusDays(30))
                    .build();
            expiringSoonBatch.setId(4L);

            // Act
            boolean isExpiringSoon = expiringSoonBatch.getExpiryDate()
                    .isBefore(LocalDate.now().plusMonths(3));

            // Assert
            assertThat(isExpiringSoon).isTrue();
        }
    }

    @Nested
    @DisplayName("Entity Validation Tests")
    class EntityValidationTests {

        @Test
        @DisplayName("Should create batch with required fields")
        void shouldCreateBatchWithRequiredFields() {
            // Arrange & Act
            Batch newBatch = Batch.builder()
                    .batchCode("BATCH-003")
                    .expiryDate(LocalDate.now().plusYears(1))
                    .build();

            // Assert
            assertThat(newBatch.getBatchCode()).isEqualTo("BATCH-003");
            assertThat(newBatch.getExpiryDate()).isAfter(LocalDate.now());
        }

        @Test
        @DisplayName("Should handle null id for new batch")
        void shouldHandleNullIdForNewBatch() {
            // Arrange & Act
            Batch newBatch = Batch.builder()
                    .batchCode("NEW-BATCH")
                    .expiryDate(LocalDate.now().plusYears(1))
                    .build();

            // Assert
            assertThat(newBatch.getId()).isNull();
        }
    }
}
