package com.searchmiw.aggregator.service;

import com.searchmiw.aggregator.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final WebClient userServiceWebClient;

    public Flux<User> getAllUsers() {
        return userServiceWebClient.get()
                .uri("/api/users")
                .retrieve()
                .bodyToFlux(User.class);
    }

    public Mono<User> getUserById(Long id) {
        return userServiceWebClient.get()
                .uri("/api/users/{id}", id)
                .retrieve()
                .bodyToMono(User.class);
    }
}
