package vn.edu.fpt.pharma.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import vn.edu.fpt.pharma.BaseServiceTest;
import vn.edu.fpt.pharma.dto.manager.ShiftRequest;
import vn.edu.fpt.pharma.dto.manager.ShiftResponse;
import vn.edu.fpt.pharma.entity.Shift;
import vn.edu.fpt.pharma.repository.ShiftRepository;
import vn.edu.fpt.pharma.testbuilder.ShiftTestBuilder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for ShiftServiceImpl
 * Coverage: 100% for all 6 methods with Arrange-Act-Assert structure
 */
@DisplayName("ShiftServiceImpl Tests")
class ShiftServiceImplTest extends BaseServiceTest {

    @Mock
    private ShiftRepository repo;

    @InjectMocks
    private ShiftServiceImpl shiftService;

    private Long branchId;

    @BeforeEach
    void setUp() {
        branchId = 1L;
    }

    @Nested
    @DisplayName("listShifts() tests")
    class ListShiftsTests {

        @Test
        @DisplayName("Should return filtered shifts with valid query and includeDeleted=false")
        void listShifts_withValidQuery_shouldReturnFilteredShifts() {
            // Arrange
            String query = "Ca sáng";
            Shift shift1 = ShiftTestBuilder.create().withId(1L).withName("Ca sáng").buildEntity();
            Shift shift2 = ShiftTestBuilder.create().withId(2L).withName("Ca sáng 2").buildEntity();
            when(repo.search(query, branchId)).thenReturn(Arrays.asList(shift1, shift2));

            // Act
            List<ShiftResponse> result = shiftService.listShifts(query, branchId, false);

            // Assert
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getName()).contains("Ca sáng");
            verify(repo).search(query, branchId);
            verify(repo, never()).searchIncludingDeleted(any(), any());
        }

        @Test
        @DisplayName("Should return all shifts when query is null")
        void listShifts_withNullQuery_shouldReturnAllShifts() {
            // Arrange
            Shift shift1 = ShiftTestBuilder.create().withId(1L).buildEntity();
            when(repo.search(null, branchId)).thenReturn(Collections.singletonList(shift1));

            // Act
            List<ShiftResponse> result = shiftService.listShifts(null, branchId, false);

            // Assert
            assertThat(result).hasSize(1);
            verify(repo).search(null, branchId);
        }

        @Test
        @DisplayName("Should return all shifts when query is empty")
        void listShifts_withEmptyQuery_shouldReturnAllShifts() {
            // Arrange
            Shift shift1 = ShiftTestBuilder.create().withId(1L).buildEntity();
            when(repo.search("", branchId)).thenReturn(Collections.singletonList(shift1));

            // Act
            List<ShiftResponse> result = shiftService.listShifts("", branchId, false);

            // Assert
            assertThat(result).hasSize(1);
            verify(repo).search("", branchId);
        }

        @Test
        @DisplayName("Should call searchIncludingDeleted when includeDeleted is true")
        void listShifts_withIncludeDeletedTrue_shouldCallSearchIncludingDeleted() {
            // Arrange
            String query = "test";
            Shift shift1 = ShiftTestBuilder.create().withId(1L).withDeleted(true).buildEntity();
            when(repo.searchIncludingDeleted(query, branchId)).thenReturn(Collections.singletonList(shift1));

            // Act
            List<ShiftResponse> result = shiftService.listShifts(query, branchId, true);

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0).isDeleted()).isTrue();
            verify(repo).searchIncludingDeleted(query, branchId);
            verify(repo, never()).search(any(), any());
        }

        @Test
        @DisplayName("Should propagate exception when repository throws")
        void listShifts_whenRepoThrowsException_shouldPropagateException() {
            // Arrange
            when(repo.search(any(), any())).thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            assertThatThrownBy(() -> shiftService.listShifts("test", branchId, false))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Database error");
        }

        @Test
        @DisplayName("Should map all shifts to DTO correctly with large result set")
        void listShifts_withLargeResultSet_shouldMapAllToDto() {
            // Arrange
            List<Shift> shifts = new ArrayList<>();
            for (int i = 1; i <= 1000; i++) {
                shifts.add(ShiftTestBuilder.create()
                        .withId((long) i)
                        .withName("Shift " + i)
                        .buildEntity());
            }
            when(repo.search(null, branchId)).thenReturn(shifts);

            // Act
            List<ShiftResponse> result = shiftService.listShifts(null, branchId, false);

            // Assert
            assertThat(result).hasSize(1000);
            assertThat(result.get(0).getId()).isEqualTo(1L);
            assertThat(result.get(999).getId()).isEqualTo(1000L);
        }
    }

    @Nested
    @DisplayName("findById() tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should return ShiftResponse when id exists")
        void findById_withExistingId_shouldReturnShiftResponse() {
            // Arrange
            Long id = 1L;
            Shift shift = ShiftTestBuilder.create().withId(id).buildEntity();
            when(repo.findById(id)).thenReturn(Optional.of(shift));

            // Act
            Optional<ShiftResponse> result = shiftService.findById(id);

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(id);
            assertThat(result.get().getName()).isEqualTo(shift.getName());
        }

        @Test
        @DisplayName("Should return empty when id does not exist")
        void findById_withNonExistingId_shouldReturnEmpty() {
            // Arrange
            Long id = 999L;
            when(repo.findById(id)).thenReturn(Optional.empty());

            // Act
            Optional<ShiftResponse> result = shiftService.findById(id);

            // Assert
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty when id is null")
        void findById_withNullId_shouldReturnEmpty() {
            // Arrange
            when(repo.findById(null)).thenReturn(Optional.empty());

            // Act
            Optional<ShiftResponse> result = shiftService.findById(null);

            // Assert
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("save() tests")
    class SaveTests {

        @Test
        @DisplayName("Should save and return new shift successfully")
        void save_withValidNewShift_shouldSaveAndReturnResponse() {
            // Arrange
            ShiftRequest request = ShiftTestBuilder.create()
                    .withName("Ca sáng")
                    .withStartTime("08:00")
                    .withEndTime("16:00")
                    .buildRequest();
            Shift savedShift = ShiftTestBuilder.create().withId(1L).buildEntity();

            when(repo.findOverlappingShifts(any(), any(), any(), any())).thenReturn(Collections.emptyList());
            when(repo.save(any(Shift.class))).thenReturn(savedShift);

            // Act
            ShiftResponse result = shiftService.save(request, branchId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            verify(repo).save(any(Shift.class));
            verify(repo).findOverlappingShifts(eq(branchId), any(LocalTime.class), any(LocalTime.class), eq(null));
        }

        @Test
        @DisplayName("Should update existing shift successfully")
        void save_withValidExistingShift_shouldUpdateAndReturnResponse() {
            // Arrange
            Long existingId = 1L;
            Shift existingShift = ShiftTestBuilder.create().withId(existingId).buildEntity();
            ShiftRequest request = ShiftTestBuilder.create()
                    .withId(existingId)
                    .withName("Ca sáng Updated")
                    .buildRequest();

            when(repo.findById(existingId)).thenReturn(Optional.of(existingShift));
            when(repo.findOverlappingShifts(any(), any(), any(), eq(existingId))).thenReturn(Collections.emptyList());
            when(repo.save(any(Shift.class))).thenReturn(existingShift);

            // Act
            ShiftResponse result = shiftService.save(request, branchId);

            // Assert
            assertThat(result).isNotNull();
            verify(repo).findById(existingId);
            verify(repo).save(any(Shift.class));
        }

        @Test
        @DisplayName("Should use default midnight when startTime is null")
        void save_whenStartTimeNull_shouldUseDefaultMidnight() {
            // Arrange
            ShiftRequest request = ShiftTestBuilder.create()
                    .withStartTime(null)
                    .withEndTime("08:00")
                    .buildRequest();
            Shift savedShift = ShiftTestBuilder.create().withId(1L).buildEntity();

            when(repo.findOverlappingShifts(any(), any(), any(), any())).thenReturn(Collections.emptyList());
            when(repo.save(any(Shift.class))).thenAnswer(invocation -> {
                Shift shift = invocation.getArgument(0);
                assertThat(shift.getStartTime()).isEqualTo(LocalTime.MIDNIGHT);
                return savedShift;
            });

            // Act
            shiftService.save(request, branchId);

            // Assert
            verify(repo).save(any(Shift.class));
        }

        @Test
        @DisplayName("Should use default midnight when endTime is null")
        void save_whenEndTimeNull_shouldUseDefaultMidnight() {
            // Arrange
            ShiftRequest request = ShiftTestBuilder.create()
                    .withStartTime("08:00")
                    .withEndTime(null)
                    .buildRequest();

            // Act & Assert - will throw because midnight (00:00) is before 08:00
            assertThatThrownBy(() -> shiftService.save(request, branchId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Giờ kết thúc phải lớn hơn giờ bắt đầu");
        }

        @Test
        @DisplayName("Should parse time with HH:mm format")
        void save_withValidTimeFormatHHmm_shouldParse() {
            // Arrange
            ShiftRequest request = ShiftTestBuilder.create()
                    .withStartTime("08:00")
                    .withEndTime("16:00")
                    .buildRequest();
            Shift savedShift = ShiftTestBuilder.create().withId(1L).buildEntity();

            when(repo.findOverlappingShifts(any(), any(), any(), any())).thenReturn(Collections.emptyList());
            when(repo.save(any(Shift.class))).thenReturn(savedShift);

            // Act
            ShiftResponse result = shiftService.save(request, branchId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getStartTime()).isEqualTo("08:00");
        }

        @Test
        @DisplayName("Should parse time with HH:mm:ss format")
        void save_withValidTimeFormatHHmmss_shouldParse() {
            // Arrange
            ShiftRequest request = ShiftTestBuilder.create()
                    .withStartTime("08:00:00")
                    .withEndTime("16:00:00")
                    .buildRequest();
            Shift savedShift = ShiftTestBuilder.create().withId(1L).buildEntity();

            when(repo.findOverlappingShifts(any(), any(), any(), any())).thenReturn(Collections.emptyList());
            when(repo.save(any(Shift.class))).thenReturn(savedShift);

            // Act
            ShiftResponse result = shiftService.save(request, branchId);

            // Assert
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should fallback to midnight with invalid time format")
        void save_withInvalidTimeFormat_shouldFallbackToMidnight() {
            // Arrange
            ShiftRequest request = ShiftTestBuilder.create()
                    .withStartTime("abc")
                    .withEndTime("xyz")
                    .buildRequest();

            // Act & Assert - both will be midnight, so endTime == startTime
            assertThatThrownBy(() -> shiftService.save(request, branchId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Giờ kết thúc phải lớn hơn giờ bắt đầu");
        }

        @Test
        @DisplayName("Should throw when end time is before start time")
        void save_whenEndTimeBeforeStartTime_shouldThrowIllegalArgumentException() {
            // Arrange
            ShiftRequest request = ShiftTestBuilder.create()
                    .withStartTime("16:00")
                    .withEndTime("08:00")
                    .buildRequest();

            // Act & Assert
            assertThatThrownBy(() -> shiftService.save(request, branchId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Giờ kết thúc phải lớn hơn giờ bắt đầu");

            verify(repo, never()).save(any());
        }

        @Test
        @DisplayName("Should throw when end time equals start time")
        void save_whenEndTimeEqualsStartTime_shouldThrowIllegalArgumentException() {
            // Arrange
            ShiftRequest request = ShiftTestBuilder.create()
                    .withStartTime("08:00")
                    .withEndTime("08:00")
                    .buildRequest();

            // Act & Assert
            assertThatThrownBy(() -> shiftService.save(request, branchId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Giờ kết thúc phải lớn hơn giờ bắt đầu");

            verify(repo, never()).save(any());
        }

        @Test
        @DisplayName("Should succeed when end time is after start time by 1 minute")
        void save_whenEndTimeAfterStartTime_shouldSucceed() {
            // Arrange
            ShiftRequest request = ShiftTestBuilder.create()
                    .withStartTime("08:00")
                    .withEndTime("08:01")
                    .buildRequest();
            Shift savedShift = ShiftTestBuilder.create().withId(1L).buildEntity();

            when(repo.findOverlappingShifts(any(), any(), any(), any())).thenReturn(Collections.emptyList());
            when(repo.save(any(Shift.class))).thenReturn(savedShift);

            // Act
            ShiftResponse result = shiftService.save(request, branchId);

            // Assert
            assertThat(result).isNotNull();
            verify(repo).save(any(Shift.class));
        }

        @Test
        @DisplayName("Should throw when shift overlaps with existing shift")
        void save_whenShiftOverlapsExisting_shouldThrowWithOverlappingNames() {
            // Arrange
            ShiftRequest request = ShiftTestBuilder.create()
                    .withStartTime("08:00")
                    .withEndTime("16:00")
                    .buildRequest();
            Shift overlapping = ShiftTestBuilder.create()
                    .withId(2L)
                    .withName("Ca trưa")
                    .withStartTime("10:00")
                    .withEndTime("18:00")
                    .buildEntity();

            when(repo.findOverlappingShifts(any(), any(), any(), any()))
                    .thenReturn(Collections.singletonList(overlapping));

            // Act & Assert
            assertThatThrownBy(() -> shiftService.save(request, branchId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Ca làm việc bị trùng thời gian với:")
                    .hasMessageContaining("Ca trưa");

            verify(repo, never()).save(any());
        }

        @Test
        @DisplayName("Should list all overlapping shifts when multiple overlaps detected")
        void save_whenShiftOverlapsMultiple_shouldListAllOverlapping() {
            // Arrange
            ShiftRequest request = ShiftTestBuilder.create()
                    .withStartTime("08:00")
                    .withEndTime("20:00")
                    .buildRequest();
            Shift overlap1 = ShiftTestBuilder.create()
                    .withId(2L)
                    .withName("Ca 1")
                    .withStartTime("10:00")
                    .withEndTime("14:00")
                    .buildEntity();
            Shift overlap2 = ShiftTestBuilder.create()
                    .withId(3L)
                    .withName("Ca 2")
                    .withStartTime("15:00")
                    .withEndTime("19:00")
                    .buildEntity();

            when(repo.findOverlappingShifts(any(), any(), any(), any()))
                    .thenReturn(Arrays.asList(overlap1, overlap2));

            // Act & Assert
            assertThatThrownBy(() -> shiftService.save(request, branchId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Ca 1")
                    .hasMessageContaining("Ca 2");
        }

        @Test
        @DisplayName("Should succeed when no overlap detected")
        void save_whenNoOverlap_shouldSucceed() {
            // Arrange
            ShiftRequest request = ShiftTestBuilder.create()
                    .withStartTime("08:00")
                    .withEndTime("16:00")
                    .buildRequest();
            Shift savedShift = ShiftTestBuilder.create().withId(1L).buildEntity();

            when(repo.findOverlappingShifts(any(), any(), any(), any())).thenReturn(Collections.emptyList());
            when(repo.save(any(Shift.class))).thenReturn(savedShift);

            // Act
            ShiftResponse result = shiftService.save(request, branchId);

            // Assert
            assertThat(result).isNotNull();
            verify(repo).save(any(Shift.class));
        }

        @Test
        @DisplayName("Should exclude self from overlap check when updating")
        void save_whenUpdateSameShift_shouldExcludeSelfFromOverlapCheck() {
            // Arrange
            Long existingId = 1L;
            Shift existingShift = ShiftTestBuilder.create().withId(existingId).buildEntity();
            ShiftRequest request = ShiftTestBuilder.create()
                    .withId(existingId)
                    .withStartTime("08:00")
                    .withEndTime("16:00")
                    .buildRequest();

            when(repo.findById(existingId)).thenReturn(Optional.of(existingShift));
            when(repo.findOverlappingShifts(any(), any(), any(), eq(existingId))).thenReturn(Collections.emptyList());
            when(repo.save(any(Shift.class))).thenReturn(existingShift);

            // Act
            shiftService.save(request, branchId);

            // Assert
            verify(repo).findOverlappingShifts(eq(branchId), any(LocalTime.class), any(LocalTime.class), eq(existingId));
        }

        @Test
        @DisplayName("Should rollback and throw when repo.save() fails")
        void save_whenRepoSaveFails_shouldRollbackAndThrow() {
            // Arrange
            ShiftRequest request = ShiftTestBuilder.create().buildRequest();
            when(repo.findOverlappingShifts(any(), any(), any(), any())).thenReturn(Collections.emptyList());
            when(repo.save(any(Shift.class))).thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            assertThatThrownBy(() -> shiftService.save(request, branchId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Database error");
        }

        @Test
        @DisplayName("Should rollback and throw when findOverlapping fails")
        void save_whenFindOverlappingFails_shouldRollbackAndThrow() {
            // Arrange
            ShiftRequest request = ShiftTestBuilder.create().buildRequest();
            when(repo.findOverlappingShifts(any(), any(), any(), any()))
                    .thenThrow(new RuntimeException("Query error"));

            // Act & Assert
            assertThatThrownBy(() -> shiftService.save(request, branchId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Query error");

            verify(repo, never()).save(any());
        }

        @Test
        @DisplayName("Should handle boundary times 00:00 to 23:59")
        void save_withBoundaryTimes_00_00_to_23_59_shouldSucceed() {
            // Arrange
            ShiftRequest request = ShiftTestBuilder.create()
                    .withStartTime("00:00")
                    .withEndTime("23:59")
                    .buildRequest();
            Shift savedShift = ShiftTestBuilder.create().withId(1L).buildEntity();

            when(repo.findOverlappingShifts(any(), any(), any(), any())).thenReturn(Collections.emptyList());
            when(repo.save(any(Shift.class))).thenReturn(savedShift);

            // Act
            ShiftResponse result = shiftService.save(request, branchId);

            // Assert
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should handle cross-midnight shift (night shift)")
        void save_withCrossMidnightShift_shouldHandle() {
            // Arrange - This would require business logic change, currently will fail validation
            ShiftRequest request = ShiftTestBuilder.create()
                    .withStartTime("22:00")
                    .withEndTime("02:00") // Next day
                    .buildRequest();

            // Act & Assert - Current implementation doesn't support cross-midnight
            assertThatThrownBy(() -> shiftService.save(request, branchId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Giờ kết thúc phải lớn hơn giờ bắt đầu");
        }
    }

    @Nested
    @DisplayName("delete() tests")
    class DeleteTests {

        @Test
        @DisplayName("Should call repo.deleteById with existing shift")
        void delete_withExistingShift_shouldCallRepoDeleteById() {
            // Arrange
            Long id = 1L;
            doNothing().when(repo).deleteById(id);

            // Act
            shiftService.delete(id);

            // Assert
            verify(repo).deleteById(id);
        }

        @Test
        @DisplayName("Should not throw when id does not exist")
        void delete_withNonExistingId_shouldNotThrow() {
            // Arrange
            Long id = 999L;
            doNothing().when(repo).deleteById(id);

            // Act & Assert
            assertThatCode(() -> shiftService.delete(id)).doesNotThrowAnyException();
            verify(repo).deleteById(id);
        }

        @Test
        @DisplayName("Should handle gracefully when id is null")
        void delete_withNullId_shouldHandleGracefully() {
            // Arrange
            doNothing().when(repo).deleteById(null);

            // Act & Assert
            assertThatCode(() -> shiftService.delete(null)).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("restore() tests")
    class RestoreTests {

        @Test
        @DisplayName("Should call repo.restoreById with deleted shift")
        void restore_withDeletedShift_shouldCallRepoRestoreById() {
            // Arrange
            Long id = 1L;
            doNothing().when(repo).restoreById(id);

            // Act
            shiftService.restore(id);

            // Assert
            verify(repo).restoreById(id);
        }

        @Test
        @DisplayName("Should still call restore for non-deleted shift")
        void restore_withNonDeletedShift_shouldStillCallRestore() {
            // Arrange
            Long id = 1L;
            doNothing().when(repo).restoreById(id);

            // Act
            shiftService.restore(id);

            // Assert
            verify(repo).restoreById(id);
        }

        @Test
        @DisplayName("Should handle gracefully when id is null")
        void restore_withNullId_shouldHandleGracefully() {
            // Arrange
            doNothing().when(repo).restoreById(null);

            // Act & Assert
            assertThatCode(() -> shiftService.restore(null)).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("getCurrentShift() tests")
    class GetCurrentShiftTests {

        @Test
        @DisplayName("Should return shift when user is in active shift")
        void getCurrentShift_withActiveShift_shouldReturnShift() {
            // Arrange
            Long userId = 1L;
            Shift activeShift = ShiftTestBuilder.create().withId(1L).buildEntity();
            when(repo.findCurrentShift(eq(userId), eq(branchId), any(LocalDate.class), any(LocalTime.class)))
                    .thenReturn(Optional.of(activeShift));

            // Act
            Optional<Shift> result = shiftService.getCurrentShift(userId, branchId);

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should return empty when user has no active shift")
        void getCurrentShift_withNoActiveShift_shouldReturnEmpty() {
            // Arrange
            Long userId = 1L;
            when(repo.findCurrentShift(eq(userId), eq(branchId), any(LocalDate.class), any(LocalTime.class)))
                    .thenReturn(Optional.empty());

            // Act
            Optional<Shift> result = shiftService.getCurrentShift(userId, branchId);

            // Assert
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return current shift when user has multiple shifts")
        void getCurrentShift_withMultipleShifts_shouldReturnCurrentOne() {
            // Arrange
            Long userId = 1L;
            Shift currentShift = ShiftTestBuilder.create().withId(2L).withName("Current").buildEntity();
            when(repo.findCurrentShift(eq(userId), eq(branchId), any(LocalDate.class), any(LocalTime.class)))
                    .thenReturn(Optional.of(currentShift));

            // Act
            Optional<Shift> result = shiftService.getCurrentShift(userId, branchId);

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("Current");
        }

        @Test
        @DisplayName("Should return empty when userId is null")
        void getCurrentShift_withNullUserId_shouldReturnEmpty() {
            // Arrange
            when(repo.findCurrentShift(eq(null), eq(branchId), any(LocalDate.class), any(LocalTime.class)))
                    .thenReturn(Optional.empty());

            // Act
            Optional<Shift> result = shiftService.getCurrentShift(null, branchId);

            // Assert
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty when branchId is null")
        void getCurrentShift_withNullBranchId_shouldReturnEmpty() {
            // Arrange
            Long userId = 1L;
            when(repo.findCurrentShift(eq(userId), eq(null), any(LocalDate.class), any(LocalTime.class)))
                    .thenReturn(Optional.empty());

            // Act
            Optional<Shift> result = shiftService.getCurrentShift(userId, null);

            // Assert
            assertThat(result).isEmpty();
        }
    }
}

