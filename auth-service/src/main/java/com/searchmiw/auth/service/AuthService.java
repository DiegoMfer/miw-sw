package com.searchmiw.auth.service;

import com.searchmiw.auth.client.UserServiceClient;
import com.searchmiw.auth.config.JwtConfig;
import com.searchmiw.auth.model.AuthRequest;
import com.searchmiw.auth.model.AuthResponse;
import com.searchmiw.auth.model.RegisterRequest;
import com.searchmiw.auth.model.UserDto;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserServiceClient userServiceClient;
    private final JwtConfig jwtConfig;
    
    public ResponseEntity<AuthResponse> login(AuthRequest authRequest) {
        try {
            // Verify credentials with user service
            ResponseEntity<Boolean> verified = userServiceClient.verifyCredentials(authRequest);
            
            if (verified.getBody() != null && verified.getBody()) {
                // Get user details to include in token
                ResponseEntity<UserDto> userResponse = userServiceClient.getUserByEmail(authRequest.getEmail());
                UserDto user = userResponse.getBody();
                
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
                    
        } catch (FeignException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.builder().message("Authentication failed").build());
        }
    }
    
    public ResponseEntity<AuthResponse> register(RegisterRequest registerRequest) {
        try {
            // Create user through user service
            ResponseEntity<UserDto> userResponse = userServiceClient.createUser(registerRequest);
            UserDto user = userResponse.getBody();
            
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
                    
        } catch (FeignException.Conflict e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(AuthResponse.builder().message("Email already in use").build());
        } catch (FeignException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthResponse.builder().message("Registration failed").build());
        }
    }
    
    public boolean validateToken(String token) {
        return jwtConfig.validateToken(token);
    }
}
