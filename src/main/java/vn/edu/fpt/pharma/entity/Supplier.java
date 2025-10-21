package vn.edu.fpt.pharma.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.edu.fpt.pharma.base.BaseEntity;

@Entity
@Table(name = "suppliers")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE suppliers SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")

public class Supplier extends BaseEntity<Long> {
    private String name;
    private String phone;
    private String address;
}
