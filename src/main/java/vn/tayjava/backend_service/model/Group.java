package vn.tayjava.backend_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;


@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="tbl_group")
public class Group extends  AbstractEntity<Integer> implements Serializable {

    @Column(name="name")
    private String name;

    @Column(name="description")
    private String description;

    @OneToOne
    @JoinColumn(name="role_id")
    private Role role;
}
