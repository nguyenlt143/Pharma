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
import vn.edu.fpt.pharma.dto.price.PriceRequest;
import vn.edu.fpt.pharma.dto.price.PriceResponse;
import vn.edu.fpt.pharma.service.PriceService;

import java.util.Map;

@RestController
@RequestMapping("/api/warehouse/price")
@RequiredArgsConstructor
public class PriceWarehouseApiController {
    private final PriceService priceService;

    /**
     * View price of product (Search, list price) – Price
     * GET /api/warehouse/price?draw=1&start=0&length=10&variantId=1&branchId=1
     */
    @GetMapping
    public ResponseEntity<DataTableResponse<PriceResponse>> getPrices(
            @RequestParam Map<String, ?> params,
            @RequestParam(required = false) Long variantId,
            @RequestParam(required = false) Long branchId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // Verify warehouse role
        if (userDetails == null || !"WAREHOUSE".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        DataTableRequest request = DataTableRequest.fromParams(params);
        DataTableResponse<PriceResponse> response = priceService.getPrices(request, variantId, branchId);
        return ResponseEntity.ok(response);
    }

    /**
     * Adjust price (for customer/branches) – Price
     * POST /api/warehouse/price
     */
    @PostMapping
    public ResponseEntity<PriceResponse> adjustPrice(
            @Valid @RequestBody PriceRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // Verify warehouse role
        if (userDetails == null || !"WAREHOUSE".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        PriceResponse response = priceService.createOrUpdatePrice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update price
     * PUT /api/warehouse/price/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<PriceResponse> updatePrice(
            @PathVariable Long id,
            @Valid @RequestBody PriceRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // Verify warehouse role
        if (userDetails == null || !"WAREHOUSE".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Ensure the service knows this is an update, not a create
        request.setId(id);

        PriceResponse response = priceService.createOrUpdatePrice(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get price by ID
     * GET /api/warehouse/price/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<PriceResponse> getPriceById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // Verify warehouse role
        if (userDetails == null || !"WAREHOUSE".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        PriceResponse response = priceService.getPriceById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete price
     * DELETE /api/warehouse/price/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrice(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // Verify warehouse role
        if (userDetails == null || !"WAREHOUSE".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        priceService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

