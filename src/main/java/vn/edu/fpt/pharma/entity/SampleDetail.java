package vn.edu.fpt.pharma.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.edu.fpt.pharma.base.BaseEntity;

@Entity
@Table(name = "sampledetails")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE sampledetails SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class SampleDetail extends BaseEntity<Long> {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sample_id")
    private Sample sample;
    private Long variantId;
    private String instruction;
}
