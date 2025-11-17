package vn.edu.fpt.pharma.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.edu.fpt.pharma.base.BaseEntity;

@Entity
@Table(name = "medicines")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE medicines SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Medicine extends BaseEntity<Long> {
    private String name;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;
    private String activeIngredient;
    private String brandName;
    private String manufacturer;
    private String country;
    
    // Additional fields
    @Column(name = "registration_number")
    private String registrationNumber;
    
    @Column(name = "storage_conditions", columnDefinition = "TEXT")
    private String storageConditions;
    
    @Column(columnDefinition = "TEXT")
    private String indications;
    
    @Column(columnDefinition = "TEXT")
    private String contraindications;
    
    @Column(name = "side_effects", columnDefinition = "TEXT")
    private String sideEffects;
    
    @Column(columnDefinition = "TEXT")
    private String instructions;
    
    @Column(name = "prescription_required")
    private Boolean prescriptionRequired = false;
    
    private Integer status = 1;
}
