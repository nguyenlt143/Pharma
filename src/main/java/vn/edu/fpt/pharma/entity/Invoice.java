package vn.edu.fpt.pharma.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.edu.fpt.pharma.base.BaseEntity;
import vn.edu.fpt.pharma.constant.BatchStatus;
import vn.edu.fpt.pharma.constant.InvoiceType;

@Entity
@Table(name = "invoices")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE invoices SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Invoice extends BaseEntity<Long> {
    private String invoiceNumber;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    private Customer customer;
    private Long shiftworkId;
    private Long branchId;
    private Double totalPrice;
    private String description;
    private String paymentMethod;
    @Enumerated(EnumType.STRING)
    private InvoiceType  invoiceType;
}
