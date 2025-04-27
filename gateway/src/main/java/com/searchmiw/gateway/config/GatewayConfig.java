package com.searchmiw.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

@Configuration
public class GatewayConfig {

    @Value("${service.user.url}")
    private String userServiceUrl;

    @Value("${service.search.url}")
    private String searchServiceUrl;

    @Value("${service.history.url}")
    private String historyServiceUrl;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // User service routes
                .route("user-service", r -> r
                        .path("/api/users/**")
                        .uri(userServiceUrl))
                .route("user-health", r -> r
                        .path("/api/user-health/**")
                        .filters(f -> f.rewritePath("/api/user-health(?<segment>.*)", "/api/health${segment}"))
                        .uri(userServiceUrl))

                // Search service routes
                .route("search-service", r -> r
                        .path("/api/search/**")
                        .uri(searchServiceUrl))
                .route("search-health", r -> r
                        .path("/api/search-health/**")
                        .filters(f -> f.rewritePath("/api/search-health(?<segment>.*)", "/api/health${segment}"))
                        .uri(searchServiceUrl))

                // History service routes
                .route("history-service", r -> r
                        .path("/api/history/**")
                        .uri(historyServiceUrl))
                .route("history-health", r -> r
                        .path("/api/history-health/**")
                        .filters(f -> f.rewritePath("/api/history-health(?<segment>.*)", "/api/health${segment}"))
                        .uri(historyServiceUrl))

                // API Documentation routes
                .route("user-api-docs", r -> r
                        .path("/user-api-docs/**")
                        .filters(f -> f.rewritePath("/user-api-docs(?<segment>.*)", "/v3/api-docs${segment}"))
                        .uri(userServiceUrl))
                .route("search-api-docs", r -> r
                        .path("/search-api-docs/**")
                        .filters(f -> f.rewritePath("/search-api-docs(?<segment>.*)", "/v3/api-docs${segment}"))
                        .uri(searchServiceUrl))
                .route("history-api-docs", r -> r
                        .path("/history-api-docs/**")
                        .filters(f -> f.rewritePath("/history-api-docs(?<segment>.*)", "/v3/api-docs${segment}"))
                        .uri(historyServiceUrl))

                .build();
    }
}
