package vn.edu.fpt.pharma.controller.wareHouse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.pharma.dto.warehouse.CreateReceiptRequest;
import vn.edu.fpt.pharma.dto.warehouse.MedicineVariantDTO;
import vn.edu.fpt.pharma.dto.warehouse.SupplierDTO;
import vn.edu.fpt.pharma.entity.InventoryMovement;
import vn.edu.fpt.pharma.service.WarehouseReceiptService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/warehouse")
@RequiredArgsConstructor
public class WarehouseApiController {

    private final WarehouseReceiptService receiptService;

    /**
     * Tạo phiếu nhập từ nhà cung cấp
     */
    @PostMapping("/receipts")
    public ResponseEntity<?> createReceipt(@RequestBody CreateReceiptRequest request) {
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
    public ResponseEntity<List<SupplierDTO>> searchSuppliers(
            @RequestParam(required = false) String q) {
        List<SupplierDTO> suppliers = receiptService.searchSuppliers(q);
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
}

