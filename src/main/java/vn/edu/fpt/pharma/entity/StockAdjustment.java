package vn.edu.fpt.pharma.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.edu.fpt.pharma.base.BaseEntity;

@Entity
@Table(name = "unitconversions")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE unitconversions SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class UnitConversion extends BaseEntity<Long> {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medicine_id")
    private Medicine  medicine;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "from_unit_id")
    private Unit fromUnit;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "to_unit_id_id")
    private Unit toUnitId;
    private Double conversionRate;
}
