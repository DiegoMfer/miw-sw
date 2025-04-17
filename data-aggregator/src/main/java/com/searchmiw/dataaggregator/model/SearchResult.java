package com.searchmiw.dataaggregator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult {
    private String query;
    private List<SearchResultItem> results;
    private long totalResults;
    private long searchTime;
}
