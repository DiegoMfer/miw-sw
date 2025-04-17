package com.searchmiw.search.service;

import com.searchmiw.search.model.SearchResult;
import com.searchmiw.search.model.SearchResultItem;
import com.searchmiw.search.model.WikidataResponse;
import com.searchmiw.search.model.WikidataSearchEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SearchService {

    private final WebClient webClient;
    
    @Value("${wikidata.api.url}")
    private String wikidataApiUrl;

    public SearchService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Cacheable(value = "wikidata-searches", key = "#query")
    public SearchResult search(String query, String language) {
        log.info("Searching Wikidata for query: {}", query);
        
        Instant startTime = Instant.now();
        
        String url = UriComponentsBuilder.fromHttpUrl(wikidataApiUrl)
                .queryParam("action", "wbsearchentities")
                .queryParam("search", query)
                .queryParam("language", language)
                .queryParam("format", "json")
                .queryParam("limit", 10)
                .build()
                .toUriString();
        
        try {
            WikidataResponse response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(WikidataResponse.class)
                    .block();
            
            long searchTime = Duration.between(startTime, Instant.now()).toMillis();
            
            if (response != null && response.getSearch() != null) {
                List<SearchResultItem> results = response.getSearch().stream()
                        .map(this::convertToSearchResultItem)
                        .collect(Collectors.toList());
                
                return SearchResult.builder()
                        .query(query)
                        .results(results)
                        .totalResults(results.size())
                        .searchTime(searchTime)
                        .build();
            } else {
                return createEmptyResult(query, searchTime);
            }
        } catch (Exception e) {
            log.error("Error searching Wikidata: {}", e.getMessage(), e);
            long searchTime = Duration.between(startTime, Instant.now()).toMillis();
            return createEmptyResult(query, searchTime);
        }
    }

    private SearchResultItem convertToSearchResultItem(WikidataSearchEntity entity) {
        return SearchResultItem.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .url("https://www.wikidata.org/wiki/" + entity.getId())
                .build();
    }
    
    private SearchResult createEmptyResult(String query, long searchTime) {
        return SearchResult.builder()
                .query(query)
                .results(Collections.emptyList())
                .totalResults(0)
                .searchTime(searchTime)
                .build();
    }
}
