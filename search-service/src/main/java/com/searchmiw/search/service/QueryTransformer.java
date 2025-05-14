package com.searchmiw.search.service;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class QueryTransformer {

    /**
     * Transforms a query to potentially improve search results.
     * Implements multiple strategies for query transformation.
     *
     * @param query The original query that returned no results
     * @param attempt The current transformation attempt (1-based)
     * @return A transformed query
     */
    public String transformQuery(String query, int attempt) {
        if (query == null || query.trim().isEmpty()) {
            return query;
        }

        log.info("Transforming query: '{}', attempt: {}", query, attempt);
        
        switch (attempt) {
            case 1:
                // First attempt: Remove special characters and normalize spaces
                return removeSpecialCharsAndNormalize(query);
            case 2:
                // Second attempt: Extract keywords (remove common words)
                return extractKeywords(query);
            case 3:
                // Third attempt: Reverse word order
                return reverseWordOrder(query);
            default:
                return query;
        }
    }
    
    private String removeSpecialCharsAndNormalize(String query) {
        String transformed = query.replaceAll("[^a-zA-Z0-9\\s]", " ")
                                 .replaceAll("\\s+", " ")
                                 .trim();
        log.debug("Removed special chars: '{}' -> '{}'", query, transformed);
        return transformed;
    }
    
    private String extractKeywords(String query) {
        List<String> stopWords = Arrays.asList("the", "a", "an", "and", "or", "but", "of", "on", "in", "with", 
                                              "for", "to", "from", "by", "about", "as", "into", "like", "through");
        
        List<String> words = Arrays.asList(query.toLowerCase().split("\\s+"));
        
        String keywords = words.stream()
                              .filter(word -> !stopWords.contains(word) && word.length() > 1)
                              .collect(Collectors.joining(" "));
        
        log.debug("Extracted keywords: '{}' -> '{}'", query, keywords);
        return keywords.isEmpty() ? query : keywords;
    }
    
    private String reverseWordOrder(String query) {
        String[] words = query.split("\\s+");
        StringBuilder reversed = new StringBuilder();
        
        for (int i = words.length - 1; i >= 0; i--) {
            reversed.append(words[i]).append(" ");
        }
        
        String result = reversed.toString().trim();
        log.debug("Reversed word order: '{}' -> '{}'", query, result);
        return result;
    }
}
