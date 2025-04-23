package com.searchmiw.aggregator.service;

import com.searchmiw.aggregator.dto.HistoryEntryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final WebClient historyServiceWebClient;

    public Flux<HistoryEntryDto> getAllHistoryEntries() {
        return historyServiceWebClient.get()
                .uri("/api/history")
                .retrieve()
                .bodyToFlux(HistoryEntryDto.class);
    }

    public Mono<HistoryEntryDto> getHistoryEntryById(Long id) {
        return historyServiceWebClient.get()
                .uri("/api/history/{id}", id)
                .retrieve()
                .bodyToMono(HistoryEntryDto.class);
    }

    public Flux<HistoryEntryDto> getHistoryEntriesByUserId(Long userId) {
        return historyServiceWebClient.get()
                .uri("/api/history/user/{userId}", userId)
                .retrieve()
                .bodyToFlux(HistoryEntryDto.class);
    }
}
