package vn.edu.fpt.pharma.dto.requestform;

import vn.edu.fpt.pharma.entity.RequestForm;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record RequestFormVM(
        Long id,
        String code,
        String note,
        String requestType,
        String requestStatus,
        LocalDateTime createdAt
) {
    //public RequestFormVM(RequestForm entity) {
        //this(
                //"#RQ" + String.format("%03d", entity.getId()),
                //entity.getRequestType() != null ? entity.getRequestType().name() : "N/A",
                //entity.getRequestStatus() != null ? entity.getRequestStatus().name() : "N/A",
                //entity.getNote() != null ? entity.getNote() : "",
                //entity.getCreatedAt() != null
                        //? entity.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                        //: ""
        //);
    //}
    public RequestFormVM(RequestForm entity) {
        this(
                entity.getId(),
                "#RQ" + String.format("%03d", entity.getId()), // ✅ sinh mã phiếu
                entity.getNote(),
                entity.getRequestType() != null ? entity.getRequestType().name() : null,
                entity.getRequestStatus() != null ? entity.getRequestStatus().name() : null,
                entity.getCreatedAt()
        );
    }
}
