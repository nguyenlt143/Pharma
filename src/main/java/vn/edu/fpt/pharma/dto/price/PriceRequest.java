package vn.edu.fpt.pharma.dto.price;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceRequest {
    private Long id;
    @NotNull(message = "Variant ID is required")
    private Long variantId;
    
    private Long branchId;
    
    @NotNull(message = "Sale price is required")
    @Positive(message = "Sale price must be positive")
    private Double salePrice;
    
    private Double branchPrice;
    
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}

