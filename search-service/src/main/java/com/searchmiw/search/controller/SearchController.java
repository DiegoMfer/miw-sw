package com.searchmiw.search.controller;

import com.searchmiw.search.model.SearchResult;
import com.searchmiw.search.service.HistoryService;
import com.searchmiw.search.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Slf4j
public class SearchController {

    private final SearchService searchService;
    private final HistoryService historyService;

    @Operation(
        summary = "Search entities in Wikidata",
        description = "Search for entities in Wikidata based on a query string"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successful search operation",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SearchResult.class)
            )
        )
    })
    @GetMapping
    public ResponseEntity<SearchResult> search(
            @Parameter(
                description = "Search query term",
                required = true,
                example = "Albert Einstein"
            )
            @RequestParam String query,
            @Parameter(
                description = "Language code for results",
                required = false,
                example = "en"
            )
            @RequestParam(required = false, defaultValue = "en") String language,
            
            @Parameter(
                description = "User identifier for tracking searches",
                required = false,
                example = "12345"
            )
            @RequestParam(required = false) Long userId) {
        
        log.info("Received search request for query: {}", query);
        
        // Perform search
        SearchResult result = searchService.search(query, language);
        
        // Record search history if userId is provided
        if (userId != null) {
            historyService.recordSearchHistory(userId, query);
        }
        
        log.info("Returning search results: {} results found", 
                result.getResults() != null ? result.getResults().size() : 0);
        return ResponseEntity.ok(result);
    }
}
