package com.searchmiw.dataaggregator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultItem {
    private String id;
    private String title;
    private String description;
    private String url;
}
