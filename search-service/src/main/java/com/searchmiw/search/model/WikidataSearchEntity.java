package com.searchmiw.search.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)  // This ensures any unknown fields are ignored
public class WikidataSearchEntity {
    private String id;
    private String title;
    private String description;
    private String url;
    private Map<String, String> match;
    
    // Aliases field is completely removed since we're ignoring unknown properties
}
