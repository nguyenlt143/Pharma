package vn.edu.fpt.pharma.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.pharma.entity.Branch;
import vn.edu.fpt.pharma.repository.BranchRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/admin/branches")
@RequiredArgsConstructor
public class AdminBranchApiController {
    private final BranchRepository branchRepository;

    // Get all branches for dropdown
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllBranches(@RequestParam(name = "showDeleted", defaultValue = "false") boolean showDeleted) {
        List<Branch> branches;
        if (showDeleted) {
            branches = branchRepository.findAllIncludingDeleted();
        } else {
            branches = branchRepository.findAll();
        }
        List<Map<String, Object>> branchDtos = branches.stream()
                .map(branch -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", branch.getId());
                    map.put("name", branch.getName());
                    map.put("branchType", branch.getBranchType());
                    map.put("address", branch.getAddress());
                    map.put("deleted", branch.isDeleted());
                    return map;
                })
                .toList();
        return ResponseEntity.ok(branchDtos);
    }

    // Create new branch
    @PostMapping
    public ResponseEntity<?> createBranch(@RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            String address = request.get("address");

            // Validate required fields
            if (name == null || name.trim().isEmpty()) {
                Map<String, String> errors = new HashMap<>();
                errors.put("name", "Tên chi nhánh không được để trống");
                return ResponseEntity.status(400).body(errors);
            }
            if (address == null || address.trim().isEmpty()) {
                Map<String, String> errors = new HashMap<>();
                errors.put("address", "Địa chỉ không được để trống");
                return ResponseEntity.status(400).body(errors);
            }

            String trimmedName = name.trim();

            // Check for duplicate name
            if (branchRepository.existsByNameAndDeletedFalse(trimmedName)) {
                Map<String, String> errors = new HashMap<>();
                errors.put("name", "Tên chi nhánh đã tồn tại");
                return ResponseEntity.status(400).body(errors);
            }

            Branch branch = new Branch();
            branch.setName(trimmedName);
            branch.setAddress(address.trim());
            branch.setBranchType(vn.edu.fpt.pharma.constant.BranchType.valueOf(request.getOrDefault("branchType", "BRANCH")));
            Branch saved = branchRepository.save(branch);

            Map<String, Object> response = new HashMap<>();
            response.put("id", saved.getId());
            response.put("name", saved.getName());
            response.put("branchType", saved.getBranchType());
            response.put("address", saved.getAddress());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> errors = new HashMap<>();
            errors.put("branchType", "Loại chi nhánh không hợp lệ");
            return ResponseEntity.status(400).body(errors);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // Update branch
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBranch(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            Branch branch = branchRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy chi nhánh"));

            String name = request.get("name");
            String address = request.get("address");

            // Validate required fields
            if (name == null || name.trim().isEmpty()) {
                Map<String, String> errors = new HashMap<>();
                errors.put("name", "Tên chi nhánh không được để trống");
                return ResponseEntity.status(400).body(errors);
            }
            if (address == null || address.trim().isEmpty()) {
                Map<String, String> errors = new HashMap<>();
                errors.put("address", "Địa chỉ không được để trống");
                return ResponseEntity.status(400).body(errors);
            }

            String trimmedName = name.trim();

            // Check for duplicate name (excluding current branch)
            branchRepository.findByNameAndDeletedFalse(trimmedName).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    Map<String, String> errors = new HashMap<>();
                    errors.put("name", "Tên chi nhánh đã tồn tại");
                    throw new DuplicateBranchNameException(errors);
                }
            });

            branch.setName(trimmedName);
            branch.setAddress(address.trim());
            if (request.containsKey("branchType")) {
                branch.setBranchType(vn.edu.fpt.pharma.constant.BranchType.valueOf(request.get("branchType")));
            }
            Branch saved = branchRepository.save(branch);

            Map<String, Object> response = new HashMap<>();
            response.put("id", saved.getId());
            response.put("name", saved.getName());
            response.put("branchType", saved.getBranchType());
            response.put("address", saved.getAddress());
            return ResponseEntity.ok(response);
        } catch (DuplicateBranchNameException e) {
            return ResponseEntity.status(400).body(e.getErrors());
        } catch (IllegalArgumentException e) {
            Map<String, String> errors = new HashMap<>();
            errors.put("branchType", "Loại chi nhánh không hợp lệ");
            return ResponseEntity.status(400).body(errors);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // Helper exception class for duplicate branch name
    private static class DuplicateBranchNameException extends RuntimeException {
        private final Map<String, String> errors;

        public DuplicateBranchNameException(Map<String, String> errors) {
            this.errors = errors;
        }

        public Map<String, String> getErrors() {
            return errors;
        }
    }

    // Delete branch
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBranch(@PathVariable Long id) {
        try {
            Branch branch = branchRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy chi nhánh"));
            if (branch.getBranchType() == vn.edu.fpt.pharma.constant.BranchType.HEAD_QUARTER) {
                return ResponseEntity.status(400).body("Không thể xóa chi nhánh tổng");
            }
            branchRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Lỗi khi xóa chi nhánh: " + e.getMessage());
        }
    }

    // Restore soft-deleted branch
    @PatchMapping("/{id}/restore")
    public ResponseEntity<?> restoreBranch(@PathVariable Long id) {
        try {
            branchRepository.restoreById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Lỗi khi khôi phục chi nhánh: " + e.getMessage());
        }
    }

}
