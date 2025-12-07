package vn.edu.fpt.pharma.testbuilder;

import vn.edu.fpt.pharma.entity.Shift;
import vn.edu.fpt.pharma.entity.ShiftAssignment;

/**
 * Test data builder for ShiftAssignment entities
 */
public class ShiftAssignmentTestBuilder {

    private Long id;
    private Shift shift;
    private Long userId = 1L;
    private Long shiftId = 1L;

    public static ShiftAssignmentTestBuilder create() {
        return new ShiftAssignmentTestBuilder();
    }

    public static ShiftAssignmentTestBuilder defaultAssignment() {
        return new ShiftAssignmentTestBuilder();
    }

    public ShiftAssignmentTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public ShiftAssignmentTestBuilder withShift(Shift shift) {
        this.shift = shift;
        this.shiftId = shift.getId();
        return this;
    }

    public ShiftAssignmentTestBuilder withShiftId(Long shiftId) {
        this.shiftId = shiftId;
        return this;
    }

    public ShiftAssignmentTestBuilder withUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public ShiftAssignment buildEntity() {
        ShiftAssignment assignment = new ShiftAssignment();
        assignment.setId(id);
        assignment.setUserId(userId);

        if (shift == null && shiftId != null) {
            shift = ShiftTestBuilder.create()
                    .withId(shiftId)
                    .buildEntity();
        }
        assignment.setShift(shift);

        return assignment;
    }
}

