package com.searchmiw.history.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.searchmiw.history.model.HistoryEntry;
import com.searchmiw.history.service.HistoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HistoryController.class)
class HistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HistoryService historyService;

    @Test
    void getAllEntries_ShouldReturnListOfEntries() throws Exception {
        // Given
        HistoryEntry entry1 = new HistoryEntry(1L, 101L, "query1", LocalDateTime.now());
        HistoryEntry entry2 = new HistoryEntry(2L, 102L, "query2", LocalDateTime.now());
        
        when(historyService.getAllEntries()).thenReturn(Arrays.asList(entry1, entry2));

        // When & Then
        mockMvc.perform(get("/api/history")
                .param("size", "0")) // signal to use non-paginated method
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].userId").value(101))
                .andExpect(jsonPath("$[0].query").value("query1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].userId").value(102))
                .andExpect(jsonPath("$[1].query").value("query2"));
    }

    @Test
    void getEntryById_WithExistingId_ShouldReturnEntry() throws Exception {
        // Given
        HistoryEntry entry = new HistoryEntry(1L, 101L, "query1", LocalDateTime.now());
        when(historyService.getEntryById(1L)).thenReturn(Optional.of(entry));

        // When & Then
        mockMvc.perform(get("/api/history/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(101))
                .andExpect(jsonPath("$.query").value("query1"));
    }

    @Test
    void getEntryById_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        // Given
        when(historyService.getEntryById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/history/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getEntriesByUserId_ShouldReturnUserEntries() throws Exception {
        // Given
        HistoryEntry entry1 = new HistoryEntry(1L, 101L, "query1", LocalDateTime.now());
        HistoryEntry entry2 = new HistoryEntry(2L, 101L, "query2", LocalDateTime.now());
        
        when(historyService.getEntriesByUserId(101L)).thenReturn(Arrays.asList(entry1, entry2));

        // When & Then
        mockMvc.perform(get("/api/history/user/101")
                .param("size", "0")) // signal to use non-paginated method
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].userId").value(101))
                .andExpect(jsonPath("$[1].userId").value(101));
    }

    // Removed the test for POST /api/history since that endpoint was removed

    @Test
    void createEntryForUser_ShouldReturnCreatedEntry() throws Exception {
        // Given
        HistoryEntry createdEntry = new HistoryEntry(1L, 101L, "test query", LocalDateTime.now());
        
        when(historyService.createEntry(eq(101L), eq("test query"))).thenReturn(createdEntry);

        // When & Then
        mockMvc.perform(post("/api/history/user/101")
                .param("query", "test query"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(101))
                .andExpect(jsonPath("$.query").value("test query"));
    }

    @Test
    void deleteEntry_ShouldReturnNoContent() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/history/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteAllEntriesForUser_ShouldReturnNoContent() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/history/user/101"))
                .andExpect(status().isNoContent());
    }
}
