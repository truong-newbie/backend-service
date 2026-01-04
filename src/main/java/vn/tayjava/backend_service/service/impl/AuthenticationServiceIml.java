package vn.tayjava.backend_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;
import vn.tayjava.backend_service.controller.request.SignInRequest;
import vn.tayjava.backend_service.controller.response.TokenResponse;
import vn.tayjava.backend_service.repository.UserRepository;
import vn.tayjava.backend_service.service.AuthenticationService;
import vn.tayjava.backend_service.service.JwtService;
import vn.tayjava.backend_service.service.UserService;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AUTHENTICATION-SERVICE")
public class AuthenticationServiceIml implements AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    @Override
    public TokenResponse getAccessToken(SignInRequest request) {
        log.info("get access token");



        String accessToken = jwtService.generateAccessToken(1L, request.getEmail(), null);
        String refreshToken = jwtService.generateRefreshToken(1L, request.getEmail(), null);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

    }

    @Override
    public TokenResponse getRefreshToken(String request) {
        return null;
    }
}
