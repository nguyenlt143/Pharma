package vn.edu.fpt.pharma.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.edu.fpt.pharma.base.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categorys")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE categorys SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Category extends BaseEntity<Long> {
    @Column(unique = true, nullable = false)
    private String name;
    private String description;
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Category> children = new ArrayList<>();
}
