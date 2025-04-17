package com.searchmiw.history.controller;

import com.searchmiw.history.dto.SearchHistoryRequest;
import com.searchmiw.history.dto.SearchHistoryResponse;
import com.searchmiw.history.model.SearchHistory;
import com.searchmiw.history.service.SearchHistoryService;
import com.searchmiw.history.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class SearchHistoryController {

    private final SearchHistoryService searchHistoryService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<SearchHistoryResponse> saveSearch(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody SearchHistoryRequest request) {
        
        // Extract token and get user ID
        String token = authHeader.replace("Bearer ", "");
        String userId = jwtUtil.extractUserId(token);
        
        // Save the search history
        SearchHistory savedSearch = searchHistoryService.saveSearch(userId, request.getQuery());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(savedSearch));
    }

    @GetMapping
    public ResponseEntity<List<SearchHistoryResponse>> getUserHistory(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false, defaultValue = "false") boolean recent) {
        
        // Extract token and get user ID
        String token = authHeader.replace("Bearer ", "");
        String userId = jwtUtil.extractUserId(token);
        
        // Get user's search history
        List<SearchHistory> history;
        if (recent) {
            history = searchHistoryService.getRecentSearchHistory(userId);
        } else {
            history = searchHistoryService.getUserSearchHistory(userId);
        }
        
        List<SearchHistoryResponse> response = history.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSearch(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        
        // Extract token and get user ID
        String token = authHeader.replace("Bearer ", "");
        String userId = jwtUtil.extractUserId(token);
        
        // Verify ownership before deletion
        searchHistoryService.getSearchById(id)
                .ifPresent(search -> {
                    if (search.getUserId().equals(userId)) {
                        searchHistoryService.deleteSearch(id);
                    }
                });
        
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearHistory(
            @RequestHeader("Authorization") String authHeader) {
        
        // Extract token and get user ID
        String token = authHeader.replace("Bearer ", "");
        String userId = jwtUtil.extractUserId(token);
        
        // Clear user's history
        searchHistoryService.clearUserHistory(userId);
        
        return ResponseEntity.noContent().build();
    }

    private SearchHistoryResponse mapToResponse(SearchHistory searchHistory) {
        return SearchHistoryResponse.builder()
                .id(searchHistory.getId())
                .userId(searchHistory.getUserId())
                .query(searchHistory.getQuery())
                .timestamp(searchHistory.getTimestamp())
                .build();
    }
}
