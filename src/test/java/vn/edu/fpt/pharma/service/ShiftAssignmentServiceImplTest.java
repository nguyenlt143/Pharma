package vn.edu.fpt.pharma.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.edu.fpt.pharma.entity.Shift;
import vn.edu.fpt.pharma.entity.ShiftAssignment;
import vn.edu.fpt.pharma.entity.ShiftWork;
import vn.edu.fpt.pharma.repository.ShiftAssignmentRepository;
import vn.edu.fpt.pharma.repository.ShiftRepository;
import vn.edu.fpt.pharma.repository.ShiftWorkRepository;
import vn.edu.fpt.pharma.service.impl.ShiftAssignmentServiceImpl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ShiftAssignmentServiceImpl Tests")
class ShiftAssignmentServiceImplTest {

    @Mock
    private ShiftAssignmentRepository assignmentRepository;

    @Mock
    private ShiftRepository shiftRepository;

    @Mock
    private ShiftWorkRepository shiftWorkRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private ShiftAssignmentServiceImpl assignmentService;

    @Nested
    @DisplayName("findByShiftIdAndUserId Tests")
    class FindByShiftIdAndUserIdTests {

        @Test
        @DisplayName("Should return assignment when exists")
        void shouldReturnAssignment_whenExists() {
            Long shiftId = 1L;
            Long userId = 100L;
            ShiftAssignment expected = createAssignment(1L, shiftId, userId);

            when(assignmentRepository.findByShiftIdAndUserId(shiftId, userId))
                    .thenReturn(Optional.of(expected));

            ShiftAssignment result = assignmentService.findByShiftIdAndUserId(shiftId, userId);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getUserId()).isEqualTo(userId);
            verify(assignmentRepository).findByShiftIdAndUserId(shiftId, userId);
        }

        @Test
        @DisplayName("Should return null when not exists")
        void shouldReturnNull_whenNotExists() {
            Long shiftId = 1L;
            Long userId = 100L;

            when(assignmentRepository.findByShiftIdAndUserId(shiftId, userId))
                    .thenReturn(Optional.empty());

            ShiftAssignment result = assignmentService.findByShiftIdAndUserId(shiftId, userId);

            assertThat(result).isNull();
            verify(assignmentRepository).findByShiftIdAndUserId(shiftId, userId);
        }
    }

    @Nested
    @DisplayName("findByShiftId Tests")
    class FindByShiftIdTests {

        @Test
        @DisplayName("Should return first assignment when multiple exist")
        void shouldReturnFirstAssignment_whenMultipleExist() {
            Long shiftId = 1L;
            List<ShiftAssignment> assignments = Arrays.asList(
                    createAssignment(1L, shiftId, 100L),
                    createAssignment(2L, shiftId, 101L)
            );

            when(assignmentRepository.findByShiftId(shiftId)).thenReturn(assignments);

            ShiftAssignment result = assignmentService.findByShiftId(shiftId);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            verify(assignmentRepository).findByShiftId(shiftId);
        }

        @Test
        @DisplayName("Should return null when no assignments")
        void shouldReturnNull_whenNoAssignments() {
            Long shiftId = 1L;

            when(assignmentRepository.findByShiftId(shiftId)).thenReturn(Arrays.asList());

            ShiftAssignment result = assignmentService.findByShiftId(shiftId);

            assertThat(result).isNull();
            verify(assignmentRepository).findByShiftId(shiftId);
        }
    }

    @Nested
    @DisplayName("findAllByShiftId Tests")
    class FindAllByShiftIdTests {

        @Test
        @DisplayName("Should return all assignments for shift")
        void shouldReturnAllAssignments() {
            Long shiftId = 1L;
            List<ShiftAssignment> expected = Arrays.asList(
                    createAssignment(1L, shiftId, 100L),
                    createAssignment(2L, shiftId, 101L),
                    createAssignment(3L, shiftId, 102L)
            );

            when(assignmentRepository.findByShiftId(shiftId)).thenReturn(expected);

            List<ShiftAssignment> result = assignmentService.findAllByShiftId(shiftId);

            assertThat(result).hasSize(3);
            assertThat(result).extracting(ShiftAssignment::getUserId)
                    .containsExactly(100L, 101L, 102L);
            verify(assignmentRepository).findByShiftId(shiftId);
        }

        @Test
        @DisplayName("Should return empty list when no assignments")
        void shouldReturnEmptyList_whenNoAssignments() {
            Long shiftId = 1L;

            when(assignmentRepository.findByShiftId(shiftId)).thenReturn(Arrays.asList());

            List<ShiftAssignment> result = assignmentService.findAllByShiftId(shiftId);

            assertThat(result).isEmpty();
            verify(assignmentRepository).findByShiftId(shiftId);
        }
    }

    @Nested
    @DisplayName("createAssignment Tests")
    class CreateAssignmentTests {

        @Test
        @DisplayName("Should create new assignment and 30 days of shift works")
        void shouldCreateNewAssignment_withShiftWorks() {
            Long shiftId = 1L;
            Long userId = 100L;
            Shift shift = createShift(shiftId, "Morning", "08:00", "16:00");
            ShiftAssignment newAssignment = createAssignment(null, shiftId, userId);
            ShiftAssignment savedAssignment = createAssignment(10L, shiftId, userId);

            when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
            when(assignmentRepository.findByShiftIdAndUserId(shiftId, userId))
                    .thenReturn(Optional.empty());
            when(assignmentRepository.save(any(ShiftAssignment.class))).thenReturn(savedAssignment);
            when(assignmentRepository.findById(10L)).thenReturn(Optional.of(savedAssignment));
            when(shiftWorkRepository.findLastWorkDateByAssignmentId(10L)).thenReturn(null);

            ShiftAssignment result = assignmentService.createAssignment(shiftId, userId);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(10L);

            // Verify assignment was saved
            ArgumentCaptor<ShiftAssignment> assignmentCaptor = ArgumentCaptor.forClass(ShiftAssignment.class);
            verify(assignmentRepository).save(assignmentCaptor.capture());
            ShiftAssignment captured = assignmentCaptor.getValue();
            assertThat(captured.getShift()).isEqualTo(shift);
            assertThat(captured.getUserId()).isEqualTo(userId);

            // Verify 30 shift works were created
            ArgumentCaptor<List<ShiftWork>> worksCaptor = ArgumentCaptor.forClass(List.class);
            verify(shiftWorkRepository).saveAll(worksCaptor.capture());
            List<ShiftWork> capturedWorks = worksCaptor.getValue();
            assertThat(capturedWorks).hasSize(30);
        }

        @Test
        @DisplayName("Should return existing assignment when already exists")
        void shouldReturnExisting_whenAlreadyExists() {
            Long shiftId = 1L;
            Long userId = 100L;
            Shift shift = createShift(shiftId, "Morning", "08:00", "16:00");
            ShiftAssignment existing = createAssignment(5L, shiftId, userId);

            when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
            when(assignmentRepository.findByShiftIdAndUserId(shiftId, userId))
                    .thenReturn(Optional.of(existing));

            ShiftAssignment result = assignmentService.createAssignment(shiftId, userId);

            assertThat(result).isEqualTo(existing);
            assertThat(result.getId()).isEqualTo(5L);

            // Should not create new assignment
            verify(assignmentRepository, never()).save(any());
            verify(shiftWorkRepository, never()).saveAll(any());
        }

        @Test
        @DisplayName("Should throw exception when shift not found")
        void shouldThrowException_whenShiftNotFound() {
            Long shiftId = 999L;
            Long userId = 100L;

            when(shiftRepository.findById(shiftId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> assignmentService.createAssignment(shiftId, userId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Shift not found");

            verify(assignmentRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("removeAssignment Tests")
    class RemoveAssignmentTests {

        @Test
        @DisplayName("Should delete assignment when exists")
        void shouldDeleteAssignment_whenExists() {
            Long shiftId = 1L;
            Long userId = 100L;
            ShiftAssignment assignment = createAssignment(5L, shiftId, userId);

            when(assignmentRepository.findByShiftIdAndUserId(shiftId, userId))
                    .thenReturn(Optional.of(assignment));

            assignmentService.removeAssignment(shiftId, userId);

            verify(assignmentRepository).delete(assignment);
        }

        @Test
        @DisplayName("Should do nothing when assignment not exists")
        void shouldDoNothing_whenNotExists() {
            Long shiftId = 1L;
            Long userId = 100L;

            when(assignmentRepository.findByShiftIdAndUserId(shiftId, userId))
                    .thenReturn(Optional.empty());

            assignmentService.removeAssignment(shiftId, userId);

            verify(assignmentRepository, never()).delete(any(ShiftAssignment.class));
        }
    }

    @Nested
    @DisplayName("extendShiftWorks Tests")
    class ExtendShiftWorksTests {

        @Test
        @DisplayName("Should extend from last date when works exist")
        void shouldExtendFromLastDate_whenWorksExist() {
            Long assignmentId = 1L;
            Shift shift = createShift(1L, "Morning", "08:00", "16:00");
            ShiftAssignment assignment = ShiftAssignment.builder()
                    .userId(100L)
                    .shift(shift)
                    .build();
            assignment.setId(assignmentId);
            LocalDate lastDate = LocalDate.now().plusDays(29);

            when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
            when(shiftWorkRepository.findLastWorkDateByAssignmentId(assignmentId))
                    .thenReturn(lastDate);

            assignmentService.extendShiftWorks(assignmentId, 15);

            ArgumentCaptor<List<ShiftWork>> worksCaptor = ArgumentCaptor.forClass(List.class);
            verify(shiftWorkRepository).saveAll(worksCaptor.capture());

            List<ShiftWork> capturedWorks = worksCaptor.getValue();
            assertThat(capturedWorks).hasSize(15);
            assertThat(capturedWorks.get(0).getWorkDate()).isEqualTo(lastDate.plusDays(1));
            assertThat(capturedWorks.get(14).getWorkDate()).isEqualTo(lastDate.plusDays(15));
        }

        @Test
        @DisplayName("Should start from today when no existing works")
        void shouldStartFromToday_whenNoExistingWorks() {
            Long assignmentId = 1L;
            Shift shift = createShift(1L, "Morning", "08:00", "16:00");
            ShiftAssignment assignment = ShiftAssignment.builder()
                    .userId(100L)
                    .shift(shift)
                    .build();
            assignment.setId(assignmentId);
            LocalDate today = LocalDate.now();

            when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
            when(shiftWorkRepository.findLastWorkDateByAssignmentId(assignmentId)).thenReturn(null);

            assignmentService.extendShiftWorks(assignmentId, 10);

            ArgumentCaptor<List<ShiftWork>> worksCaptor = ArgumentCaptor.forClass(List.class);
            verify(shiftWorkRepository).saveAll(worksCaptor.capture());

            List<ShiftWork> capturedWorks = worksCaptor.getValue();
            assertThat(capturedWorks).hasSize(10);
            assertThat(capturedWorks.get(0).getWorkDate()).isEqualTo(today);
            assertThat(capturedWorks.get(9).getWorkDate()).isEqualTo(today.plusDays(9));
        }

        @Test
        @DisplayName("Should throw exception when assignment not found")
        void shouldThrowException_whenAssignmentNotFound() {
            Long assignmentId = 999L;

            when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> assignmentService.extendShiftWorks(assignmentId, 10))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Assignment not found");

            verify(shiftWorkRepository, never()).saveAll(any());
        }
    }

    @Nested
    @DisplayName("getLastWorkDate Tests")
    class GetLastWorkDateTests {

        @Test
        @DisplayName("Should return last work date")
        void shouldReturnLastWorkDate() {
            Long assignmentId = 1L;
            LocalDate expected = LocalDate.now().plusDays(29);

            when(shiftWorkRepository.findLastWorkDateByAssignmentId(assignmentId))
                    .thenReturn(expected);

            LocalDate result = assignmentService.getLastWorkDate(assignmentId);

            assertThat(result).isEqualTo(expected);
            verify(shiftWorkRepository).findLastWorkDateByAssignmentId(assignmentId);
        }

        @Test
        @DisplayName("Should return null when no works")
        void shouldReturnNull_whenNoWorks() {
            Long assignmentId = 1L;

            when(shiftWorkRepository.findLastWorkDateByAssignmentId(assignmentId))
                    .thenReturn(null);

            LocalDate result = assignmentService.getLastWorkDate(assignmentId);

            assertThat(result).isNull();
            verify(shiftWorkRepository).findLastWorkDateByAssignmentId(assignmentId);
        }
    }

    @Nested
    @DisplayName("getRemainingWorkDays Tests")
    class GetRemainingWorkDaysTests {

        @Test
        @DisplayName("Should return remaining work days count")
        void shouldReturnRemainingWorkDaysCount() {
            Long assignmentId = 1L;
            LocalDate today = LocalDate.now();

            when(shiftWorkRepository.countRemainingWorkDays(assignmentId, today))
                    .thenReturn(25L);

            long result = assignmentService.getRemainingWorkDays(assignmentId);

            assertThat(result).isEqualTo(25L);
            verify(shiftWorkRepository).countRemainingWorkDays(assignmentId, today);
        }

        @Test
        @DisplayName("Should return zero when no remaining days")
        void shouldReturnZero_whenNoRemainingDays() {
            Long assignmentId = 1L;
            LocalDate today = LocalDate.now();

            when(shiftWorkRepository.countRemainingWorkDays(assignmentId, today))
                    .thenReturn(0L);

            long result = assignmentService.getRemainingWorkDays(assignmentId);

            assertThat(result).isEqualTo(0L);
            verify(shiftWorkRepository).countRemainingWorkDays(assignmentId, today);
        }
    }

    // Helper methods
    private ShiftAssignment createAssignment(Long id, Long shiftId, Long userId) {
        ShiftAssignment assignment = ShiftAssignment.builder()
                .userId(userId)
                .shift(createShift(shiftId, "Morning", "08:00", "16:00"))
                .build();
        if (id != null) {
            assignment.setId(id);
        }
        return assignment;
    }

    private Shift createShift(Long id, String name, String startTime, String endTime) {
        Shift shift = Shift.builder()
                .name(name)
                .startTime(LocalTime.parse(startTime))
                .endTime(LocalTime.parse(endTime))
                .branchId(1L)
                .build();
        shift.setId(id);
        return shift;
    }
}

