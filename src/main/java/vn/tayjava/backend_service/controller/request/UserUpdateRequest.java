package vn.tayjava.backend_service.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import vn.tayjava.backend_service.common.Gender;
import vn.tayjava.backend_service.model.AddressEntity;

import java.util.Date;
import java.util.List;

@Getter
@ToString
@Setter
public class UserUpdateRequest {

    @NotNull(message = "id must be not null")
    @Min(value=1 , message=" userId must be equals or greater than 1")
    private Long id;

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
    private List<AddressEntity> addresses;
}
