package com.searchmiw.history.repository;

import com.searchmiw.history.model.HistoryEntry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class HistoryRepositoryTest {

    @Autowired
    private HistoryRepository historyRepository;

    @Test
    void findByUserId_ShouldReturnUserEntries() {
        // Given
        HistoryEntry entry1 = new HistoryEntry();
        entry1.setUserId(101L);
        entry1.setQuery("test query 1");

        HistoryEntry entry2 = new HistoryEntry();
        entry2.setUserId(101L);
        entry2.setQuery("test query 2");

        HistoryEntry entry3 = new HistoryEntry();
        entry3.setUserId(102L);
        entry3.setQuery("different user query");

        historyRepository.saveAll(List.of(entry1, entry2, entry3));

        // When
        List<HistoryEntry> userEntries = historyRepository.findByUserId(101L);

        // Then
        assertThat(userEntries).hasSize(2);
        assertThat(userEntries.stream().map(HistoryEntry::getUserId))
                .containsOnly(101L);
    }

    @Test
    void findByUserIdOrderByTimestampDesc_ShouldReturnSortedEntries() throws InterruptedException {
        // Given
        HistoryEntry olderEntry = new HistoryEntry();
        olderEntry.setUserId(101L);
        olderEntry.setQuery("older query");
        historyRepository.save(olderEntry);

        // Small delay to ensure different timestamps
        Thread.sleep(100);

        HistoryEntry newerEntry = new HistoryEntry();
        newerEntry.setUserId(101L);
        newerEntry.setQuery("newer query");
        historyRepository.save(newerEntry);

        // When
        List<HistoryEntry> sortedEntries = historyRepository.findByUserIdOrderByTimestampDesc(101L);

        // Then
        assertThat(sortedEntries).hasSize(2);
        assertThat(sortedEntries.get(0).getQuery()).isEqualTo("newer query");
        assertThat(sortedEntries.get(1).getQuery()).isEqualTo("older query");
    }

    @Test
    void deleteByUserId_ShouldRemoveUserEntries() {
        // Given
        HistoryEntry entry1 = new HistoryEntry();
        entry1.setUserId(101L);
        entry1.setQuery("test query 1");

        HistoryEntry entry2 = new HistoryEntry();
        entry2.setUserId(101L);
        entry2.setQuery("test query 2");

        HistoryEntry entry3 = new HistoryEntry();
        entry3.setUserId(102L);
        entry3.setQuery("different user query");

        historyRepository.saveAll(List.of(entry1, entry2, entry3));

        // When
        historyRepository.deleteByUserId(101L);

        // Then
        assertThat(historyRepository.findByUserId(101L)).isEmpty();
        assertThat(historyRepository.findByUserId(102L)).hasSize(1);
    }
}
