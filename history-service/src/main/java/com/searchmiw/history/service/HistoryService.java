package com.searchmiw.history.service;

import com.searchmiw.history.model.HistoryEntry;
import com.searchmiw.history.repository.HistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HistoryService {
    
    private final HistoryRepository historyRepository;
    
    public List<HistoryEntry> getAllEntries() {
        return historyRepository.findAll();
    }

    public Page<HistoryEntry> getAllEntries(Pageable pageable) {
        return historyRepository.findAll(pageable);
    }
    
    public Optional<HistoryEntry> getEntryById(Long id) {
        return historyRepository.findById(id);
    }
    
    public List<HistoryEntry> getEntriesByUserId(Long userId) {
        return historyRepository.findByUserIdOrderByTimestampDesc(userId);
    }
    
    public Page<HistoryEntry> getEntriesByUserId(Long userId, Pageable pageable) {
        return historyRepository.findByUserIdOrderByTimestampDesc(userId, pageable);
    }
    
    public HistoryEntry saveEntry(HistoryEntry entry) {
        // The timestamp is set automatically by @PrePersist
        return historyRepository.save(entry);
    }
    
    public HistoryEntry createEntry(Long userId, String query) {
        HistoryEntry entry = new HistoryEntry();
        entry.setUserId(userId);
        entry.setQuery(query);
        return historyRepository.save(entry);
    }
    
    public void deleteEntry(Long id) {
        historyRepository.deleteById(id);
    }
    
    @Transactional
    public void deleteAllEntriesByUserId(Long userId) {
        historyRepository.deleteByUserId(userId);
    }
}
