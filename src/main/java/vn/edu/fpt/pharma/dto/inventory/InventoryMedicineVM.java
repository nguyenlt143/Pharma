package vn.edu.fpt.pharma.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryMedicineVM {
    private Long inventoryId;
    private Long variantId;
    private Long batchId;
    private String medicineName;
    private String activeIngredient;
    private String strength;
    private String dosageForm;
    private String manufacturer;
    private String batchCode;
    private LocalDate expiryDate;
    private Long quantity;
    private String unit;
    private String categoryName;
    private Long branchId;
    private Long minStock;
    private Long branchStock;
}