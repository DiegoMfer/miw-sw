package com.searchmiw.dataaggregator.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MutationResponse {
    private boolean success;
    private String message;
}
