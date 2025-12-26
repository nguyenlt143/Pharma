package vn.edu.fpt.pharma.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.edu.fpt.pharma.base.BaseEntity;

@Entity
@Table(name = "packagings")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE packagings SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Packaging extends BaseEntity<Long> {

    @ManyToOne
    @JoinColumn(name = "medicine_variant_id", nullable = false)
    private MedicineVariant medicineVariant;

    private String packageType; // Chai, Hộp, Thùng

    private Integer quantity;   // 90

    @ManyToOne
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;           // ml / viên

}

