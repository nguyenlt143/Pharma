package vn.edu.fpt.pharma.dto.requestform;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestFormVM {
    public String code;
    public String type;
    public String status;
    public String note;
    public String createdAt;
}
