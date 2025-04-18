package com.searchmiw.search.service;

import com.searchmiw.search.model.SearchResult;
import com.searchmiw.search.model.WikidataResponse;
import com.searchmiw.search.model.WikidataSearchEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class SearchServiceTest {

    @Autowired
    private SearchService searchService;

    @MockBean
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        // Set up the mock WebClient chain
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        
        // Set the Wikidata API URL via reflection since it's a private field
        ReflectionTestUtils.setField(searchService, "wikidataApiUrl", "https://www.wikidata.org/w/api.php");
    }


    
    @Test
    void testSearchWithEmptyResults() {
        // Prepare empty response
        WikidataResponse emptyResponse = new WikidataResponse();
        emptyResponse.setSuccess(true);
        emptyResponse.setSearch(null);
        
        // Configure mock response
        when(responseSpec.bodyToMono(WikidataResponse.class)).thenReturn(Mono.just(emptyResponse));
        
        // Execute search
        SearchResult result = searchService.search("no results", "en");
        
        // Verify results
        assertNotNull(result);
        assertEquals("no results", result.getQuery());
        assertEquals(0, result.getTotalResults());
        assertTrue(result.getResults().isEmpty());
    }
    
    @Test
    void testSearchWithException() {
        // Configure mock to throw exception
        when(responseSpec.bodyToMono(WikidataResponse.class)).thenReturn(Mono.error(new RuntimeException("API Error")));
        
        // Execute search
        SearchResult result = searchService.search("error query", "en");
        
        // Verify results - should return empty result
        assertNotNull(result);
        assertEquals("error query", result.getQuery());
        assertEquals(0, result.getTotalResults());
        assertTrue(result.getResults().isEmpty());
    }
}
