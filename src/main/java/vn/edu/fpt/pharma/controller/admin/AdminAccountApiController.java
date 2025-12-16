package vn.edu.fpt.pharma.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.pharma.dto.manager.UserDto;
import vn.edu.fpt.pharma.dto.manager.UserRequest;
import vn.edu.fpt.pharma.entity.Branch;
import vn.edu.fpt.pharma.repository.BranchRepository;
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
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // Update account
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody UserRequest req) {
        try {
            return ResponseEntity.ok(userService.update(id, req));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // Soft delete account
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            userService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // Restore soft-deleted account
    @PatchMapping("/{id}/restore")
    public ResponseEntity<Void> restore(@PathVariable Long id) {
        userService.restore(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Migrate all MedicineVariant to UnitConversion
     * POST /api/admin/accounts/unit-conversion-migration
     *
     * Chạy một lần duy nhất để tạo UnitConversion cho tất cả MedicineVariant hiện có
     */
    @PostMapping("/unit-conversion-migration")
    public ResponseEntity<?> migrateUnitConversions() {
        //http://localhost:8080/migration.html

        try {
            System.out.println("\n🚀 Bắt đầu quá trình migrate UnitConversions...\n");

            medicineVariantService.migrateAllVariantsToUnitConversions();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "✅ Migration hoàn tất! Tất cả MedicineVariant đã được xử lý. Xem console để biết chi tiết.");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "❌ Lỗi trong quá trình migration: " + e.getMessage());

            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
