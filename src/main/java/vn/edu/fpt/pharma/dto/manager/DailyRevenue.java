package vn.edu.fpt.pharma.dto.manager;

import lombok.Data;

import java.time.LocalDate;

public interface DailyRevenue {
    LocalDate getDate();
    Double getRevenue();
}

