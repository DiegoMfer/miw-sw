package com.miw.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouterConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth_route", r -> r
                        .path("/auth/**")
                        .uri("lb://auth-service"))
                .route("user_route", r -> r
                        .path("/api/users/**")
                        .uri("lb://user-service"))
                // Add more routes as needed
                .build();
    }
}
