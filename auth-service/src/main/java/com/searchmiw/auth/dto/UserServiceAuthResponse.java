package com.searchmiw.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserServiceAuthResponse {
    private String id;
    private String email;
    private String name;
    private boolean authenticated;
}
