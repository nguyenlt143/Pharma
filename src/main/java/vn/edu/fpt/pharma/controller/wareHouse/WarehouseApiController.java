package vn.edu.fpt.pharma.controller.wareHouse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import vn.edu.fpt.pharma.dto.warehouse.CreateReceiptRequest;
import vn.edu.fpt.pharma.dto.warehouse.MedicineVariantDTO;
import vn.edu.fpt.pharma.dto.supplier.SupplierResponse;
import vn.edu.fpt.pharma.entity.Inventory;
import vn.edu.fpt.pharma.entity.InventoryMovement;
import vn.edu.fpt.pharma.service.InventoryService;
import vn.edu.fpt.pharma.service.WarehouseReceiptService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/warehouse")
@RequiredArgsConstructor
public class WarehouseApiController {

    private final WarehouseReceiptService receiptService;
    private final InventoryService inventoryService;

    /**
     * Tạo phiếu nhập từ nhà cung cấp
     */
    @PostMapping("/receipts")
    public ResponseEntity<?> createReceipt(@Valid @RequestBody CreateReceiptRequest request) {
        try {
            InventoryMovement movement = receiptService.createReceipt(request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Tạo phiếu nhập thành công");
            response.put("movementId", movement.getId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Tìm kiếm nhà cung cấp
     */
    @GetMapping("/suppliers/search")
    public ResponseEntity<List<SupplierResponse>> searchSuppliers(
            @RequestParam(required = false) String q) {
        List<SupplierResponse> suppliers = receiptService.searchSuppliers(q);
        return ResponseEntity.ok(suppliers);
    }

    /**
     * Tìm kiếm thuốc (medicine variants)
     */
    @GetMapping("/medicines/search")
    public ResponseEntity<List<MedicineVariantDTO>> searchMedicines(
            @RequestParam(required = false) String q) {
        List<MedicineVariantDTO> variants = receiptService.searchMedicineVariants(q);
        return ResponseEntity.ok(variants);
    }

    /**
     * Cập nhật min stock cho inventory item
     */
    @PostMapping("/inventory/{id}/min-stock")
    public ResponseEntity<?> updateMinStock(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload) {
        try {
            Long minStock = payload.get("minStock") != null
                ? ((Number) payload.get("minStock")).longValue()
                : null;

            Inventory updated = inventoryService.updateMinStock(id, minStock);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cập nhật mức tồn tối thiểu thành công");
            response.put("minStock", updated.getMinStock());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }
}
