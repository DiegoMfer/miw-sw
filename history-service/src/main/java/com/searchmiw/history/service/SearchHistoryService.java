package com.searchmiw.history.service;

import com.searchmiw.history.model.SearchHistory;
import com.searchmiw.history.repository.SearchHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;

    public SearchHistory saveSearch(String userId, String query) {
        SearchHistory searchHistory = SearchHistory.builder()
                .userId(userId)
                .query(query)
                .timestamp(LocalDateTime.now())
                .build();
        
        return searchHistoryRepository.save(searchHistory);
    }

    public List<SearchHistory> getUserSearchHistory(String userId) {
        return searchHistoryRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    public List<SearchHistory> getRecentSearchHistory(String userId) {
        return searchHistoryRepository.findTop10ByUserIdOrderByTimestampDesc(userId);
    }

    public Optional<SearchHistory> getSearchById(Long id) {
        return searchHistoryRepository.findById(id);
    }

    public void deleteSearch(Long id) {
        searchHistoryRepository.deleteById(id);
    }

    public void clearUserHistory(String userId) {
        List<SearchHistory> userHistory = searchHistoryRepository.findByUserIdOrderByTimestampDesc(userId);
        searchHistoryRepository.deleteAll(userHistory);
    }
}
