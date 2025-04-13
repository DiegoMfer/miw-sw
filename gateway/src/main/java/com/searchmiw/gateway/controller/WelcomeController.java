package com.searchmiw.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
public class WelcomeController {

    @GetMapping("/")
    public Mono<ResponseEntity<Map<String, Object>>> welcome() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to SearchMIW API Gateway");
        response.put("status", "UP");
        response.put("version", "1.0.0");
        
        return Mono.just(new ResponseEntity<>(response, HttpStatus.OK));
    }
}
