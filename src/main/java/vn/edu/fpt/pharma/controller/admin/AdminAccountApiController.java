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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/accounts")
@RequiredArgsConstructor
public class AdminAccountApiController {

    private final UserService userService;
    private final BranchRepository branchRepository;

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

    // Get all branches for dropdown
    @GetMapping("/branches")
    public ResponseEntity<List<Map<String, Object>>> getAllBranches() {
        List<Branch> branches = branchRepository.findAll();
        List<Map<String, Object>> branchDtos = branches.stream()
                .map(branch -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", branch.getId());
                    map.put("name", branch.getName());
                    map.put("branchType", branch.getBranchType());
                    map.put("address", branch.getAddress());
                    return map;
                })
                .toList();
        return ResponseEntity.ok(branchDtos);
    }

    // Create new branch
    @PostMapping("/branches")
    public ResponseEntity<?> createBranch(@RequestBody Map<String, String> request) {
        try {
            Branch branch = new Branch();
            branch.setName(request.get("name"));
            branch.setAddress(request.get("address"));
            branch.setBranchType(vn.edu.fpt.pharma.constant.BranchType.valueOf(request.getOrDefault("branchType", "BRANCH")));
            Branch saved = branchRepository.save(branch);

            Map<String, Object> response = new HashMap<>();
            response.put("id", saved.getId());
            response.put("name", saved.getName());
            response.put("branchType", saved.getBranchType());
            response.put("address", saved.getAddress());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Lỗi khi tạo chi nhánh: " + e.getMessage());
        }
    }

    // Update branch
    @PutMapping("/branches/{id}")
    public ResponseEntity<?> updateBranch(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            Branch branch = branchRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy chi nhánh"));
            branch.setName(request.get("name"));
            branch.setAddress(request.get("address"));
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
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Lỗi khi cập nhật chi nhánh: " + e.getMessage());
        }
    }

    // Delete branch
    @DeleteMapping("/branches/{id}")
    public ResponseEntity<?> deleteBranch(@PathVariable Long id) {
        try {
            branchRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Lỗi khi xóa chi nhánh: " + e.getMessage());
        }
    }
}

