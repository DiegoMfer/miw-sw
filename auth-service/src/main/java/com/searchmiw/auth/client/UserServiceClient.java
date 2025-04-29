package com.searchmiw.auth.client;

import com.searchmiw.auth.model.AuthRequest;
import com.searchmiw.auth.model.RegisterRequest;
import com.searchmiw.auth.model.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service", url = "${services.user.url}")
public interface UserServiceClient {
    
    @PostMapping("/api/users")
    ResponseEntity<UserDto> createUser(@RequestBody RegisterRequest request);
    
    @PostMapping("/api/users/verify")
    ResponseEntity<Boolean> verifyCredentials(@RequestBody AuthRequest request);
    
    @GetMapping("/api/users/email/{email}")
    ResponseEntity<UserDto> getUserByEmail(@PathVariable("email") String email);
}
