package com.searchmiw.gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
@Slf4j
public class JwtUtil {

    public Claims extractAllClaims(String token) {
        try {
            // Extract the payload part (second part) of the JWT token
            String[] chunks = token.split("\\.");
            if (chunks.length < 2) {
                throw new JwtException("Invalid JWT token format");
            }
            
            // Base64 decode the payload
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chunks[1]));
            
            // Parse the token and extract claims
            // Note: We're not verifying the signature here since Auth Service already did that
            return Jwts.parser().parseClaimsJwt("." + payload + ".").getBody();
        } catch (Exception e) {
            log.error("Error extracting claims from JWT token: {}", e.getMessage());
            return null;
        }
    }

    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        if (claims != null && claims.containsKey("userId")) {
            return claims.get("userId", Long.class);
        }
        return null;
    }

    public String extractTokenFromAuthHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
