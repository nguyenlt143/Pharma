package vn.edu.fpt.pharma.dto.shifts;

import java.time.LocalDate;
import java.util.Map;

public record ShiftSummaryVM(
        String shiftName,
        Map<LocalDate, Boolean> days
) {
}
