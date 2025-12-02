package vn.edu.fpt.pharma.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import vn.edu.fpt.pharma.entity.Shift;
import vn.edu.fpt.pharma.entity.ShiftAssignment;
import vn.edu.fpt.pharma.entity.ShiftWork;
import vn.edu.fpt.pharma.testutil.BaseDataJpaTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ShiftWorkRepository Tests")
class ShiftWorkRepositoryTest extends BaseDataJpaTest {

    @Autowired
    private ShiftWorkRepository shiftWorkRepository;

    @Autowired
    private ShiftAssignmentRepository assignmentRepository;

    @Autowired
    private ShiftRepository shiftRepository;

    @Nested
    @DisplayName("findByShiftIdAndWorkDate Tests")
    class FindByShiftIdAndWorkDateTests {

        @Test
        @DisplayName("Should find all works for shift on date")
        void shouldFindAllWorks_forShiftOnDate() {
            Shift shift = createAndSaveShift("Morning", "08:00", "16:00", 1L);
            ShiftAssignment assignment1 = createAndSaveAssignment(shift, 100L);
            ShiftAssignment assignment2 = createAndSaveAssignment(shift, 101L);
            LocalDate date = LocalDate.now();

            ShiftWork work1 = createAndSaveShiftWork(assignment1, date);
            ShiftWork work2 = createAndSaveShiftWork(assignment2, date);

            List<ShiftWork> result = shiftWorkRepository
                    .findByShiftIdAndWorkDate(shift.getId(), date);

            assertThat(result).hasSize(2);
            assertThat(result).extracting(ShiftWork::getId)
                    .containsExactlyInAnyOrder(work1.getId(), work2.getId());
        }

        @Test
        @DisplayName("Should return empty list when no works for date")
        void shouldReturnEmptyList_whenNoWorksForDate() {
            Shift shift = createAndSaveShift("Morning", "08:00", "16:00", 1L);
            LocalDate date = LocalDate.now().plusDays(10);

            List<ShiftWork> result = shiftWorkRepository
                    .findByShiftIdAndWorkDate(shift.getId(), date);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return only works for specific date")
        void shouldReturnOnlyWorksForSpecificDate() {
            Shift shift = createAndSaveShift("Morning", "08:00", "16:00", 1L);
            ShiftAssignment assignment = createAndSaveAssignment(shift, 100L);

            LocalDate today = LocalDate.now();
            LocalDate tomorrow = today.plusDays(1);

            ShiftWork todayWork = createAndSaveShiftWork(assignment, today);
            createAndSaveShiftWork(assignment, tomorrow);

            List<ShiftWork> result = shiftWorkRepository
                    .findByShiftIdAndWorkDate(shift.getId(), today);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(todayWork.getId());
            assertThat(result.get(0).getWorkDate()).isEqualTo(today);
        }

        @Test
        @DisplayName("Should not include deleted works")
        void shouldNotIncludeDeletedWorks() {
            Shift shift = createAndSaveShift("Morning", "08:00", "16:00", 1L);
            ShiftAssignment assignment1 = createAndSaveAssignment(shift, 100L);
            ShiftAssignment assignment2 = createAndSaveAssignment(shift, 101L);
            LocalDate date = LocalDate.now();

            ShiftWork work1 = createAndSaveShiftWork(assignment1, date);
            ShiftWork work2 = createAndSaveShiftWork(assignment2, date);

            work2.setDeleted(true);
            shiftWorkRepository.save(work2);

            List<ShiftWork> result = shiftWorkRepository
                    .findByShiftIdAndWorkDate(shift.getId(), date);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(work1.getId());
        }
    }

    @Nested
    @DisplayName("findByShiftIdAndUserIdAndWorkDate Tests")
    class FindByShiftIdAndUserIdAndWorkDateTests {

        @Test
        @DisplayName("Should find work by shift, user and date")
        void shouldFindWork_byShiftUserAndDate() {
            Shift shift = createAndSaveShift("Morning", "08:00", "16:00", 1L);
            Long userId = 100L;
            ShiftAssignment assignment = createAndSaveAssignment(shift, userId);
            LocalDate date = LocalDate.now();

            ShiftWork work = createAndSaveShiftWork(assignment, date);

            Optional<ShiftWork> result = shiftWorkRepository
                    .findByShiftIdAndUserIdAndWorkDate(shift.getId(), userId, date);

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(work.getId());
            assertThat(result.get().getAssignment().getUserId()).isEqualTo(userId);
            assertThat(result.get().getWorkDate()).isEqualTo(date);
        }

        @Test
        @DisplayName("Should return empty when work not exists")
        void shouldReturnEmpty_whenNotExists() {
            Shift shift = createAndSaveShift("Morning", "08:00", "16:00", 1L);
            Long userId = 999L;
            LocalDate date = LocalDate.now();

            Optional<ShiftWork> result = shiftWorkRepository
                    .findByShiftIdAndUserIdAndWorkDate(shift.getId(), userId, date);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty when date does not match")
        void shouldReturnEmpty_whenDateDoesNotMatch() {
            Shift shift = createAndSaveShift("Morning", "08:00", "16:00", 1L);
            Long userId = 100L;
            ShiftAssignment assignment = createAndSaveAssignment(shift, userId);

            createAndSaveShiftWork(assignment, LocalDate.now());

            Optional<ShiftWork> result = shiftWorkRepository
                    .findByShiftIdAndUserIdAndWorkDate(shift.getId(), userId,
                            LocalDate.now().plusDays(5));

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should not return deleted work")
        void shouldNotReturnDeletedWork() {
            Shift shift = createAndSaveShift("Morning", "08:00", "16:00", 1L);
            Long userId = 100L;
            ShiftAssignment assignment = createAndSaveAssignment(shift, userId);
            LocalDate date = LocalDate.now();

            ShiftWork work = createAndSaveShiftWork(assignment, date);
            work.setDeleted(true);
            shiftWorkRepository.save(work);

            Optional<ShiftWork> result = shiftWorkRepository
                    .findByShiftIdAndUserIdAndWorkDate(shift.getId(), userId, date);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findLastWorkDateByAssignmentId Tests")
    class FindLastWorkDateByAssignmentIdTests {

        @Test
        @DisplayName("Should find last work date")
        void shouldFindLastWorkDate() {
            Shift shift = createAndSaveShift("Morning", "08:00", "16:00", 1L);
            ShiftAssignment assignment = createAndSaveAssignment(shift, 100L);

            LocalDate date1 = LocalDate.now();
            LocalDate date2 = date1.plusDays(5);
            LocalDate date3 = date1.plusDays(10);

            createAndSaveShiftWork(assignment, date1);
            createAndSaveShiftWork(assignment, date2);
            createAndSaveShiftWork(assignment, date3);

            LocalDate result = shiftWorkRepository
                    .findLastWorkDateByAssignmentId(assignment.getId());

            assertThat(result).isEqualTo(date3);
        }

        @Test
        @DisplayName("Should return null when no works")
        void shouldReturnNull_whenNoWorks() {
            Shift shift = createAndSaveShift("Morning", "08:00", "16:00", 1L);
            ShiftAssignment assignment = createAndSaveAssignment(shift, 100L);

            LocalDate result = shiftWorkRepository
                    .findLastWorkDateByAssignmentId(assignment.getId());

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should handle single work date")
        void shouldHandleSingleWorkDate() {
            Shift shift = createAndSaveShift("Morning", "08:00", "16:00", 1L);
            ShiftAssignment assignment = createAndSaveAssignment(shift, 100L);
            LocalDate date = LocalDate.now();

            createAndSaveShiftWork(assignment, date);

            LocalDate result = shiftWorkRepository
                    .findLastWorkDateByAssignmentId(assignment.getId());

            assertThat(result).isEqualTo(date);
        }

        @Test
        @DisplayName("Should not include deleted works")
        void shouldNotIncludeDeletedWorks() {
            Shift shift = createAndSaveShift("Morning", "08:00", "16:00", 1L);
            ShiftAssignment assignment = createAndSaveAssignment(shift, 100L);

            LocalDate date1 = LocalDate.now();
            LocalDate date2 = date1.plusDays(10);

            createAndSaveShiftWork(assignment, date1);
            ShiftWork work2 = createAndSaveShiftWork(assignment, date2);
            work2.setDeleted(true);
            shiftWorkRepository.save(work2);

            LocalDate result = shiftWorkRepository
                    .findLastWorkDateByAssignmentId(assignment.getId());

            assertThat(result).isEqualTo(date1);
        }
    }

    @Nested
    @DisplayName("countRemainingWorkDays Tests")
    class CountRemainingWorkDaysTests {

        @Test
        @DisplayName("Should count remaining work days from date")
        void shouldCountRemainingWorkDays() {
            Shift shift = createAndSaveShift("Morning", "08:00", "16:00", 1L);
            ShiftAssignment assignment = createAndSaveAssignment(shift, 100L);

            LocalDate today = LocalDate.now();
            LocalDate tomorrow = today.plusDays(1);
            LocalDate dayAfter = today.plusDays(2);

            createAndSaveShiftWork(assignment, today);
            createAndSaveShiftWork(assignment, tomorrow);
            createAndSaveShiftWork(assignment, dayAfter);

            long result = shiftWorkRepository
                    .countRemainingWorkDays(assignment.getId(), today);

            assertThat(result).isEqualTo(3L);
        }

        @Test
        @DisplayName("Should count only future days")
        void shouldCountOnlyFutureDays() {
            Shift shift = createAndSaveShift("Morning", "08:00", "16:00", 1L);
            ShiftAssignment assignment = createAndSaveAssignment(shift, 100L);

            LocalDate past = LocalDate.now().minusDays(5);
            LocalDate today = LocalDate.now();
            LocalDate future1 = today.plusDays(1);
            LocalDate future2 = today.plusDays(2);

            createAndSaveShiftWork(assignment, past);
            createAndSaveShiftWork(assignment, today);
            createAndSaveShiftWork(assignment, future1);
            createAndSaveShiftWork(assignment, future2);

            long result = shiftWorkRepository
                    .countRemainingWorkDays(assignment.getId(), today);

            assertThat(result).isEqualTo(3L); // today, future1, future2
        }

        @Test
        @DisplayName("Should return zero when no remaining days")
        void shouldReturnZero_whenNoRemainingDays() {
            Shift shift = createAndSaveShift("Morning", "08:00", "16:00", 1L);
            ShiftAssignment assignment = createAndSaveAssignment(shift, 100L);

            LocalDate past = LocalDate.now().minusDays(5);
            createAndSaveShiftWork(assignment, past);

            long result = shiftWorkRepository
                    .countRemainingWorkDays(assignment.getId(), LocalDate.now());

            assertThat(result).isEqualTo(0L);
        }

        @Test
        @DisplayName("Should not count deleted works")
        void shouldNotCountDeletedWorks() {
            Shift shift = createAndSaveShift("Morning", "08:00", "16:00", 1L);
            ShiftAssignment assignment = createAndSaveAssignment(shift, 100L);

            LocalDate today = LocalDate.now();
            LocalDate tomorrow = today.plusDays(1);

            createAndSaveShiftWork(assignment, today);
            ShiftWork work2 = createAndSaveShiftWork(assignment, tomorrow);
            work2.setDeleted(true);
            shiftWorkRepository.save(work2);

            long result = shiftWorkRepository
                    .countRemainingWorkDays(assignment.getId(), today);

            assertThat(result).isEqualTo(1L); // Only today
        }
    }

    @Nested
    @DisplayName("CRUD Operations Tests")
    class CrudOperationsTests {

        @Test
        @DisplayName("Should save new shift work")
        void shouldSaveNewShiftWork() {
            Shift shift = createAndSaveShift("Morning", "08:00", "16:00", 1L);
            ShiftAssignment assignment = createAndSaveAssignment(shift, 100L);
            LocalDate date = LocalDate.now();

            ShiftWork work = ShiftWork.builder()
                    .assignment(assignment)
                    .workDate(date)
                    .build();

            ShiftWork saved = shiftWorkRepository.save(work);

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getAssignment().getId()).isEqualTo(assignment.getId());
            assertThat(saved.getWorkDate()).isEqualTo(date);
            assertThat(saved.isDeleted()).isFalse();
        }

        @Test
        @DisplayName("Should update existing shift work")
        void shouldUpdateExistingShiftWork() {
            Shift shift = createAndSaveShift("Morning", "08:00", "16:00", 1L);
            ShiftAssignment assignment = createAndSaveAssignment(shift, 100L);
            ShiftWork work = createAndSaveShiftWork(assignment, LocalDate.now());

            LocalDate newDate = LocalDate.now().plusDays(5);
            work.setWorkDate(newDate);
            ShiftWork updated = shiftWorkRepository.save(work);

            assertThat(updated.getId()).isEqualTo(work.getId());
            assertThat(updated.getWorkDate()).isEqualTo(newDate);
        }

        @Test
        @DisplayName("Should soft delete shift work")
        void shouldSoftDeleteShiftWork() {
            Shift shift = createAndSaveShift("Morning", "08:00", "16:00", 1L);
            ShiftAssignment assignment = createAndSaveAssignment(shift, 100L);
            ShiftWork work = createAndSaveShiftWork(assignment, LocalDate.now());
            Long workId = work.getId();

            shiftWorkRepository.delete(work);
            shiftWorkRepository.flush();

            Optional<ShiftWork> result = shiftWorkRepository.findById(workId);
            assertThat(result).isEmpty(); // Soft deleted entities are filtered
        }

        @Test
        @DisplayName("Should find by id")
        void shouldFindById() {
            Shift shift = createAndSaveShift("Morning", "08:00", "16:00", 1L);
            ShiftAssignment assignment = createAndSaveAssignment(shift, 100L);
            ShiftWork work = createAndSaveShiftWork(assignment, LocalDate.now());

            Optional<ShiftWork> result = shiftWorkRepository.findById(work.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(work.getId());
            assertThat(result.get().getWorkDate()).isEqualTo(work.getWorkDate());
        }

        @Test
        @DisplayName("Should save multiple shift works in batch")
        void shouldSaveMultipleShiftWorks() {
            Shift shift = createAndSaveShift("Morning", "08:00", "16:00", 1L);
            ShiftAssignment assignment = createAndSaveAssignment(shift, 100L);

            List<ShiftWork> works = List.of(
                    ShiftWork.builder().assignment(assignment).workDate(LocalDate.now()).build(),
                    ShiftWork.builder().assignment(assignment).workDate(LocalDate.now().plusDays(1)).build(),
                    ShiftWork.builder().assignment(assignment).workDate(LocalDate.now().plusDays(2)).build()
            );

            List<ShiftWork> saved = shiftWorkRepository.saveAll(works);

            assertThat(saved).hasSize(3);
            assertThat(saved).allMatch(w -> w.getId() != null);
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

    private ShiftWork createAndSaveShiftWork(ShiftAssignment assignment, LocalDate date) {
        ShiftWork work = ShiftWork.builder()
                .assignment(assignment)
                .workDate(date)
                .build();
        return shiftWorkRepository.save(work);
    }
}

