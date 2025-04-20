package com.searchmiw.dataaggregator.model.response;

import com.searchmiw.dataaggregator.model.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserMutationResponse {
    private boolean success;
    private String message;
    private User user;
}
