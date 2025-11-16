package vn.edu.fpt.pharma.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.edu.fpt.pharma.base.BaseEntity;
import vn.edu.fpt.pharma.constant.BatchStatus;
import vn.edu.fpt.pharma.constant.MovementStatus;
import vn.edu.fpt.pharma.constant.MovementType;

@Entity
@Table(name = "inventory_movements")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE inventory_movements SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class InventoryMovement extends BaseEntity<Long> {
    @Enumerated(EnumType.STRING)
    private MovementType  movementType;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;
    private Long sourceBranchId;
    private Long destinationBranchId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "request_form_id")
    private RequestForm requestForm;
    @Enumerated(EnumType.STRING)
    private MovementStatus movementStatus;
    private Double totalMoney;
}
