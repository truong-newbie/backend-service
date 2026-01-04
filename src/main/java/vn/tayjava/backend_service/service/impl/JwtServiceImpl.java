package vn.tayjava.backend_service.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.Token;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import vn.tayjava.backend_service.common.TokenType;
import vn.tayjava.backend_service.service.JwtService;

import java.nio.file.AccessDeniedException;
import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
@Slf4j(topic="JWT-SERVICE")
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.expiryMinutes}")
    private long expiryMinutes;

    @Value("${jwt.expiryDay}")
    private long expiryDay;

    @Value("${jwt.accessKey}")
    private String accessKey;

    @Value("${jwt.refreshKey}")
    private String refreshKey;



    @Override
    public String generateAccessToken(long userId, String username, Collection<? extends GrantedAuthority> authorities) {
        log.info("Generate access token for user {} with authorities {}", userId, authorities);

        Map<String , Object> claims= new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", authorities);

        return generateToken(claims, username);
    }

    @Override
    public String generateRefreshToken(long userId, String username, Collection<? extends GrantedAuthority> authorities) {
        log.info("Generate refresh token for user {} with authorities {}", userId, authorities);

        Map<String , Object> claims= new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", authorities);

        return generateRefreshToken(claims, username);
    }


    @Override
    public String extractUsername(String token, TokenType type) throws AccessDeniedException {
        log.info("extract username form token {} with type {}", token, type);
        return extractClaims(type, token, Claims::getSubject);
    }

    private<T> T extractClaims(TokenType type, String token , Function<Claims, T> claimsExtractor) throws AccessDeniedException {
        final Claims claims= extraAllClaim(token, type);
        return claimsExtractor.apply(claims);
    }

    private Claims extraAllClaim(String token, TokenType type) throws AccessDeniedException {
        try{
            return Jwts.parser().setSigningKey(accessKey).parseClaimsJws(token).getBody();
        }catch (SignatureException | ExpiredJwtException e){
            throw new AccessDeniedException("Access denied, error:"+ e.getMessage());
        }
    }



    private String generateToken(Map<String , Object> claims , String username){
        log.info("Generate access token for user {} with claims {}", username, claims);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() +1000*60*expiryMinutes))
                .signWith(getKey(TokenType.ACCESS_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }

    private String generateRefreshToken(Map<String , Object> claims , String username){
        log.info("Generate refresh token for user {} with claims {}", username, claims);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() +1000*60*60*24*expiryDay))
                .signWith(getKey(TokenType.REFRESH_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getKey(TokenType type){
        switch (type){
            case ACCESS_TOKEN -> {
                return Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessKey));
            }
            case REFRESH_TOKEN -> {
                return Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshKey));
            }
            default -> throw new IllegalStateException("Invalid token type");
        }

    }
}
