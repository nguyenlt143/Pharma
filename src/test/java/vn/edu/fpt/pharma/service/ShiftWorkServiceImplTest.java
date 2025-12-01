package vn.edu.fpt.pharma.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.edu.fpt.pharma.dto.manager.ShiftWorkAssignRequest;
import vn.edu.fpt.pharma.dto.manager.ShiftWorkResponse;
import vn.edu.fpt.pharma.entity.Shift;
import vn.edu.fpt.pharma.entity.ShiftAssignment;
import vn.edu.fpt.pharma.entity.ShiftWork;
import vn.edu.fpt.pharma.entity.User;
import vn.edu.fpt.pharma.repository.ShiftAssignmentRepository;
import vn.edu.fpt.pharma.repository.ShiftRepository;
import vn.edu.fpt.pharma.repository.ShiftWorkRepository;
import vn.edu.fpt.pharma.repository.UserRepository;
import vn.edu.fpt.pharma.service.impl.ShiftWorkServiceImpl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ShiftWorkServiceImpl Tests")
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class ShiftWorkServiceImplTest {

    @Mock
    private ShiftWorkRepository shiftWorkRepository;

    @Mock
    private ShiftRepository shiftRepository;

    @Mock
    private ShiftAssignmentRepository assignmentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ShiftWorkServiceImpl shiftWorkService;

    @Nested
    @DisplayName("findByShiftAndDate Tests")
    class FindByShiftAndDateTests {

        @Test
        @DisplayName("Should return shift works for date")
        void shouldReturnShiftWorks_forDate() {
            Long shiftId = 1L;
            LocalDate date = LocalDate.now();
            User user1 = createUser(100L, "John Doe", "john", "0901234567");
            User user2 = createUser(101L, "Jane Smith", "jane", "0907654321");

            ShiftAssignment assignment1 = createAssignment(1L, shiftId, 100L);
            ShiftAssignment assignment2 = createAssignment(2L, shiftId, 101L);

            ShiftWork work1 = ShiftWork.builder()
                    .assignment(assignment1)
                    .workDate(date)
                    .build();
            work1.setId(1L);

            ShiftWork work2 = ShiftWork.builder()
                    .assignment(assignment2)
                    .workDate(date)
                    .build();
            work2.setId(2L);

            List<ShiftWork> works = Arrays.asList(work1, work2);

            when(shiftWorkRepository.findByShiftIdAndWorkDate(shiftId, date)).thenReturn(works);
            // Stub both user lookups - they will be called in toDto for each ShiftWork
            when(userRepository.findById(100L)).thenReturn(Optional.of(user1));
            when(userRepository.findById(101L)).thenReturn(Optional.of(user2));

            List<ShiftWorkResponse> result = shiftWorkService.findByShiftAndDate(shiftId, date);

            assertThat(result).hasSize(2);
            assertThat(result).extracting(ShiftWorkResponse::getId)
                    .containsExactlyInAnyOrder(1L, 2L);
            assertThat(result).extracting(ShiftWorkResponse::getUserId)
                    .containsExactlyInAnyOrder(100L, 101L);
            verify(shiftWorkRepository).findByShiftIdAndWorkDate(shiftId, date);
        }

        @Test
        @DisplayName("Should return empty list when no works for date")
        void shouldReturnEmptyList_whenNoWorks() {
            Long shiftId = 1L;
            LocalDate date = LocalDate.now();

            when(shiftWorkRepository.findByShiftIdAndWorkDate(shiftId, date))
                    .thenReturn(Arrays.asList());

            List<ShiftWorkResponse> result = shiftWorkService.findByShiftAndDate(shiftId, date);

            assertThat(result).isEmpty();
            verify(shiftWorkRepository).findByShiftIdAndWorkDate(shiftId, date);
        }
    }

    @Nested
    @DisplayName("assignToShift Tests")
    class AssignToShiftTests {

        @Test
        @DisplayName("Should create new shift work with existing assignment")
        void shouldCreateShiftWork_withExistingAssignment() {
            Long shiftId = 1L;
            Long userId = 100L;
            LocalDate date = LocalDate.parse("2025-12-15");

            ShiftWorkAssignRequest request = new ShiftWorkAssignRequest();
            request.setUserId(userId);
            request.setWorkDate("2025-12-15");

            Shift shift = createShift(shiftId, "Morning", "08:00", "16:00");
            User user = createUser(userId, "John Doe", "john", "0901234567");
            ShiftAssignment assignment = createAssignment(5L, shiftId, userId);

            ShiftWork savedWork = ShiftWork.builder()
                    .assignment(assignment)
                    .workDate(date)
                    .build();
            savedWork.setId(10L);

            when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(shiftWorkRepository.findByShiftIdAndUserIdAndWorkDate(shiftId, userId, date))
                    .thenReturn(Optional.empty());
            when(assignmentRepository.findByShiftIdAndUserId(shiftId, userId))
                    .thenReturn(Optional.of(assignment));
            when(shiftWorkRepository.save(any(ShiftWork.class))).thenReturn(savedWork);

            ShiftWorkResponse result = shiftWorkService.assignToShift(shiftId, request);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(10L);
            assertThat(result.getUserId()).isEqualTo(userId);
            assertThat(result.getUserFullName()).isEqualTo("John Doe");

            verify(shiftWorkRepository).save(any(ShiftWork.class));
            verify(assignmentRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should create new assignment and shift work when no assignment exists")
        void shouldCreateAssignmentAndShiftWork_whenNoAssignment() {
            Long shiftId = 1L;
            Long userId = 100L;
            LocalDate date = LocalDate.parse("2025-12-15");

            ShiftWorkAssignRequest request = new ShiftWorkAssignRequest();
            request.setUserId(userId);
            request.setWorkDate("2025-12-15");

            Shift shift = createShift(shiftId, "Morning", "08:00", "16:00");
            User user = createUser(userId, "John Doe", "john", "0901234567");
            ShiftAssignment newAssignment = createAssignment(5L, shiftId, userId);

            ShiftWork savedWork = ShiftWork.builder()
                    .assignment(newAssignment)
                    .workDate(date)
                    .build();
            savedWork.setId(10L);

            when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(shiftWorkRepository.findByShiftIdAndUserIdAndWorkDate(shiftId, userId, date))
                    .thenReturn(Optional.empty());
            when(assignmentRepository.findByShiftIdAndUserId(shiftId, userId))
                    .thenReturn(Optional.empty());
            when(assignmentRepository.save(any(ShiftAssignment.class))).thenReturn(newAssignment);
            when(shiftWorkRepository.save(any(ShiftWork.class))).thenReturn(savedWork);

            ShiftWorkResponse result = shiftWorkService.assignToShift(shiftId, request);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(10L);

            ArgumentCaptor<ShiftAssignment> assignmentCaptor = ArgumentCaptor.forClass(ShiftAssignment.class);
            verify(assignmentRepository).save(assignmentCaptor.capture());
            ShiftAssignment captured = assignmentCaptor.getValue();
            assertThat(captured.getUserId()).isEqualTo(userId);
            assertThat(captured.getShift()).isEqualTo(shift);

            verify(shiftWorkRepository).save(any(ShiftWork.class));
        }

        @Test
        @DisplayName("Should throw exception when user already assigned to shift on date")
        void shouldThrowException_whenDuplicateAssignment() {
            Long shiftId = 1L;
            Long userId = 100L;
            LocalDate date = LocalDate.parse("2025-12-15");

            ShiftWorkAssignRequest request = new ShiftWorkAssignRequest();
            request.setUserId(userId);
            request.setWorkDate("2025-12-15");

            Shift shift = createShift(shiftId, "Morning", "08:00", "16:00");
            User user = createUser(userId, "John Doe", "john", "0901234567");
            ShiftAssignment assignment = createAssignment(5L, shiftId, userId);

            ShiftWork existing = ShiftWork.builder()
                    .assignment(assignment)
                    .workDate(date)
                    .build();
            existing.setId(10L);

            when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(shiftWorkRepository.findByShiftIdAndUserIdAndWorkDate(shiftId, userId, date))
                    .thenReturn(Optional.of(existing));

            assertThatThrownBy(() -> shiftWorkService.assignToShift(shiftId, request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("User already assigned to this shift on date");

            verify(shiftWorkRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when shift not found")
        void shouldThrowException_whenShiftNotFound() {
            Long shiftId = 999L;

            ShiftWorkAssignRequest request = new ShiftWorkAssignRequest();
            request.setUserId(100L);
            request.setWorkDate("2025-12-15");

            when(shiftRepository.findById(shiftId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> shiftWorkService.assignToShift(shiftId, request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Shift not found");

            verify(shiftWorkRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowException_whenUserNotFound() {
            Long shiftId = 1L;
            Long userId = 999L;

            ShiftWorkAssignRequest request = new ShiftWorkAssignRequest();
            request.setUserId(userId);
            request.setWorkDate("2025-12-15");

            Shift shift = createShift(shiftId, "Morning", "08:00", "16:00");

            when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> shiftWorkService.assignToShift(shiftId, request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("User not found");

            verify(shiftWorkRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should parse date correctly from request")
        void shouldParseDateCorrectly() {
            Long shiftId = 1L;
            Long userId = 100L;

            ShiftWorkAssignRequest request = new ShiftWorkAssignRequest();
            request.setUserId(userId);
            request.setWorkDate("2025-01-20");

            Shift shift = createShift(shiftId, "Morning", "08:00", "16:00");
            User user = createUser(userId, "John Doe", "john", "0901234567");
            ShiftAssignment assignment = createAssignment(5L, shiftId, userId);

            when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(shiftWorkRepository.findByShiftIdAndUserIdAndWorkDate(eq(shiftId), eq(userId), any(LocalDate.class)))
                    .thenReturn(Optional.empty());
            when(assignmentRepository.findByShiftIdAndUserId(shiftId, userId))
                    .thenReturn(Optional.of(assignment));
            when(shiftWorkRepository.save(any(ShiftWork.class)))
                    .thenAnswer(invocation -> {
                        ShiftWork work = invocation.getArgument(0);
                        work.setId(10L);
                        return work;
                    });

            ShiftWorkResponse result = shiftWorkService.assignToShift(shiftId, request);

            assertThat(result).isNotNull();

            ArgumentCaptor<ShiftWork> workCaptor = ArgumentCaptor.forClass(ShiftWork.class);
            verify(shiftWorkRepository).save(workCaptor.capture());
            ShiftWork captured = workCaptor.getValue();
            assertThat(captured.getWorkDate()).isEqualTo(LocalDate.parse("2025-01-20"));
        }
    }

    @Nested
    @DisplayName("removeShiftWork Tests")
    class RemoveShiftWorkTests {

        @Test
        @DisplayName("Should delete shift work by id")
        void shouldDeleteShiftWork() {
            Long workId = 10L;

            shiftWorkService.removeShiftWork(workId);

            verify(shiftWorkRepository).deleteById(workId);
        }

        @Test
        @DisplayName("Should handle non-existent work id gracefully")
        void shouldHandleNonExistentId() {
            Long workId = 999L;

            doNothing().when(shiftWorkRepository).deleteById(workId);

            assertThatCode(() -> shiftWorkService.removeShiftWork(workId))
                    .doesNotThrowAnyException();

            verify(shiftWorkRepository).deleteById(workId);
        }
    }

    // Helper methods (removed createShiftWork helper - create directly in tests to avoid confusion)

    private ShiftAssignment createAssignment(Long id, Long shiftId, Long userId) {
        ShiftAssignment assignment = ShiftAssignment.builder()
                .userId(userId)
                .shift(createShift(shiftId, "Morning", "08:00", "16:00"))
                .build();
        assignment.setId(id);
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

    private User createUser(Long id, String fullName, String userName, String phoneNumber) {
        User user = User.builder()
                .fullName(fullName)
                .userName(userName)
                .phoneNumber(phoneNumber)
                .build();
        user.setId(id);
        return user;
    }
}

