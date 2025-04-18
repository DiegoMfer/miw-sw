package com.searchmiw.history.dto;

import com.searchmiw.history.model.HistoryEntry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryEntryResponse {
    
    private Long id;
    private Long userId;
    private String query;
    private LocalDateTime timestamp;
    
    public static HistoryEntryResponse fromEntity(HistoryEntry entity) {
        return HistoryEntryResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .query(entity.getQuery())
                .timestamp(entity.getTimestamp())
                .build();
    }
    
    public static List<HistoryEntryResponse> fromEntities(List<HistoryEntry> entities) {
        return entities.stream()
                .map(HistoryEntryResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
