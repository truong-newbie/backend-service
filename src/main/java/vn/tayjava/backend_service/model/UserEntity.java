package vn.tayjava.backend_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import vn.tayjava.backend_service.common.Gender;
import vn.tayjava.backend_service.common.UserStatus;
import vn.tayjava.backend_service.common.UserType;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name="tbl_user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="first_name" , length=255)
    private String firstName;

    @Column(name="last_name" , length=255)
    private String lastName;

    @Column(name="phone" , length=15)
    private String phone;

    @Column(name="email" , length=255)
    private String email;

    @Column(name="username" , unique=true, nullable = false , length=255)
    private String username;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name="gender" )
    private Gender gender;

    @Temporal(TemporalType.DATE)
    @Column(name="date_of_birth" )
    private Date birthday;

    @Column(name="password" , length=255)
    private String password;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name="type" , length=255)
    private UserType type;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name="status" , length=255)
    private UserStatus status;

    @Column(name="created_at" , length=255)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdAt;

    @Column(name="updated_at" , length=255)
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date updatedAt;
}
