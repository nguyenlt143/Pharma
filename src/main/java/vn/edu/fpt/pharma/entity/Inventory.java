package vn.edu.fpt.pharma.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.edu.fpt.pharma.base.BaseEntity;

@Entity
@Table(name = "inventorys")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE inventorys SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Inventory extends BaseEntity<Long> {
    private Long branchId;
    private Long variantId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "batch_id_id")
    private Batch batchId;
    private Long quantity;
    private Long minStock;
    private Long lastMovementId;
}
