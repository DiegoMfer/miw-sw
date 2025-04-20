package com.searchmiw.dataaggregator.resolver;

import lombok.Data;

@Data
public class CreateUserInput {
    private String username;
    private String email;
    private String password;
    private String name;
}
