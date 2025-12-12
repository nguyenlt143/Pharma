package vn.edu.fpt.pharma.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.edu.fpt.pharma.base.BaseEntity;

@Entity
@Table(name = "inventory")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE inventory SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Inventory extends BaseEntity<Long> {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    private MedicineVariant variant;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "batch_id")
    private Batch batch;
    @Column(name = "quantity")
    private Long quantity;
    @Column(name = "cost_price")
    private Double costPrice;
    @Column(name = "min_stock")
    private Long minStock;

    /**
     * Business invariant: inventory.variant must match batch.variant when batch is set.
     * We enforce this at persist/update time to avoid inconsistent seed data or programmer errors.
     */
    @PrePersist
    @PreUpdate
    private void ensureVariantMatchesBatch() {
        if (this.batch != null) {
            MedicineVariant batchVariant = this.batch.getVariant();
            if (batchVariant != null) {
                // If inventory.variant is null or different, align it to the batch's variant
                if (this.variant == null || !this.variant.getId().equals(batchVariant.getId())) {
                    this.variant = batchVariant;
                }
            }
        }
    }
}