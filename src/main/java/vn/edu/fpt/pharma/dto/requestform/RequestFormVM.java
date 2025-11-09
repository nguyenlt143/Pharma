package vn.edu.fpt.pharma.dto.requestform;

import vn.edu.fpt.pharma.entity.RequestForm;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hibernate.validator.internal.engine.messageinterpolation.el.RootResolver.FORMATTER;

public record RequestFormVM(
      
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

}
