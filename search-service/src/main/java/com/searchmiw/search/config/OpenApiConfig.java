package com.searchmiw.search.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.models.OpenAPI;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Search Service API",
        description = "API for searching entities using Wikidata",
        version = "v1.0.0",
        contact = @Contact(
            name = "SearchMIW Team",
            url = "https://github.com/DiegoMfer/miw-sw",
            email = "contact@searchmiw.com"
        ),
        license = @License(
            name = "MIT License",
            url = "https://opensource.org/licenses/MIT"
        )
    ),
    security = @SecurityRequirement(name = "Bearer Authentication")
)
@SecurityScheme(
    name = "Bearer Authentication",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
public class OpenApiConfig {
    // Configuration is now handled by annotations
}
