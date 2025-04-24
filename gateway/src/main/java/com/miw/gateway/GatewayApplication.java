package com.miw.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
    
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            // Search Service route
            .route("search_service_route", r -> r.path("/api/search/**")
                .filters(f -> f.stripPrefix(1))
                .uri("http://localhost:8084"))
                
            // History Service route
            .route("history_service_route", r -> r.path("/api/history/**")
                .filters(f -> f.stripPrefix(1))
                .uri("http://localhost:8085"))
                
            // User Service route
            .route("user_service_route", r -> r.path("/api/users/**")
                .filters(f -> f.stripPrefix(1))
                .uri("http://localhost:8086"))
                
            // Aggregator Service (GraphQL) route
            .route("aggregator_service_route", r -> r.path("/graphql/**")
                .uri("http://localhost:8087"))
            
            .build();
    }
}
