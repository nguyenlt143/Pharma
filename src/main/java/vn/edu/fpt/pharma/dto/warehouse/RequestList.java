package vn.edu.fpt.pharma.dto.warehouse;

import vn.edu.fpt.pharma.entity.RequestForm;

import java.time.format.DateTimeFormatter;

public record RequestList(
        Long id,
        Long branchId,
        String createdAt,
        String requestType,
        String requestStatus
) {
    public RequestList(RequestForm entity) {
        this(
                entity.getId(),
                entity.getBranchId(),
                entity.getCreatedAt() != null
                        ? entity.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                        : "",
                entity.getRequestType() != null ? entity.getRequestType().name() : "N/A",
                entity.getRequestStatus() != null ? entity.getRequestStatus().name() : "N/A"
        );
    }
}
