package com.searchmiw.search.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class HistoryService {

    private final WebClient webClient;

    public HistoryService(@Value("${services.history.url:http://history-service:8085}") String historyServiceUrl) {
        this.webClient = createWebClient(historyServiceUrl);
    }
    
    // Protected method for easier testing via override
    protected WebClient createWebClient(String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public void recordSearchHistory(Long userId, String query) {
        if (userId == null) {
            return; // Don't record if no user ID provided
        }

        log.info("Recording search history for user: {}, query: {}", userId, query);
        webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/history/user/{userId}")
                        .queryParam("query", query)
                        .build(userId))
                .retrieve()
                .toBodilessEntity()
                .subscribe(
                    response -> log.info("Successfully recorded search history"),
                    error -> log.error("Error recording search history: {}", error.getMessage())
                );
    }
}
