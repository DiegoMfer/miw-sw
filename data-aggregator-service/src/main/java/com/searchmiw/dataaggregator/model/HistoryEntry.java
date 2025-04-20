package com.searchmiw.dataaggregator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryEntry {
    private Long id;
    private Long userId;
    private String query;
    private LocalDateTime timestamp;
    
    // For GraphQL resolvers
    private User user;
    private SearchResult searchResult;
}
