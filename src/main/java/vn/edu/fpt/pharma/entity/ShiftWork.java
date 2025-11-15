package vn.edu.fpt.pharma.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.edu.fpt.pharma.base.BaseEntity;
import vn.edu.fpt.pharma.constant.WorkType;
import java.time.LocalDate;

@Entity
@Table(name = "shift_works")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE shift_works SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class ShiftWork extends BaseEntity<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id")
    private ShiftAssignment assignment;

    private LocalDate workDate;
//    private int realMoney;
//    private boolean isClosed;
}

