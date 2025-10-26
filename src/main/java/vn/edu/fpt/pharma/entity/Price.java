package vn.edu.fpt.pharma.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.edu.fpt.pharma.base.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "prices")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE prices SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Price extends BaseEntity<Long> {
    private Long variantId;
    private Double salePrice;
    private Double branchPrice;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
