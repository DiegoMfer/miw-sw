package com.searchmiw.auth.service;

import com.searchmiw.auth.config.JwtConfig;
import com.searchmiw.auth.model.AuthRequest;
import com.searchmiw.auth.model.AuthResponse;
import com.searchmiw.auth.model.RegisterRequest;
import com.searchmiw.auth.model.UserDto;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtConfig jwtConfig;

    public Mono<ResponseEntity<AuthResponse>> login(AuthRequest authRequest) {
        return userService.verifyCredentials(authRequest.getEmail(), authRequest.getPassword())
            .flatMap(verified -> {
                if (verified) {
                    return userService.getUserByEmail(authRequest.getEmail())
                        .flatMap(user -> {
                            if (user != null) {
                                // Generate JWT token
                                String token = jwtConfig.generateToken(user.getEmail(), user.getId());

                                // Create auth response
                                AuthResponse response = AuthResponse.builder()
                                        .userId(user.getId())
                                        .name(user.getName())
                                        .email(user.getEmail())
                                        .token(token)
                                        .message("Authentication successful")
                                        .build();

                                return Mono.just(ResponseEntity.ok(response));
                            }
                            return Mono.just(ResponseEntity
                                    .status(HttpStatus.UNAUTHORIZED)
                                    .body(AuthResponse.builder().message("Invalid credentials").build()));
                        })
                        .switchIfEmpty(Mono.just(ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body(AuthResponse.builder().message("Invalid credentials").build())));
                } else {
                    return Mono.just(ResponseEntity
                            .status(HttpStatus.UNAUTHORIZED)
                            .body(AuthResponse.builder().message("Invalid credentials").build()));
                }
            })
            .onErrorResume(e -> Mono.just(ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.builder().message("Authentication failed").build())));
    }

    public Mono<ResponseEntity<AuthResponse>> register(RegisterRequest registerRequest) {
        return userService.createUser(registerRequest)
            .flatMap(user -> {
                // Generate JWT token
                String token = jwtConfig.generateToken(user.getEmail(), user.getId());

                // Create auth response
                AuthResponse response = AuthResponse.builder()
                        .userId(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .token(token)
                        .message("Registration successful")
                        .build();

                return Mono.just(ResponseEntity.status(HttpStatus.CREATED).body(response));
            })
            .onErrorResume(e -> {
                if (e.getMessage() != null && e.getMessage().contains("Email already in use")) {
                    return Mono.just(ResponseEntity
                            .status(HttpStatus.CONFLICT)
                            .body(AuthResponse.builder().message("Email already in use").build()));
                }
                return Mono.just(ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(AuthResponse.builder().message("Registration failed").build()));
            });
    }

    public boolean validateToken(String token) {
        return jwtConfig.validateToken(token);
    }
}