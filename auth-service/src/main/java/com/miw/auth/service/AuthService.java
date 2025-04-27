package com.miw.auth.service;

import com.miw.auth.client.UserServiceClient;
import com.miw.auth.dto.*;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import javax.crypto.SecretKey;

@Service
public class AuthService {

    @Value("${jwt.secret:yourSuperSecretKeyThatShouldBeAtLeast256BitsInProduction}")
    private String jwtSecret;
    
    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;
    
    private final UserServiceClient userServiceClient;
    
    public AuthService(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    public LoginResponse login(LoginRequest request) {
        // Verify credentials with user-service
        UserCredentials credentials = new UserCredentials(request.getUsername(), request.getPassword());
        
        if (!userServiceClient.verifyCredentials(credentials)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        
        // Get user details
        UserDTO user = userServiceClient.getUserByEmail(request.getUsername());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }
        
        // Generate token
        String token = generateToken(user.getEmail());
        return new LoginResponse(token, user.getName());
    }

    public LoginResponse register(RegisterRequest request) {
        // Check if user already exists
        UserDTO existingUser = userServiceClient.getUserByEmail(request.getEmail());
        if (existingUser != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }
        
        // Create new user in user-service
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO();
        registrationDTO.setName(request.getName());
        registrationDTO.setEmail(request.getEmail());
        registrationDTO.setPassword(request.getPassword());
        
        UserDTO createdUser = userServiceClient.createUser(registrationDTO);
        
        // Generate token for the new user
        String token = generateToken(createdUser.getEmail());
        return new LoginResponse(token, createdUser.getName());
    }

    private String generateToken(String username) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(key)
                .compact();
    }
}
