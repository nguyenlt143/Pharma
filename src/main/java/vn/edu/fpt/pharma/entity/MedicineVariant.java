package vn.edu.fpt.pharma.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.edu.fpt.pharma.base.BaseEntity;

@Entity
@Table(name = "medicinevariants")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE medicinevariants SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class MedicineVariant extends BaseEntity<Long> {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medicine_id")
    private Medicine  medicine;
    private String dosage_form;
    private String dosage;
    private String strength;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "package_unit_id_id")
    private Unit packageUnitId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "base_unit_id_id")
    private Unit baseUnitId;
    private Double quantityPerPackage;
    private String Barcode;
    private String registrationNumber;
    private String storageConditions;
    private String indications;
    private String contraindications;
    private String sideEffects;
    private String instructions;
    private boolean prescription_require;
    private String uses;
}
