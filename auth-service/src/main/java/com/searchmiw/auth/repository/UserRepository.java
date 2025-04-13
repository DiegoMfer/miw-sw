package com.searchmiw.auth.repository;

import com.searchmiw.auth.model.User;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserRepository {
    
    // In-memory user storage (replace with a real database in production)
    private final Map<String, User> users = new ConcurrentHashMap<>();
    
    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }
    
    public User save(User user) {
        users.put(user.getId(), user);
        return user;
    }
    
    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }
}
