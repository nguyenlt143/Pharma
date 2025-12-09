package vn.edu.fpt.pharma.controller.wareHouse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.dto.DataTableRequest;
import vn.edu.fpt.pharma.dto.DataTableResponse;
import vn.edu.fpt.pharma.dto.medicine.MedicineRequest;
import vn.edu.fpt.pharma.dto.medicine.MedicineResponse;
import vn.edu.fpt.pharma.dto.medicine.MedicineVariantRequest;
import vn.edu.fpt.pharma.dto.medicine.MedicineVariantResponse;
import vn.edu.fpt.pharma.service.MedicineService;
import vn.edu.fpt.pharma.service.MedicineVariantService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/warehouse/medicine")
@RequiredArgsConstructor
public class MedicineWarehouseApiController {

    private final MedicineService medicineService;
    private final MedicineVariantService medicineVariantService;

    /**
     * View medicine list (Sort, view status) – List medicine
     * GET /api/warehouse/medicine?draw=1&start=0&length=10&orderColumn=name&orderDir=asc&status=1
     */
    @GetMapping
    public ResponseEntity<DataTableResponse<MedicineResponse>> getMedicines(
            @RequestParam Map<String, ?> params,
            @RequestParam(required = false) Integer status,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // Verify warehouse role
        if (userDetails == null || !"WAREHOUSE".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        DataTableRequest request = DataTableRequest.fromParams(params);
        DataTableResponse<MedicineResponse> response = medicineService.getMedicines(request, status);
        return ResponseEntity.ok(response);
    }

    /**
     * Create new medicine – List medicine
     * POST /api/warehouse/medicine
     */
    @PostMapping
    public ResponseEntity<MedicineResponse> createMedicine(
            @Valid @RequestBody MedicineRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // Verify warehouse role
        if (userDetails == null || !"WAREHOUSE".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        MedicineResponse response = medicineService.createMedicine(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update medicine
     * PUT /api/warehouse/medicine/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<MedicineResponse> updateMedicine(
            @PathVariable Long id,
            @Valid @RequestBody MedicineRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // Verify warehouse role
        if (userDetails == null || !"WAREHOUSE".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        MedicineResponse response = medicineService.updateMedicine(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get medicine by ID
     * GET /api/warehouse/medicine/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<MedicineResponse> getMedicineById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // Verify warehouse role
        if (userDetails == null || !"WAREHOUSE".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        MedicineResponse response = medicineService.getMedicineById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get medicine variants by medicine ID
     * GET /api/warehouse/medicine/{medicineId}/variants
     */
    @GetMapping("/{medicineId}/variants")
    public ResponseEntity<List<MedicineVariantResponse>> getVariantsByMedicineId(
            @PathVariable Long medicineId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // Verify warehouse role
        if (userDetails == null || !"WAREHOUSE".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<MedicineVariantResponse> response = medicineVariantService.getVariantsByMedicineId(medicineId);
        return ResponseEntity.ok(response);
    }

    /**
     * Add medicine variant – List medicine
     * POST /api/warehouse/medicine/variant
     */
    @PostMapping("/variant")
    public ResponseEntity<MedicineVariantResponse> addMedicineVariant(
            @Valid @RequestBody MedicineVariantRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // Verify warehouse role
        if (userDetails == null || !"WAREHOUSE".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        MedicineVariantResponse response = medicineVariantService.createVariant(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update medicine variant
     * PUT /api/warehouse/medicine/variant/{id}
     */
    @PutMapping("/variant/{id}")
    public ResponseEntity<MedicineVariantResponse> updateVariant(
            @PathVariable Long id,
            @Valid @RequestBody MedicineVariantRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // Verify warehouse role
        if (userDetails == null || !"WAREHOUSE".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        MedicineVariantResponse response = medicineVariantService.updateVariant(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get medicine variant by ID
     * GET /api/warehouse/medicine/variant/{id}
     */
    @GetMapping("/variant/{id}")
    public ResponseEntity<MedicineVariantResponse> getVariantById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // Verify warehouse role
        if (userDetails == null || !"WAREHOUSE".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        MedicineVariantResponse response = medicineVariantService.getVariantById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete medicine variant
     * DELETE /api/warehouse/medicine/variant/{id}
     */
    @DeleteMapping("/variant/{id}")
    public ResponseEntity<Void> deleteVariant(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // Verify warehouse role
        if (userDetails == null || !"WAREHOUSE".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        medicineVariantService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Delete medicine
     * DELETE /api/warehouse/medicine/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedicine(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // Verify warehouse role
        if (userDetails == null || !"WAREHOUSE".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        medicineService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get unit conversions for a variant
     * GET /api/warehouse/medicine/variant/{variantId}/unit-conversions
     */
    @GetMapping("/variant/{variantId}/unit-conversions")
    public ResponseEntity<List<Map<String, Object>>> getUnitConversions(
            @PathVariable Long variantId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // Verify warehouse role
        if (userDetails == null || !"WAREHOUSE".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Map<String, Object>> conversions = medicineVariantService.getUnitConversions(variantId);
        return ResponseEntity.ok(conversions);
    }
}

