package com.searchmiw.gateway.filter;

import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class WelcomePageModifier implements RewriteFunction<String, String> {

    @Override
    public Publisher<String> apply(ServerWebExchange exchange, String body) {
        return Mono.just(
            "<html><head><title>SearchMIW API Gateway</title>" +
            "<style>" +
            "body { font-family: Arial, sans-serif; line-height: 1.6; max-width: 800px; margin: 0 auto; padding: 20px; }" +
            "h1 { color: #4285F4; }" +
            "h2 { color: #34A853; margin-top: 30px; }" +
            "code { background: #f4f4f4; padding: 2px 5px; border-radius: 3px; }" +
            ".endpoint { background: #f8f9fa; padding: 15px; border-left: 4px solid #4285F4; margin-bottom: 15px; }" +
            "</style></head>" +
            "<body>" +
            "<h1>SearchMIW API Gateway</h1>" +
            "<p>Welcome to the SearchMIW API Gateway. Below are the available endpoints:</p>" +
            "<h2>Authentication Endpoints</h2>" +
            "<div class='endpoint'>" +
            "<strong>POST /auth/login</strong><br>" +
            "<p>Login with email and password</p>" +
            "</div>" +
            "<div class='endpoint'>" +
            "<strong>POST /auth/register</strong><br>" +
            "<p>Register a new user</p>" +
            "</div>" +
            "<p>For more information, please refer to the API documentation.</p>" +
            "</body></html>"
        );
    }
}
