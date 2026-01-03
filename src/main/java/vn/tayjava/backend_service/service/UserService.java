package vn.tayjava.backend_service.service;

import vn.tayjava.backend_service.controller.request.UserCreationRequest;
import vn.tayjava.backend_service.controller.request.UserUpdatePassword;
import vn.tayjava.backend_service.controller.request.UserUpdateRequest;
import vn.tayjava.backend_service.controller.response.UserPageResponse;
import vn.tayjava.backend_service.controller.response.UserResponse;

import java.util.List;

public interface UserService {
    UserPageResponse findAll(String keyword, String sort, int page, int size);
    UserResponse findById(Long id);
    UserResponse findByUsername(String username);
    UserResponse findByEmail(String email);
    long save(UserCreationRequest req);
    void update(UserUpdateRequest req);
    void changePassword(UserUpdatePassword req);
    void delete(Long id);
}
