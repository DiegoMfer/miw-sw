package com.searchmiw.history.repository;

import com.searchmiw.history.model.HistoryEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<HistoryEntry, Long> {
    List<HistoryEntry> findByUserId(Long userId);
    List<HistoryEntry> findByUserIdOrderByTimestampDesc(Long userId);
    Page<HistoryEntry> findByUserId(Long userId, Pageable pageable);
    Page<HistoryEntry> findByUserIdOrderByTimestampDesc(Long userId, Pageable pageable);
    void deleteByUserId(Long userId);
}
