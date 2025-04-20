package com.searchmiw.dataaggregator.service;

import com.searchmiw.dataaggregator.model.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class SearchService {

    private final WebClient webClient;

    public SearchService(WebClient.Builder webClientBuilder, 
                         @Value("${service.search.url}") String searchServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(searchServiceUrl).build();
    }

    public SearchResult search(String query, String language) {
        log.info("Searching for query: '{}' in language: {}", query, language);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/search")
                        .queryParam("query", query)
                        .queryParam("language", language)
                        .build())
                .retrieve()
                .bodyToMono(SearchResult.class)
                .doOnError(e -> log.error("Error performing search: {}", e.getMessage(), e))
                .block();
    }
}
