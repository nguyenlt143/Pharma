package vn.edu.fpt.pharma.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.edu.fpt.pharma.base.BaseEntity;

import java.util.List;

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
    private Medicine medicine;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dosage_form_id", nullable = false)
    private DosageForm dosageForm;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "base_unit_id", nullable = false)
    private Unit baseUnit;

    @OneToMany(mappedBy = "medicineVariant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Packaging> packagings;

    private String dosage;
    private String strength;
    private String packaging;
    private String barcode;
    private String registrationNumber;
    private String storageConditions;
    private String instructions;
    private boolean prescription_require;
}
