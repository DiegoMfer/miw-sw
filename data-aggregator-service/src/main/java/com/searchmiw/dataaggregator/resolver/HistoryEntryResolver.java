package com.searchmiw.dataaggregator.resolver;

import com.searchmiw.dataaggregator.model.HistoryEntry;
import com.searchmiw.dataaggregator.model.SearchResult;
import com.searchmiw.dataaggregator.model.User;
import com.searchmiw.dataaggregator.service.SearchService;
import com.searchmiw.dataaggregator.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class HistoryEntryResolver {

    private final UserService userService;
    private final SearchService searchService;
    
    @SchemaMapping(typeName = "HistoryEntry")
    public User user(HistoryEntry historyEntry) {
        return userService.getUserById(historyEntry.getUserId()).orElse(null);
    }
    
    @SchemaMapping(typeName = "HistoryEntry")
    public SearchResult searchResult(HistoryEntry historyEntry) {
        return searchService.search(historyEntry.getQuery(), "en");
    }
}
