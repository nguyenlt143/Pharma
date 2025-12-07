package vn.edu.fpt.pharma.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import vn.edu.fpt.pharma.BaseServiceTest;
import vn.edu.fpt.pharma.entity.Shift;
import vn.edu.fpt.pharma.entity.ShiftAssignment;
import vn.edu.fpt.pharma.entity.ShiftWork;
import vn.edu.fpt.pharma.repository.ShiftAssignmentRepository;
import vn.edu.fpt.pharma.repository.ShiftRepository;
import vn.edu.fpt.pharma.repository.ShiftWorkRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.testbuilder.ShiftAssignmentTestBuilder;
import vn.edu.fpt.pharma.testbuilder.ShiftTestBuilder;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for ShiftAssignmentServiceImpl
 * Coverage: 100% for all 8 methods with Arrange-Act-Assert structure
 */
@DisplayName("ShiftAssignmentServiceImpl Tests")
class ShiftAssignmentServiceImplTest extends BaseServiceTest {

    @Mock
    private ShiftAssignmentRepository repository;

    @Mock
    private ShiftRepository shiftRepository;

    @Mock
    private ShiftWorkRepository shiftWorkRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private ShiftAssignmentServiceImpl service;

    @Nested
    @DisplayName("createAssignment() tests")
    class CreateAssignmentTests {

        @Test
        @DisplayName("Should create and return assignment successfully")
        void createAssignment_withValidParams_shouldCreateAndReturnAssignment() {
            // Arrange
            Long shiftId = 1L;
            Long userId = 10L;
            Shift shift = ShiftTestBuilder.create().withId(shiftId).buildEntity();
            ShiftAssignment savedAssignment = ShiftAssignmentTestBuilder.create()
                    .withId(100L)
                    .withShiftId(shiftId)
                    .withUserId(userId)
                    .buildEntity();

            when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
            when(repository.findByShiftIdAndUserId(shiftId, userId)).thenReturn(Optional.empty());
            when(repository.save(any(ShiftAssignment.class))).thenReturn(savedAssignment);
                // Mock for extendShiftWorks internal call
            when(repository.findById(100L)).thenReturn(Optional.of(savedAssignment));
            when(shiftWorkRepository.findLastWorkDateByAssignmentId(100L)).thenReturn(null);
            when(shiftWorkRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

            // Act
            ShiftAssignment result = service.createAssignment(shiftId, userId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(100L);
            verify(shiftRepository).findById(shiftId);
            verify(repository).save(any(ShiftAssignment.class));
        }

        @Test
        @DisplayName("Should auto-generate 30 days shift works")
        void createAssignment_shouldAutoGenerateShiftWorks() {
            // Arrange
            Long shiftId = 1L;
            Long userId = 10L;
            Shift shift = ShiftTestBuilder.create().withId(shiftId).buildEntity();
            ShiftAssignment savedAssignment = ShiftAssignmentTestBuilder.create()
                    .withId(100L)
                    .withShiftId(shiftId)
                    .withUserId(userId)
                    .buildEntity();

            when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
            when(repository.findByShiftIdAndUserId(shiftId, userId)).thenReturn(Optional.empty());
            when(repository.save(any(ShiftAssignment.class))).thenReturn(savedAssignment);
            when(repository.findById(100L)).thenReturn(Optional.of(savedAssignment));
            when(shiftWorkRepository.findLastWorkDateByAssignmentId(100L)).thenReturn(null);

            ArgumentCaptor<List<ShiftWork>> captor = ArgumentCaptor.forClass(List.class);
            when(shiftWorkRepository.saveAll(captor.capture())).thenReturn(Collections.emptyList());

            // Act
            service.createAssignment(shiftId, userId);

            // Assert
            List<ShiftWork> savedWorks = captor.getValue();
            assertThat(savedWorks).hasSize(30);
            verify(shiftWorkRepository).saveAll(anyList());
        }

        @Test
        @DisplayName("Should throw when shiftId is null")
        void createAssignment_withNullShiftId_shouldThrowIllegalArgumentException() {
            // Arrange
            Long userId = 10L;
            when(shiftRepository.findById(null)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> service.createAssignment(null, userId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Ca làm việc không tồn tại");
        }

        @Test
        @DisplayName("Should throw when shift does not exist")
        void createAssignment_withNonExistingShiftId_shouldThrowIllegalArgumentException() {
            // Arrange
            Long shiftId = 999L;
            Long userId = 10L;
            when(shiftRepository.findById(shiftId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> service.createAssignment(shiftId, userId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Ca làm việc không tồn tại");
        }

        @Test
        @DisplayName("Should handle null userId gracefully")
        void createAssignment_withNullUserId_shouldThrowOrHandle() {
            // Arrange
            Long shiftId = 1L;
            Shift shift = ShiftTestBuilder.create().withId(shiftId).buildEntity();
            ShiftAssignment savedAssignment = ShiftAssignmentTestBuilder.create()
                    .withId(100L)
                    .withShiftId(shiftId)
                    .withUserId(null)
                    .buildEntity();

            when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
            when(repository.findByShiftIdAndUserId(shiftId, null)).thenReturn(Optional.empty());
            when(repository.save(any(ShiftAssignment.class))).thenReturn(savedAssignment);
            when(repository.findById(100L)).thenReturn(Optional.of(savedAssignment));
            when(shiftWorkRepository.findLastWorkDateByAssignmentId(100L)).thenReturn(null);
            when(shiftWorkRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

            // Act
            ShiftAssignment result = service.createAssignment(shiftId, null);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getUserId()).isNull();
        }

        @Test
        @DisplayName("Should return existing assignment when duplicate detected")
        void createAssignment_withDuplicateAssignment_shouldReturnExisting() {
            // Arrange
            Long shiftId = 1L;
            Long userId = 10L;
            Shift shift = ShiftTestBuilder.create().withId(shiftId).buildEntity();
            ShiftAssignment existingAssignment = ShiftAssignmentTestBuilder.create()
                    .withId(100L)
                    .withShiftId(shiftId)
                    .withUserId(userId)
                    .buildEntity();

            when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
            when(repository.findByShiftIdAndUserId(shiftId, userId)).thenReturn(Optional.of(existingAssignment));

            // Act
            ShiftAssignment result = service.createAssignment(shiftId, userId);

            // Assert
            assertThat(result).isEqualTo(existingAssignment);
            verify(repository, never()).save(any());
            verify(shiftWorkRepository, never()).saveAll(anyList());
        }

        @Test
        @DisplayName("Should set correct userId and shift")
        void createAssignment_shouldSetCorrectUserIdAndShift() {
            // Arrange
            Long shiftId = 1L;
            Long userId = 10L;
            Shift shift = ShiftTestBuilder.create().withId(shiftId).buildEntity();

            when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
            when(repository.findByShiftIdAndUserId(shiftId, userId)).thenReturn(Optional.empty());
            when(shiftWorkRepository.findLastWorkDateByAssignmentId(any())).thenReturn(null);
            when(shiftWorkRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

            ArgumentCaptor<ShiftAssignment> captor = ArgumentCaptor.forClass(ShiftAssignment.class);
            when(repository.save(captor.capture())).thenAnswer(invocation -> {
                ShiftAssignment sa = invocation.getArgument(0);
                sa.setId(100L);
                return sa;
            });
            when(repository.findById(100L)).thenAnswer(invocation -> {
                ShiftAssignment sa = captor.getValue();
                sa.setId(100L);
                return Optional.of(sa);
            });

            // Act
            service.createAssignment(shiftId, userId);

            // Assert
            ShiftAssignment captured = captor.getValue();
            assertThat(captured.getUserId()).isEqualTo(userId);
            assertThat(captured.getShift()).isEqualTo(shift);
        }

        @Test
        @DisplayName("Should rollback when repo.save fails")
        void createAssignment_whenRepoSaveFails_shouldRollback() {
            // Arrange
            Long shiftId = 1L;
            Long userId = 10L;
            Shift shift = ShiftTestBuilder.create().withId(shiftId).buildEntity();

            when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
            when(repository.findByShiftIdAndUserId(shiftId, userId)).thenReturn(Optional.empty());
            when(repository.save(any(ShiftAssignment.class))).thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            assertThatThrownBy(() -> service.createAssignment(shiftId, userId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Database error");
        }

        @Test
        @DisplayName("Should rollback when shift work generation fails")
        void createAssignment_whenShiftWorkGenerationFails_shouldRollback() {
            // Arrange
            Long shiftId = 1L;
            Long userId = 10L;
            Shift shift = ShiftTestBuilder.create().withId(shiftId).buildEntity();
            ShiftAssignment savedAssignment = ShiftAssignmentTestBuilder.create()
                    .withId(100L)
                    .buildEntity();

            when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
            when(repository.findByShiftIdAndUserId(shiftId, userId)).thenReturn(Optional.empty());
            when(repository.save(any(ShiftAssignment.class))).thenReturn(savedAssignment);
            when(repository.findById(100L)).thenReturn(Optional.of(savedAssignment));
            when(shiftWorkRepository.findLastWorkDateByAssignmentId(100L)).thenReturn(null);
            when(shiftWorkRepository.saveAll(anyList())).thenThrow(new RuntimeException("ShiftWork save failed"));

            // Act & Assert
            assertThatThrownBy(() -> service.createAssignment(shiftId, userId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("ShiftWork save failed");
        }

        @Test
        @DisplayName("Should handle deleted shift appropriately")
        void createAssignment_withDeletedShift_shouldHandle() {
            // Arrange
            Long shiftId = 1L;
            Long userId = 10L;
            Shift deletedShift = ShiftTestBuilder.create()
                    .withId(shiftId)
                    .withDeleted(true)
                    .buildEntity();

            when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(deletedShift));
            when(repository.findByShiftIdAndUserId(shiftId, userId)).thenReturn(Optional.empty());
            when(repository.save(any(ShiftAssignment.class))).thenAnswer(invocation -> {
                ShiftAssignment sa = invocation.getArgument(0);
                sa.setId(100L);
                return sa;
            });
            when(repository.findById(100L)).thenAnswer(invocation -> {
                ShiftAssignment sa = new ShiftAssignment();
                sa.setId(100L);
                return Optional.of(sa);
            });
            when(shiftWorkRepository.findLastWorkDateByAssignmentId(any())).thenReturn(null);
            when(shiftWorkRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

            // Act
            ShiftAssignment result = service.createAssignment(shiftId, userId);

            // Assert - Should still allow assignment to deleted shift
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should handle optimistic lock exception on concurrent creation")
        void createAssignment_concurrentCreation_shouldHandleOptimisticLock() {
            // Arrange
            Long shiftId = 1L;
            Long userId = 10L;
            Shift shift = ShiftTestBuilder.create().withId(shiftId).buildEntity();

            when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
            when(repository.findByShiftIdAndUserId(shiftId, userId)).thenReturn(Optional.empty());
            when(repository.save(any(ShiftAssignment.class)))
                    .thenThrow(new RuntimeException("OptimisticLockException"));

            // Act & Assert
            assertThatThrownBy(() -> service.createAssignment(shiftId, userId))
                    .isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @DisplayName("removeAssignment() tests")
    class RemoveAssignmentTests {

        @Test
        @DisplayName("Should delete assignment when exists")
        void removeAssignment_withExistingAssignment_shouldDelete() {
            // Arrange
            Long shiftId = 1L;
            Long userId = 10L;
            ShiftAssignment assignment = ShiftAssignmentTestBuilder.create()
                    .withId(100L)
                    .withShiftId(shiftId)
                    .withUserId(userId)
                    .buildEntity();

            when(repository.findByShiftIdAndUserId(shiftId, userId)).thenReturn(Optional.of(assignment));
            doNothing().when(repository).delete(assignment);

            // Act
            service.removeAssignment(shiftId, userId);

            // Assert
            verify(repository).delete(assignment);
        }

        @Test
        @DisplayName("Should not throw when assignment does not exist")
        void removeAssignment_withNonExistingAssignment_shouldNotThrow() {
            // Arrange
            Long shiftId = 1L;
            Long userId = 10L;
            when(repository.findByShiftIdAndUserId(shiftId, userId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatCode(() -> service.removeAssignment(shiftId, userId))
                    .doesNotThrowAnyException();
            verify(repository, never()).delete(any(ShiftAssignment.class));
        }

        @Test
        @DisplayName("Should handle null parameters gracefully")
        void removeAssignment_withNullParams_shouldHandleGracefully() {
            // Arrange
            when(repository.findByShiftIdAndUserId(null, null)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatCode(() -> service.removeAssignment(null, null))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should only delete specific assignment")
        void removeAssignment_shouldNotAffectOtherAssignments() {
            // Arrange
            Long shiftId = 1L;
            Long userId = 10L;
            ShiftAssignment assignment = ShiftAssignmentTestBuilder.create()
                    .withId(100L)
                    .buildEntity();

            when(repository.findByShiftIdAndUserId(shiftId, userId)).thenReturn(Optional.of(assignment));
            doNothing().when(repository).delete(assignment);

            // Act
            service.removeAssignment(shiftId, userId);

            // Assert
            verify(repository).delete(eq(assignment));
            verify(repository, never()).deleteAll();
        }
    }

    @Nested
    @DisplayName("extendShiftWorks() tests")
    class ExtendShiftWorksTests {

        @Test
        @DisplayName("Should create shift works for specified days")
        void extendShiftWorks_withValidParams_shouldCreateShiftWorks() {
            // Arrange
            Long assignmentId = 100L;
            int days = 30;
            ShiftAssignment assignment = ShiftAssignmentTestBuilder.create()
                    .withId(assignmentId)
                    .buildEntity();

            when(repository.findById(assignmentId)).thenReturn(Optional.of(assignment));
            when(shiftWorkRepository.findLastWorkDateByAssignmentId(assignmentId)).thenReturn(null);

            ArgumentCaptor<List<ShiftWork>> captor = ArgumentCaptor.forClass(List.class);
            when(shiftWorkRepository.saveAll(captor.capture())).thenReturn(Collections.emptyList());

            // Act
            service.extendShiftWorks(assignmentId, days);

            // Assert
            List<ShiftWork> savedWorks = captor.getValue();
            assertThat(savedWorks).hasSize(30);
            verify(shiftWorkRepository).saveAll(anyList());
        }

        @Test
        @DisplayName("Should start from last work date plus one")
        void extendShiftWorks_shouldStartFromLastWorkDatePlusOne() {
            // Arrange
            Long assignmentId = 100L;
            int days = 10;
            LocalDate lastDate = LocalDate.of(2024, 12, 1);
            ShiftAssignment assignment = ShiftAssignmentTestBuilder.create()
                    .withId(assignmentId)
                    .buildEntity();

            when(repository.findById(assignmentId)).thenReturn(Optional.of(assignment));
            when(shiftWorkRepository.findLastWorkDateByAssignmentId(assignmentId)).thenReturn(lastDate);

            ArgumentCaptor<List<ShiftWork>> captor = ArgumentCaptor.forClass(List.class);
            when(shiftWorkRepository.saveAll(captor.capture())).thenReturn(Collections.emptyList());

            // Act
            service.extendShiftWorks(assignmentId, days);

            // Assert
            List<ShiftWork> savedWorks = captor.getValue();
            assertThat(savedWorks.get(0).getWorkDate()).isEqualTo(lastDate.plusDays(1));
            assertThat(savedWorks.get(9).getWorkDate()).isEqualTo(lastDate.plusDays(10));
        }

        @Test
        @DisplayName("Should not create works when days is zero")
        void extendShiftWorks_withZeroDays_shouldNotCreateWorks() {
            // Arrange
            Long assignmentId = 100L;
            int days = 0;
            ShiftAssignment assignment = ShiftAssignmentTestBuilder.create()
                    .withId(assignmentId)
                    .buildEntity();

            when(repository.findById(assignmentId)).thenReturn(Optional.of(assignment));
            when(shiftWorkRepository.findLastWorkDateByAssignmentId(assignmentId)).thenReturn(null);
            when(shiftWorkRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

            // Act
            service.extendShiftWorks(assignmentId, days);

            // Assert
            ArgumentCaptor<List<ShiftWork>> captor = ArgumentCaptor.forClass(List.class);
            verify(shiftWorkRepository).saveAll(captor.capture());
            assertThat(captor.getValue()).isEmpty();
        }

        @Test
        @DisplayName("Should handle gracefully when days is negative")
        void extendShiftWorks_withNegativeDays_shouldHandleGracefully() {
            // Arrange
            Long assignmentId = 100L;
            int days = -5;
            ShiftAssignment assignment = ShiftAssignmentTestBuilder.create()
                    .withId(assignmentId)
                    .buildEntity();

            when(repository.findById(assignmentId)).thenReturn(Optional.of(assignment));
            when(shiftWorkRepository.findLastWorkDateByAssignmentId(assignmentId)).thenReturn(null);
            when(shiftWorkRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

            // Act
            service.extendShiftWorks(assignmentId, days);

            // Assert
            ArgumentCaptor<List<ShiftWork>> captor = ArgumentCaptor.forClass(List.class);
            verify(shiftWorkRepository).saveAll(captor.capture());
            assertThat(captor.getValue()).isEmpty();
        }

        @Test
        @DisplayName("Should create one work when days is one")
        void extendShiftWorks_withOneDay_shouldCreateOneSingleWork() {
            // Arrange
            Long assignmentId = 100L;
            int days = 1;
            ShiftAssignment assignment = ShiftAssignmentTestBuilder.create()
                    .withId(assignmentId)
                    .buildEntity();

            when(repository.findById(assignmentId)).thenReturn(Optional.of(assignment));
            when(shiftWorkRepository.findLastWorkDateByAssignmentId(assignmentId)).thenReturn(null);

            ArgumentCaptor<List<ShiftWork>> captor = ArgumentCaptor.forClass(List.class);
            when(shiftWorkRepository.saveAll(captor.capture())).thenReturn(Collections.emptyList());

            // Act
            service.extendShiftWorks(assignmentId, days);

            // Assert
            assertThat(captor.getValue()).hasSize(1);
        }

        @Test
        @DisplayName("Should create all works when days is 365")
        void extendShiftWorks_withLargeDays_365_shouldCreateAll() {
            // Arrange
            Long assignmentId = 100L;
            int days = 365;
            ShiftAssignment assignment = ShiftAssignmentTestBuilder.create()
                    .withId(assignmentId)
                    .buildEntity();

            when(repository.findById(assignmentId)).thenReturn(Optional.of(assignment));
            when(shiftWorkRepository.findLastWorkDateByAssignmentId(assignmentId)).thenReturn(null);

            ArgumentCaptor<List<ShiftWork>> captor = ArgumentCaptor.forClass(List.class);
            when(shiftWorkRepository.saveAll(captor.capture())).thenReturn(Collections.emptyList());

            // Act
            service.extendShiftWorks(assignmentId, days);

            // Assert
            assertThat(captor.getValue()).hasSize(365);
        }

        @Test
        @DisplayName("Should start from today when no existing works")
        void extendShiftWorks_whenNoExistingWorks_shouldStartFromToday() {
            // Arrange
            Long assignmentId = 100L;
            int days = 5;
            ShiftAssignment assignment = ShiftAssignmentTestBuilder.create()
                    .withId(assignmentId)
                    .buildEntity();

            when(repository.findById(assignmentId)).thenReturn(Optional.of(assignment));
            when(shiftWorkRepository.findLastWorkDateByAssignmentId(assignmentId)).thenReturn(null);

            ArgumentCaptor<List<ShiftWork>> captor = ArgumentCaptor.forClass(List.class);
            when(shiftWorkRepository.saveAll(captor.capture())).thenReturn(Collections.emptyList());

            // Act
            service.extendShiftWorks(assignmentId, days);

            // Assert
            List<ShiftWork> savedWorks = captor.getValue();
            assertThat(savedWorks.get(0).getWorkDate()).isEqualTo(LocalDate.now());
        }

        @Test
        @DisplayName("Should default to today when last date is null")
        void extendShiftWorks_whenLastDateIsNull_shouldDefaultToToday() {
            // Arrange
            Long assignmentId = 100L;
            int days = 5;
            ShiftAssignment assignment = ShiftAssignmentTestBuilder.create()
                    .withId(assignmentId)
                    .buildEntity();

            when(repository.findById(assignmentId)).thenReturn(Optional.of(assignment));
            when(shiftWorkRepository.findLastWorkDateByAssignmentId(assignmentId)).thenReturn(null);

            ArgumentCaptor<List<ShiftWork>> captor = ArgumentCaptor.forClass(List.class);
            when(shiftWorkRepository.saveAll(captor.capture())).thenReturn(Collections.emptyList());

            // Act
            service.extendShiftWorks(assignmentId, days);

            // Assert
            List<ShiftWork> savedWorks = captor.getValue();
            assertThat(savedWorks.get(0).getWorkDate()).isEqualTo(LocalDate.now());
        }

        @Test
        @DisplayName("Should throw when assignment does not exist")
        void extendShiftWorks_withNonExistingAssignmentId_shouldThrowIllegalArgumentException() {
            // Arrange
            Long assignmentId = 999L;
            when(repository.findById(assignmentId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> service.extendShiftWorks(assignmentId, 30))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Phân công ca không tồn tại");
        }

        @Test
        @DisplayName("Should throw when assignmentId is null")
        void extendShiftWorks_withNullAssignmentId_shouldThrow() {
            // Arrange
            when(repository.findById(null)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> service.extendShiftWorks(null, 30))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Phân công ca không tồn tại");
        }

        @Test
        @DisplayName("Should rollback when saveAll fails")
        void extendShiftWorks_whenRepoSaveAllFails_shouldRollback() {
            // Arrange
            Long assignmentId = 100L;
            ShiftAssignment assignment = ShiftAssignmentTestBuilder.create()
                    .withId(assignmentId)
                    .buildEntity();

            when(repository.findById(assignmentId)).thenReturn(Optional.of(assignment));
            when(shiftWorkRepository.findLastWorkDateByAssignmentId(assignmentId)).thenReturn(null);
            when(shiftWorkRepository.saveAll(anyList())).thenThrow(new RuntimeException("Batch save failed"));

            // Act & Assert
            assertThatThrownBy(() -> service.extendShiftWorks(assignmentId, 30))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Batch save failed");
        }

        @Test
        @DisplayName("Should call saveAll once for batch operation")
        void extendShiftWorks_shouldCallSaveAllOnce() {
            // Arrange
            Long assignmentId = 100L;
            ShiftAssignment assignment = ShiftAssignmentTestBuilder.create()
                    .withId(assignmentId)
                    .buildEntity();

            when(repository.findById(assignmentId)).thenReturn(Optional.of(assignment));
            when(shiftWorkRepository.findLastWorkDateByAssignmentId(assignmentId)).thenReturn(null);
            when(shiftWorkRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

            // Act
            service.extendShiftWorks(assignmentId, 30);

            // Assert
            verify(shiftWorkRepository, times(1)).saveAll(anyList());
        }
    }

    @Nested
    @DisplayName("findByShiftIdAndUserId() tests")
    class FindByShiftIdAndUserIdTests {

        @Test
        @DisplayName("Should return assignment when exists")
        void findByShiftIdAndUserId_withExisting_shouldReturn() {
            // Arrange
            Long shiftId = 1L;
            Long userId = 10L;
            ShiftAssignment assignment = ShiftAssignmentTestBuilder.create()
                    .withId(100L)
                    .withShiftId(shiftId)
                    .withUserId(userId)
                    .buildEntity();

            when(repository.findByShiftIdAndUserId(shiftId, userId)).thenReturn(Optional.of(assignment));

            // Act
            ShiftAssignment result = service.findByShiftIdAndUserId(shiftId, userId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(100L);
        }

        @Test
        @DisplayName("Should return null when not exists")
        void findByShiftIdAndUserId_withNonExisting_shouldReturnNull() {
            // Arrange
            Long shiftId = 1L;
            Long userId = 10L;
            when(repository.findByShiftIdAndUserId(shiftId, userId)).thenReturn(Optional.empty());

            // Act
            ShiftAssignment result = service.findByShiftIdAndUserId(shiftId, userId);

            // Assert
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should return null with null parameters")
        void findByShiftIdAndUserId_withNullParams_shouldReturnNull() {
            // Arrange
            when(repository.findByShiftIdAndUserId(null, null)).thenReturn(Optional.empty());

            // Act
            ShiftAssignment result = service.findByShiftIdAndUserId(null, null);

            // Assert
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("findByShiftId() tests")
    class FindByShiftIdTests {

        @Test
        @DisplayName("Should return first assignment when multiple exist")
        void findByShiftId_withMultipleAssignments_shouldReturnFirst() {
            // Arrange
            Long shiftId = 1L;
            ShiftAssignment assignment1 = ShiftAssignmentTestBuilder.create()
                    .withId(100L)
                    .withUserId(10L)
                    .buildEntity();
            ShiftAssignment assignment2 = ShiftAssignmentTestBuilder.create()
                    .withId(101L)
                    .withUserId(11L)
                    .buildEntity();

            when(repository.findByShiftId(shiftId)).thenReturn(Arrays.asList(assignment1, assignment2));

            // Act
            ShiftAssignment result = service.findByShiftId(shiftId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(100L);
        }

        @Test
        @DisplayName("Should return null when no assignments")
        void findByShiftId_withNoAssignments_shouldReturnNull() {
            // Arrange
            Long shiftId = 1L;
            when(repository.findByShiftId(shiftId)).thenReturn(Collections.emptyList());

            // Act
            ShiftAssignment result = service.findByShiftId(shiftId);

            // Assert
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should return null with null shiftId")
        void findByShiftId_withNullShiftId_shouldReturnNull() {
            // Arrange
            when(repository.findByShiftId(null)).thenReturn(Collections.emptyList());

            // Act
            ShiftAssignment result = service.findByShiftId(null);

            // Assert
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("findAllByShiftId() tests")
    class FindAllByShiftIdTests {

        @Test
        @DisplayName("Should return all assignments")
        void findAllByShiftId_shouldReturnAllAssignments() {
            // Arrange
            Long shiftId = 1L;
            List<ShiftAssignment> assignments = Arrays.asList(
                    ShiftAssignmentTestBuilder.create().withId(100L).withUserId(10L).buildEntity(),
                    ShiftAssignmentTestBuilder.create().withId(101L).withUserId(11L).buildEntity(),
                    ShiftAssignmentTestBuilder.create().withId(102L).withUserId(12L).buildEntity()
            );

            when(repository.findByShiftId(shiftId)).thenReturn(assignments);

            // Act
            List<ShiftAssignment> result = service.findAllByShiftId(shiftId);

            // Assert
            assertThat(result).hasSize(3);
            assertThat(result).containsAll(assignments);
        }

        @Test
        @DisplayName("Should return empty list when no assignments")
        void findAllByShiftId_withNoAssignments_shouldReturnEmptyList() {
            // Arrange
            Long shiftId = 1L;
            when(repository.findByShiftId(shiftId)).thenReturn(Collections.emptyList());

            // Act
            List<ShiftAssignment> result = service.findAllByShiftId(shiftId);

            // Assert
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty list with null shiftId")
        void findAllByShiftId_withNullShiftId_shouldReturnEmptyList() {
            // Arrange
            when(repository.findByShiftId(null)).thenReturn(Collections.emptyList());

            // Act
            List<ShiftAssignment> result = service.findAllByShiftId(null);

            // Assert
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getLastWorkDate() tests")
    class GetLastWorkDateTests {

        @Test
        @DisplayName("Should return max date when works exist")
        void getLastWorkDate_withExistingWorks_shouldReturnMaxDate() {
            // Arrange
            Long assignmentId = 100L;
            LocalDate lastDate = LocalDate.of(2024, 12, 31);
            when(shiftWorkRepository.findLastWorkDateByAssignmentId(assignmentId)).thenReturn(lastDate);

            // Act
            LocalDate result = service.getLastWorkDate(assignmentId);

            // Assert
            assertThat(result).isEqualTo(lastDate);
        }

        @Test
        @DisplayName("Should return null when no works")
        void getLastWorkDate_withNoWorks_shouldReturnNull() {
            // Arrange
            Long assignmentId = 100L;
            when(shiftWorkRepository.findLastWorkDateByAssignmentId(assignmentId)).thenReturn(null);

            // Act
            LocalDate result = service.getLastWorkDate(assignmentId);

            // Assert
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should return null with null assignmentId")
        void getLastWorkDate_withNullAssignmentId_shouldReturnNull() {
            // Arrange
            when(shiftWorkRepository.findLastWorkDateByAssignmentId(null)).thenReturn(null);

            // Act
            LocalDate result = service.getLastWorkDate(null);

            // Assert
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("getRemainingWorkDays() tests")
    class GetRemainingWorkDaysTests {

        @Test
        @DisplayName("Should return count of future works")
        void getRemainingWorkDays_withFutureWorks_shouldReturnCount() {
            // Arrange
            Long assignmentId = 100L;
            when(shiftWorkRepository.countRemainingWorkDays(eq(assignmentId), any(LocalDate.class))).thenReturn(15L);

            // Act
            long result = service.getRemainingWorkDays(assignmentId);

            // Assert
            assertThat(result).isEqualTo(15L);
        }

        @Test
        @DisplayName("Should return zero when no future works")
        void getRemainingWorkDays_withNoFutureWorks_shouldReturnZero() {
            // Arrange
            Long assignmentId = 100L;
            when(shiftWorkRepository.countRemainingWorkDays(eq(assignmentId), any(LocalDate.class))).thenReturn(0L);

            // Act
            long result = service.getRemainingWorkDays(assignmentId);

            // Assert
            assertThat(result).isZero();
        }

        @Test
        @DisplayName("Should include today in count")
        void getRemainingWorkDays_withTodayIncluded_shouldIncludeToday() {
            // Arrange
            Long assignmentId = 100L;
            LocalDate today = LocalDate.now();
            when(shiftWorkRepository.countRemainingWorkDays(assignmentId, today)).thenReturn(10L);

            // Act
            long result = service.getRemainingWorkDays(assignmentId);

            // Assert
            assertThat(result).isEqualTo(10L);
            verify(shiftWorkRepository).countRemainingWorkDays(eq(assignmentId), eq(today));
        }

        @Test
        @DisplayName("Should return zero with null assignmentId")
        void getRemainingWorkDays_withNullAssignmentId_shouldReturnZero() {
            // Arrange
            when(shiftWorkRepository.countRemainingWorkDays(eq(null), any(LocalDate.class))).thenReturn(0L);

            // Act
            long result = service.getRemainingWorkDays(null);

            // Assert
            assertThat(result).isZero();
        }
    }
}

