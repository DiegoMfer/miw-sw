package com.searchmiw.search.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WikidataSearchEntity {
    private String id;
    private String title;
    private String url;
    private String description;
    
    // Changed from Map<String, String> to Map<String, Object> to handle nested structures
    private Map<String, Object> display;
    
    // Used to extract matched text
    private Map<String, Object> match;  // Changed from String to Map to handle nested structure
    private Map<String, Object> aliases;
}
