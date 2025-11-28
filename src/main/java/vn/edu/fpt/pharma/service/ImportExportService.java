package vn.edu.fpt.pharma.service;

import java.time.LocalDateTime;
import java.util.Map;
import vn.edu.fpt.pharma.constant.MovementType;

public interface ImportExportService {
    Double calculateTotalInventoryValue(Long branchId);
    int countLowStock(Long branchId);
    int countPendingInbound(Long branchId);
    int countPendingOutbound(Long branchId);
    String formatCurrencyReadable(Double value);
    String formatTimeAgo(LocalDateTime dateTime);
    void medicineVariantOptionalPut(Map<String, Object> detail, Long variantId);
    String movementTypeLabel(MovementType type);
    String movementTypeClass(MovementType type);
}
