package vn.edu.fpt.pharma.controller.wareHouse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.constant.DosageForm;
import vn.edu.fpt.pharma.dto.DataTableRequest;
import vn.edu.fpt.pharma.dto.DataTableResponse;
import vn.edu.fpt.pharma.dto.unit.UnitRequest;
import vn.edu.fpt.pharma.dto.unit.UnitResponse;
import vn.edu.fpt.pharma.service.UnitService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/warehouse/unit")
@RequiredArgsConstructor
public class UnitWarehouseApiController {

    private final UnitService unitService;

    /**
     * Get all units with pagination and search
     * GET /api/warehouse/unit?draw=1&start=0&length=10
     */
    @GetMapping
    public ResponseEntity<DataTableResponse<UnitResponse>> getUnits(
            @RequestParam Map<String, ?> params,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null || !"WAREHOUSE".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        DataTableRequest request = DataTableRequest.fromParams(params);
        DataTableResponse<UnitResponse> response = unitService.getUnits(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all base units
     * GET /api/warehouse/unit/base
     */
    @GetMapping("/base")
    public ResponseEntity<List<UnitResponse>> getAllBaseUnits(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null || !"WAREHOUSE".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<UnitResponse> baseUnits = unitService.getAllBaseUnits();
        return ResponseEntity.ok(baseUnits);
    }

    /**
     * Get all units (no pagination) for dropdown/mapping
     * GET /api/warehouse/unit/all
     */
    @GetMapping("/all")
    public ResponseEntity<List<UnitResponse>> getAllUnits(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null || !"WAREHOUSE".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<UnitResponse> allUnits = unitService.findAll().stream()
                .map(UnitResponse::fromEntity)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(allUnits);
    }

    /**
     * Get available units for a specific dosage form
     * GET /api/warehouse/unit/available?dosageForm=VIEN_NEN
     */
    @GetMapping("/available")
    public ResponseEntity<List<UnitResponse>> getAvailableUnits(
            @RequestParam String dosageForm,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null || !"WAREHOUSE".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            DosageForm form = DosageForm.valueOf(dosageForm);
            List<UnitResponse> availableUnits = unitService.getAvailableUnitsForDosageForm(form);
            return ResponseEntity.ok(availableUnits);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get all dosage forms
     * GET /api/warehouse/unit/dosage-forms
     */
    @GetMapping("/dosage-forms")
    public ResponseEntity<List<Map<String, String>>> getAllDosageForms(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null || !"WAREHOUSE".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Map<String, String>> dosageForms = Arrays.stream(DosageForm.values())
                .map(form -> Map.of(
                        "name", form.name(),
                        "displayName", form.getDisplayName(),
                        "baseUnit", form.getBaseUnitName()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dosageForms);
    }

    /**
     * Get unit by ID
     * GET /api/warehouse/unit/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UnitResponse> getUnitById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null || !"WAREHOUSE".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            UnitResponse unit = unitService.getUnitById(id);
            return ResponseEntity.ok(unit);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Create new unit
     * POST /api/warehouse/unit
     */
    @PostMapping
    public ResponseEntity<UnitResponse> createUnit(
            @Valid @RequestBody UnitRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null || !"WAREHOUSE".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            UnitResponse unit = unitService.createUnit(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(unit);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update unit
     * PUT /api/warehouse/unit/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<UnitResponse> updateUnit(
            @PathVariable Long id,
            @Valid @RequestBody UnitRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null || !"WAREHOUSE".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            UnitResponse unit = unitService.updateUnit(id, request);
            return ResponseEntity.ok(unit);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete unit
     * DELETE /api/warehouse/unit/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUnit(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null || !"WAREHOUSE".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            unitService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

