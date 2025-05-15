package com.searchmiw.search.service;

import com.searchmiw.search.model.SearchResult;
import com.searchmiw.search.model.SearchResultItem;
import com.searchmiw.search.model.WikidataResponse;
import com.searchmiw.search.model.WikidataSearchEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SearchService {

    private final WebClient.Builder webClientBuilder;
    private WebClient webClient;
    
    @Value("${wikidata.api.url}")
    private String wikidataApiUrl;

    @Autowired
    public SearchService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
        // Initialize lazily to avoid issues in tests
    }
    
    private WebClient getWebClient() {
        if (webClient == null) {
            webClient = webClientBuilder.build();
        }
        return webClient;
    }

    @Cacheable(value = "wikidata-searches", key = "#query + '-' + #language")
    public SearchResult search(String query, String language) {
        log.info("Searching Wikidata for query: {} in language: {}", query, language);

        Instant startTime = Instant.now();

        String[] words = query.trim().split("\\s+");
        Map<String, SearchResultItem> uniqueResults = new LinkedHashMap<>();
        final long[] totalSearchTime = {0}; // Use array to allow mutation in lambda

        java.util.function.Consumer<String> doSearch = q -> {
            String url = UriComponentsBuilder.fromHttpUrl(wikidataApiUrl)
                    .queryParam("action", "wbsearchentities")
                    .queryParam("search", q)
                    .queryParam("language", language)
                    .queryParam("uselang", language)
                    .queryParam("format", "json")
                    .queryParam("limit", 100)
                    .build()
                    .toUriString();
            Instant partStart = Instant.now();
            try {
                WikidataResponse response = getWebClient().get()
                        .uri(url)
                        .retrieve()
                        .bodyToMono(WikidataResponse.class)
                        .block();
                long partTime = Duration.between(partStart, Instant.now()).toMillis();
                totalSearchTime[0] += partTime;
                if (response != null && response.getSearch() != null) {
                    response.getSearch().forEach(entity -> {
                        SearchResultItem item = convertToSearchResultItem(entity);
                        uniqueResults.putIfAbsent(item.getId(), item);
                    });
                }
            } catch (Exception e) {
                log.error("Error searching Wikidata for '{}': {}", q, e.getMessage());
                long partTime = Duration.between(partStart, Instant.now()).toMillis();
                totalSearchTime[0] += partTime;
            }
        };

        doSearch.accept(query);

        if (words.length > 1) {
            for (String word : words) {
                if (!word.equalsIgnoreCase(query)) {
                    doSearch.accept(word);
                }
            }
        }

        long elapsed = Duration.between(startTime, Instant.now()).toMillis();
        long searchTime = totalSearchTime[0] > 0 ? totalSearchTime[0] : elapsed;

        return SearchResult.builder()
                .query(query)
                .results(new java.util.ArrayList<>(uniqueResults.values()))
                .totalResults(uniqueResults.size())
                .searchTime(searchTime)
                .build();
    }

    private SearchResultItem convertToSearchResultItem(WikidataSearchEntity entity) {
        String url;
        
        if (entity.getUrl() != null) {
            // Handle URLs that already include the domain (starting with //www.wikidata.org)
            if (entity.getUrl().startsWith("//www.wikidata.org")) {
                url = "https:" + entity.getUrl();
            } else if (entity.getUrl().startsWith("/")) {
                // Handles relative URLs that start with a slash
                url = "https://www.wikidata.org" + entity.getUrl();
            } else {
                // Fallback for any other URL format
                url = "https://www.wikidata.org/wiki/" + entity.getId();
            }
        } else {
            // If URL is null, construct it from the ID
            url = "https://www.wikidata.org/wiki/" + entity.getId();
        }
        
        return SearchResultItem.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .url(url)
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
