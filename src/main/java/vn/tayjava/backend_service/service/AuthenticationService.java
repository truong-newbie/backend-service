package vn.tayjava.backend_service.service;

import vn.tayjava.backend_service.controller.request.SignInRequest;
import vn.tayjava.backend_service.controller.response.TokenResponse;

import java.nio.file.AccessDeniedException;

public interface AuthenticationService {
    TokenResponse getAccessToken(SignInRequest signInRequest) throws AccessDeniedException;
    TokenResponse getRefreshToken(String request) ;
}
