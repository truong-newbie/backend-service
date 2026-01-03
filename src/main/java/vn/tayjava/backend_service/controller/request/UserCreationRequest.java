package vn.tayjava.backend_service.controller.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import vn.tayjava.backend_service.common.Gender;
import vn.tayjava.backend_service.common.UserType;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@ToString
public class UserCreationRequest implements Serializable {
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String username;
    private Gender gender;
    private Date birthday;
    private UserType type;
    private List<AddressRequest> addresses;
}
