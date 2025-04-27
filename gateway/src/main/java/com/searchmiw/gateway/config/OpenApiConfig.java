package com.searchmiw.gateway.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
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
                .components(new Components())
                .info(new Info()
                        .title("API Gateway")
                        .description("Gateway service routing requests to user, search, and history microservices.")
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
