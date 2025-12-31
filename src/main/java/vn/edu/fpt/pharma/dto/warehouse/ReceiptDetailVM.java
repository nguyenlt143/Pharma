package vn.edu.fpt.pharma.dto.warehouse;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ReceiptDetailVM {
    private String medicineName;
    private String activeIngredient;
    private String concentration;
    private String dosageForm;
    private String categoryName;
    private String unit;  // Base unit (đơn vị cơ bản)
    private String importUnit;  // Import unit (đơn vị nhập) - NEW
    private Integer quantity;
    private String batchCode;
    private LocalDate mfgDate;
    private LocalDate expiryDate;
    private Double importPrice;

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
        this.mfgDate = null;
        this.expiryDate = null;
        this.importPrice = 0.0;
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
        this.mfgDate = null;
        this.expiryDate = null;
        this.importPrice = 0.0;
    }

    // Full constructor with batch info and price
    public ReceiptDetailVM(String medicineName, String concentration, String unit,
                          Integer quantity, String batchCode, LocalDate mfgDate,
                          LocalDate expiryDate, Double importPrice) {
        this.medicineName = medicineName;
        this.concentration = concentration;
        this.unit = unit;
        this.quantity = quantity;
        this.batchCode = batchCode;
        this.mfgDate = mfgDate;
        this.expiryDate = expiryDate;
        this.importPrice = importPrice;
        // Set default values for other fields
        this.activeIngredient = "-";
        this.dosageForm = "-";
        this.categoryName = "-";
    }
}
