package vn.tayjava.backend_service.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.tayjava.backend_service.controller.request.SignInRequest;
import vn.tayjava.backend_service.controller.response.TokenResponse;
import vn.tayjava.backend_service.service.AuthenticationService;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/auth")
@Slf4j(topic="AUTHENTICATION-CONTROLLER")
@Tag(name="Authentication Controller")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Operation(summary = "Access token" , description = "Get access token and refresh token by username and password")
    @PostMapping("/access-token")
    public TokenResponse getAccessToken(@RequestBody SignInRequest request) throws AccessDeniedException {
        log.info("Access token request");
//        return TokenResponse.builder()
//                .accessToken("DUMMY-ACCESS-TOKEN")
//                        .refreshToken("DUMMY-REFRESH-TOKEN")
//                .build();
        return authenticationService.getAccessToken(request);

    }

    @Operation(summary = "Refresh token" , description = "Get new refresh token by refresh token")
    @PostMapping("/refresh-token")
    public TokenResponse getRefreshToken(@RequestBody String refreshToken){
        log.info("Refresh token request");
        return TokenResponse.builder()
                .accessToken("DUMMY-NEW-ACCESS-TOKEN")
                .refreshToken("DUMMY-REFRESH-TOKEN")
                .build();

    }
}
