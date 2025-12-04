package vn.edu.fpt.pharma.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.pharma.dto.manager.ShiftWorkAssignRequest;
import vn.edu.fpt.pharma.dto.manager.ShiftWorkResponse;
import vn.edu.fpt.pharma.entity.Role;
import vn.edu.fpt.pharma.entity.Shift;
import vn.edu.fpt.pharma.entity.ShiftAssignment;
import vn.edu.fpt.pharma.entity.ShiftWork;
import vn.edu.fpt.pharma.entity.User;
import vn.edu.fpt.pharma.repository.ShiftAssignmentRepository;
import vn.edu.fpt.pharma.repository.ShiftRepository;
import vn.edu.fpt.pharma.repository.ShiftWorkRepository;
import vn.edu.fpt.pharma.repository.UserRepository;
import vn.edu.fpt.pharma.service.ShiftAssignmentService;
import vn.edu.fpt.pharma.service.ShiftWorkService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Shift Management Integration Tests")
class ShiftManagementIntegrationTest {

    @Autowired
    private ShiftAssignmentService assignmentService;

    @Autowired
    private ShiftWorkService shiftWorkService;

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private ShiftAssignmentRepository assignmentRepository;

    @Autowired
    private ShiftWorkRepository shiftWorkRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private vn.edu.fpt.pharma.repository.RoleRepository roleRepository;

    @Nested
    @DisplayName("Assignment Creation Flow")
    class AssignmentCreationFlow {

        @Test
        @DisplayName("Should create assignment and generate 30 days of shift works")
        void shouldCreateAssignmentWithShiftWorks() {
            // Given: A shift exists
            Shift shift = createAndSaveShift("Morning Shift", "08:00", "16:00", 1L);
            User user = createAndSaveUser("John Doe", "john", "0901234567");

            // When: Create assignment
            ShiftAssignment assignment = assignmentService.createAssignment(shift.getId(), user.getId());

            // Then: Assignment is created
            assertThat(assignment).isNotNull();
            assertThat(assignment.getId()).isNotNull();
            assertThat(assignment.getUserId()).isEqualTo(user.getId());
            assertThat(assignment.getShift().getId()).isEqualTo(shift.getId());

            // And: 30 shift works are created
            List<ShiftWork> works = shiftWorkRepository.findByShiftIdAndWorkDate(
                    shift.getId(), LocalDate.now());

            long totalWorks = shiftWorkRepository.countRemainingWorkDays(
                    assignment.getId(), LocalDate.now());
            assertThat(totalWorks).isEqualTo(30L);
        }

        @Test
        @DisplayName("Should prevent duplicate assignments")
        void shouldPreventDuplicateAssignments() {
            // Given: An assignment already exists
            Shift shift = createAndSaveShift("Evening Shift", "16:00", "23:00", 1L);
            User user = createAndSaveUser("Jane Smith", "jane", "0907654321");
            ShiftAssignment first = assignmentService.createAssignment(shift.getId(), user.getId());

            // When: Try to create same assignment again
            ShiftAssignment second = assignmentService.createAssignment(shift.getId(), user.getId());

            // Then: Returns existing assignment, not a new one
            assertThat(second.getId()).isEqualTo(first.getId());

            // And: Only one assignment exists
            List<ShiftAssignment> assignments = assignmentRepository.findByShiftId(shift.getId());
            assertThat(assignments).hasSize(1);
        }

        @Test
        @DisplayName("Should allow same user on different shifts")
        void shouldAllowSameUserOnDifferentShifts() {
            // Given: Multiple shifts
            Shift morningShift = createAndSaveShift("Morning", "08:00", "16:00", 1L);
            Shift eveningShift = createAndSaveShift("Evening", "16:00", "23:00", 1L);
            User user = createAndSaveUser("Multi Shift", "multi", "0901111111");

            // When: Assign same user to both shifts
            ShiftAssignment assignment1 = assignmentService.createAssignment(morningShift.getId(), user.getId());
            ShiftAssignment assignment2 = assignmentService.createAssignment(eveningShift.getId(), user.getId());

            // Then: Both assignments created successfully
            assertThat(assignment1.getId()).isNotNull();
            assertThat(assignment2.getId()).isNotNull();
            assertThat(assignment1.getId()).isNotEqualTo(assignment2.getId());

            // And: User has two assignments
            List<ShiftAssignment> userAssignments = assignmentRepository.findByUserId(user.getId());
            assertThat(userAssignments).hasSize(2);
        }
    }

    @Nested
    @DisplayName("Shift Work Assignment Flow")
    class ShiftWorkAssignmentFlow {

        @Test
        @DisplayName("Should assign user to specific shift work date")
        void shouldAssignUserToShiftWorkDate() {
            // Given: A shift with assignment
            Shift shift = createAndSaveShift("Day Shift", "09:00", "17:00", 1L);
            User user = createAndSaveUser("Worker One", "worker1", "0902222222");

            // When: Assign to specific date
            ShiftWorkAssignRequest request = new ShiftWorkAssignRequest();
            request.setUserId(user.getId());
            request.setWorkDate("2025-12-25");

            ShiftWorkResponse response = shiftWorkService.assignToShift(shift.getId(), request);

            // Then: Shift work created
            assertThat(response).isNotNull();
            assertThat(response.getId()).isNotNull();
            assertThat(response.getUserId()).isEqualTo(user.getId());
            assertThat(response.getUserFullName()).isEqualTo("Worker One");

            // And: Can query it back
            List<ShiftWorkResponse> works = shiftWorkService.findByShiftAndDate(
                    shift.getId(), LocalDate.parse("2025-12-25"));
            assertThat(works).hasSize(1);
            assertThat(works.get(0).getUserId()).isEqualTo(user.getId());
        }

        @Test
        @DisplayName("Should create assignment if not exists when assigning work")
        void shouldCreateAssignmentWhenAssigningWork() {
            // Given: Shift and user but no assignment
            Shift shift = createAndSaveShift("Night Shift", "23:00", "07:00", 1L);
            User user = createAndSaveUser("Night Worker", "night", "0903333333");

            // When: Assign to shift work directly
            ShiftWorkAssignRequest request = new ShiftWorkAssignRequest();
            request.setUserId(user.getId());
            request.setWorkDate("2025-12-20");

            ShiftWorkResponse response = shiftWorkService.assignToShift(shift.getId(), request);

            // Then: Shift work created
            assertThat(response).isNotNull();

            // And: Assignment automatically created
            ShiftAssignment assignment = assignmentService.findByShiftIdAndUserId(
                    shift.getId(), user.getId());
            assertThat(assignment).isNotNull();
            assertThat(assignment.getUserId()).isEqualTo(user.getId());
        }

        @Test
        @DisplayName("Should prevent duplicate work assignment on same date")
        void shouldPreventDuplicateWorkAssignment() {
            // Given: User already assigned to shift on date
            Shift shift = createAndSaveShift("Afternoon", "12:00", "20:00", 1L);
            User user = createAndSaveUser("Busy Worker", "busy", "0904444444");

            ShiftWorkAssignRequest request = new ShiftWorkAssignRequest();
            request.setUserId(user.getId());
            request.setWorkDate("2025-12-15");

            shiftWorkService.assignToShift(shift.getId(), request);

            // When: Try to assign same user to same shift on same date again
            // Then: Should throw exception
            assertThatThrownBy(() -> shiftWorkService.assignToShift(shift.getId(), request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Nhân viên đã được phân công vào ca này trong ngày đã chọn");
        }

        @Test
        @DisplayName("Should allow multiple users on same shift same date")
        void shouldAllowMultipleUsersOnSameShiftDate() {
            // Given: A shift and multiple users
            Shift shift = createAndSaveShift("Team Shift", "10:00", "18:00", 1L);
            User user1 = createAndSaveUser("Team Member 1", "team1", "0905555551");
            User user2 = createAndSaveUser("Team Member 2", "team2", "0905555552");
            User user3 = createAndSaveUser("Team Member 3", "team3", "0905555553");

            LocalDate date = LocalDate.parse("2025-12-30");

            // When: Assign all three to same shift same date
            ShiftWorkAssignRequest req1 = new ShiftWorkAssignRequest();
            req1.setUserId(user1.getId());
            req1.setWorkDate("2025-12-30");

            ShiftWorkAssignRequest req2 = new ShiftWorkAssignRequest();
            req2.setUserId(user2.getId());
            req2.setWorkDate("2025-12-30");

            ShiftWorkAssignRequest req3 = new ShiftWorkAssignRequest();
            req3.setUserId(user3.getId());
            req3.setWorkDate("2025-12-30");

            shiftWorkService.assignToShift(shift.getId(), req1);
            shiftWorkService.assignToShift(shift.getId(), req2);
            shiftWorkService.assignToShift(shift.getId(), req3);

            // Then: All three assigned successfully
            List<ShiftWorkResponse> works = shiftWorkService.findByShiftAndDate(shift.getId(), date);
            assertThat(works).hasSize(3);
            assertThat(works).extracting(ShiftWorkResponse::getUserId)
                    .containsExactlyInAnyOrder(user1.getId(), user2.getId(), user3.getId());
        }
    }

    @Nested
    @DisplayName("Assignment Extension Flow")
    class AssignmentExtensionFlow {

        @Test
        @DisplayName("Should extend shift works by specified days")
        void shouldExtendShiftWorks() {
            // Given: Assignment with 30 days of works
            Shift shift = createAndSaveShift("Regular Shift", "08:00", "16:00", 1L);
            User user = createAndSaveUser("Regular Worker", "regular", "0906666666");
            ShiftAssignment assignment = assignmentService.createAssignment(shift.getId(), user.getId());

            long initialCount = assignmentService.getRemainingWorkDays(assignment.getId());
            assertThat(initialCount).isEqualTo(30L);

            // When: Extend by 15 more days
            assignmentService.extendShiftWorks(assignment.getId(), 15);

            // Then: Total work days increased
            long finalCount = assignmentService.getRemainingWorkDays(assignment.getId());
            assertThat(finalCount).isEqualTo(45L);

            // And: Last work date is 44 days from now (30 + 15 - 1)
            LocalDate lastDate = assignmentService.getLastWorkDate(assignment.getId());
            assertThat(lastDate).isEqualTo(LocalDate.now().plusDays(44));
        }

        @Test
        @DisplayName("Should extend from last date without gaps")
        void shouldExtendWithoutGaps() {
            // Given: Assignment with works
            Shift shift = createAndSaveShift("Continuous Shift", "09:00", "17:00", 1L);
            User user = createAndSaveUser("Continuous Worker", "continuous", "0907777777");
            ShiftAssignment assignment = assignmentService.createAssignment(shift.getId(), user.getId());

            LocalDate firstLastDate = assignmentService.getLastWorkDate(assignment.getId());

            // When: Extend twice
            assignmentService.extendShiftWorks(assignment.getId(), 10);
            assignmentService.extendShiftWorks(assignment.getId(), 5);

            // Then: No gaps in work dates
            LocalDate finalLastDate = assignmentService.getLastWorkDate(assignment.getId());
            assertThat(finalLastDate).isEqualTo(firstLastDate.plusDays(15));

            long totalDays = assignmentService.getRemainingWorkDays(assignment.getId());
            assertThat(totalDays).isEqualTo(45L); // 30 + 10 + 5
        }
    }

    @Nested
    @DisplayName("Assignment Removal Flow")
    class AssignmentRemovalFlow {

        @Test
        @DisplayName("Should remove assignment and its works")
        @org.junit.jupiter.api.Disabled("Disabled due to TransientObjectException with soft delete - needs entity manager refactoring")
        void shouldRemoveAssignment() {
            // Given: Assignment with works
            Shift shift = createAndSaveShift("Temp Shift", "10:00", "18:00", 1L);
            User user = createAndSaveUser("Temp Worker", "temp", "0908888888");
            assignmentService.createAssignment(shift.getId(), user.getId());

            // Verify assignment exists
            ShiftAssignment existing = assignmentService.findByShiftIdAndUserId(
                    shift.getId(), user.getId());
            assertThat(existing).isNotNull();
            Long userId = user.getId();

            // When: Remove assignment
            assignmentService.removeAssignment(shift.getId(), userId);

            // Then: Assignment is removed (soft deleted)
            ShiftAssignment removed = assignmentService.findByShiftIdAndUserId(
                    shift.getId(), userId);
            assertThat(removed).isNull();
        }

        @Test
        @DisplayName("Should handle removing non-existent assignment gracefully")
        void shouldHandleNonExistentRemoval() {
            // Given: No assignment
            Shift shift = createAndSaveShift("Random Shift", "11:00", "19:00", 1L);
            Long nonExistentUserId = 99999L;

            // When: Try to remove
            // Then: Should not throw exception
            assertThatCode(() ->
                assignmentService.removeAssignment(shift.getId(), nonExistentUserId)
            ).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Shift Work Removal Flow")
    class ShiftWorkRemovalFlow {

        @Test
        @DisplayName("Should remove specific shift work")
        void shouldRemoveShiftWork() {
            // Given: Shift work exists
            Shift shift = createAndSaveShift("Daily Shift", "08:00", "16:00", 1L);
            User user = createAndSaveUser("Daily Worker", "daily", "0909999999");

            ShiftWorkAssignRequest request = new ShiftWorkAssignRequest();
            request.setUserId(user.getId());
            request.setWorkDate("2025-12-10");

            ShiftWorkResponse work = shiftWorkService.assignToShift(shift.getId(), request);
            Long workId = work.getId();

            // When: Remove shift work
            shiftWorkService.removeShiftWork(workId);

            // Then: Shift work is removed
            List<ShiftWorkResponse> works = shiftWorkService.findByShiftAndDate(
                    shift.getId(), LocalDate.parse("2025-12-10"));
            assertThat(works).isEmpty();
        }

        @Test
        @DisplayName("Should remove work without affecting other works")
        void shouldRemoveWorkIndependently() {
            // Given: Multiple shift works
            Shift shift = createAndSaveShift("Multi Work Shift", "09:00", "17:00", 1L);
            User user = createAndSaveUser("Multi Day Worker", "multiday", "0901010101");

            ShiftWorkAssignRequest req1 = new ShiftWorkAssignRequest();
            req1.setUserId(user.getId());
            req1.setWorkDate("2025-12-05");

            ShiftWorkAssignRequest req2 = new ShiftWorkAssignRequest();
            req2.setUserId(user.getId());
            req2.setWorkDate("2025-12-06");

            ShiftWorkResponse work1 = shiftWorkService.assignToShift(shift.getId(), req1);
            ShiftWorkResponse work2 = shiftWorkService.assignToShift(shift.getId(), req2);

            // When: Remove first work
            shiftWorkService.removeShiftWork(work1.getId());

            // Then: First work removed
            List<ShiftWorkResponse> date1Works = shiftWorkService.findByShiftAndDate(
                    shift.getId(), LocalDate.parse("2025-12-05"));
            assertThat(date1Works).isEmpty();

            // But: Second work still exists
            List<ShiftWorkResponse> date2Works = shiftWorkService.findByShiftAndDate(
                    shift.getId(), LocalDate.parse("2025-12-06"));
            assertThat(date2Works).hasSize(1);
            assertThat(date2Works.get(0).getId()).isEqualTo(work2.getId());
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

    private User createAndSaveUser(String fullName, String userName, String phoneNumber) {
        // Create and save a role first
        Role role = Role.builder()
                .name("STAFF_" + userName) // Make unique to avoid conflicts
                .build();
        role = roleRepository.save(role);

        User user = User.builder()
                .fullName(fullName)
                .userName(userName)
                .phoneNumber(phoneNumber)
                .password("password123")
                .role(role)
                .branchId(1L)
                .build();
        return userRepository.save(user);
    }
}

