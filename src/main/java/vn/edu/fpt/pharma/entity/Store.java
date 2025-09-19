package vn.edu.fpt.pharma.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.edu.fpt.pharma.base.BaseEntity;

@Entity
@Table(name = "stores")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE stores SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Store extends BaseEntity<Long> {
    private String storeCode;
    private String storeName;
    private String address;
    private String addressCode;
    private String phoneNumber;
    private Double latitude;
    private Double longitude;
}