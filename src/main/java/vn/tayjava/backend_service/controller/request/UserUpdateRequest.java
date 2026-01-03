package vn.tayjava.backend_service.controller.request;

import lombok.Getter;
import lombok.ToString;
import vn.tayjava.backend_service.common.Gender;
import vn.tayjava.backend_service.model.AddressEntity;

import java.util.Date;
import java.util.List;

@Getter
@ToString
public class UserUpdateRequest {

    private Long id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String username;
    private Gender gender;
    private Date birthday;
    private List<AddressEntity> addresses;
}
