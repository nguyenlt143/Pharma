package vn.edu.fpt.pharma.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.edu.fpt.pharma.base.BaseEntity;

@Entity
@Table(name = "samples")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE samples SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Sample extends BaseEntity<Long> {
    private String name;
    private String description;
}
