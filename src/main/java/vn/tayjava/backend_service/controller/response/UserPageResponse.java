package vn.tayjava.backend_service.controller.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPageResponse extends PageReponseAbstract {
    private List<UserResponse> users;
}
