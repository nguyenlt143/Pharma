package vn.edu.fpt.pharma.entity;

import jakarta.persistence.*;
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
    @Column(unique = true, nullable = false)
    private String name;
    @Enumerated(EnumType.STRING)
    private BranchType branchType;
    private String address;
    private Long userId;
}
