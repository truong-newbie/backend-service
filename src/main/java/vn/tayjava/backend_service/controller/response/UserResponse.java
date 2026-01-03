package vn.tayjava.backend_service.controller.response;

import lombok.*;
import vn.tayjava.backend_service.common.Gender;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String username;
    private Gender gender;
    private Date birthday;

}
