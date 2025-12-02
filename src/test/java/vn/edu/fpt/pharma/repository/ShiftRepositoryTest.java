package vn.edu.fpt.pharma.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import vn.edu.fpt.pharma.entity.Shift;
import vn.edu.fpt.pharma.testutil.BaseDataJpaTest;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ShiftRepository Tests")
class ShiftRepositoryTest extends BaseDataJpaTest {

    @Autowired
    private ShiftRepository shiftRepository;

    @Test
    void search_withoutQuery_returnsAllActiveShifts() {
        createAndSaveShift("Morning", "08:00", "16:00", 1L, false);
        createAndSaveShift("Evening", "16:00", "23:00", 1L, false);
        Shift deleted = createAndSaveShift("Night", "23:00", "08:00", 1L, false);
        shiftRepository.deleteById(deleted.getId());

        List<Shift> shifts = shiftRepository.search(null, 1L);

        assertThat(shifts).hasSize(2);
        assertThat(shifts).extracting(Shift::getName).containsExactlyInAnyOrder("Morning", "Evening");
    }

    @Test
    void search_withQuery_filtersbyName() {
        createAndSaveShift("Morning Shift", "08:00", "16:00", 1L, false);
        createAndSaveShift("Evening Shift", "16:00", "23:00", 1L, false);

        List<Shift> shifts = shiftRepository.search("Morning", 1L);

        assertThat(shifts).hasSize(1);
        assertThat(shifts.get(0).getName()).contains("Morning");
    }

    @Test
    void searchIncludingDeleted_returnsAllShifts() {
        createAndSaveShift("Active", "08:00", "16:00", 1L, false);
        Shift deleted = createAndSaveShift("Deleted", "16:00", "23:00", 1L, false);
        shiftRepository.deleteById(deleted.getId());
        shiftRepository.flush();

        List<Shift> shifts = shiftRepository.searchIncludingDeleted(null, 1L);

        assertThat(shifts).hasSize(2);
    }

    @Test
    void findById_deletedShift_returnsEmpty() {
        Shift shift = createAndSaveShift("ToDelete", "08:00", "16:00", 1L, false);
        Long shiftId = shift.getId();

        shiftRepository.deleteById(shiftId);
        shiftRepository.flush();

        Optional<Shift> found = shiftRepository.findById(shiftId);
        assertThat(found).isEmpty();
    }

    @Test
    void restoreById_restoresDeletedShift() {
        Shift shift = createAndSaveShift("ToRestore", "08:00", "16:00", 1L, false);
        Long shiftId = shift.getId();

        shiftRepository.deleteById(shiftId);
        shiftRepository.flush();
        shiftRepository.restoreById(shiftId);
        shiftRepository.flush();

        Optional<Shift> found = shiftRepository.findById(shiftId);
        assertThat(found).isPresent();
    }

    @Test
    void search_filtersByBranch() {
        createAndSaveShift("Branch1 Shift", "08:00", "16:00", 1L, false);
        createAndSaveShift("Branch2 Shift", "08:00", "16:00", 2L, false);

        List<Shift> branch1Shifts = shiftRepository.search(null, 1L);
        List<Shift> branch2Shifts = shiftRepository.search(null, 2L);

        assertThat(branch1Shifts).hasSize(1);
        assertThat(branch2Shifts).hasSize(1);
        assertThat(branch1Shifts.get(0).getName()).isEqualTo("Branch1 Shift");
        assertThat(branch2Shifts.get(0).getName()).isEqualTo("Branch2 Shift");
    }

    // Helper method
    private Shift createAndSaveShift(String name, String startTime, String endTime, Long branchId, boolean deleted) {
        Shift shift = new Shift();
        shift.setName(name);
        shift.setStartTime(LocalTime.parse(startTime));
        shift.setEndTime(LocalTime.parse(endTime));
        shift.setBranchId(branchId);
        shift.setDeleted(deleted);
        return shiftRepository.save(shift);
    }
}

