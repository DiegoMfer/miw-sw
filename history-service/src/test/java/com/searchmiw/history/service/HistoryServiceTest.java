package com.searchmiw.history.service;

import com.searchmiw.history.model.HistoryEntry;
import com.searchmiw.history.repository.HistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoryServiceTest {

    @Mock
    private HistoryRepository historyRepository;

    @InjectMocks
    private HistoryService historyService;

    private HistoryEntry testEntry1;
    private HistoryEntry testEntry2;

    @BeforeEach
    void setUp() {
        testEntry1 = new HistoryEntry(1L, 101L, "test query 1", LocalDateTime.now());
        testEntry2 = new HistoryEntry(2L, 101L, "test query 2", LocalDateTime.now().minusHours(1));
    }

    @Test
    void getAllEntries_ShouldReturnAllEntries() {
        // Given
        when(historyRepository.findAll()).thenReturn(Arrays.asList(testEntry1, testEntry2));

        // When
        List<HistoryEntry> result = historyService.getAllEntries();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).contains(testEntry1, testEntry2);
    }

    @Test
    void getEntryById_WithValidId_ShouldReturnEntry() {
        // Given
        when(historyRepository.findById(1L)).thenReturn(Optional.of(testEntry1));

        // When
        Optional<HistoryEntry> result = historyService.getEntryById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testEntry1);
    }

    @Test
    void getEntriesByUserId_ShouldReturnUserEntries() {
        // Given
        when(historyRepository.findByUserIdOrderByTimestampDesc(101L))
                .thenReturn(Arrays.asList(testEntry1, testEntry2));

        // When
        List<HistoryEntry> result = historyService.getEntriesByUserId(101L);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(testEntry1, testEntry2);
    }

    @Test
    void createEntry_ShouldSaveAndReturnEntry() {
        // Given
        HistoryEntry inputEntry = new HistoryEntry();
        inputEntry.setUserId(101L);
        inputEntry.setQuery("new query");

        HistoryEntry savedEntry = new HistoryEntry(3L, 101L, "new query", LocalDateTime.now());
        
        when(historyRepository.save(any(HistoryEntry.class))).thenReturn(savedEntry);

        // When
        HistoryEntry result = historyService.createEntry(101L, "new query");

        // Then
        assertThat(result).isEqualTo(savedEntry);
        assertThat(result.getUserId()).isEqualTo(101L);
        assertThat(result.getQuery()).isEqualTo("new query");
        verify(historyRepository, times(1)).save(any(HistoryEntry.class));
    }

    @Test
    void deleteEntry_ShouldCallRepository() {
        // When
        historyService.deleteEntry(1L);

        // Then
        verify(historyRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteAllEntriesByUserId_ShouldCallRepository() {
        // When
        historyService.deleteAllEntriesByUserId(101L);

        // Then
        verify(historyRepository, times(1)).deleteByUserId(101L);
    }
}
