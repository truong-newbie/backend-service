package vn.tayjava.backend_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.apache.coyote.Request;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.tayjava.backend_service.common.Gender;
import vn.tayjava.backend_service.controller.request.UserCreationRequest;
import vn.tayjava.backend_service.controller.request.UserUpdatePassword;
import vn.tayjava.backend_service.controller.request.UserUpdateRequest;
import vn.tayjava.backend_service.controller.response.UserPageResponse;
import vn.tayjava.backend_service.controller.response.UserResponse;
import vn.tayjava.backend_service.service.UserService;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Tag(name = "User Controller")
@RequiredArgsConstructor
@Slf4j(topic = " USER-CONTROLLER")
public class UserController {

    private final UserService userService;

    @Operation(summary = " get user list", description = "API retrieve user from db")
    @GetMapping("/list")
    public Map<String, Object> getList(@RequestParam(required = false) String keyword,
                                       @RequestParam(required = false) String sort,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "0") int size) {

        log.info(("Get list of user"));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "user list");
        result.put("data", userService.findAll(keyword, sort, page, size));
        return result;
    }

    @Operation(summary = " get user detail", description = "API retrieve userdetail by id")
    @GetMapping("/{userId}")
    public Map<String, Object> getUserDetail(@PathVariable @Min(value = 1, message = "userId must be equals or greater than 1") Long userId) {

        UserResponse userDetail = userService.findById(userId);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "user ");
        result.put("data", userDetail);
        return result;
    }

    @Operation(summary = " create user", description = "API add new user to db")
    @PostMapping("/add")
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserCreationRequest request) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "user created successfully ");
        result.put("data", userService.save(request));
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @Operation(summary = " update user", description = "API update user in db")
    @PutMapping("/upd")
    public Map<String, Object> createUser(@RequestBody @Valid UserUpdateRequest request) {
        log.info("Updating user :{}", request);

        userService.update(request);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.ACCEPTED.value());
        result.put("message", "user updated successfully ");
        result.put("data", "blank");
        return result;
    }

    @Operation(summary = " change password", description = "API update password for user to db")
    @PatchMapping("/{userId}/change-pwd")
    public Map<String, Object> updatePassoword(@RequestBody @Valid UserUpdatePassword request) {

        log.info("Changing password for user: {}", request);
        userService.changePassword(request);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.NO_CONTENT.value());
        result.put("message", "password updated successfully ");
        result.put("data", "blank");
        return result;
    }

    @Operation(summary = " delete user", description = "API activate user")
    @DeleteMapping("/del/{userId}")
    public Map<String, Object> deleteUser(@PathVariable @Min(value = 1, message = " userId must be equals or greater than 1")
                                          Long userId) {

        log.info("deleteing user:{}", userId);

        userService.delete(userId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.RESET_CONTENT.value());
        result.put("message", "user deleted successfully ");
        result.put("data", "blank");
        return result;
    }

}
