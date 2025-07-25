package com.searchmiw.search.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WikidataResponse {
    private List<WikidataSearchEntity> search;
    private SearchInfo searchinfo;  // Changed from String to SearchInfo
    private boolean success;
}
