package vn.edu.fpt.pharma.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.edu.fpt.pharma.base.BaseEntity;

@Entity
@Table(name = "units")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE units SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Unit extends BaseEntity<Long> {
    @Column(unique = true, nullable = false)
    private String name;
    private String description;
}
