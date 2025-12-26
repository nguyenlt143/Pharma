package vn.edu.fpt.pharma.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.edu.fpt.pharma.base.BaseEntity;
import vn.edu.fpt.pharma.constant.PackagingLevel;
import vn.edu.fpt.pharma.constant.UnitType;

@Entity
@Table(name = "units")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE units SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Unit extends BaseEntity<Long> {
    @Column(unique = true, nullable = false)
    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnitType unitType;

    private PackagingLevel packagingLevel;

    // đánh dấu đơn vị cơ sở
    @Column(nullable = false)
    private boolean baseUnit;
}
