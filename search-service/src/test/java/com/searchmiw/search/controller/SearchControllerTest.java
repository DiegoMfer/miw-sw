package com.searchmiw.search.controller;

import com.searchmiw.search.model.SearchResult;
import com.searchmiw.search.model.SearchResultItem;
import com.searchmiw.search.service.SearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SearchController.class)
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SearchService searchService;

    @Test
    void testSearch() throws Exception {
        // Prepare test data
        String query = "Albert Einstein";
        
        SearchResult mockResult = new SearchResult();
        mockResult.setQuery(query);
        mockResult.setTotalResults(2);
        mockResult.setSearchTime(150);
        
        SearchResultItem item1 = new SearchResultItem();
        item1.setId("Q937");
        item1.setTitle("Albert Einstein");
        item1.setDescription("Physicist");
        item1.setUrl("https://www.wikidata.org/wiki/Q937");
        
        mockResult.setResults(Arrays.asList(item1));
        
        // Configure mocks
        when(searchService.search(query, "en")).thenReturn(mockResult);
        
        // Execute and verify
        mockMvc.perform(get("/api/search")
                .param("query", query)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.query", is(query)))
                .andExpect(jsonPath("$.results", not(empty())));
    }
    
    @Test
    void testSearchWithEmptyResults() throws Exception {
        // Prepare test data
        String query = "empty";
        
        SearchResult emptyResult = new SearchResult();
        emptyResult.setQuery(query);
        emptyResult.setTotalResults(0);
        emptyResult.setSearchTime(50);
        emptyResult.setResults(Collections.emptyList());
        
        // Configure mocks
        when(searchService.search(query, "en")).thenReturn(emptyResult);
        
        // Execute and verify
        mockMvc.perform(get("/api/search")
                .param("query", query)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.query", is(query)))
                .andExpect(jsonPath("$.totalResults", is(0)))
                .andExpect(jsonPath("$.results", is(empty())));
    }

    @Test
    void testAlbertEinsteinSearch() throws Exception {
        // Prepare test data for Albert Einstein query
        String query = "Albert Einstein";
        
        SearchResult einsteinResult = new SearchResult();
        einsteinResult.setQuery(query);
        einsteinResult.setTotalResults(2);
        einsteinResult.setSearchTime(150);
        
        SearchResultItem einstein = new SearchResultItem();
        einstein.setId("Q937");
        einstein.setTitle("Albert Einstein");
        einstein.setDescription("German-born theoretical physicist; developer of the theory of relativity (1879–1955)");
        einstein.setUrl("https://www.wikidata.org/wiki/Q937");
        
        SearchResultItem einsteinInstitute = new SearchResultItem();
        einsteinInstitute.setId("Q1343246");
        einsteinInstitute.setTitle("Albert Einstein Institute");
        einsteinInstitute.setDescription("Research institute in Germany");
        einsteinInstitute.setUrl("https://www.wikidata.org/wiki/Q1343246");
        
        einsteinResult.setResults(Arrays.asList(einstein, einsteinInstitute));
        
        // Configure mock
        when(searchService.search(query, "en")).thenReturn(einsteinResult);
        
        // Execute and verify
        mockMvc.perform(get("/api/search")
                .param("query", query)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.query", is(query)))
                .andExpect(jsonPath("$.totalResults", is(2)))
                .andExpect(jsonPath("$.results", hasSize(2)))
                .andExpect(jsonPath("$.results[0].id", is("Q937")))
                .andExpect(jsonPath("$.results[0].title", is("Albert Einstein")))
                .andExpect(jsonPath("$.results[0].description", containsString("theoretical physicist")))
                .andExpect(jsonPath("$.results[1].title", is("Albert Einstein Institute")));
    }

    @Test
    void testSearchWithQueryAndLanguageParameters() throws Exception {
        // Prepare test data
        String query = "Albert Einstein";
        String language = "en";
        
        SearchResult mockResult = new SearchResult();
        mockResult.setQuery(query);
        mockResult.setTotalResults(1);
        mockResult.setSearchTime(100);
        
        SearchResultItem item = new SearchResultItem();
        item.setId("Q937");
        item.setTitle("Albert Einstein");
        item.setDescription("German-born theoretical physicist");
        item.setUrl("https://www.wikidata.org/wiki/Q937");
        
        mockResult.setResults(Arrays.asList(item));
        
        // Configure mock with specific language parameter
        when(searchService.search(query, language)).thenReturn(mockResult);
        
        // Execute and verify
        mockMvc.perform(get("/api/search")
                .param("query", query)
                .param("language", language)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.query", is(query)))
                .andExpect(jsonPath("$.totalResults", is(1)))
                .andExpect(jsonPath("$.results", hasSize(1)))
                .andExpect(jsonPath("$.results[0].id", is("Q937")));
    }
}
