package vn.edu.fpt.pharma.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.edu.fpt.pharma.base.BaseEntity;

@Entity
@Table(name = "requestapprovals")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE requestapprovals SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class RequestApproval extends BaseEntity<Long> {
    private Long requestDetailId;
    private Long variant_id;
    private Long quantity;
    private String note;
}
