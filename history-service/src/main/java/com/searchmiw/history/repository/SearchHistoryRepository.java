package com.searchmiw.history.repository;

import com.searchmiw.history.model.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    List<SearchHistory> findByUserIdOrderByTimestampDesc(String userId);
    List<SearchHistory> findTop10ByUserIdOrderByTimestampDesc(String userId);
}
