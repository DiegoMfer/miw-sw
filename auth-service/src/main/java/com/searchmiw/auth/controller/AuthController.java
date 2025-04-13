package com.searchmiw.auth.controller;

import com.searchmiw.auth.dto.LoginRequest;
import com.searchmiw.auth.dto.LoginResponse;
import com.searchmiw.auth.dto.RegisterRequest;
import com.searchmiw.auth.dto.RegisterResponse;
import com.searchmiw.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        String token = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.ok(new LoginResponse(token));
    }
    
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            String token = authService.register(
                registerRequest.getEmail(), 
                registerRequest.getPassword(),
                registerRequest.getName()
            );
            return ResponseEntity.ok(new RegisterResponse(token, registerRequest.getEmail()));
        } catch (IllegalArgumentException e) {
            // Return a more descriptive error for client
            return ResponseEntity.badRequest()
                .body(new RegisterResponse(null, registerRequest.getEmail()));
        }
    }
    
    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        authService.validateToken(token);
        return ResponseEntity.ok().build();
    }
}
