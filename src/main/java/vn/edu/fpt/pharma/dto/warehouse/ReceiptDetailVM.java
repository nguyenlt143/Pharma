package vn.edu.fpt.pharma.dto.warehouse;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReceiptDetailVM {
    private String medicineName;
    private String activeIngredient;
    private String concentration;
    private String dosageForm;
    private String categoryName;
    private String unit;
    private Integer quantity;
    private String batchCode;

    // Constructor for warehouse role (simple version - backward compatible)
    public ReceiptDetailVM(String medicineName, String concentration, String unit, Integer quantity) {
        this.medicineName = medicineName;
        this.concentration = concentration;
        this.unit = unit;
        this.quantity = quantity;
        // Set default values for new fields
        this.activeIngredient = "-";
        this.dosageForm = "-";
        this.categoryName = "-";
        this.batchCode = "-";
    }

    // Constructor for inventory role
    public ReceiptDetailVM(String medicineName, String activeIngredient, String concentration,
                          String dosageForm, String categoryName, String unit,
                          Integer quantity, String batchCode) {
        this.medicineName = medicineName;
        this.activeIngredient = activeIngredient;
        this.concentration = concentration;
        this.dosageForm = dosageForm;
        this.categoryName = categoryName;
        this.unit = unit;
        this.quantity = quantity;
        this.batchCode = batchCode;
    }
}
