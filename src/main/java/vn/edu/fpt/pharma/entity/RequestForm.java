package vn.edu.fpt.pharma.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.edu.fpt.pharma.base.BaseEntity;
import vn.edu.fpt.pharma.constant.BatchStatus;
import vn.edu.fpt.pharma.constant.ImportType;

@Entity
@Table(name = "importforms")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE importforms SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class ImportForm extends BaseEntity<Long> {
    private String branchId;
    private String supplierId;
    private String sourceBranchId;
    private ImportType importType;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
}
