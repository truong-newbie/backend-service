package vn.tayjava.backend_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.tayjava.backend_service.controller.request.SignInRequest;
import vn.tayjava.backend_service.controller.response.TokenResponse;
import vn.tayjava.backend_service.repository.UserRepository;
import vn.tayjava.backend_service.service.AuthenticationService;
import vn.tayjava.backend_service.service.JwtService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AUTHENTICATION-SERVICE")
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public TokenResponse getAccessToken(SignInRequest request) {
        log.info("get access token");

        try {

            //thuc hien xac thuc voi username va password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // neu xac thuc thanh cong, luu thong itn vao securityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (AuthenticationException e) {
            log.error("Login failed: {}", e.getMessage());
            throw new AccessDeniedException("Invalid username or password");
        }
        //get user
        var user = userRepository.findByUsername(request.getEmail())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));


        List<String> authorities= new ArrayList<>();
        user.getAuthorities().forEach(authority -> authorities.add(authority.getAuthority()));

        String accessToken = jwtService.generateAccessToken(
                user.getUsername(),
                authorities
        );

        String refreshToken = jwtService.generateRefreshToken(
                user.getUsername(),
                authorities
        );

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
