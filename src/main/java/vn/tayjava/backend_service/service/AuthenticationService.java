package vn.tayjava.backend_service.service;

import vn.tayjava.backend_service.controller.request.SignInRequest;
import vn.tayjava.backend_service.controller.response.TokenResponse;

public interface AuthenticationService {
    TokenResponse getAccessToken(SignInRequest signInRequest) ;
    TokenResponse getRefreshToken(String request) ;
}
