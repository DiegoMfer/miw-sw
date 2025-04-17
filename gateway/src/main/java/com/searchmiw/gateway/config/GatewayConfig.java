package com.searchmiw.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Value("${AUTH_SERVICE_URL:http://auth:8081}")
    private String authServiceUrl;

    @Value("${DATA_AGGREGATOR_URL:http://data-aggregator:8083}")
    private String dataAggregatorUrl;
    
    @Value("${USER_SERVICE_URL:http://user:8084}")
    private String userServiceUrl;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Auth Service Routes
                .route("auth-service", r -> r.path("/auth/**")
                        .uri(authServiceUrl))
                
                // User Service Routes
                .route("user-service", r -> r.path("/api/users/**")
                        .uri(userServiceUrl))
                
                // Data Aggregator GraphQL Routes
                .route("data-aggregator-graphql", r -> r.path("/graphql/**")
                        .uri(dataAggregatorUrl))
                .route("data-aggregator-graphiql", r -> r.path("/graphiql")
                        .uri(dataAggregatorUrl))
                
                .build();
    }
}
