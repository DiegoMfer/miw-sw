package com.searchmiw.dataaggregator.service;

import com.searchmiw.dataaggregator.model.SearchHistory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class HistoryService {

    private final WebClient webClient;

    public HistoryService(WebClient.Builder webClientBuilder, 
                         @Value("${services.history.url}") String historyServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(historyServiceUrl).build();
    }

    public Flux<SearchHistory> getUserSearchHistory(String token, Integer limit) {
        String url = limit != null ? "/api/history?recent=true" : "/api/history";
        
        return webClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToFlux(SearchHistory.class);
    }

    public Mono<SearchHistory> saveSearch(String token, String query) {
        return webClient.post()
                .uri("/api/history")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(Map.of("query", query))
                .retrieve()
                .bodyToMono(SearchHistory.class);
    }

    public Mono<Void> deleteSearch(String token, Long id) {
        return webClient.delete()
                .uri("/api/history/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<Void> clearHistory(String token) {
        return webClient.delete()
                .uri("/api/history")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(Void.class);
    }
}
