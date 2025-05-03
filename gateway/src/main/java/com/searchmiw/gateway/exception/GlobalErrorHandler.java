package com.searchmiw.gateway.exception;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
@Order(-2) // High precedence
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        int statusCode;
        String statusText;
        String message;
        
        if (ex instanceof ResponseStatusException) {
            ResponseStatusException responseStatusException = (ResponseStatusException) ex;
            HttpStatusCode httpStatusCode = responseStatusException.getStatusCode();
            statusCode = httpStatusCode.value();
            statusText = httpStatusCode.toString();
            // Use HTTP standard reason if available, otherwise use the status code text
            statusText = (httpStatusCode instanceof HttpStatus) ? 
                    ((HttpStatus) httpStatusCode).getReasonPhrase() : 
                    httpStatusCode.toString().replace("_", " ");
            message = responseStatusException.getReason();
        } else {
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            statusText = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();
            message = "Internal Server Error";
        }
        
        response.setStatusCode(HttpStatusCode.valueOf(statusCode));
        
        String errorJson = "{\"status\":" + statusCode + 
                ",\"error\":\"" + statusText + 
                "\",\"message\":\"" + message + 
                "\",\"path\":\"" + exchange.getRequest().getURI().getPath() + "\"}";
        
        byte[] bytes = errorJson.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
