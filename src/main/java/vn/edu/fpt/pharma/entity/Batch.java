package vn.edu.fpt.pharma.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.edu.fpt.pharma.base.BaseEntity;
import vn.edu.fpt.pharma.constant.BatchStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "batchs")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE batchs SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Batch extends BaseEntity<Long> {
    private Long variantId;
    private String batchCode;
    private LocalDateTime mfgDate;
    private LocalDateTime expiryDate;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_id_id")
    private Supplier supplierId;
    private Long sourceMovementId;
    private Long totalReceived;
    private Long totalIssued;
    private Long movementId;
    @Enumerated(EnumType.STRING)
    private BatchStatus batchStatus;
}
