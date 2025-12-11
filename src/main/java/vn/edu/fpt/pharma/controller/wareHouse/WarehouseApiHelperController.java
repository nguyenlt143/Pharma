package vn.edu.fpt.pharma.controller.wareHouse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.entity.Branch;
import vn.edu.fpt.pharma.entity.MedicineVariant;
import vn.edu.fpt.pharma.entity.Unit;
import vn.edu.fpt.pharma.repository.BranchRepository;
import vn.edu.fpt.pharma.repository.MedicineVariantRepository;
import vn.edu.fpt.pharma.repository.UnitRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/warehouse")
@RequiredArgsConstructor
public class WarehouseApiHelperController {

    private final UnitRepository unitRepository;
    private final MedicineVariantRepository medicineVariantRepository;
    private final BranchRepository branchRepository;

    /**
     * Get all units for dropdown
     * GET /api/warehouse/units
     */
    @GetMapping("/units")
    public ResponseEntity<List<UnitResponse>> getUnits(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // Verify warehouse role
        if (userDetails == null || !"WAREHOUSE".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN).build();
        }

        List<Unit> units = unitRepository.findAll();
        List<UnitResponse> response = units.stream()
                .map(u -> new UnitResponse(u.getId(), u.getName(), u.getDescription()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Get all medicine variants for dropdown
     * GET /api/warehouse/variants
     */
    @GetMapping("/variants")
    public ResponseEntity<List<VariantResponse>> getVariants(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // Verify warehouse role
        if (userDetails == null || !"WAREHOUSE".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN).build();
        }

        List<MedicineVariant> variants = medicineVariantRepository.findAll();
        List<VariantResponse> response = variants.stream()
                .map(v -> {
                    String displayName = v.getMedicine() != null ? v.getMedicine().getName() : "";
                    if (v.getStrength() != null && !v.getStrength().isEmpty()) {
                        displayName += " - " + v.getStrength();
                    }
                    if (v.getDosage_form() != null && !v.getDosage_form().isEmpty()) {
                        displayName += " (" + v.getDosage_form() + ")";
                    }
                    return new VariantResponse(v.getId(), displayName, v.getMedicine() != null ? v.getMedicine().getId() : null);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Get all branches for dropdown
     * GET /api/warehouse/branches
     */
    @GetMapping("/branches")
    public ResponseEntity<List<BranchResponse>> getBranches(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // Verify warehouse role
        if (userDetails == null || !"WAREHOUSE".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN).build();
        }

        List<Branch> branches = branchRepository.findAll();
        List<BranchResponse> response = branches.stream()
                .map(b -> new BranchResponse(b.getId(), b.getName(), b.getAddress()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    private record UnitResponse(Long id, String name, String description) {}
    private record VariantResponse(Long id, String displayName, Long medicineId) {}
    private record BranchResponse(Long id, String name, String address) {}
}

