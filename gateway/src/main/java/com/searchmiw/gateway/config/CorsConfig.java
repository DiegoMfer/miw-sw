package com.searchmiw.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        
        // Allow frontend origins
        corsConfig.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",   // Development for search-miw-client
            "http://127.0.0.1:3000",   // Alternative local address
            "http://localhost:3001",   // Development for data-panel-client
            "https://your-production-domain.com" // Add your production domain when ready
        ));
        
        // Allow all common HTTP methods
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));
        
        // Allow all headers
        corsConfig.setAllowedHeaders(Collections.singletonList("*"));
        
        // Allow cookies and authorization headers
        corsConfig.setAllowCredentials(true);
        
        // Expose headers that frontend might need
        corsConfig.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        
        // Apply this configuration to all routes
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        
        return new CorsWebFilter(source);
    }
}
