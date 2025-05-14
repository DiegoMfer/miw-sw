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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class SearchServiceTest {

    @Autowired
    private SearchService searchService;

    @MockBean
    private WebClient.Builder webClientBuilder;
    
    @MockBean
    private QueryTransformer queryTransformer;

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
        ReflectionTestUtils.setField(searchService, "maxTransformationAttempts", 3);
    }

    @Test
    void testSearchWithEmptyResults() {
        // Prepare empty response
        WikidataResponse emptyResponse = new WikidataResponse();
        emptyResponse.setSuccess(true);
        emptyResponse.setSearch(Collections.emptyList());
        
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
    
    @Test
    void testSearchWithQueryTransformation() {
        // Original query returns no results
        String originalQuery = "difficult query";
        String transformedQuery1 = "better query";
        String transformedQuery2 = "best query";
        
        // Empty response for original query and first transformed query
        WikidataResponse emptyResponse = new WikidataResponse();
        emptyResponse.setSuccess(true);
        emptyResponse.setSearch(Collections.emptyList());
        
        // Successful response for second transformed query
        WikidataResponse successResponse = new WikidataResponse();
        successResponse.setSuccess(true);
        
        WikidataSearchEntity entity = new WikidataSearchEntity();
        entity.setId("Q123");
        entity.setTitle("Test Result");
        entity.setDescription("A test result description");
        entity.setUrl("//www.wikidata.org/wiki/Q123");
        successResponse.setSearch(Collections.singletonList(entity));
        
        // Configure query transformer
        when(queryTransformer.transformQuery(originalQuery, 1)).thenReturn(transformedQuery1);
        when(queryTransformer.transformQuery(originalQuery, 2)).thenReturn(transformedQuery2);
        
        // Configure response sequence: empty -> empty -> success
        when(responseSpec.bodyToMono(WikidataResponse.class))
            .thenReturn(Mono.just(emptyResponse))  // Original query
            .thenReturn(Mono.just(emptyResponse))  // First transformation
            .thenReturn(Mono.just(successResponse));  // Second transformation
        
        // Execute search
        SearchResult result = searchService.search(originalQuery, "en");
        
        // Verify results
        assertNotNull(result);
        assertEquals(transformedQuery2, result.getQuery());
        assertEquals(originalQuery, result.getOriginalQuery());
        assertEquals(transformedQuery2, result.getTransformedQuery());
        assertEquals(1, result.getTotalResults());
        assertFalse(result.getResults().isEmpty());
        
        // Verify the transformer was called the expected number of times
        verify(queryTransformer, times(1)).transformQuery(originalQuery, 1);
        verify(queryTransformer, times(1)).transformQuery(originalQuery, 2);
        verify(queryTransformer, never()).transformQuery(originalQuery, 3);
    }
    
    @Test
    void testSearchWithMaximumTransformationAttempts() {
        // Original query
        String originalQuery = "impossible query";
        
        // All responses are empty
        WikidataResponse emptyResponse = new WikidataResponse();
        emptyResponse.setSuccess(true);
        emptyResponse.setSearch(Collections.emptyList());
        
        // Configure transformer to return different queries
        when(queryTransformer.transformQuery(originalQuery, 1)).thenReturn("transformed1");
        when(queryTransformer.transformQuery(originalQuery, 2)).thenReturn("transformed2");
        when(queryTransformer.transformQuery(originalQuery, 3)).thenReturn("transformed3");
        
        // All responses return no results
        when(responseSpec.bodyToMono(WikidataResponse.class)).thenReturn(Mono.just(emptyResponse));
        
        // Execute search
        SearchResult result = searchService.search(originalQuery, "en");
        
        // Verify results - should return empty result with the last tried query
        assertNotNull(result);
        assertEquals("transformed3", result.getQuery());
        assertEquals(0, result.getTotalResults());
        assertTrue(result.getResults().isEmpty());
        
        // Verify transformer was called the maximum number of times
        verify(queryTransformer, times(1)).transformQuery(originalQuery, 1);
        verify(queryTransformer, times(1)).transformQuery(originalQuery, 2);
        verify(queryTransformer, times(1)).transformQuery(originalQuery, 3);
        verify(queryTransformer, never()).transformQuery(anyString(), eq(4));
    }
}
