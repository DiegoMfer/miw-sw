package com.searchmiw.dataaggregator.service;

import com.searchmiw.dataaggregator.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class UserService {

    private final WebClient webClient;

    public UserService(WebClient.Builder webClientBuilder, 
                      @Value("${services.user.url}") String userServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(userServiceUrl).build();
    }

    public Mono<User> getCurrentUser(String token) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/users/{id}")
                        .build(extractUserIdFromToken(token)))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(User.class);
    }
    
    public Mono<User> getUserById(String token, String id) {
        return webClient.get()
                .uri("/api/users/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(User.class);
    }
    
    public Mono<User> updateUserProfile(String token, String name, String username) {
        Map<String, Object> requestBody = Map.of(
                "name", name != null ? name : "",
                "username", username != null ? username : "");
        
        return webClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/users/{id}")
                        .build(extractUserIdFromToken(token)))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(User.class);
    }
    
    private String extractUserIdFromToken(String token) {
        // This would typically use JWT parsing to extract the user ID
        // For now, we'll depend on the JwtUtil from the controller
        return "current"; // This will be replaced by the actual ID
    }
}
