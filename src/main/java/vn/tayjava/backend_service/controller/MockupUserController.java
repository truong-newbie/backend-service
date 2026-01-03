package vn.tayjava.backend_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vn.tayjava.backend_service.common.Gender;
import vn.tayjava.backend_service.controller.request.UserCreationRequest;
import vn.tayjava.backend_service.controller.request.UserUpdatePassword;
import vn.tayjava.backend_service.controller.request.UserUpdateRequest;
import vn.tayjava.backend_service.controller.response.UserResponse;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mockup/user")
@Tag(name="Mockup User Controller")
public class MockupUserController {

    @Operation(summary=" get user list", description="API retrieve user from db")
    @GetMapping("/list")
    public Map<String, Object> getList(@RequestParam(required = false) String keyword
    , @RequestParam(defaultValue = "0")  int page, @RequestParam(defaultValue = "0") int size) {
        UserResponse userResponse1 = new UserResponse();
        userResponse1.setId(1L);
        userResponse1.setFirstName("Tay");
        userResponse1.setLastName("Java");
        userResponse1.setGender(Gender.MALE);
        userResponse1.setBirthday(new Date());
        userResponse1.setUsername("admin");
        userResponse1.setEmail("admin@gmail.com");
        userResponse1.setPhone("0975118228");

        UserResponse userResponse2 = new UserResponse();
        userResponse2.setId(2L);
        userResponse2.setFirstName("Leo");
        userResponse2.setLastName("Messi");
        userResponse2.setGender(Gender.MALE);
        userResponse2.setBirthday(new Date());
        userResponse2.setUsername("user");
        userResponse2.setEmail("user@gmail.com");
        userResponse2.setPhone("0971234567");

        List<UserResponse> userList = List.of(userResponse1, userResponse2);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message" , "user list");
        result.put("data", userList);
        return result;
    }

    @Operation(summary=" get user detail", description="API retrieve userdetail by id")
    @GetMapping("/{userId}")
    public Map<String, Object> getUserDetail(@PathVariable  Long userId) {
        UserResponse userDetail = new UserResponse();
        userDetail.setId(1L);
        userDetail.setFirstName("Tay");
        userDetail.setLastName("Java");
        userDetail.setGender(Gender.MALE);
        userDetail.setBirthday(new Date());
        userDetail.setUsername("admin");
        userDetail.setEmail("admin@gmail.com");
        userDetail.setPhone("0975118228");


        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message" , "user ");
        result.put("data", userDetail);
        return result;
    }

    @Operation(summary=" create user", description="API add new user to db")
    @PostMapping("/add")
    public Map<String, Object> createUser(UserCreationRequest request) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message" , "user created successfully ");
        result.put("data", 3);
        return result;
    }

    @Operation(summary=" update user", description="API update user in db")
    @PutMapping("/upd")
    public Map<String, Object> createUser(UserUpdateRequest request) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.ACCEPTED.value());
        result.put("message" , "user updated successfully ");
        result.put("data", "blank");
        return result;
    }

    @Operation(summary=" change password", description="API update password for user to db")
    @PatchMapping("/{userId}/change-pwd")
    public Map<String, Object> updatePassoword(UserUpdatePassword request) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.NO_CONTENT.value());
        result.put("message" , "password updated successfully ");
        result.put("data", "blank");
        return result;
    }

    @Operation(summary=" delete user", description="API activate user")
    @DeleteMapping("/del/{userId}")
    public Map<String, Object> deleteUser(@PathVariable Long userId) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.RESET_CONTENT.value());
        result.put("message" , "user deleted successfully ");
        result.put("data", "blank");
        return result;
    }

}
