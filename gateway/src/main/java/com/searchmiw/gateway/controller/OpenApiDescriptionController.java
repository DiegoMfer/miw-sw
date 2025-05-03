package com.searchmiw.gateway.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/docs")
@Tag(name = "API Documentation", description = "API documentation information")
public class OpenApiDescriptionController {

    @Operation(
            summary = "Authentication information",
            description = "How to authenticate with the API using JWT tokens"
    )
    @GetMapping("/auth-info")
    public String getAuthInfo() {
        return "To authenticate with the API:\n" +
                "1. Get a JWT token by making a POST request to /api/auth/login or /api/auth/register\n" +
                "2. Include the token in the Authorization header as 'Bearer YOUR_TOKEN_HERE' for all protected endpoints\n" +
                "3. Tokens are valid for 24 hours by default";
    }
}
