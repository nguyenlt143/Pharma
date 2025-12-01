package vn.edu.fpt.pharma.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.edu.fpt.pharma.dto.manager.ShiftRequest;
import vn.edu.fpt.pharma.dto.manager.ShiftResponse;
import vn.edu.fpt.pharma.entity.Shift;
import vn.edu.fpt.pharma.repository.ShiftRepository;
import vn.edu.fpt.pharma.service.impl.ShiftServiceImpl;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ShiftServiceImpl Tests")
class ShiftServiceImplTest {

    @Mock
    private ShiftRepository shiftRepository;

    @InjectMocks
    private ShiftServiceImpl shiftService;

    @Nested
    @DisplayName("listShifts Tests")
    class ListShiftsTests {
        @Test
        void listShifts_activeOnly_returnsActiveShifts() {
            Long branchId = 1L;
            List<Shift> shifts = Arrays.asList(
                    createShift(1L, "Morning", "08:00", "16:00", false),
                    createShift(2L, "Evening", "16:00", "23:00", false)
            );
            when(shiftRepository.search(null, branchId)).thenReturn(shifts);

            List<ShiftResponse> result = shiftService.listShifts(null, branchId, false);

            assertThat(result).hasSize(2);
            assertThat(result).allMatch(shift -> !shift.isDeleted());
            verify(shiftRepository).search(null, branchId);
            verify(shiftRepository, never()).searchIncludingDeleted(anyString(), anyLong());
        }

        @Test
        void listShifts_includeDeleted_returnsAll() {
            Long branchId = 1L;
            List<Shift> shifts = Arrays.asList(
                    createShift(1L, "Morning", "08:00", "16:00", false),
                    createShift(2L, "Night", "23:00", "08:00", true)
            );
            when(shiftRepository.searchIncludingDeleted(null, branchId)).thenReturn(shifts);

            List<ShiftResponse> result = shiftService.listShifts(null, branchId, true);

            assertThat(result).hasSize(2);
            verify(shiftRepository).searchIncludingDeleted(null, branchId);
            verify(shiftRepository, never()).search(anyString(), anyLong());
        }

        @Test
        void listShifts_withSearchQuery_filtersResults() {
            Long branchId = 1L;
            List<Shift> shifts = Arrays.asList(
                    createShift(1L, "Morning", "08:00", "16:00", false)
            );
            when(shiftRepository.search("Morning", branchId)).thenReturn(shifts);

            List<ShiftResponse> result = shiftService.listShifts("Morning", branchId, false);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("Morning");
            verify(shiftRepository).search("Morning", branchId);
        }
    }

    @Nested
    @DisplayName("findById Tests")
    class FindByIdTests {
        @Test
        void findById_found_returnsShiftResponse() {
            Shift shift = createShift(1L, "Morning", "08:00", "16:00", false);
            when(shiftRepository.findById(1L)).thenReturn(Optional.of(shift));

            Optional<ShiftResponse> result = shiftService.findById(1L);

            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("Morning");
            assertThat(result.get().getStartTime()).isEqualTo("08:00");
            verify(shiftRepository).findById(1L);
        }

        @Test
        void findById_notFound_returnsEmpty() {
            when(shiftRepository.findById(999L)).thenReturn(Optional.empty());

            Optional<ShiftResponse> result = shiftService.findById(999L);

            assertThat(result).isEmpty();
            verify(shiftRepository).findById(999L);
        }
    }

    @Nested
    @DisplayName("save Tests")
    class SaveTests {
        @Test
        void save_createNewShift_success() {
            ShiftRequest request = createShiftRequest(null, "Morning", "08:00", "16:00", "Morning shift");
            Shift savedShift = createShift(1L, "Morning", "08:00", "16:00", false);

            when(shiftRepository.save(any(Shift.class))).thenReturn(savedShift);

            ShiftResponse result = shiftService.save(request, 1L);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Morning");
            assertThat(result.getStartTime()).isEqualTo("08:00");
            assertThat(result.getEndTime()).isEqualTo("16:00");
            verify(shiftRepository).save(any(Shift.class));
        }

        @Test
        void save_updateExistingShift_success() {
            ShiftRequest request = createShiftRequest(1L, "Updated Morning", "09:00", "17:00", "Updated");
            Shift existingShift = createShift(1L, "Morning", "08:00", "16:00", false);
            Shift updatedShift = createShift(1L, "Updated Morning", "09:00", "17:00", false);

            when(shiftRepository.findById(1L)).thenReturn(Optional.of(existingShift));
            when(shiftRepository.save(any(Shift.class))).thenReturn(updatedShift);

            ShiftResponse result = shiftService.save(request, 1L);

            assertThat(result.getName()).isEqualTo("Updated Morning");
            assertThat(result.getStartTime()).isEqualTo("09:00");
            verify(shiftRepository).findById(1L);
            verify(shiftRepository).save(any(Shift.class));
        }

        @Test
        void save_endTimeBeforeStartTime_throwsException() {
            ShiftRequest request = createShiftRequest(null, "Invalid", "16:00", "08:00", "Invalid");

            assertThatThrownBy(() -> shiftService.save(request, 1L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Giờ kết thúc phải lớn hơn giờ bắt đầu");

            verify(shiftRepository, never()).save(any(Shift.class));
        }

        @Test
        void save_endTimeEqualsStartTime_throwsException() {
            ShiftRequest request = createShiftRequest(null, "Invalid", "12:00", "12:00", "Invalid");

            assertThatThrownBy(() -> shiftService.save(request, 1L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Giờ kết thúc phải lớn hơn giờ bắt đầu");

            verify(shiftRepository, never()).save(any(Shift.class));
        }

        @Test
        void save_timeFormatWithSeconds_parsedCorrectly() {
            ShiftRequest request = createShiftRequest(null, "Shift", "08:00:00", "16:00:00", "With seconds");
            Shift savedShift = createShift(1L, "Shift", "08:00", "16:00", false);

            when(shiftRepository.save(any(Shift.class))).thenReturn(savedShift);

            ShiftResponse result = shiftService.save(request, 1L);

            assertThat(result).isNotNull();
            verify(shiftRepository).save(any(Shift.class));
        }

        @Test
        void save_setBranchIdCorrectly() {
            ShiftRequest request = createShiftRequest(null, "Shift", "08:00", "16:00", "Note");
            Shift savedShift = createShift(1L, "Shift", "08:00", "16:00", false);
            savedShift.setBranchId(2L);

            when(shiftRepository.save(any(Shift.class))).thenReturn(savedShift);

            shiftService.save(request, 2L);

            verify(shiftRepository).save(argThat(shift -> shift.getBranchId().equals(2L)));
        }
    }

    @Nested
    @DisplayName("delete Tests")
    class DeleteTests {
        @Test
        void delete_softDeletesShift() {
            doNothing().when(shiftRepository).deleteById(1L);

            shiftService.delete(1L);

            verify(shiftRepository).deleteById(1L);
        }
    }

    @Nested
    @DisplayName("restore Tests")
    class RestoreTests {
        @Test
        void restore_restoresDeletedShift() {
            doNothing().when(shiftRepository).restoreById(1L);

            shiftService.restore(1L);

            verify(shiftRepository).restoreById(1L);
        }
    }

    // Helper methods
    private Shift createShift(Long id, String name, String startTime, String endTime, boolean deleted) {
        Shift shift = new Shift();
        shift.setId(id);
        shift.setName(name);
        shift.setStartTime(LocalTime.parse(startTime));
        shift.setEndTime(LocalTime.parse(endTime));
        shift.setDeleted(deleted);
        shift.setBranchId(1L);
        return shift;
    }

    private ShiftRequest createShiftRequest(Long id, String name, String startTime, String endTime, String note) {
        ShiftRequest request = new ShiftRequest();
        request.setId(id);
        request.setName(name);
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        request.setNote(note);
        return request;
    }
}

