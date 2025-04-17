package com.searchmiw.search.service;

import com.searchmiw.search.model.SearchResult;
import com.searchmiw.search.model.SearchResultItem;
import com.searchmiw.search.model.WikidataResponse;
import com.searchmiw.search.model.WikidataSearchEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private SearchService searchService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(searchService, "wikidataApiUrl", "https://www.wikidata.org/w/api.php");
        
        // Mock the WebClient chain
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void testSuccessfulSearch() {
        // Prepare test data
        String query = "test query";
        String language = "en";
        
        List<WikidataSearchEntity> mockEntities = Arrays.asList(
            createMockEntity("Q1", "Test Entity 1", "Test Description 1"),
            createMockEntity("Q2", "Test Entity 2", "Test Description 2")
        );
        
        WikidataResponse mockResponse = new WikidataResponse();
        mockResponse.setSearch(mockEntities);
        mockResponse.setSuccess(true);
        
        when(responseSpec.bodyToMono(WikidataResponse.class)).thenReturn(Mono.just(mockResponse));
        
        // Execute the method
        SearchResult result = searchService.search(query, language);
        
        // Verify results
        assertNotNull(result);
        assertEquals(query, result.getQuery());
        assertEquals(2, result.getTotalResults());
        assertEquals(2, result.getResults().size());
        
        // Verify first result
        SearchResultItem firstItem = result.getResults().get(0);
        assertEquals("Q1", firstItem.getId());
        assertEquals("Test Entity 1", firstItem.getTitle());
        assertEquals("Test Description 1", firstItem.getDescription());
        assertEquals("https://www.wikidata.org/wiki/Q1", firstItem.getUrl());
        
        // Verify that WebClient was called with the right params
        verify(webClient).get();
        verify(requestHeadersUriSpec).retrieve();
    }

    @Test
    void testEmptySearchResults() {
        // Prepare test data
        String query = "nonexistent";
        String language = "en";
        
        WikidataResponse mockResponse = new WikidataResponse();
        mockResponse.setSearch(Collections.emptyList());
        mockResponse.setSuccess(true);
        
        when(responseSpec.bodyToMono(WikidataResponse.class)).thenReturn(Mono.just(mockResponse));
        
        // Execute the method
        SearchResult result = searchService.search(query, language);
        
        // Verify results
        assertNotNull(result);
        assertEquals(query, result.getQuery());
        assertEquals(0, result.getTotalResults());
        assertTrue(result.getResults().isEmpty());
    }

    @Test
    void testErrorHandling() {
        // Prepare test data
        String query = "error test";
        String language = "en";
        
        when(responseSpec.bodyToMono(WikidataResponse.class)).thenReturn(Mono.error(new RuntimeException("API Error")));
        
        // Execute the method
        SearchResult result = searchService.search(query, language);
        
        // Verify results
        assertNotNull(result);
        assertEquals(query, result.getQuery());
        assertEquals(0, result.getTotalResults());
        assertTrue(result.getResults().isEmpty());
    }

    @Test
    void testNullResponse() {
        // Prepare test data
        String query = "null test";
        String language = "en";
        
        when(responseSpec.bodyToMono(WikidataResponse.class)).thenReturn(Mono.just(null));
        
        // Execute the method
        SearchResult result = searchService.search(query, language);
        
        // Verify results
        assertNotNull(result);
        assertEquals(query, result.getQuery());
        assertEquals(0, result.getTotalResults());
        assertTrue(result.getResults().isEmpty());
    }

    private WikidataSearchEntity createMockEntity(String id, String title, String description) {
        WikidataSearchEntity entity = new WikidataSearchEntity();
        entity.setId(id);
        entity.setTitle(title);
        entity.setDescription(description);
        return entity;
    }
}
