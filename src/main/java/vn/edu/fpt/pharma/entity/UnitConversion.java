package vn.edu.fpt.pharma.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.edu.fpt.pharma.base.BaseEntity;
@Entity
@Table(name = "unit_conversions")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE unit_conversions SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class UnitConversion extends BaseEntity<Long> {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "variant_id")
    private MedicineVariant variantId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "unit_id")
    private Unit unitId;

    private Double multiplier;

    @Builder.Default
    @Column(name = "is_sale", nullable = false)
    private Boolean isSale = false;
}
