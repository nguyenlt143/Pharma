package vn.edu.fpt.pharma.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.edu.fpt.pharma.base.BaseEntity;

@Entity
@Table(name = "shift_assignments")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE shift_assignments SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class ShiftAssignment extends BaseEntity<Long> {

    private Long userId;
    @ManyToOne
    @JoinColumn(name = "shift_id")
    private Shift shift;

}
