package vn.edu.fpt.pharma.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.edu.fpt.pharma.base.BaseEntity;

@Entity
@Table(name = "medicine_variant")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE medicine_variant SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class MedicineVariant extends BaseEntity<Long> {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medicine_id")
    private Medicine  medicine;
    private String dosage_form;
    private String dosage;
    private String strength;
    private String Barcode;
    private String registrationNumber;
    private String storageConditions;
    private String instructions;
    private boolean prescription_require;
}
