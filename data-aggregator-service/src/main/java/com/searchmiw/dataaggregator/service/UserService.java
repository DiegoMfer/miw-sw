package com.searchmiw.dataaggregator.service;

import com.searchmiw.dataaggregator.exception.ServiceException;
import com.searchmiw.dataaggregator.model.User;
import com.searchmiw.dataaggregator.resolver.CreateUserInput;
import com.searchmiw.dataaggregator.resolver.UpdateUserInput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    private final WebClient webClient;

    public UserService(WebClient.Builder webClientBuilder,
                      @Value("${service.user.url}") String userServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(userServiceUrl).build();
    }

    public List<User> getAllUsers() {
        log.info("Fetching all users");
        return webClient.get()
                .uri("/api/users")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<User>>() {})
                .doOnError(e -> log.error("Error fetching users: {}", e.getMessage(), e))
                .onErrorReturn(List.of())
                .block();
    }

    public Optional<User> getUserById(Long id) {
        log.info("Fetching user with id: {}", id);
        return webClient.get()
                .uri("/api/users/{id}", id)
                .retrieve()
                .bodyToMono(User.class)
                .map(Optional::of)
                .onErrorReturn(Optional.empty())
                .block();
    }

    public Optional<User> getUserByEmail(String email) {
        log.info("Fetching user with email: {}", email);
        return webClient.get()
                .uri("/api/users/email/{email}", email)
                .retrieve()
                .bodyToMono(User.class)
                .map(Optional::of)
                .onErrorReturn(Optional.empty())
                .block();
    }

    public User createUser(CreateUserInput input) {
        log.info("Creating new user: {}", input);
        try {
            return webClient.post()
                    .uri("/api/users")
                    .bodyValue(input)
                    .retrieve()
                    .bodyToMono(User.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Error creating user: {} - Status: {}", e.getMessage(), e.getStatusCode(), e);
            ErrorType errorType = e.getStatusCode().is4xxClientError() ? 
                    ErrorType.BAD_REQUEST : ErrorType.INTERNAL_ERROR;
            throw new ServiceException("Failed to create user: " + e.getMessage(), e, errorType);
        } catch (Exception e) {
            log.error("Unexpected error creating user: {}", e.getMessage(), e);
            throw new ServiceException("Unexpected error creating user", e, ErrorType.INTERNAL_ERROR);
        }
    }

    public User updateUser(Long id, UpdateUserInput input) {
        log.info("Updating user with id: {}", id);
        try {
            return webClient.put()
                    .uri("/api/users/{id}", id)
                    .bodyValue(input)
                    .retrieve()
                    .bodyToMono(User.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Error updating user: {} - Status: {}", e.getMessage(), e.getStatusCode(), e);
            ErrorType errorType = e.getStatusCode().is4xxClientError() ? 
                    ErrorType.BAD_REQUEST : ErrorType.INTERNAL_ERROR;
            throw new ServiceException("Failed to update user: " + e.getMessage(), e, errorType);
        } catch (Exception e) {
            log.error("Unexpected error updating user: {}", e.getMessage(), e);
            throw new ServiceException("Unexpected error updating user", e, ErrorType.INTERNAL_ERROR);
        }
    }

    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        try {
            webClient.delete()
                    .uri("/api/users/{id}", id)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Error deleting user: {} - Status: {}", e.getMessage(), e.getStatusCode(), e);
            ErrorType errorType = e.getStatusCode().is4xxClientError() ? 
                    ErrorType.BAD_REQUEST : ErrorType.INTERNAL_ERROR;
            throw new ServiceException("Failed to delete user: " + e.getMessage(), e, errorType);
        } catch (Exception e) {
            log.error("Unexpected error deleting user: {}", e.getMessage(), e);
            throw new ServiceException("Unexpected error deleting user", e, ErrorType.INTERNAL_ERROR);
        }
    }
}
