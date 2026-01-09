package vn.tayjava.backend_service.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
@Setter
public class UserCreationRequest implements Serializable {
    @NotBlank(message = "firstName must be not blank")
    private String firstName;

    @NotBlank(message = "lastName must be not blank")
    private String lastName;
    private String phone;

    @Email( message= "Email invalid")
    private String email;
    private String username;
    private Gender gender;
    private Date birthday;
    private UserType type;
    private List<AddressRequest> addresses;
}
