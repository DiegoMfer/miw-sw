package com.searchmiw.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SecurityException;
import javax.crypto.SecretKey;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {
    
    private final SecretKey secretKey;
    
    public JwtAuthenticationFilter(SecretKey secretKey) {
        super(Config.class);
        this.secretKey = secretKey;
    }
    
    @Override
    public GatewayFilter apply(Config config) {
        return new OrderedGatewayFilter((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            
            // Skip auth for login and public endpoints
            if (this.isPublicPath(request.getPath().toString())) {
                return chain.filter(exchange);
            }
            
            // Check for Authorization header
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return this.onError(exchange, HttpStatus.UNAUTHORIZED);
            }
            
            // Extract and validate JWT
            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            String jwt = null;
            
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7);
            } else {
                return this.onError(exchange, HttpStatus.UNAUTHORIZED);
            }
            
            try {
                // Validate token - Use setSigningKey instead of verifyWith
                Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();
                
                // Add user info to headers for downstream services
                ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", claims.getSubject())
                    .build();
                
                return chain.filter(exchange.mutate().request(mutatedRequest).build());
                
            } catch (SecurityException | MalformedJwtException e) {
                return this.onError(exchange, HttpStatus.UNAUTHORIZED, "Invalid JWT signature");
            } catch (ExpiredJwtException e) {
                return this.onError(exchange, HttpStatus.UNAUTHORIZED, "JWT token has expired");
            } catch (UnsupportedJwtException e) {
                return this.onError(exchange, HttpStatus.UNAUTHORIZED, "JWT token format is not supported");
            } catch (Exception e) {
                return this.onError(exchange, HttpStatus.UNAUTHORIZED, "JWT validation error: " + e.getMessage());
            }
        }, 1);
    }
    
    private boolean isPublicPath(String path) {
        return path.contains("/auth/login") || 
               path.contains("/auth/register") || 
               path.contains("/auth/validate");
    }
    
    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status) {
        return this.onError(exchange, status, null);
    }
    
    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }
    
    public static class Config {
        // Add any configuration properties if needed
    }
}
