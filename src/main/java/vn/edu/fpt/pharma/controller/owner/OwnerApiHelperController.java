package vn.edu.fpt.pharma.controller.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.entity.Branch;
import vn.edu.fpt.pharma.entity.MedicineVariant;
import vn.edu.fpt.pharma.entity.Shift;
import vn.edu.fpt.pharma.entity.Unit;
import vn.edu.fpt.pharma.entity.User;
import vn.edu.fpt.pharma.repository.BranchRepository;
import vn.edu.fpt.pharma.repository.MedicineVariantRepository;
import vn.edu.fpt.pharma.repository.ShiftRepository;
import vn.edu.fpt.pharma.repository.UnitRepository;
import vn.edu.fpt.pharma.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/owner")
@RequiredArgsConstructor
public class OwnerApiHelperController {

    private final UnitRepository unitRepository;
    private final MedicineVariantRepository medicineVariantRepository;
    private final BranchRepository branchRepository;
    private final ShiftRepository shiftRepository;
    private final UserRepository userRepository;

    /**
     * Get all units for dropdown
     * GET /api/owner/units
     */
    @GetMapping("/units")
    public ResponseEntity<List<UnitResponse>> getUnits(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // Verify owner role
        if (userDetails == null || !"OWNER".equalsIgnoreCase(userDetails.getRole())) {
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
     * GET /api/owner/variants
     */
    @GetMapping("/variants")
    public ResponseEntity<List<VariantResponse>> getVariants(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // Verify owner role
        if (userDetails == null || !"OWNER".equalsIgnoreCase(userDetails.getRole())) {
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
     * GET /api/owner/branches
     */
    @GetMapping("/branches")
    public ResponseEntity<List<BranchResponse>> getBranches(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // Verify owner role
        if (userDetails == null || !"OWNER".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN).build();
        }

        List<Branch> branches = branchRepository.findAll();
        List<BranchResponse> response = branches.stream()
                .map(b -> new BranchResponse(b.getId(), b.getName(), b.getAddress()))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get all shifts for dropdown
     * GET /api/owner/shifts
     */
    @GetMapping("/shifts")
    public ResponseEntity<List<ShiftResponse>> getShifts(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // Verify owner role
        if (userDetails == null || !"OWNER".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN).build();
        }

        List<Shift> shifts = shiftRepository.findAll();
        List<ShiftResponse> response = shifts.stream()
                .map(s -> new ShiftResponse(s.getId(), s.getName()))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get all employees for dropdown
     * GET /api/owner/employees
     */
    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeResponse>> getEmployees(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // Verify owner role
        if (userDetails == null || !"OWNER".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN).build();
        }

        List<User> users = userRepository.findAll();
        List<EmployeeResponse> response = users.stream()
                .map(u -> new EmployeeResponse(u.getId(), u.getFullName(), u.getUserName()))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    private record UnitResponse(Long id, String name, String description) {}
    private record VariantResponse(Long id, String displayName, Long medicineId) {}
    private record BranchResponse(Long id, String name, String address) {}
    private record ShiftResponse(Long id, String name) {}
    private record EmployeeResponse(Long id, String fullName, String userName) {}
}

