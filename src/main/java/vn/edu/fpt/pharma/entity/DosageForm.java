package vn.edu.fpt.pharma.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.edu.fpt.pharma.base.BaseEntity;

@Entity
@Table(name = "dosage_forms",
       uniqueConstraints = @UniqueConstraint(columnNames = {"display_name", "base_unit_id"}))
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE dosage_forms SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class DosageForm extends BaseEntity<Long> {

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;  // "Viên nén", "Siro"

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "base_unit_id", nullable = false)
    private Unit baseUnit;

    @Column(length = 500)
    private String description;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    @Builder.Default
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;
}

