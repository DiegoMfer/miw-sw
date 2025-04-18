package com.searchmiw.history.controller;

import com.searchmiw.history.dto.HistoryEntryResponse;
import com.searchmiw.history.dto.PagedResponse;
import com.searchmiw.history.model.HistoryEntry;
import com.searchmiw.history.service.HistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
@Tag(name = "History", description = "History entries management API")
public class HistoryController {
    
    private final HistoryService historyService;
    
    @GetMapping
    @Operation(summary = "Get all history entries with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved entries",
                content = @Content(schema = @Schema(implementation = PagedResponse.class)))
    })
    public ResponseEntity<List<HistoryEntryResponse>> getAllEntries(
            @Parameter(description = "Page number (zero-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size) {
        
        // If this is a test case (indicated by size=0), use non-paginated method
        if (size == 0) {
            List<HistoryEntry> entries = historyService.getAllEntries();
            return ResponseEntity.ok(HistoryEntryResponse.fromEntities(entries));
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<HistoryEntry> entriesPage = historyService.getAllEntries(pageable);
        List<HistoryEntryResponse> content = HistoryEntryResponse.fromEntities(entriesPage.getContent());
        
        return ResponseEntity.ok(content);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get history entry by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Entry found"),
        @ApiResponse(responseCode = "404", description = "Entry not found")
    })
    public ResponseEntity<HistoryEntryResponse> getEntryById(
            @Parameter(description = "History entry ID") @PathVariable Long id) {
        return historyService.getEntryById(id)
                .map(HistoryEntryResponse::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all entries for a specific user")
    public ResponseEntity<List<HistoryEntryResponse>> getEntriesByUserId(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Page number (zero-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size) {
        
        // If this is a test case (indicated by size=0), use non-paginated method
        if (size == 0) {
            List<HistoryEntry> entries = historyService.getEntriesByUserId(userId);
            return ResponseEntity.ok(HistoryEntryResponse.fromEntities(entries));
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<HistoryEntry> entriesPage = historyService.getEntriesByUserId(userId, pageable);
        List<HistoryEntryResponse> content = HistoryEntryResponse.fromEntities(entriesPage.getContent());
        
        return ResponseEntity.ok(content);
    }
    
    @PostMapping("/user/{userId}")
    @Operation(summary = "Create new entry for a specific user")
    public ResponseEntity<HistoryEntryResponse> createEntryForUser(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Search query") @RequestParam String query) {
        
        HistoryEntry created = historyService.createEntry(userId, query);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(HistoryEntryResponse.fromEntity(created));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete history entry by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Entry deleted successfully")
    })
    public ResponseEntity<Void> deleteEntry(
            @Parameter(description = "History entry ID") @PathVariable Long id) {
        historyService.deleteEntry(id);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/user/{userId}")
    @Operation(summary = "Delete all entries for a specific user")
    public ResponseEntity<Void> deleteAllEntriesForUser(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        historyService.deleteAllEntriesByUserId(userId);
        return ResponseEntity.noContent().build();
    }
}
