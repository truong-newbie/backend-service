package vn.tayjava.backend_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import vn.tayjava.backend_service.common.Gender;
import vn.tayjava.backend_service.common.UserStatus;
import vn.tayjava.backend_service.common.UserType;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Table(name = "tbl_user")
public class UserEntity extends AbstractEntity<Long> implements UserDetails, Serializable {


    @Column(name = "first_name", length = 255)
    private String firstName;

    @Column(name = "last_name", length = 255)
    private String lastName;

    @Column(name = "phone", length = 15)
    private String phone;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "username", unique = true, nullable = false, length = 255)
    private String username;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "gender")
    private Gender gender;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_of_birth")
    private Date birthday;

    @Column(name = "password", length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "type", length = 255)
    private UserType type;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", length = 255)
    private UserStatus status;

    @OneToMany(mappedBy = "user" ,cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<UserHasRole> roles= new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<GroupHasUser> groups= new HashSet<>();



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //b1: get role
        List<Role> roleList=roles.stream().map(UserHasRole::getRole).toList();

        //b2:get role name
        List<String> roleNames= roleList.stream().map(Role::getName).toList();
        //b3: add role name to authority
//        return roleNames.stream().map(SimpleGrantedAuthority::new).toList();
        return roleNames.stream().map(s-> new SimpleGrantedAuthority("ROLE_"+ s.toUpperCase())).toList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserStatus.ACTIVE.equals(status);
    }
}
