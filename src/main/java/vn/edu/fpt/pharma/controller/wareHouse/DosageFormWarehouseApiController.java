package vn.edu.fpt.pharma.controller.wareHouse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.dto.dosageform.DosageFormRequest;
import vn.edu.fpt.pharma.dto.dosageform.DosageFormResponse;
import vn.edu.fpt.pharma.service.DosageFormService;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse/dosage-form")
@RequiredArgsConstructor
public class DosageFormWarehouseApiController {

    private final DosageFormService dosageFormService;

    /**
     * Get all dosage forms
     * GET /api/warehouse/dosage-form
     */
    @GetMapping
    public ResponseEntity<List<DosageFormResponse>> getAllDosageForms(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null || !"WAREHOUSE".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<DosageFormResponse> dosageForms = dosageFormService.getAllDosageForms();
        return ResponseEntity.ok(dosageForms);
    }

    /**
     * Get dosage form by ID
     * GET /api/warehouse/dosage-form/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<DosageFormResponse> getDosageFormById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null || !"WAREHOUSE".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            DosageFormResponse dosageForm = dosageFormService.getDosageFormById(id);
            return ResponseEntity.ok(dosageForm);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Create new dosage form
     * POST /api/warehouse/dosage-form
     */
    @PostMapping
    public ResponseEntity<?> createDosageForm(
            @Valid @RequestBody DosageFormRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null || !"WAREHOUSE".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            DosageFormResponse created = dosageFormService.createDosageForm(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Update dosage form
     * PUT /api/warehouse/dosage-form/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDosageForm(
            @PathVariable Long id,
            @Valid @RequestBody DosageFormRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null || !"WAREHOUSE".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            DosageFormResponse updated = dosageFormService.updateDosageForm(id, request);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Delete dosage form
     * DELETE /api/warehouse/dosage-form/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDosageForm(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null || !"WAREHOUSE".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            dosageFormService.deleteDosageForm(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

