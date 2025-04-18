package com.searchmiw.search.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
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
    )
)
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Search Service API")
                        .description("API for searching entities using Wikidata")
                        .version("v1.0.0")
                        .contact(new io.swagger.v3.oas.models.info.Contact()
                                .name("SearchMIW Team")
                                .url("https://github.com/DiegoMfer/miw-sw")
                                .email("contact@searchmiw.com"))
                        .license(new io.swagger.v3.oas.models.info.License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
