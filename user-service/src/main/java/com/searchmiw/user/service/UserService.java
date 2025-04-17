package com.searchmiw.user.service;

import com.searchmiw.user.dto.AuthRequest;
import com.searchmiw.user.dto.AuthResponse;
import com.searchmiw.user.dto.CreateUserRequest;
import com.searchmiw.user.dto.UpdateUserRequest;
import com.searchmiw.user.model.User;
import com.searchmiw.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
    }
    
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Transactional
    public User createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }
        
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .username(request.getUsername())
                .status(User.UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
        
        return userRepository.save(user);
    }
    
    @Transactional
    public User updateUser(String id, UpdateUserRequest request) {
        User user = getUserById(id);
        
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        
        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        
        return userRepository.save(user);
    }
    
    @Transactional
    public void deleteUser(String id) {
        User user = getUserById(id);
        user.setStatus(User.UserStatus.DELETED);
        userRepository.save(user);
    }
    
    public AuthResponse authenticate(AuthRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        
        if (userOptional.isEmpty()) {
            return new AuthResponse(null, request.getEmail(), null, false);
        }
        
        User user = userOptional.get();
        boolean isAuthenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        
        if (isAuthenticated) {
            // Update last login time
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
            return new AuthResponse(user.getId(), user.getEmail(), user.getName(), true);
        }
        
        return new AuthResponse(null, request.getEmail(), null, false);
    }
    
    public boolean checkEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
