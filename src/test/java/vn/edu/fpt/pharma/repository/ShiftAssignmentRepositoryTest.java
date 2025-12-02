package vn.edu.fpt.pharma.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import vn.edu.fpt.pharma.entity.Shift;
import vn.edu.fpt.pharma.entity.ShiftAssignment;
import vn.edu.fpt.pharma.testutil.BaseDataJpaTest;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ShiftAssignmentRepository Tests")
class ShiftAssignmentRepositoryTest extends BaseDataJpaTest {

    @Autowired
    private ShiftAssignmentRepository assignmentRepository;

    @Autowired
    private ShiftRepository shiftRepository;

    @Nested
    @DisplayName("findByShiftId Tests")
    class FindByShiftIdTests {

        @Test
        @DisplayName("Should find all assignments for shift")
        void shouldFindAllAssignments_forShift() {
            Shift shift = createAndSaveShift("Morning", "08:00", "16:00", 1L);

            ShiftAssignment assignment1 = createAndSaveAssignment(shift, 100L);
            ShiftAssignment assignment2 = createAndSaveAssignment(shift, 101L);
            ShiftAssignment assignment3 = createAndSaveAssignment(shift, 102L);

            List<ShiftAssignment> result = assignmentRepository.findByShiftId(shift.getId());

            assertThat(result).hasSize(3);
            assertThat(result).extracting(ShiftAssignment::getUserId)
                    .containsExactlyInAnyOrder(100L, 101L, 102L);
        }

        @Test
        @DisplayName("Should return empty list when no assignments")
        void shouldReturnEmptyList_whenNoAssignments() {
            Shift shift = createAndSaveShift("Evening", "16:00", "23:00", 1L);

            List<ShiftAssignment> result = assignmentRepository.findByShiftId(shift.getId());

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should not include deleted assignments")
        void shouldNotIncludeDeletedAssignments() {
            Shift shift = createAndSaveShift("Morning", "08:00", "16:00", 1L);

            ShiftAssignment assignment1 = createAndSaveAssignment(shift, 100L);
            ShiftAssignment assignment2 = createAndSaveAssignment(shift, 101L);

            // Soft delete assignment2
            assignment2.setDeleted(true);
            assignmentRepository.save(assignment2);

            List<ShiftAssignment> result = assignmentRepository.findByShiftId(shift.getId());

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getUserId()).isEqualTo(100L);
        }

        @Test
        @DisplayName("Should return empty list for non-existent shift")
        void shouldReturnEmptyList_forNonExistentShift() {
            List<ShiftAssignment> result = assignmentRepository.findByShiftId(999L);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByShiftIdAndUserId Tests")
    class FindByShiftIdAndUserIdTests {

        @Test
        @DisplayName("Should find assignment by shift and user")
        void shouldFindAssignment_byShiftAndUser() {
            Shift shift = createAndSaveShift("Morning", "08:00", "16:00", 1L);
            Long userId = 100L;

            ShiftAssignment saved = createAndSaveAssignment(shift, userId);

            Optional<ShiftAssignment> result = assignmentRepository
                    .findByShiftIdAndUserId(shift.getId(), userId);

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(saved.getId());
            assertThat(result.get().getUserId()).isEqualTo(userId);
            assertThat(result.get().getShift().getId()).isEqualTo(shift.getId());
        }

        @Test
        @DisplayName("Should return empty when assignment not exists")
        void shouldReturnEmpty_whenNotExists() {
            Shift shift = createAndSaveShift("Morning", "08:00", "16:00", 1L);
            Long userId = 999L;

            Optional<ShiftAssignment> result = assignmentRepository
                    .findByShiftIdAndUserId(shift.getId(), userId);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should not return deleted assignment")
        void shouldNotReturnDeletedAssignment() {
            Shift shift = createAndSaveShift("Morning", "08:00", "16:00", 1L);
            Long userId = 100L;

            ShiftAssignment assignment = createAndSaveAssignment(shift, userId);
            assignment.setDeleted(true);
            assignmentRepository.save(assignment);

            Optional<ShiftAssignment> result = assignmentRepository
                    .findByShiftIdAndUserId(shift.getId(), userId);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return only matching user assignment")
        void shouldReturnOnlyMatchingUser() {
            Shift shift = createAndSaveShift("Morning", "08:00", "16:00", 1L);

            createAndSaveAssignment(shift, 100L);
            ShiftAssignment assignment2 = createAndSaveAssignment(shift, 101L);
            createAndSaveAssignment(shift, 102L);

            Optional<ShiftAssignment> result = assignmentRepository
                    .findByShiftIdAndUserId(shift.getId(), 101L);

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(assignment2.getId());
            assertThat(result.get().getUserId()).isEqualTo(101L);
        }
    }

    @Nested
    @DisplayName("findByUserId Tests")
    class FindByUserIdTests {

        @Test
        @DisplayName("Should find all assignments for user across shifts")
        void shouldFindAllAssignments_forUser() {
            Shift morningShift = createAndSaveShift("Morning", "08:00", "16:00", 1L);
            Shift eveningShift = createAndSaveShift("Evening", "16:00", "23:00", 1L);
            Long userId = 100L;

            ShiftAssignment assignment1 = createAndSaveAssignment(morningShift, userId);
            ShiftAssignment assignment2 = createAndSaveAssignment(eveningShift, userId);

            List<ShiftAssignment> result = assignmentRepository.findByUserId(userId);

            assertThat(result).hasSize(2);
            assertThat(result).extracting(ShiftAssignment::getId)
                    .containsExactlyInAnyOrder(assignment1.getId(), assignment2.getId());
        }

        @Test
        @DisplayName("Should return empty list when user has no assignments")
        void shouldReturnEmptyList_whenUserHasNoAssignments() {
            Long userId = 999L;

            List<ShiftAssignment> result = assignmentRepository.findByUserId(userId);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should not include deleted assignments")
        void shouldNotIncludeDeletedAssignments() {
            Shift morningShift = createAndSaveShift("Morning", "08:00", "16:00", 1L);
            Shift eveningShift = createAndSaveShift("Evening", "16:00", "23:00", 1L);
            Long userId = 100L;

            ShiftAssignment assignment1 = createAndSaveAssignment(morningShift, userId);
            ShiftAssignment assignment2 = createAndSaveAssignment(eveningShift, userId);

            // Soft delete assignment2
            assignment2.setDeleted(true);
            assignmentRepository.save(assignment2);

            List<ShiftAssignment> result = assignmentRepository.findByUserId(userId);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(assignment1.getId());
        }
    }

    @Nested
    @DisplayName("existsByUserIdAndDeletedFalse Tests")
    class ExistsByUserIdAndDeletedFalseTests {

        @Test
        @DisplayName("Should return true when user has active assignments")
        void shouldReturnTrue_whenUserHasActiveAssignments() {
            Shift shift = createAndSaveShift("Morning", "08:00", "16:00", 1L);
            Long userId = 100L;

            createAndSaveAssignment(shift, userId);

            boolean result = assignmentRepository.existsByUserIdAndDeletedFalse(userId);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should return false when user has no assignments")
        void shouldReturnFalse_whenUserHasNoAssignments() {
            Long userId = 999L;

            boolean result = assignmentRepository.existsByUserIdAndDeletedFalse(userId);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return false when user only has deleted assignments")
        void shouldReturnFalse_whenOnlyDeletedAssignments() {
            Shift shift = createAndSaveShift("Morning", "08:00", "16:00", 1L);
            Long userId = 100L;

            ShiftAssignment assignment = createAndSaveAssignment(shift, userId);
            assignment.setDeleted(true);
            assignmentRepository.save(assignment);

            boolean result = assignmentRepository.existsByUserIdAndDeletedFalse(userId);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return true when user has at least one active assignment")
        void shouldReturnTrue_whenHasAtLeastOneActive() {
            Shift shift1 = createAndSaveShift("Morning", "08:00", "16:00", 1L);
            Shift shift2 = createAndSaveShift("Evening", "16:00", "23:00", 1L);
            Long userId = 100L;

            createAndSaveAssignment(shift1, userId);
            ShiftAssignment assignment2 = createAndSaveAssignment(shift2, userId);
            assignment2.setDeleted(true);
            assignmentRepository.save(assignment2);

            boolean result = assignmentRepository.existsByUserIdAndDeletedFalse(userId);

            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("CRUD Operations Tests")
    class CrudOperationsTests {

        @Test
        @DisplayName("Should save new assignment")
        void shouldSaveNewAssignment() {
            Shift shift = createAndSaveShift("Morning", "08:00", "16:00", 1L);
            Long userId = 100L;

            ShiftAssignment assignment = ShiftAssignment.builder()
                    .shift(shift)
                    .userId(userId)
                    .build();

            ShiftAssignment saved = assignmentRepository.save(assignment);

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getUserId()).isEqualTo(userId);
            assertThat(saved.getShift().getId()).isEqualTo(shift.getId());
            assertThat(saved.isDeleted()).isFalse();
        }

        @Test
        @DisplayName("Should update existing assignment")
        void shouldUpdateExistingAssignment() {
            Shift shift = createAndSaveShift("Morning", "08:00", "16:00", 1L);
            ShiftAssignment assignment = createAndSaveAssignment(shift, 100L);

            assignment.setUserId(200L);
            ShiftAssignment updated = assignmentRepository.save(assignment);

            assertThat(updated.getId()).isEqualTo(assignment.getId());
            assertThat(updated.getUserId()).isEqualTo(200L);
        }

        @Test
        @DisplayName("Should soft delete assignment")
        void shouldSoftDeleteAssignment() {
            Shift shift = createAndSaveShift("Morning", "08:00", "16:00", 1L);
            ShiftAssignment assignment = createAndSaveAssignment(shift, 100L);
            Long assignmentId = assignment.getId();

            assignmentRepository.delete(assignment);
            assignmentRepository.flush();

            Optional<ShiftAssignment> result = assignmentRepository.findById(assignmentId);
            assertThat(result).isEmpty(); // Soft deleted entities are filtered by @SQLRestriction
        }

        @Test
        @DisplayName("Should find by id")
        void shouldFindById() {
            Shift shift = createAndSaveShift("Morning", "08:00", "16:00", 1L);
            ShiftAssignment assignment = createAndSaveAssignment(shift, 100L);

            Optional<ShiftAssignment> result = assignmentRepository.findById(assignment.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(assignment.getId());
            assertThat(result.get().getUserId()).isEqualTo(100L);
        }
    }

    // Helper methods
    private Shift createAndSaveShift(String name, String startTime, String endTime, Long branchId) {
        Shift shift = Shift.builder()
                .name(name)
                .startTime(LocalTime.parse(startTime))
                .endTime(LocalTime.parse(endTime))
                .branchId(branchId)
                .build();
        return shiftRepository.save(shift);
    }

    private ShiftAssignment createAndSaveAssignment(Shift shift, Long userId) {
        ShiftAssignment assignment = ShiftAssignment.builder()
                .shift(shift)
                .userId(userId)
                .build();
        return assignmentRepository.save(assignment);
    }
}

