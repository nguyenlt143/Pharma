package vn.edu.fpt.pharma.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.edu.fpt.pharma.base.BaseEntity;

@Entity
@Table(name = "stockadjustment")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE stockadjustment SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class StockAdjustment extends BaseEntity<Long> {
    private Long BrandId;
    private Long variantId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "batch_id_id")
    private Batch batchId;
    private Long beforeQuantity;
    private Long afterQuantity;
    private Long differenceQuantity;
    private String reason;
}
