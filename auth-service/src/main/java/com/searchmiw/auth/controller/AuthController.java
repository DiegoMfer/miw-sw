package com.searchmiw.auth.controller;

import com.searchmiw.auth.model.AuthRequest;
import com.searchmiw.auth.model.AuthResponse;
import com.searchmiw.auth.model.RegisterRequest;
import com.searchmiw.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication API for user registration, login, and token validation")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    @Operation(
        summary = "Authenticate user and get JWT token",
        description = "Authenticate with email and password to receive a valid JWT token"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Authentication successful", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content)
    })
    public Mono<ResponseEntity<AuthResponse>> login(@Valid @RequestBody AuthRequest authRequest) {
        return authService.login(authRequest);
    }
    
    @PostMapping("/register")
    @Operation(
        summary = "Register a new user", 
        description = "Create a new user account with name, email and password"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User registered successfully", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
        @ApiResponse(responseCode = "409", description = "Email already in use", content = @Content)
    })
    public Mono<ResponseEntity<AuthResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }
    
    @GetMapping("/validate")
    @Operation(
        summary = "Validate JWT token", 
        description = "Check if a JWT token is valid and not expired."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token validation result", content = @Content(schema = @Schema(implementation = Boolean.class))),
        @ApiResponse(responseCode = "401", description = "Invalid or expired token", content = @Content)
    })
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String authHeader) {
        // Extract token from Bearer format
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            boolean valid = authService.validateToken(token);
            return ResponseEntity.ok(valid);
        }
        return ResponseEntity.ok(false);
    }
}
