package com.searchmiw.gateway.filter;

import com.searchmiw.gateway.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class ProfileRedirectFilter extends AbstractGatewayFilterFactory<ProfileRedirectFilter.Config> {

    private final JwtUtil jwtUtil;

    public ProfileRedirectFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            
            if (authHeader != null) {
                String token = jwtUtil.extractTokenFromAuthHeader(authHeader);
                Long userId = jwtUtil.extractUserId(token);
                
                if (userId != null) {
                    // Build the new URI to redirect to the user service endpoint
                    UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/api/users/" + userId);
                    
                    // Create a new request with the modified URI
                    ServerHttpRequest modifiedRequest = request.mutate()
                        .path(builder.build().getPath())
                        .uri(builder.build().toUri())
                        .build();
                    
                    // Replace the request in the exchange
                    return chain.filter(exchange.mutate().request(modifiedRequest).build());
                }
            }
            
            // If we couldn't extract userId or there's no auth header, just continue with original request
            return chain.filter(exchange);
        };
    }

    public static class Config {
        // Configuration properties if needed
    }
}
