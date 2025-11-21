package vn.edu.fpt.pharma.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicineSearchDTO {
    private Long variantId;
    private Long batchId;
    private String medicineName;
    private String activeIngredient;
    private String strength;
    private String dosageForm;
    private String batchCode;
    private String expiryDate;
    private Long currentStock;
    private String unit;
    private String manufacturer;
}

