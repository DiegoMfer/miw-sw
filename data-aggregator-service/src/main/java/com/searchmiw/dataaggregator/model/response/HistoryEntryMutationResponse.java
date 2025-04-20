package com.searchmiw.dataaggregator.model.response;

import com.searchmiw.dataaggregator.model.HistoryEntry;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HistoryEntryMutationResponse {
    private boolean success;
    private String message;
    private HistoryEntry historyEntry;
}
