package vn.tayjava.backend_service.service;

import org.springframework.security.core.GrantedAuthority;
import vn.tayjava.backend_service.common.TokenType;

import java.nio.file.AccessDeniedException;
import java.util.Collection;
import java.util.List;

public interface JwtService {
    String generateAccessToken( String username, List<String> authorities);

    String generateRefreshToken( String username, List<String> authorities);

    String extractUsername(String token, TokenType type) throws AccessDeniedException;
}
