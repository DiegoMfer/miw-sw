package com.miw.auth.service;

import com.miw.auth.dto.LoginRequest;
import com.miw.auth.dto.LoginResponse;
import com.miw.auth.dto.RegisterRequest;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
import javax.crypto.SecretKey;

@Service
public class AuthService {

    @Value("${jwt.secret:yourSuperSecretKeyThatShouldBeAtLeast256BitsInProduction}")
    private String jwtSecret;
    
    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    public LoginResponse login(LoginRequest request) {
        // In a real app, you'd validate credentials against a database
        // For now, we'll just generate a token
        return new LoginResponse(generateToken(request.getUsername()), request.getUsername());
    }

    public LoginResponse register(RegisterRequest request) {
        // In a real app, you'd create a user in the database
        // For now, we'll just generate a token
        return new LoginResponse(generateToken(request.getUsername()), request.getUsername());
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
