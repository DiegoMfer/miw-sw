package com.searchmiw.dataaggregator.resolver;

import com.searchmiw.dataaggregator.model.HistoryEntry;
import com.searchmiw.dataaggregator.model.User;
import com.searchmiw.dataaggregator.model.response.HistoryEntryMutationResponse;
import com.searchmiw.dataaggregator.model.response.MutationResponse;
import com.searchmiw.dataaggregator.model.response.UserMutationResponse;
import com.searchmiw.dataaggregator.service.HistoryService;
import com.searchmiw.dataaggregator.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MutationResolver {

    private final UserService userService;
    private final HistoryService historyService;
    
    @MutationMapping
    public UserMutationResponse createUser(@Argument CreateUserInput input) {
        try {
            User user = userService.createUser(input);
            return UserMutationResponse.builder()
                    .success(true)
                    .message("User created successfully")
                    .user(user)
                    .build();
        } catch (WebClientResponseException e) {
            log.error("Error creating user: {} - Status: {}", e.getMessage(), e.getStatusCode());
            return UserMutationResponse.builder()
                    .success(false)
                    .message("Failed to create user: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error creating user: {}", e.getMessage(), e);
            return UserMutationResponse.builder()
                    .success(false)
                    .message("An unexpected error occurred")
                    .build();
        }
    }
    
    @MutationMapping
    public UserMutationResponse updateUser(@Argument String id, @Argument UpdateUserInput input) {
        try {
            User user = userService.updateUser(Long.valueOf(id), input);
            return UserMutationResponse.builder()
                    .success(true)
                    .message("User updated successfully")
                    .user(user)
                    .build();
        } catch (WebClientResponseException e) {
            log.error("Error updating user: {} - Status: {}", e.getMessage(), e.getStatusCode());
            return UserMutationResponse.builder()
                    .success(false)
                    .message("Failed to update user: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error updating user: {}", e.getMessage(), e);
            return UserMutationResponse.builder()
                    .success(false)
                    .message("An unexpected error occurred")
                    .build();
        }
    }
    
    @MutationMapping
    public MutationResponse deleteUser(@Argument String id) {
        try {
            userService.deleteUser(Long.valueOf(id));
            return MutationResponse.builder()
                    .success(true)
                    .message("User deleted successfully")
                    .build();
        } catch (WebClientResponseException e) {
            log.error("Error deleting user: {} - Status: {}", e.getMessage(), e.getStatusCode());
            return MutationResponse.builder()
                    .success(false)
                    .message("Failed to delete user: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error deleting user: {}", e.getMessage(), e);
            return MutationResponse.builder()
                    .success(false)
                    .message("An unexpected error occurred")
                    .build();
        }
    }
    
    @MutationMapping
    public HistoryEntryMutationResponse saveSearch(@Argument String userId, @Argument String query) {
        try {
            HistoryEntry entry = historyService.createEntry(Long.valueOf(userId), query);
            return HistoryEntryMutationResponse.builder()
                    .success(true)
                    .message("Search saved successfully")
                    .historyEntry(entry)
                    .build();
        } catch (WebClientResponseException e) {
            log.error("Error saving search: {} - Status: {}", e.getMessage(), e.getStatusCode());
            return HistoryEntryMutationResponse.builder()
                    .success(false)
                    .message("Failed to save search: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error saving search: {}", e.getMessage(), e);
            return HistoryEntryMutationResponse.builder()
                    .success(false)
                    .message("An unexpected error occurred")
                    .build();
        }
    }
    
    @MutationMapping
    public MutationResponse deleteSearch(@Argument String id) {
        try {
            historyService.deleteEntry(Long.valueOf(id));
            return MutationResponse.builder()
                    .success(true)
                    .message("Search history entry deleted successfully")
                    .build();
        } catch (WebClientResponseException e) {
            log.error("Error deleting search: {} - Status: {}", e.getMessage(), e.getStatusCode());
            return MutationResponse.builder()
                    .success(false)
                    .message("Failed to delete search: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error deleting search: {}", e.getMessage(), e);
            return MutationResponse.builder()
                    .success(false)
                    .message("An unexpected error occurred")
                    .build();
        }
    }
    
    @MutationMapping
    public MutationResponse clearHistory(@Argument String userId) {
        try {
            historyService.deleteAllEntriesForUser(Long.valueOf(userId));
            return MutationResponse.builder()
                    .success(true)
                    .message("Search history cleared successfully")
                    .build();
        } catch (WebClientResponseException e) {
            log.error("Error clearing history: {} - Status: {}", e.getMessage(), e.getStatusCode());
            return MutationResponse.builder()
                    .success(false)
                    .message("Failed to clear search history: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error clearing history: {}", e.getMessage(), e);
            return MutationResponse.builder()
                    .success(false)
                    .message("An unexpected error occurred")
                    .build();
        }
    }
}
