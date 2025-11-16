package vn.edu.fpt.pharma.dto.importform;

import vn.edu.fpt.pharma.entity.RequestForm;
import java.time.format.DateTimeFormatter;

public record ImportFormVM(
        Long id,
        String note,
        String requestType,
        String requestStatus,
        String createdAt
) {
    public ImportFormVM(RequestForm entity) {
        this(
                entity.getId(),
                entity.getNote(),
                entity.getRequestType() != null ? entity.getRequestType().name() : null,
                entity.getRequestStatus() != null ? entity.getRequestStatus().name() : null,
                entity.getCreatedAt() != null
                        ? entity.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                        : null
        );
    }
}
