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

@Service
@Slf4j
public class UserService {

    private final WebClient webClient;

    public UserService(@Value("${services.user.url:http://user-service:8086}") String userServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(userServiceUrl)
                .build();
    }

    public boolean verifyCredentials(String email, String password) {
        try {
            AuthRequest request = new AuthRequest(email, password);
            Boolean result = webClient.post()
                    .uri("/api/users/verify")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
            return Boolean.TRUE.equals(result);
        } catch (WebClientResponseException.Unauthorized e) {
            return false;
        } catch (Exception e) {
            log.error("Error verifying credentials: {}", e.getMessage());
            throw e;
        }
    }

    public UserDto getUserByEmail(String email) {
        try {
            return webClient.get()
                    .uri("/api/users/email/{email}", email)
                    .retrieve()
                    .bodyToMono(UserDto.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            return null;
        } catch (Exception e) {
            log.error("Error fetching user by email: {}", e.getMessage());
            throw e;
        }
    }

    public UserDto createUser(RegisterRequest registerRequest) {
        try {
            return webClient.post()
                    .uri("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(registerRequest)
                    .retrieve()
                    .bodyToMono(UserDto.class)
                    .block();
        } catch (WebClientResponseException.Conflict e) {
            throw new RuntimeException("Email already in use");
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage());
            throw e;
        }
    }
}