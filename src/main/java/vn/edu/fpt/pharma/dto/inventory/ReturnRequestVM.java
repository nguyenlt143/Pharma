package vn.edu.fpt.pharma.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Data;
import vn.edu.fpt.pharma.entity.RequestForm;

import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
public class ReturnRequestVM {
    private Long id;
    private String code;
    private String status;
    private String note;
    private String createdAt;
    private Long medicineCount;

    public static ReturnRequestVM from(RequestForm entity, Long medicineCount) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return new ReturnRequestVM(
            entity.getId(),
            "#RQ" + String.format("%03d", entity.getId()),
            entity.getRequestStatus() != null ? entity.getRequestStatus().name() : "N/A",
            entity.getNote() != null ? entity.getNote() : "",
            entity.getCreatedAt() != null ? entity.getCreatedAt().format(fmt) : "",
            medicineCount
        );
    }
}

