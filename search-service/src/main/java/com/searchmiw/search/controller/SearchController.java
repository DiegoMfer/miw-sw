package com.searchmiw.search.controller;

import com.searchmiw.search.model.SearchResult;
import com.searchmiw.search.service.SearchService;
import com.searchmiw.search.util.JwtUtil;
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
    private final JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<SearchResult> search(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "en") String language) {
        
        log.info("Received search request for query: {}", query);
        
        // Validate token
        String token = authHeader.replace("Bearer ", "");
        if (!jwtUtil.validateToken(token)) {
            log.warn("Invalid token received in search request");
            return ResponseEntity.status(401).build();
        }
        
        // Perform search
        SearchResult result = searchService.search(query, language);
        log.info("Returning search results: {} results found", 
                result.getResults() != null ? result.getResults().size() : 0);
        return ResponseEntity.ok(result);
    }
}
