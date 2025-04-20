package com.searchmiw.dataaggregator.service;

import com.searchmiw.dataaggregator.exception.ServiceException;
import com.searchmiw.dataaggregator.model.HistoryEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class HistoryService {

    private final WebClient webClient;

    public HistoryService(WebClient.Builder webClientBuilder, 
                          @Value("${service.history.url}") String historyServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(historyServiceUrl).build();
    }

    public List<HistoryEntry> getAllEntries() {
        log.info("Fetching all history entries");
        return webClient.get()
                .uri("/api/history")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<HistoryEntry>>() {})
                .doOnError(e -> log.error("Error fetching history entries: {}", e.getMessage(), e))
                .onErrorReturn(List.of())
                .block();
    }

    public Optional<HistoryEntry> getEntryById(Long id) {
        log.info("Fetching history entry with id: {}", id);
        return webClient.get()
                .uri("/api/history/{id}", id)
                .retrieve()
                .bodyToMono(HistoryEntry.class)
                .map(Optional::of)
                .onErrorReturn(Optional.empty())
                .block();
    }

    public List<HistoryEntry> getEntriesByUserId(Long userId) {
        log.info("Fetching history entries for user id: {}", userId);
        return webClient.get()
                .uri("/api/history/user/{userId}", userId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<HistoryEntry>>() {})
                .doOnError(e -> log.error("Error fetching history for user: {}", e.getMessage(), e))
                .onErrorReturn(List.of())
                .block();
    }

    public HistoryEntry createEntry(Long userId, String query) {
        log.info("Creating history entry for user: {} with query: '{}'", userId, query);
        try {
            return webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/history/user/{userId}")
                            .queryParam("query", query)
                            .build(userId))
                    .retrieve()
                    .bodyToMono(HistoryEntry.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Error creating history entry: {} - Status: {}", e.getMessage(), e.getStatusCode(), e);
            ErrorType errorType = e.getStatusCode().is4xxClientError() ? 
                    ErrorType.BAD_REQUEST : ErrorType.INTERNAL_ERROR;
            throw new ServiceException("Failed to create history entry: " + e.getMessage(), e, errorType);
        } catch (Exception e) {
            log.error("Unexpected error creating history entry: {}", e.getMessage(), e);
            throw new ServiceException("Unexpected error creating history entry", e, ErrorType.INTERNAL_ERROR);
        }
    }

    public void deleteEntry(Long id) {
        log.info("Deleting history entry with id: {}", id);
        try {
            webClient.delete()
                    .uri("/api/history/{id}", id)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Error deleting history entry: {} - Status: {}", e.getMessage(), e.getStatusCode(), e);
            ErrorType errorType = e.getStatusCode().is4xxClientError() ? 
                    ErrorType.BAD_REQUEST : ErrorType.INTERNAL_ERROR;
            throw new ServiceException("Failed to delete history entry: " + e.getMessage(), e, errorType);
        } catch (Exception e) {
            log.error("Unexpected error deleting history entry: {}", e.getMessage(), e);
            throw new ServiceException("Unexpected error deleting history entry", e, ErrorType.INTERNAL_ERROR);
        }
    }

    public void deleteAllEntriesForUser(Long userId) {
        log.info("Deleting all history entries for user id: {}", userId);
        try {
            webClient.delete()
                    .uri("/api/history/user/{userId}", userId)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Error clearing history for user: {} - Status: {}", e.getMessage(), e.getStatusCode(), e);
            ErrorType errorType = e.getStatusCode().is4xxClientError() ? 
                    ErrorType.BAD_REQUEST : ErrorType.INTERNAL_ERROR;
            throw new ServiceException("Failed to clear user history: " + e.getMessage(), e, errorType);
        } catch (Exception e) {
            log.error("Unexpected error clearing user history: {}", e.getMessage(), e);
            throw new ServiceException("Unexpected error clearing user history", e, ErrorType.INTERNAL_ERROR);
        }
    }
}
