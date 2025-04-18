package com.searchmiw.search.integration;

import com.searchmiw.search.model.SearchResult;
import com.searchmiw.search.service.SearchService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for SearchService.
 * These tests make real API calls to Wikidata.
 * They may be slow and could fail if the API is down or changed.
 */
@SpringBootTest
@Tag("integration")
public class SearchServiceIntegrationTest {

    @Autowired
    private SearchService searchService;

    @Test
    void testRealSearchWithCommonQuery() {
        // This test can be skipped in automated builds by setting the property to false
        boolean runLiveTest = Boolean.parseBoolean(
                System.getProperty("runLiveTests", "true"));
        
        if (!runLiveTest) {
            System.out.println("Skipping real API call in integration test. Tests will pass without validation.");
            return;
        }

        // Use a query that should consistently return results
        String query = "Albert Einstein";
        
        // Call the real service
        SearchResult result = searchService.search(query, "en");
        
        // Print debug info in build logs
        System.out.println("Integration test results: Found " + 
            (result.getResults() != null ? result.getResults().size() : 0) + 
            " results for query '" + query + "'");
        
        // Verify we got actual results
        assertNotNull(result);
        assertEquals(query, result.getQuery());
        assertTrue(result.getSearchTime() > 0, "Search should take some time");
        assertFalse(result.getResults().isEmpty(), "Einstein search should return results");
        assertTrue(result.getTotalResults() > 0, "Should have at least one result");
        
        // Check that Albert Einstein is in the results
        boolean foundEinstein = result.getResults().stream()
                .anyMatch(item -> 
                    item.getTitle().contains("Einstein") && 
                    item.getDescription() != null && 
                    item.getDescription().contains("physicist"));
        
        assertTrue(foundEinstein, "Should find Albert Einstein in results");
    }

    @Test
    void testRealSearchWithUnlikelyQuery() {
        // This test can be skipped in automated builds
        boolean runLiveTest = Boolean.parseBoolean(
                System.getProperty("runLiveTests", "true"));
        
        if (!runLiveTest) {
            System.out.println("Skipping live Wikidata API test. Set -DrunLiveTests=true to enable.");
            return;
        }

        // Use a query that likely won't return results
        String query = "xz9812nonexistentquerystring78217bzx";
        
        // Call the real service
        SearchResult result = searchService.search(query, "en");
        
        // Verify we got an empty result but not a null
        assertNotNull(result);
        assertEquals(query, result.getQuery());
        assertTrue(result.getSearchTime() > 0, "Search should take some time");
        assertTrue(result.getResults().isEmpty(), "Unlikely query should return no results");
        assertEquals(0, result.getTotalResults());
    }

    @Test
    void testRealSearchWithDifferentLanguage() {
        // This test can be skipped in automated builds
        boolean runLiveTest = Boolean.parseBoolean(
                System.getProperty("runLiveTests", "true"));
        
        if (!runLiveTest) {
            System.out.println("Skipping live Wikidata API test. Set -DrunLiveTests=true to enable.");
            return;
        }

        // Use a query in Spanish
        String query = "Pablo Picasso";
        String language = "es";
        
        // Call the real service with Spanish language code
        SearchResult result = searchService.search(query, language);
        
        // Verify we got actual results in Spanish
        assertNotNull(result);
        assertEquals(query, result.getQuery());
        assertFalse(result.getResults().isEmpty(), "Picasso search should return results");
        
        // Check if we got relevant results (may contain Spanish descriptions)
        boolean foundPicasso = result.getResults().stream()
                .anyMatch(item -> 
                    item.getTitle().contains("Picasso") && 
                    item.getDescription() != null);
        
        assertTrue(foundPicasso, "Should find Pablo Picasso in results");
    }
}
