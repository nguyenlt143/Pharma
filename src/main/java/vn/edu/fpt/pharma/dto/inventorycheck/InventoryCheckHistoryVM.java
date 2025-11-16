package vn.edu.fpt.pharma.dto.inventorycheck;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryCheckHistoryVM {
    private String checkDate;
    private Integer checkedCount;
    private String checkDateRaw;


    public InventoryCheckHistoryVM(Object[] row) {
        this.checkDate = row[0] != null
                ? LocalDate.parse(row[0].toString()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : "";
        this.checkedCount = row[1] != null ? ((Number) row[1]).intValue() : 0;
        this.checkDateRaw = row[0] != null ? row[0].toString() : "";
    }

    /**
     * Tạo mã phiếu kiểm kho theo ngày
     * Format: PKK-yyyy-MM-dd
     * VD: PKK-2025-11-16
     */
    public String getCheckCode() {
        return "PKK-" + checkDateRaw;
    }
}