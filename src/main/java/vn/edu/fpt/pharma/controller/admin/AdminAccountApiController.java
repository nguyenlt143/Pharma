package vn.edu.fpt.pharma.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.pharma.dto.manager.UserDto;
import vn.edu.fpt.pharma.dto.manager.UserRequest;
import vn.edu.fpt.pharma.service.UserService;
import vn.edu.fpt.pharma.service.MedicineVariantService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/accounts")
@RequiredArgsConstructor
public class AdminAccountApiController {

    private final UserService userService;
    private final MedicineVariantService medicineVariantService;

    // Get all high-level accounts (OWNER, MANAGER, WAREHOUSE only)
    @GetMapping
    public ResponseEntity<List<UserDto>> getAll(@RequestParam(name = "showDeleted", defaultValue = "false") boolean showDeleted) {
        // Role IDs: 2=OWNER, 3=MANAGER, 5=WAREHOUSE (removed 4=INVENTORY)
        List<Long> roleIds = List.of(2L, 3L, 5L);
        return ResponseEntity.ok(userService.getAccountsByRoles(roleIds, showDeleted));
    }

    // Get account by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.getById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // Create new account
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody UserRequest req) {
        try {
            return ResponseEntity.ok(userService.create(req));
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }

    // Update account
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody UserRequest req) {
        try {
            return ResponseEntity.ok(userService.update(id, req));
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }

    // Soft delete account
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            userService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }

    // Restore soft-deleted account
    @PatchMapping("/{id}/restore")
    public ResponseEntity<Void> restore(@PathVariable Long id) {
        userService.restore(id);
        return ResponseEntity.noContent().build();
    }

    // Unit Conversion Migration endpoint
    @PostMapping("/unit-conversion-migration")
    public ResponseEntity<Map<String, Object>> runUnitConversionMigration() {
        Map<String, Object> response = new HashMap<>();
        try {
            // Check if migration is needed
            // The current MedicineVariant entity doesn't have baseUnitId, packageUnitId, or quantityPerPackage
            // So this migration is not applicable to the current schema

            response.put("success", false);
            response.put("message", "Migration không khả dụng. Schema hiện tại không có các trường legacy (baseUnitId, packageUnitId, quantityPerPackage) cần migrate.");
            response.put("info", "MedicineVariant hiện tại đã sử dụng UnitConversion từ đầu, không cần migrate.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi chạy migration: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

}
