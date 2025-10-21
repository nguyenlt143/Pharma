package vn.edu.fpt.pharma.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.edu.fpt.pharma.base.BaseEntity;
import vn.edu.fpt.pharma.constant.RequestStatus;
import vn.edu.fpt.pharma.constant.RequestType;

@Entity
@Table(name = "requestforms")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE requestforms SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class RequestForm extends BaseEntity<Long> {
    private String branchId;
    private String sourceBranchId;
    private RequestType requestType;
    private RequestStatus requestStatus;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
}
