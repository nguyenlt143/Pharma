package vn.edu.fpt.pharma.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.edu.fpt.pharma.base.BaseEntity;

@Entity
@Table(name = "requestdetails")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE requestdetails SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class RequestDetail extends BaseEntity<Long> {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "request_form_id")
    private RequestForm requestForm;
    private Long variant_id;
    private Long quantity;
    private Boolean isAccepted;
}
