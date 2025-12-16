package vn.edu.fpt.pharma.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.edu.fpt.pharma.base.BaseEntity;


@Entity
@Table(name = "users")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE users SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class User extends BaseEntity<Long> {
    @Column(unique = true, nullable = false)
    private String userName;

    @Column(nullable = false)
    private String password;
    private String fullName;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;
    private Long branchId;
    private String phoneNumber;
    private String email;
    @Column(columnDefinition = "LONGTEXT")
    private String imageUrl;

    @PrePersist
    @PreUpdate
    private void trimFields() {
        if (this.userName != null) this.userName = this.userName.trim();
        if (this.fullName != null) this.fullName = this.fullName.trim();
        if (this.email != null) this.email = this.email.trim();
        if (this.phoneNumber != null) this.phoneNumber = this.phoneNumber.trim();
    }
}
