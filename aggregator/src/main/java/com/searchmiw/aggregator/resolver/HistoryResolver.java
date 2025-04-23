package com.searchmiw.aggregator.resolver;

import com.searchmiw.aggregator.dto.HistoryEntryDto;
import com.searchmiw.aggregator.service.HistoryService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Controller
public class HistoryResolver {

    private final HistoryService historyService;

    public HistoryResolver(HistoryService historyService) {
        this.historyService = historyService;
    }

    @QueryMapping
    public Flux<HistoryEntryDto> historyEntries() {
        return historyService.getAllHistoryEntries();
    }

    @QueryMapping
    public Mono<HistoryEntryDto> historyEntry(@Argument Long id) {
        return historyService.getHistoryEntryById(id);
    }

    @QueryMapping
    public Flux<HistoryEntryDto> historyEntriesByUser(@Argument Long userId) {
        return historyService.getHistoryEntriesByUserId(userId);
    }
}
