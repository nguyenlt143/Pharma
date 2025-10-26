package vn.edu.fpt.pharma.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.edu.fpt.pharma.base.BaseEntity;
import vn.edu.fpt.pharma.constant.BatchStatus;
import vn.edu.fpt.pharma.constant.MovementType;

@Entity
@Table(name = "inventory_movement_details")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE inventory_movement_details SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class InventoryMovementDetail extends BaseEntity<Long> {
    private Long movementId;
    private Long variantId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "batch_id")
    private Batch batchId;
    private Long quantity;
    private Double price;
    private Long receivedQuantity;
    private Long returnQuantity;
    private Double cost;
}
