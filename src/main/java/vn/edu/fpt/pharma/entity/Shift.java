package vn.edu.fpt.pharma.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.edu.fpt.pharma.base.BaseEntity;

import java.time.LocalTime;

@Entity
@Table(name = "shifts")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE shifts SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Shift extends BaseEntity<Long> {
    private Long branchId;
    private String name;
    private LocalTime startTime;
    private LocalTime  endTime;
    private String note;
}
