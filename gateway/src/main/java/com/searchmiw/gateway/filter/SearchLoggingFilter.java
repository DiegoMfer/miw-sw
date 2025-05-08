package com.searchmiw.gateway.filter;

import com.searchmiw.gateway.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SearchLoggingFilter extends AbstractGatewayFilterFactory<SearchLoggingFilter.Config> {

    private final JwtUtil jwtUtil;

    public SearchLoggingFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            
            // Extract user ID from token
            Long userId = null;
            if (authHeader != null) {
                String token = jwtUtil.extractTokenFromAuthHeader(authHeader);
                if (token != null) {
                    userId = jwtUtil.extractUserId(token);
                }
            }
            
            // Extract search query
            String query = request.getQueryParams().getFirst("query");
            String language = request.getQueryParams().getFirst("language");
            
            // Log search request with user ID
            if (query != null && !query.isEmpty()) {
                log.info("Search request - Query: '{}', Language: '{}', User ID: {}", 
                        query, language, userId != null ? userId : "anonymous");
            }
            
            return chain.filter(exchange);
        };
    }

    public static class Config {
        // Configuration properties if needed
    }
}
