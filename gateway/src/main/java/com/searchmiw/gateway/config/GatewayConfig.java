package com.searchmiw.gateway.config;

import com.searchmiw.gateway.filter.AuthenticationFilter;
import com.searchmiw.gateway.filter.HistoryRedirectFilter;
import com.searchmiw.gateway.filter.ProfileRedirectFilter;
import com.searchmiw.gateway.filter.SearchLoggingFilter;
import com.searchmiw.gateway.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;

@Configuration
public class GatewayConfig {

    @Value("${service.user.url}")
    private String userServiceUrl;

    @Value("${service.search.url}")
    private String searchServiceUrl;

    @Value("${service.history.url}")
    private String historyServiceUrl;
    
    @Value("${service.auth.url}")
    private String authServiceUrl;

    @Autowired
    private AuthenticationFilter authFilter;
    
    @Autowired
    private HistoryRedirectFilter historyRedirectFilter;
    
    @Autowired
    private ProfileRedirectFilter profileRedirectFilter;
    
    @Autowired
    private SearchLoggingFilter searchLoggingFilter;

    @Autowired
    private JwtUtil jwtUtil;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Auth Service routes - Use direct URL instead of service discovery
                .route("auth-service", r -> r.path("/api/auth/**")
                        .uri(authServiceUrl))

                // User service routes - protected by auth filter
                .route("user-service", r -> r
                        .path("/api/users/**")
                        .filters(f -> f.filter(authFilter.apply(new AuthenticationFilter.Config())))
                        .uri(userServiceUrl))
                .route("user-health", r -> r
                        .path("/api/user-health/**")
                        .filters(f -> f.rewritePath("/api/user-health(?<segment>.*)", "/api/health${segment}"))
                        .uri(userServiceUrl))

                // Search service routes - protected by auth filter and logged
                .route("search-service", r -> r
                        .path("/api/search/**")
                        .filters(f -> f
                            .filter(authFilter.apply(new AuthenticationFilter.Config()))
                            .filter(searchLoggingFilter.apply(new SearchLoggingFilter.Config()))
                        )
                        .uri(searchServiceUrl))
                .route("search-health", r -> r
                        .path("/api/search-health/**")
                        .filters(f -> f.rewritePath("/api/search-health(?<segment>.*)", "/api/health${segment}"))
                        .uri(searchServiceUrl))

                // History service routes - protected by auth filter
                // Special handling for /api/history (root path) to redirect to user-specific endpoint
                .route("history-service-root", r -> r
                        .path("/api/history")
                        .filters(f -> f
                            .filter(authFilter.apply(new AuthenticationFilter.Config()))
                            .filter(historyRedirectFilter.apply(new HistoryRedirectFilter.Config()))
                        )
                        .uri(historyServiceUrl))
                // For other history paths (like /api/history/{id} or paths with special parameters)
                .route("history-service-other", r -> r
                        .path("/api/history/**")
                        .and()
                        .path("/api/history/{segment}/**", "/api/history/user/**")  // Use specific patterns instead of lambda
                        .filters(f -> f.filter(authFilter.apply(new AuthenticationFilter.Config())))
                        .uri(historyServiceUrl))
                .route("history-health", r -> r
                        .path("/api/history-health/**")
                        .filters(f -> f.rewritePath("/api/history-health(?<segment>.*)", "/api/health${segment}"))
                        .uri(historyServiceUrl))

                // Profile route - redirects to user service with user ID from JWT
                .route("profile_route", r -> r
                        .path("/api/profile")
                        .filters(f -> f
                                .filter(profileRedirectFilter.apply(new ProfileRedirectFilter.Config())))
                        .uri(userServiceUrl))

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

                // Health check route - public access
                .route("health-check", r -> r.path("/api/health")
                        .uri("lb://gateway"))

                // Swagger UI and API docs - public access
                .route("swagger-ui", r -> r.path("/swagger-ui/**", "/api-docs/**")
                        .uri("lb://gateway"))

                .build();
    }
}
