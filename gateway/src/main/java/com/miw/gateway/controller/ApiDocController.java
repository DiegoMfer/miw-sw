package com.miw.gateway.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller to provide an index of API documentation for all services
 */
@Controller
@RequestMapping("/")
public class ApiDocController {

    @GetMapping(value = "/api-docs-index", produces = "application/json")
    @ResponseBody
    public Map<String, Object> getApiDocsIndex() {
        Map<String, Object> index = new HashMap<>();
        
        // API Docs links
        Map<String, String> apiDocs = new HashMap<>();
        apiDocs.put("Gateway Swagger UI", "/swagger-ui.html");
        apiDocs.put("Search Service API Docs", "/search-docs/api-docs");
        apiDocs.put("History Service API Docs", "/history-docs/api-docs");
        apiDocs.put("User Service API Docs", "/user-docs/api-docs");
        apiDocs.put("Aggregator GraphiQL", "/aggregator-ui/graphiql");
        
        // Swagger UI links
        Map<String, String> swaggerUIs = new HashMap<>();
        swaggerUIs.put("Search Service", "/search-docs/swagger-ui.html");
        swaggerUIs.put("History Service", "/history-docs/swagger-ui.html");
        swaggerUIs.put("User Service", "/user-docs/swagger-ui.html");
        
        index.put("apiDocs", apiDocs);
        index.put("swaggerUIs", swaggerUIs);
        index.put("combinedSwaggerUI", "/swagger-ui.html");
        
        return index;
    }
    
    @GetMapping("/docs")
    public String redirectToSwaggerUi() {
        return "redirect:/swagger-ui.html";
    }
}
