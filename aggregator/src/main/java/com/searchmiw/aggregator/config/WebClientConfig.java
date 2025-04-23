package com.searchmiw.aggregator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient userServiceWebClient(@Value("${services.user.url}") String userServiceUrl) {
        return WebClient.builder()
                .baseUrl(userServiceUrl)
                .build();
    }
    
    @Bean
    public WebClient historyServiceWebClient(@Value("${service.history.url}") String historyServiceUrl) {
        return WebClient.builder()
                .baseUrl(historyServiceUrl)
                .build();
    }
}
