package vn.edu.fpt.pharma.dto.price;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.pharma.entity.MedicineVariant;
import vn.edu.fpt.pharma.entity.Price;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceResponse {
    private Long id;
    private Long variantId;
    private String variantName;
    private String medicineName;
    private Long branchId;
    private Double salePrice;
    private Double branchPrice;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PriceResponse fromEntity(Price price) {
        return fromEntity(price, null);
    }

    public static PriceResponse fromEntity(Price price, MedicineVariant variant) {
        if (price == null) return null;
        
        String displayName = "";
        String medicineName = "";
        
        if (variant != null) {
            medicineName = variant.getMedicine() != null ? variant.getMedicine().getName() : "";
            displayName = medicineName;
            if (variant.getStrength() != null && !variant.getStrength().isEmpty()) {
                displayName += " - " + variant.getStrength();
            }
            if (variant.getDosageForm() != null) {
                displayName += " (" + variant.getDosageForm().getDisplayName() + ")";
            }
        }
        
        return PriceResponse.builder()
                .id(price.getId())
                .variantId(price.getVariantId())
                .variantName(displayName)
                .medicineName(medicineName)
                .branchId(price.getBranchId())
                .salePrice(price.getSalePrice())
                .branchPrice(price.getBranchPrice())
                .startDate(price.getStartDate())
                .endDate(price.getEndDate())
                .createdAt(price.getCreatedAt())
                .updatedAt(price.getUpdatedAt())
                .build();
    }
}
