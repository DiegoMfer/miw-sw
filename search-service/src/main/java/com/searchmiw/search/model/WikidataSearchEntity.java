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
    private Map<String, String> display;
    
    // Used to extract matched text
    private String match;
    private Map<String, Object> aliases;
}
