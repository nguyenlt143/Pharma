package vn.edu.fpt.pharma.dto.inventorycheck;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryCheckHistoryVM {
    private String checkCode;
    private String checkDate;
    private String checkDateRaw;
    private Integer checkedCount;

    public InventoryCheckHistoryVM(Object[] row) {
        // row[0] format: "2025-11-19 14:30:45"
        this.checkDateRaw = row[0] != null ? row[0].toString() : "";

        // Format hiển thị: "19/11/2025 14:30:45"
        if (row[0] != null) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(row[0].toString(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                this.checkDate = dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            } catch (Exception e) {
                this.checkDate = row[0].toString();
            }
        } else {
            this.checkDate = "";
        }

        this.checkedCount = row[1] != null ? ((Number) row[1]).intValue() : 0;
    }

    /**
     * Tạo mã phiếu kiểm kho theo timestamp
     * Format: PKK-yyyyMMdd-HHmmss
     * VD: PKK-20251119-143045
     */
    public String getCheckCode() {
        if (checkDateRaw == null || checkDateRaw.isEmpty()) {
            return "PKK-UNKNOWN";
        }
        try {
            LocalDateTime dateTime = LocalDateTime.parse(checkDateRaw,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return "PKK-" + dateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        } catch (Exception e) {
            return "PKK-" + checkDateRaw.replaceAll("[^0-9]", "");
        }
    }
}