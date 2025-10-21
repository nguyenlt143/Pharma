package vn.edu.fpt.pharma.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.edu.fpt.pharma.base.BaseEntity;
import vn.edu.fpt.pharma.constant.BranchType;

@Entity
@Table(name = "branchs")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE branchs SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Branch extends BaseEntity<Long> {
    private String name;
    private BranchType branchType;
    private String address;
    private Long userId;
}
