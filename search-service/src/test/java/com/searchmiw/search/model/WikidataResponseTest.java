package com.searchmiw.search.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

@JsonTest
class WikidataResponseTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testDeserializeWikidataResponse() throws IOException {
        // Load a sample response JSON file from test resources
        try (InputStream is = getClass().getResourceAsStream("/wikidata-sample-response.json")) {
            assertNotNull(is, "Sample response file not found");
            
            // Deserialize the JSON to WikidataResponse object
            WikidataResponse response = objectMapper.readValue(is, WikidataResponse.class);
            
            // Verify the response
            assertNotNull(response);
            assertNotNull(response.getSearch());
            assertFalse(response.getSearch().isEmpty());
            
            // Check the first entity
            WikidataSearchEntity firstEntity = response.getSearch().get(0);
            assertNotNull(firstEntity.getId());
            assertNotNull(firstEntity.getTitle());
        }
    }
}
