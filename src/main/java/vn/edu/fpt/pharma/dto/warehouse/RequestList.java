package vn.edu.fpt.pharma.dto.warehouse;

import vn.edu.fpt.pharma.entity.RequestForm;

import java.time.format.DateTimeFormatter;

public record RequestList(
        Long id,
        Long branchId,
        String branchName,
        String createdAt,
        String requestType,
        String requestStatus,
        String note
) {
    public RequestList(RequestForm entity, String branchName) {
        this(
                entity.getId(),
                entity.getBranchId(),
                branchName,
                entity.getCreatedAt() != null
                        ? entity.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                        : "",
                entity.getRequestType() != null ? entity.getRequestType().name() : "N/A",
                entity.getRequestStatus() != null ? entity.getRequestStatus().name() : "N/A",
                entity.getNote() != null ? entity.getNote() : ""
        );
    }
}
