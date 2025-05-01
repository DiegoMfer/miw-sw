package com.searchmiw.auth.service;

import com.searchmiw.auth.model.AuthRequest;
import com.searchmiw.auth.model.RegisterRequest;
import com.searchmiw.auth.model.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import java.time.Duration;

@Service
@Slf4j
public class UserService {

    private final WebClient webClient;
    
    @Value("${connection.timeout:5000}")
    private Integer connectionTimeout;

    public UserService(@Value("${services.user.url:http://localhost:8086}") String userServiceUrl) {
        log.info("Initializing User Service client with base URL: {}", userServiceUrl);
        this.webClient = WebClient.builder()
                .baseUrl(userServiceUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<Boolean> verifyCredentials(String email, String password) {
        log.debug("Verifying credentials for email: {}", email);
        AuthRequest request = new AuthRequest(email, password);
        return webClient.post()
                .uri("/api/users/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Boolean.class)
                .timeout(Duration.ofMillis(connectionTimeout))
                .onErrorResume(WebClientResponseException.Unauthorized.class, e -> {
                    log.warn("Unauthorized access attempt for email: {}", email);
                    return Mono.just(false);
                })
                .onErrorResume(e -> {
                    log.error("Error verifying credentials: {}", e.getMessage());
                    return Mono.error(new RuntimeException("Error connecting to user service: " + e.getMessage(), e));
                });
    }

    public Mono<UserDto> getUserByEmail(String email) {
        log.debug("Fetching user by email: {}", email);
        return webClient.get()
                .uri("/api/users/email/{email}", email)
                .retrieve()
                .bodyToMono(UserDto.class)
                .timeout(Duration.ofMillis(connectionTimeout))
                .onErrorResume(WebClientResponseException.NotFound.class, e -> {
                    log.info("User not found with email: {}", email);
                    return Mono.empty();
                })
                .onErrorResume(e -> {
                    log.error("Error fetching user by email: {}", e.getMessage());
                    return Mono.error(new RuntimeException("Error connecting to user service: " + e.getMessage(), e));
                });
    }

    public Mono<UserDto> createUser(RegisterRequest registerRequest) {
        log.info("Creating new user with email: {}", registerRequest.getEmail());
        return webClient.post()
                .uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(registerRequest)
                .retrieve()
                .bodyToMono(UserDto.class)
                .timeout(Duration.ofMillis(connectionTimeout))
                .onErrorResume(WebClientResponseException.Conflict.class, e -> {
                    log.warn("Email already in use: {}", registerRequest.getEmail());
                    return Mono.error(new RuntimeException("Email already in use"));
                })
                .onErrorResume(e -> {
                    log.error("Error creating user: {}", e.getMessage());
                    return Mono.error(new RuntimeException("Error connecting to user service: " + e.getMessage(), e));
                });
    }
    
    /**
     * Check if the user service is available
     * 
     * @return true if the user service is reachable, false otherwise
     */
    public Mono<Boolean> isUserServiceAvailable() {
        return webClient.get()
                .uri("/api/health")
                .retrieve()
                .toBodilessEntity()
                .timeout(Duration.ofMillis(2000)) // Short timeout for health check
                .map(response -> true)
                .onErrorResume(e -> {
                    log.warn("User service is not available: {}", e.getMessage());
                    return Mono.just(false);
                });
    }
}