package vn.edu.fpt.pharma.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.edu.fpt.pharma.base.BaseEntity;
import vn.edu.fpt.pharma.constant.BatchStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "batches")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE batches SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Batch extends BaseEntity<Long> {
    private Long variantId;
    @Column(unique = true, nullable = false)
    private String batchCode;
    private LocalDateTime mfgDate;
    private LocalDateTime expiryDate;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_id")
    private Supplier supplierId;
    @Enumerated(EnumType.STRING)
    private BatchStatus batchStatus;
    private Long sourceMovementId;
    private int totalReceived;
    private int totalIssued;

}
