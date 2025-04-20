package com.searchmiw.dataaggregator.resolver;

import com.searchmiw.dataaggregator.model.HistoryEntry;
import com.searchmiw.dataaggregator.model.SearchResult;
import com.searchmiw.dataaggregator.model.User;
import com.searchmiw.dataaggregator.service.HistoryService;
import com.searchmiw.dataaggregator.service.SearchService;
import com.searchmiw.dataaggregator.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class QueryResolver {

    private final UserService userService;
    private final SearchService searchService;
    private final HistoryService historyService;
    
    // User queries
    
    @QueryMapping
    public List<User> users() {
        return userService.getAllUsers();
    }
    
    @QueryMapping
    public User user(@Argument Long id) {
        return userService.getUserById(id).orElse(null);
    }
    
    @QueryMapping
    public User userByEmail(@Argument String email) {
        return userService.getUserByEmail(email).orElse(null);
    }
    
    // Search queries
    
    @QueryMapping
    public SearchResult search(@Argument String query, @Argument String language) {
        return searchService.search(query, language);
    }
    
    // History queries
    
    @QueryMapping
    public List<HistoryEntry> historyEntries() {
        return historyService.getAllEntries();
    }
    
    @QueryMapping
    public HistoryEntry historyEntry(@Argument Long id) {
        return historyService.getEntryById(id).orElse(null);
    }
    
    @QueryMapping
    public List<HistoryEntry> historyEntriesByUser(@Argument Long userId) {
        return historyService.getEntriesByUserId(userId);
    }
}
