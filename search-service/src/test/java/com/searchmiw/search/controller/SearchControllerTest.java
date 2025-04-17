package com.searchmiw.search.controller;

import com.searchmiw.search.model.SearchResult;
import com.searchmiw.search.model.SearchResultItem;
import com.searchmiw.search.service.SearchService;
import com.searchmiw.search.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SearchController.class)
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SearchService searchService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void testSearchWithValidToken() throws Exception {
        // Prepare test data
        String query = "test";
        String token = "valid-token";
        
        SearchResult mockResult = new SearchResult();
        mockResult.setQuery(query);
        mockResult.setTotalResults(2);
        mockResult.setSearchTime(150);
        
        SearchResultItem item1 = new SearchResultItem();
        item1.setId("Q1");
        item1.setTitle("Test Entity 1");
        item1.setDescription("Description 1");
        item1.setUrl("https://www.wikidata.org/wiki/Q1");
        
        SearchResultItem item2 = new SearchResultItem();
        item2.setId("Q2");
        item2.setTitle("Test Entity 2");
        item2.setDescription("Description 2");
        item2.setUrl("https://www.wikidata.org/wiki/Q2");
        
        mockResult.setResults(Arrays.asList(item1, item2));
        
        // Configure mocks
        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(searchService.search(query, "en")).thenReturn(mockResult);
        
        // Execute and verify
        mockMvc.perform(get("/api/search")
                .param("query", query)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.query", is(query)))
                .andExpect(jsonPath("$.totalResults", is(2)))
                .andExpect(jsonPath("$.results", hasSize(2)))
                .andExpect(jsonPath("$.results[0].id", is("Q1")))
                .andExpect(jsonPath("$.results[0].title", is("Test Entity 1")))
                .andExpect(jsonPath("$.results[1].id", is("Q2")));
    }

    @Test
    void testSearchWithInvalidToken() throws Exception {
        // Prepare test data
        String query = "test";
        String token = "invalid-token";
        
        // Configure mocks
        when(jwtUtil.validateToken(token)).thenReturn(false);
        
        // Execute and verify
        mockMvc.perform(get("/api/search")
                .param("query", query)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    void testSearchWithEmptyResults() throws Exception {
        // Prepare test data
        String query = "empty";
        String token = "valid-token";
        
        SearchResult emptyResult = new SearchResult();
        emptyResult.setQuery(query);
        emptyResult.setTotalResults(0);
        emptyResult.setSearchTime(50);
        emptyResult.setResults(Collections.emptyList());
        
        // Configure mocks
        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(searchService.search(query, "en")).thenReturn(emptyResult);
        
        // Execute and verify
        mockMvc.perform(get("/api/search")
                .param("query", query)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.query", is(query)))
                .andExpect(jsonPath("$.totalResults", is(0)))
                .andExpect(jsonPath("$.results", is(empty())));
    }
}
