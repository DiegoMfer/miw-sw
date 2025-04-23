package com.searchmiw.aggregator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoryEntryDto {
    private Long id;
    private Long userId;
    private String query;
    private LocalDateTime timestamp;
}
