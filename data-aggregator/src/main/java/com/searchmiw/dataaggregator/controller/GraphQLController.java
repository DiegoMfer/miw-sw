package com.searchmiw.dataaggregator.controller;

import com.searchmiw.dataaggregator.model.SearchHistory;
import com.searchmiw.dataaggregator.model.User;
import com.searchmiw.dataaggregator.service.HistoryService;
import com.searchmiw.dataaggregator.service.UserService;
import com.searchmiw.dataaggregator.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GraphQLController {

    private final HistoryService historyService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @QueryMapping
    public List<SearchHistory> searchHistory(@Argument Integer limit) {
        String token = extractToken();
        return historyService.getUserSearchHistory(token, limit)
                .collectList()
                .block();
    }

    @QueryMapping
    public User me() {
        String token = extractToken();
        return userService.getCurrentUser(token)
                .block();
    }
    
    @QueryMapping
    public User user(@Argument String id) {
        String token = extractToken();
        return userService.getUserById(token, id)
                .block();
    }

    @MutationMapping
    public SearchHistory saveSearch(@Argument String query) {
        String token = extractToken();
        return historyService.saveSearch(token, query).block();
    }

    @MutationMapping
    public Boolean deleteSearch(@Argument String id) {
        String token = extractToken();
        try {
            historyService.deleteSearch(token, Long.parseLong(id)).block();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @MutationMapping
    public Boolean clearHistory() {
        String token = extractToken();
        try {
            historyService.clearHistory(token).block();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @MutationMapping
    public User updateProfile(@Argument String name, @Argument String username) {
        String token = extractToken();
        return userService.updateUserProfile(token, name, username)
                .block();
    }

    private String extractToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            String authHeader = attributes.getRequest().getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                return authHeader.substring(7);
            }
        }
        throw new RuntimeException("No authorization token found");
    }
}
