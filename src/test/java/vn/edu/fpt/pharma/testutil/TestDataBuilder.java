package vn.edu.fpt.pharma.testutil;

import vn.edu.fpt.pharma.entity.Role;
import vn.edu.fpt.pharma.entity.Shift;
import vn.edu.fpt.pharma.entity.ShiftAssignment;
import vn.edu.fpt.pharma.entity.ShiftWork;
import vn.edu.fpt.pharma.entity.User;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Test data builder for creating test entities
 */
public class TestDataBuilder {

    public static User.UserBuilder aUser() {
        return User.builder()
                .userName("testuser")
                .password("password123")
                .fullName("Test User")
                .email("test@example.com")
                .phoneNumber("0123456789")
                .branchId(1L);
    }

    public static User.UserBuilder aManager() {
        return aUser()
                .userName("manager1")
                .fullName("Manager One");
    }

    public static User.UserBuilder aStaff() {
        return aUser()
                .userName("staff1")
                .fullName("Staff One");
    }

    public static User.UserBuilder aPharmacist() {
        return aUser()
                .userName("pharmacist1")
                .fullName("Pharmacist One");
    }

    public static Role.RoleBuilder aRole() {
        return Role.builder()
                .name("STAFF");
    }

    public static Role.RoleBuilder aManagerRole() {
        return Role.builder()
                .name("BRANCH_MANAGER");
    }

    public static Role.RoleBuilder aPharmacistRole() {
        return Role.builder()
                .name("PHARMACIST");
    }

    public static Shift.ShiftBuilder aShift() {
        return Shift.builder()
                .branchId(1L)
                .name("Morning Shift")
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(16, 0))
                .note("Morning shift");
    }

    public static Shift.ShiftBuilder anEveningShift() {
        return Shift.builder()
                .branchId(1L)
                .name("Evening Shift")
                .startTime(LocalTime.of(16, 0))
                .endTime(LocalTime.of(23, 0))
                .note("Evening shift");
    }

    public static ShiftAssignment.ShiftAssignmentBuilder aShiftAssignment() {
        return ShiftAssignment.builder()
                .userId(20L)
                .shift(aShift().build());
    }

    public static ShiftWork.ShiftWorkBuilder aShiftWork() {
        return ShiftWork.builder()
                .assignment(aShiftAssignment().build())
                .workDate(LocalDate.now());
    }
}
