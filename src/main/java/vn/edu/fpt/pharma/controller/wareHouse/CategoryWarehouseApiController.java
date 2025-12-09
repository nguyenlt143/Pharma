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
import vn.edu.fpt.pharma.dto.category.CategoryRequest;
import vn.edu.fpt.pharma.dto.category.CategoryResponse;
import vn.edu.fpt.pharma.service.CategoryService;

import java.util.Map;

@RestController
@RequestMapping("/api/warehouse/category")
@RequiredArgsConstructor
public class CategoryWarehouseApiController {

    private final CategoryService categoryService;

    /**
     * View category (Search, list category) – List category
     * GET /api/warehouse/category?draw=1&start=0&length=10&orderColumn=categoryName&orderDir=asc&search[value]=keyword
     */
    @GetMapping
    public ResponseEntity<DataTableResponse<CategoryResponse>> getCategories(
            @RequestParam Map<String, ?> params,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // Verify warehouse role
        if (userDetails == null || !"WAREHOUSE".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        DataTableRequest request = DataTableRequest.fromParams(params);
        DataTableResponse<CategoryResponse> response = categoryService.getCategories(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Add new category – List category
     * POST /api/warehouse/category
     */
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // Verify warehouse role
        if (userDetails == null || !"WAREHOUSE".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        CategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update category
     * PUT /api/warehouse/category/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // Verify warehouse role
        if (userDetails == null || !"WAREHOUSE".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        CategoryResponse response = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get category by ID
     * GET /api/warehouse/category/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // Verify warehouse role
        if (userDetails == null || !"WAREHOUSE".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        CategoryResponse response = categoryService.getCategoryById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete category
     * DELETE /api/warehouse/category/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // Verify warehouse role
        if (userDetails == null || !"WAREHOUSE".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        categoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

