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

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtConfig jwtConfig;

    public ResponseEntity<AuthResponse> login(AuthRequest authRequest) {
        try {
            // Verify credentials with user service
            boolean verified = userService.verifyCredentials(authRequest.getEmail(), authRequest.getPassword());

            if (verified) {
                // Get user details to include in token
                UserDto user = userService.getUserByEmail(authRequest.getEmail());

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

                    return ResponseEntity.ok(response);
                }
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.builder().message("Invalid credentials").build());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.builder().message("Authentication failed").build());
        }
    }

    public ResponseEntity<AuthResponse> register(RegisterRequest registerRequest) {
        try {
            // Create user through user service
            UserDto user = userService.createUser(registerRequest);

            if (user != null) {
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

                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthResponse.builder().message("Registration failed").build());

        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("Email already in use")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(AuthResponse.builder().message("Email already in use").build());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthResponse.builder().message("Registration failed").build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthResponse.builder().message("Registration failed").build());
        }
    }

    public boolean validateToken(String token) {
        return jwtConfig.validateToken(token);
    }
}