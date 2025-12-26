package vn.edu.fpt.pharma.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.edu.fpt.pharma.base.BaseEntity;

@Entity
@Table(name = "request_details")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE request_details SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class RequestDetail extends BaseEntity<Long> {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "request_form_id")
    private RequestForm requestForm;

    @Column(name = "variant_id")
    private Long variantId;

    private Long quantity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "package_unit_id")
    private Unit packageUnitId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "base_unit_id")
    private Unit baseUnitId;

    // số lượng nhập theo bao bì
    @Column(nullable = false)
    private Long packageQuantity;

    // 1 hộp = ? viên | 1 chai = ? ml | 1 gói = ? g
    @Column(nullable = false)
    private Double quantityPerPackage;

    // packageQuantity × quantityPerPackage
    @Column(nullable = false)
    private Double baseQuantity;

}
