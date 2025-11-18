package vn.edu.fpt.pharma.controller.owner;

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
@RequestMapping("/api/owner/medicine")
@RequiredArgsConstructor
public class MedicineApiController {

    private final MedicineService medicineService;
    private final MedicineVariantService medicineVariantService;

    /**
     * View medicine list (Sort, view status) – List medicine
     * GET /api/owner/medicine?draw=1&start=0&length=10&orderColumn=name&orderDir=asc&status=1
     */
    @GetMapping
    public ResponseEntity<DataTableResponse<MedicineResponse>> getMedicines(
            @RequestParam Map<String, ?> params,
            @RequestParam(required = false) Integer status,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // Verify owner role
        if (userDetails == null || !"OWNER".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        DataTableRequest request = DataTableRequest.fromParams(params);
        DataTableResponse<MedicineResponse> response = medicineService.getMedicines(request, status);
        return ResponseEntity.ok(response);
    }

    /**
     * Create new medicine – List medicine
     * POST /api/owner/medicine
     */
    @PostMapping
    public ResponseEntity<MedicineResponse> createMedicine(
            @Valid @RequestBody MedicineRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // Verify owner role
        if (userDetails == null || !"OWNER".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        MedicineResponse response = medicineService.createMedicine(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update medicine
     * PUT /api/owner/medicine/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<MedicineResponse> updateMedicine(
            @PathVariable Long id,
            @Valid @RequestBody MedicineRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // Verify owner role
        if (userDetails == null || !"OWNER".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        MedicineResponse response = medicineService.updateMedicine(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get medicine by ID
     * GET /api/owner/medicine/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<MedicineResponse> getMedicineById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // Verify owner role
        if (userDetails == null || !"OWNER".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        MedicineResponse response = medicineService.getMedicineById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get medicine variants by medicine ID
     * GET /api/owner/medicine/{medicineId}/variants
     */
    @GetMapping("/{medicineId}/variants")
    public ResponseEntity<List<MedicineVariantResponse>> getVariantsByMedicineId(
            @PathVariable Long medicineId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // Verify owner role
        if (userDetails == null || !"OWNER".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<MedicineVariantResponse> response = medicineVariantService.getVariantsByMedicineId(medicineId);
        return ResponseEntity.ok(response);
    }

    /**
     * Add medicine variant – List medicine
     * POST /api/owner/medicine/variant
     */
    @PostMapping("/variant")
    public ResponseEntity<MedicineVariantResponse> addMedicineVariant(
            @Valid @RequestBody MedicineVariantRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // Verify owner role
        if (userDetails == null || !"OWNER".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        MedicineVariantResponse response = medicineVariantService.createVariant(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update medicine variant
     * PUT /api/owner/medicine/variant/{id}
     */
    @PutMapping("/variant/{id}")
    public ResponseEntity<MedicineVariantResponse> updateVariant(
            @PathVariable Long id,
            @Valid @RequestBody MedicineVariantRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // Verify owner role
        if (userDetails == null || !"OWNER".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        MedicineVariantResponse response = medicineVariantService.updateVariant(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get medicine variant by ID
     * GET /api/owner/medicine/variant/{id}
     */
    @GetMapping("/variant/{id}")
    public ResponseEntity<MedicineVariantResponse> getVariantById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // Verify owner role
        if (userDetails == null || !"OWNER".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        MedicineVariantResponse response = medicineVariantService.getVariantById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete medicine variant
     * DELETE /api/owner/medicine/variant/{id}
     */
    @DeleteMapping("/variant/{id}")
    public ResponseEntity<Void> deleteVariant(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // Verify owner role
        if (userDetails == null || !"OWNER".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        medicineVariantService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Delete medicine
     * DELETE /api/owner/medicine/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedicine(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // Verify owner role
        if (userDetails == null || !"OWNER".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        medicineService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

