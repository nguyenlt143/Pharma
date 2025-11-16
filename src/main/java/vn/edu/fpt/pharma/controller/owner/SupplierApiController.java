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
import vn.edu.fpt.pharma.dto.supplier.SupplierRequest;
import vn.edu.fpt.pharma.dto.supplier.SupplierResponse;
import vn.edu.fpt.pharma.service.SupplierService;

import java.util.Map;

@RestController
@RequestMapping("/api/owner/supplier")
@RequiredArgsConstructor
public class SupplierApiController {

    private final SupplierService supplierService;

    /**
     * View list supplier – List supplier
     * GET /api/owner/supplier?draw=1&start=0&length=10&orderColumn=supplierName&orderDir=asc
     */
    @GetMapping
    public ResponseEntity<DataTableResponse<SupplierResponse>> getSuppliers(
            @RequestParam Map<String, ?> params,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // Verify owner role
        if (userDetails == null || !"OWNER".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        DataTableRequest request = DataTableRequest.fromParams(params);
        DataTableResponse<SupplierResponse> response = supplierService.getSuppliers(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Add new supplier – List supplier
     * POST /api/owner/supplier
     */
    @PostMapping
    public ResponseEntity<SupplierResponse> createSupplier(
            @Valid @RequestBody SupplierRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // Verify owner role
        if (userDetails == null || !"OWNER".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        SupplierResponse response = supplierService.createSupplier(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update supplier
     * PUT /api/owner/supplier/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponse> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody SupplierRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // Verify owner role
        if (userDetails == null || !"OWNER".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        SupplierResponse response = supplierService.updateSupplier(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get supplier by ID
     * GET /api/owner/supplier/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponse> getSupplierById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // Verify owner role
        if (userDetails == null || !"OWNER".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        SupplierResponse response = supplierService.getSupplierById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete supplier
     * DELETE /api/owner/supplier/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // Verify owner role
        if (userDetails == null || !"OWNER".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        supplierService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

