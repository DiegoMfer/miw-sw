package com.searchmiw.gateway.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class OpenApiConfig {

    @Value("${server.port}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", 
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token authentication. Provide the token received from /api/auth/login")
                                        .name("Authorization")
                        ))
                .addSecurityItem(
                        new SecurityRequirement().addList("bearer-jwt")
                )
                .info(new Info()
                        .title("API Gateway")
                        .description("Gateway service routing requests to user, search, and history microservices. " +
                                "Most endpoints require JWT authentication obtained from /api/auth/login or /api/auth/register.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("SearchMIW Team")
                                .url("https://github.com/DiegoMfer/miw-sw")
                                .email("contact@searchmiw.com"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(Arrays.asList(
                        new Server().url("http://localhost:" + serverPort).description("Local development server"),
                        new Server().url("https://api.searchmiw.com").description("Production server")
                ));
    }
}
