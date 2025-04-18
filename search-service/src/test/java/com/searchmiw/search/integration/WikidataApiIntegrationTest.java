package com.searchmiw.search.integration;

import com.searchmiw.search.model.WikidataResponse;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Tag("integration")
public class WikidataApiIntegrationTest {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Test
    void testRealAlbertEinsteinApiCall() {
        // Skip this test by default since it makes a real API call
        // Set this to true to run the real API test
        boolean runLiveTest = true;
        
        if (!runLiveTest) {
            System.out.println("Skipping live Wikidata API test");
            return;
        }

        String query = "Albert Einstein";
        String language = "en";
        
        String url = UriComponentsBuilder
                .fromHttpUrl("https://www.wikidata.org/w/api.php")
                .queryParam("action", "wbsearchentities")
                .queryParam("search", query)
                .queryParam("language", language)
                .queryParam("uselang", language)
                .queryParam("format", "json")
                .queryParam("limit", 10)
                .build()
                .toUriString();

        WebClient webClient = webClientBuilder.build();
        String rawJson = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        
        System.out.println("Raw JSON response:");
        System.out.println(rawJson);
        
        // If we've gotten this far without exceptions, parsing worked
        assertNotNull(rawJson);
        assertTrue(rawJson.contains("Albert Einstein"));
    }
}
