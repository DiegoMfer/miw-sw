package com.searchmiw.auth.service;

import com.searchmiw.auth.dto.UserServiceAuthResponse;
import com.searchmiw.auth.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final WebClient.Builder webClientBuilder;
    
    @Value("${services.user.url}")
    private String userServiceUrl;
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public String login(String email, String password) {
        WebClient webClient = webClientBuilder.baseUrl(userServiceUrl).build();
        
        UserServiceAuthResponse response = webClient.post()
                .uri("/api/users/authenticate")
                .bodyValue(Map.of(
                    "email", email,
                    "password", password
                ))
                .retrieve()
                .onStatus(
                    status -> status.equals(HttpStatus.UNAUTHORIZED),
                    clientResponse -> Mono.error(new BadCredentialsException("Invalid email or password"))
                )
                .bodyToMono(UserServiceAuthResponse.class)
                .block();
        
        if (response == null || !response.isAuthenticated()) {
            throw new BadCredentialsException("Invalid email or password");
        }
        
        // Create a user object from the response to generate the token
        User user = User.builder()
                .id(response.getId())
                .email(response.getEmail())
                .name(response.getName())
                .build();
        
        return generateToken(user);
    }
    
    public String register(String email, String password, String name) {
        WebClient webClient = webClientBuilder.baseUrl(userServiceUrl).build();
        
        // First check if email already exists
        Boolean emailExists = webClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/api/users/check-email")
                    .queryParam("email", email)
                    .build())
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
        
        if (Boolean.TRUE.equals(emailExists)) {
            throw new IllegalArgumentException("Email already registered");
        }
        
        // Create user in user service
        Map<String, String> userRequest = new HashMap<>();
        userRequest.put("email", email);
        userRequest.put("password", password);
        userRequest.put("name", name);
        
        UserServiceAuthResponse createdUser = webClient.post()
                .uri("/api/users")
                .bodyValue(userRequest)
                .retrieve()
                .onStatus(
                    // Change this line from HttpStatus::isError to a lambda expression
                    status -> status.is4xxClientError() || status.is5xxServerError(),
                    response -> response.bodyToMono(String.class)
                        .flatMap(error -> Mono.error(new RuntimeException("Error creating user: " + error)))
                )
                .bodyToMono(UserServiceAuthResponse.class)
                .block();
        
        if (createdUser == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create user");
        }
        
        // Create a user object from the response to generate the token
        User user = User.builder()
                .id(createdUser.getId())
                .email(createdUser.getEmail())
                .name(createdUser.getName())
                .build();
        
        return generateToken(user);
    }
    
    public void validateToken(String token) {
        Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
            .build()
            .parseClaimsJws(token);
    }
    
    private String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("name", user.getName());
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }
}
