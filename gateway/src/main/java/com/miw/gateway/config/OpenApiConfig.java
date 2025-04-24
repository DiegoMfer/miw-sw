package com.miw.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI gatewayOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("SearchMIW Gateway API")
                .description("API Gateway for SearchMIW application providing access to all microservices")
                .version("v1.0.0")
                .contact(new Contact()
                    .name("SearchMIW Team")
                    .url("https://github.com/DiegoMfer/miw-sw")
                    .email("contact@searchmiw.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(getServers());
    }
    
    private List<Server> getServers() {
        List<Server> servers = new ArrayList<>();
        servers.add(new Server().url("http://localhost:8080").description("Gateway - Local Development"));
        return servers;
    }
}
