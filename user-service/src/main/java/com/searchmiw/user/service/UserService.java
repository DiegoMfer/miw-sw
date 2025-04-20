package com.searchmiw.user.service;

import com.searchmiw.user.dto.UserRequest;
import com.searchmiw.user.dto.UserUpdateRequest;
import com.searchmiw.user.exception.DuplicateEmailException;
import com.searchmiw.user.exception.UserNotFoundException;
import com.searchmiw.user.model.User;
import com.searchmiw.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public User createUser(UserRequest userRequest) {
        // Check if user with email already exists
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new DuplicateEmailException("Email already in use: " + userRequest.getEmail());
        }
        
        User user = User.builder()
                .email(userRequest.getEmail())
                .name(userRequest.getName())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .build();
        
        return userRepository.save(user);
    }
    
    public User updateUser(Long id, UserUpdateRequest updateRequest) {
        User user = getUserById(id);
        
        // Only update fields that are provided
        if (updateRequest.getEmail() != null && !updateRequest.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updateRequest.getEmail())) {
                throw new DuplicateEmailException("Email already in use: " + updateRequest.getEmail());
            }
            user.setEmail(updateRequest.getEmail());
        }
        
        if (updateRequest.getName() != null) {
            user.setName(updateRequest.getName());
        }
        
        if (updateRequest.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
        }
        
        return userRepository.save(user);
    }
    
    public void deleteUser(Long id) {
        // Verify user exists
        getUserById(id);
        
        // Delete user
        userRepository.deleteById(id);
    }
    
    public boolean verifyPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}
