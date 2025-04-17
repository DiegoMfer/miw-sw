package com.searchmiw.dataaggregator.service;

import com.searchmiw.dataaggregator.model.SearchResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class SearchService {

    private final WebClient webClient;

    public SearchService(WebClient.Builder webClientBuilder, 
                       @Value("${services.search.url}") String searchServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(searchServiceUrl).build();
    }

    public Mono<SearchResult> search(String token, String query, String language) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/search")
                        .queryParam("query", query)
                        .queryParam("language", language != null ? language : "en")
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(SearchResult.class);
    }
}
