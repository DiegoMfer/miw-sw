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
public class SearchHistory {
    private Long id;
    private String userId;
    private String query;
    private LocalDateTime timestamp;
}
