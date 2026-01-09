package vn.tayjava.backend_service.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.tayjava.backend_service.common.TokenType;
import vn.tayjava.backend_service.service.JwtService;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Date;


@Component
@Slf4j(topic ="CUSTOMIZE-REQUEST-FILTER")
@RequiredArgsConstructor
@EnableMethodSecurity
@Configuration
public class CustomizeRequestFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("{} {}",request.getMethod(),request.getRequestURI());


        // TODO : check authority by request url
        String authHeader= request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer")) {
            authHeader = authHeader.substring(7);
            log.info("Bearer authHeader :{}", authHeader.substring(0, 20));

            String email="";
            try {
                email = jwtService.extractUsername(authHeader, TokenType.ACCESS_TOKEN);
                log.info("email :{}", email);
            } catch (AccessDeniedException e) {
                log.error("Access Denied, message={}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(errorResponse(e.getMessage()));
                return;
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            SecurityContext securityContext= SecurityContextHolder.createEmptyContext();

            UsernamePasswordAuthenticationToken authentication= new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String errorResponse(String message){
        try{
            ErrorResponse error = new ErrorResponse();
            error.setTimestamp(new Date());
            error.setError("Forbidden");
            error.setStatus(HttpServletResponse.SC_FORBIDDEN);
            error.setMessage(message);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(error);
        }catch(Exception e){
            return "";
        }
    }

    @Setter
    @Getter
    private class ErrorResponse{
        private Date timestamp;
        private int status;
        private String error;
        private String message;
    }
}
