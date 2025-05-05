package com.searchmiw.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Predicate;

@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final WebClient.Builder webClientBuilder;

    @Value("${auth.service.url}")
    private String authServiceUrl;

    @Value("${auth.service.validate-endpoint:/api/auth/validate}")
    private String validateEndpoint;

    @Value("#{'${auth.public.paths:/api/auth/login,/api/auth/register,/api/health}'.split(',')}")
    private List<String> publicPaths;

    public AuthenticationFilter(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            
            // Special handling for search endpoint - allow without auth but extract userId if available
            if (path.startsWith("/api/search") && exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    // Validate token if present, but don't block if invalid
                    return webClientBuilder.build()
                            .get()
                            .uri(authServiceUrl + validateEndpoint)
                            .header(HttpHeaders.AUTHORIZATION, authHeader)
                            .retrieve()
                            .bodyToMono(Boolean.class)
                            .flatMap(isValid -> {
                                // Continue regardless of token validity for search endpoint
                                return chain.filter(exchange);
                            })
                            .onErrorResume(error -> {
                                // Just log the error and continue for search endpoint
                                log.warn("Invalid token for search but continuing: {}", error.getMessage());
                                return chain.filter(exchange);
                            });
                }
            }

            // Skip validation for public paths
            if (isPublicPath(path)) {
                log.debug("Allowing public access to path: {}", path);
                return chain.filter(exchange);
            }

            // Check if Authorization header exists
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                log.warn("Missing Authorization header for path: {}", path);
                return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing Authorization header"));
            }

            // Get the Authorization header (Bearer token)
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Invalid Authorization header format for path: {}", path);
                return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Authorization header format"));
            }

            // Validate token with Auth Service
            return webClientBuilder.build()
                    .get()
                    .uri(authServiceUrl + validateEndpoint)
                    .header(HttpHeaders.AUTHORIZATION, authHeader)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .flatMap(isValid -> {
                        if (Boolean.TRUE.equals(isValid)) {
                            log.debug("Token validation successful for path: {}", path);
                            return chain.filter(exchange);
                        } else {
                            log.warn("Invalid token for path: {}", path);
                            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token"));
                        }
                    })
                    .onErrorResume(error -> {
                        log.error("Error validating token: {}", error.getMessage());
                        return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed"));
                    });
        };
    }

    private boolean isPublicPath(String path) {
        return publicPaths.stream().anyMatch(path::startsWith);
    }

    public static class Config {
        // Configuration properties if needed
    }
}
