package com.searchmiw.gateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    @Value("${AUTH_SERVICE_URL:http://auth:8081}")
    private String authServiceUrl;

    private final WebClient.Builder webClientBuilder;

    public JwtAuthenticationFilter(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (shouldSkipAuth(exchange)) {
                return chain.filter(exchange);
            }

            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String authHeader = exchange.getRequest().getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.substring(7);

            return webClientBuilder.baseUrl(authServiceUrl).build()
                    .get()
                    .uri("/auth/validate")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .onStatus(
                            status -> !status.equals(HttpStatus.OK),
                            clientResponse -> Mono.error(new RuntimeException("Invalid Token"))
                    )
                    .bodyToMono(Void.class)
                    .then(chain.filter(exchange));
        };
    }

    private boolean shouldSkipAuth(ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();
        
        // Skip auth for login, register, static content, and OPTIONS requests
        return path.startsWith("/auth/login") || 
               path.startsWith("/auth/register") || 
               path.equals("/") || 
               path.startsWith("/static") || 
               path.endsWith(".ico") || 
               path.endsWith(".js") || 
               path.endsWith(".css") || 
               path.startsWith("/graphiql") || // Allow GraphiQL access without auth
               "OPTIONS".equals(exchange.getRequest().getMethodValue());
    }

    public static class Config {
        // Empty config class as required by AbstractGatewayFilterFactory
    }
}
