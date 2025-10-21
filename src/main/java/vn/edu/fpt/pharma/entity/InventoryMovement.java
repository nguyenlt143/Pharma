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
@Table(name = "inventorymovements")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE inventorymovements SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class InventoryMovement extends BaseEntity<Long> {
    private MovementType  movementType;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_id_id")
    private Supplier supplierId;
    private Long sourceBranchId;
    private Long destinationBranchId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "request_form_id")
    private RequestForm requestForm;
    private MovementStatus status;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "approved_by_id")
    private User approvedBy;
}
