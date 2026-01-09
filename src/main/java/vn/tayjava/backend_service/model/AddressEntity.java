package vn.tayjava.backend_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name="tbl_address")
public class AddressEntity extends AbstractEntity<Integer> implements  Serializable{



    @Column(name="apartment_number")
    private String apartmentNumber;

    @Column(name="floor")
    private String floor;

    @Column(name="building")
    private String building;

    @Column(name="street_number")
    private String streetNumber;

    @Column(name="street")
    private String street;

    @Column(name="city")
    private String city;

    @Column(name="user_id")
    private Long userId;

    @Column(name="country")
    private String country;

    @Column(name="address_type")
    private Integer addressType;


}
