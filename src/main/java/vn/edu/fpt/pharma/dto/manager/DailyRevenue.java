package vn.edu.fpt.pharma.dto.manager;

import java.time.LocalDate;

public interface DailyRevenue {
    LocalDate getDate();
    Double getRevenue();
}

