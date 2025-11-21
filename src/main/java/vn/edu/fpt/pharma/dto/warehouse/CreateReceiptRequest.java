package vn.edu.fpt.pharma.dto.warehouse;

import lombok.Data;
import java.util.List;

@Data
public class CreateReceiptRequest {
    private String movementType;
    private Long supplierId;
    private String movementDate;
    private String status;
    private List<ReceiptDetailRequest> details;
}

