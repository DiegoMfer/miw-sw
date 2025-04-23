package com.searchmiw.aggregator.resolver;

import com.searchmiw.aggregator.model.User;
import com.searchmiw.aggregator.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserResolver {

    private final UserService userService;

    @QueryMapping
    public Mono<List<User>> users() {
        return userService.getAllUsers().collectList();
    }

    @QueryMapping
    public Mono<User> userById(@Argument Long id) {
        return userService.getUserById(id);
    }
}
