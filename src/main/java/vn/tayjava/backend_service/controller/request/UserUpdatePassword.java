package vn.tayjava.backend_service.controller.request;


import lombok.Getter;

@Getter
public class UserUpdatePassword {
    private Long id;
    private String password;
    private String confirmPassword;
}
