package vn.edu.fpt.pharma.testbuilder;

import vn.edu.fpt.pharma.dto.manager.ShiftRequest;
import vn.edu.fpt.pharma.dto.manager.ShiftResponse;
import vn.edu.fpt.pharma.entity.Shift;

import java.time.LocalTime;

/**
 * Test data builder for Shift entities and DTOs
 */
public class ShiftTestBuilder {

    private Long id;
    private String name = "Ca sáng";
    private String startTime = "08:00";
    private String endTime = "16:00";
    private String note = "Test note";
    private Long branchId = 1L;
    private boolean deleted = false;

    public static ShiftTestBuilder create() {
        return new ShiftTestBuilder();
    }

    public static ShiftTestBuilder defaultShift() {
        return new ShiftTestBuilder();
    }

    public ShiftTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public ShiftTestBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ShiftTestBuilder withStartTime(String startTime) {
        this.startTime = startTime;
        return this;
    }

    public ShiftTestBuilder withEndTime(String endTime) {
        this.endTime = endTime;
        return this;
    }

    public ShiftTestBuilder withNote(String note) {
        this.note = note;
        return this;
    }

    public ShiftTestBuilder withBranchId(Long branchId) {
        this.branchId = branchId;
        return this;
    }

    public ShiftTestBuilder withDeleted(boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    public ShiftRequest buildRequest() {
        return ShiftRequest.builder()
                .id(id)
                .name(name)
                .startTime(startTime)
                .endTime(endTime)
                .note(note)
                .build();
    }

    public Shift buildEntity() {
        Shift shift = new Shift();
        shift.setId(id);
        shift.setName(name);
        shift.setStartTime(LocalTime.parse(startTime));
        shift.setEndTime(LocalTime.parse(endTime));
        shift.setNote(note);
        shift.setBranchId(branchId);
        shift.setDeleted(deleted);
        return shift;
    }

    public ShiftResponse buildResponse() {
        return ShiftResponse.builder()
                .id(id)
                .name(name)
                .startTime(startTime)
                .endTime(endTime)
                .note(note)
                .deleted(deleted)
                .build();
    }
}

