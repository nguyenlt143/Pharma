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
import vn.edu.fpt.pharma.dto.category.CategoryRequest;
import vn.edu.fpt.pharma.dto.category.CategoryResponse;
import vn.edu.fpt.pharma.service.CategoryService;

import java.util.Map;

@RestController
@RequestMapping("/api/owner/category")
@RequiredArgsConstructor
public class CategoryApiController {

    private final CategoryService categoryService;

    /**
     * View category (Search, list category) – List category
     * GET /api/owner/category?draw=1&start=0&length=10&orderColumn=categoryName&orderDir=asc&search[value]=keyword
     */
    @GetMapping
    public ResponseEntity<DataTableResponse<CategoryResponse>> getCategories(
            @RequestParam Map<String, ?> params,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // Verify owner role
        if (userDetails == null || !"OWNER".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        DataTableRequest request = DataTableRequest.fromParams(params);
        DataTableResponse<CategoryResponse> response = categoryService.getCategories(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Add new category – List category
     * POST /api/owner/category
     */
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // Verify owner role
        if (userDetails == null || !"OWNER".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        CategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update category
     * PUT /api/owner/category/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // Verify owner role
        if (userDetails == null || !"OWNER".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        CategoryResponse response = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get category by ID
     * GET /api/owner/category/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // Verify owner role
        if (userDetails == null || !"OWNER".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        CategoryResponse response = categoryService.getCategoryById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete category
     * DELETE /api/owner/category/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // Verify owner role
        if (userDetails == null || !"OWNER".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        categoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

