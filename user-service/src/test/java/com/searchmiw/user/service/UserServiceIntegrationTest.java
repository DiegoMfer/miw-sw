package com.searchmiw.user.service;

import com.searchmiw.user.dto.UserRequest;
import com.searchmiw.user.dto.UserUpdateRequest;
import com.searchmiw.user.exception.DuplicateEmailException;
import com.searchmiw.user.exception.UserNotFoundException;
import com.searchmiw.user.model.User;
import com.searchmiw.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createUser_ValidUser_ReturnsCreatedUser() {
        // Arrange
        UserRequest userRequest = new UserRequest();
        userRequest.setName("Test User");
        userRequest.setEmail("test@example.com");
        userRequest.setPassword("password123");

        // Act
        User createdUser = userService.createUser(userRequest);

        // Assert
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId());
        assertEquals("Test User", createdUser.getName());
        assertEquals("test@example.com", createdUser.getEmail());
        assertNotNull(createdUser.getPassword());
        assertTrue(createdUser.getPassword().length() > 0);
        assertNotEquals("password123", createdUser.getPassword()); // Password should be encoded
    }

    @Test
    void createUser_DuplicateEmail_ThrowsException() {
        // Arrange
        UserRequest userRequest1 = new UserRequest();
        userRequest1.setName("Test User 1");
        userRequest1.setEmail("duplicate@example.com");
        userRequest1.setPassword("password123");
        
        UserRequest userRequest2 = new UserRequest();
        userRequest2.setName("Test User 2");
        userRequest2.setEmail("duplicate@example.com"); // Same email
        userRequest2.setPassword("password456");

        // Act & Assert
        userService.createUser(userRequest1); // First user should be created
        assertThrows(DuplicateEmailException.class, () -> {
            userService.createUser(userRequest2); // Second user should throw exception
        });
    }

    @Test
    void getUserById_ExistingUser_ReturnsUser() {
        // Arrange
        UserRequest userRequest = new UserRequest();
        userRequest.setName("Get By Id User");
        userRequest.setEmail("getbyid@example.com");
        userRequest.setPassword("password123");
        User createdUser = userService.createUser(userRequest);

        // Act
        User foundUser = userService.getUserById(createdUser.getId());

        // Assert
        assertNotNull(foundUser);
        assertEquals(createdUser.getId(), foundUser.getId());
        assertEquals("Get By Id User", foundUser.getName());
        assertEquals("getbyid@example.com", foundUser.getEmail());
    }

    @Test
    void getUserById_NonExistingUser_ThrowsException() {
        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserById(9999L);
        });
    }

    @Test
    void getAllUsers_ReturnsAllUsers() {
        // Arrange
        userRepository.deleteAll(); // Start with clean state
        
        UserRequest userRequest1 = new UserRequest();
        userRequest1.setName("User One");
        userRequest1.setEmail("user1@example.com");
        userRequest1.setPassword("password1");
        userService.createUser(userRequest1);
        
        UserRequest userRequest2 = new UserRequest();
        userRequest2.setName("User Two");
        userRequest2.setEmail("user2@example.com");
        userRequest2.setPassword("password2");
        userService.createUser(userRequest2);

        // Act
        List<User> users = userService.getAllUsers();

        // Assert
        assertEquals(2, users.size());
    }

    @Test
    void findByEmail_ExistingEmail_ReturnsUser() {
        // Arrange
        UserRequest userRequest = new UserRequest();
        userRequest.setName("Email Search User");
        userRequest.setEmail("emailsearch@example.com");
        userRequest.setPassword("password123");
        userService.createUser(userRequest);

        // Act
        Optional<User> userOpt = userService.findByEmail("emailsearch@example.com");

        // Assert
        assertTrue(userOpt.isPresent());
        assertEquals("Email Search User", userOpt.get().getName());
        assertEquals("emailsearch@example.com", userOpt.get().getEmail());
    }

    @Test
    void findByEmail_NonExistingEmail_ReturnsEmpty() {
        // Act
        Optional<User> userOpt = userService.findByEmail("nonexistent@example.com");

        // Assert
        assertFalse(userOpt.isPresent());
    }

    @Test
    void updateUser_ValidUpdate_UpdatesUser() {
        // Arrange
        UserRequest createRequest = new UserRequest();
        createRequest.setName("Original Name");
        createRequest.setEmail("original@example.com");
        createRequest.setPassword("password123");
        User createdUser = userService.createUser(createRequest);

        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setName("Updated Name");

        // Act
        User updatedUser = userService.updateUser(createdUser.getId(), updateRequest);

        // Assert
        assertEquals("Updated Name", updatedUser.getName());
        assertEquals("original@example.com", updatedUser.getEmail()); // Email unchanged
    }

    @Test
    void updateUser_EmailUpdate_UpdatesEmail() {
        // Arrange
        UserRequest createRequest = new UserRequest();
        createRequest.setName("Email Update User");
        createRequest.setEmail("oldemail@example.com");
        createRequest.setPassword("password123");
        User createdUser = userService.createUser(createRequest);

        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setEmail("newemail@example.com");

        // Act
        User updatedUser = userService.updateUser(createdUser.getId(), updateRequest);

        // Assert
        assertEquals("Email Update User", updatedUser.getName()); // Name unchanged
        assertEquals("newemail@example.com", updatedUser.getEmail());
    }

    @Test
    void updateUser_DuplicateEmail_ThrowsException() {
        // Arrange
        // Create first user
        UserRequest createRequest1 = new UserRequest();
        createRequest1.setName("First User");
        createRequest1.setEmail("first@example.com");
        createRequest1.setPassword("password123");
        userService.createUser(createRequest1);

        // Create second user
        UserRequest createRequest2 = new UserRequest();
        createRequest2.setName("Second User");
        createRequest2.setEmail("second@example.com");
        createRequest2.setPassword("password456");
        User secondUser = userService.createUser(createRequest2);

        // Try to update second user's email to first user's email
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setEmail("first@example.com");

        // Act & Assert
        assertThrows(DuplicateEmailException.class, () -> {
            userService.updateUser(secondUser.getId(), updateRequest);
        });
    }

    @Test
    void deleteUser_ExistingUser_DeletesUser() {
        // Arrange
        UserRequest createRequest = new UserRequest();
        createRequest.setName("Delete Me");
        createRequest.setEmail("deleteme@example.com");
        createRequest.setPassword("password123");
        User createdUser = userService.createUser(createRequest);

        // Act
        userService.deleteUser(createdUser.getId());

        // Assert
        assertFalse(userRepository.existsById(createdUser.getId()));
    }

    @Test
    void deleteUser_NonExistingUser_ThrowsException() {
        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            userService.deleteUser(9999L);
        });
    }

    @Test
    void verifyPassword_CorrectPassword_ReturnsTrue() {
        // Arrange
        UserRequest createRequest = new UserRequest();
        createRequest.setName("Password Test");
        createRequest.setEmail("passwordtest@example.com");
        createRequest.setPassword("correctPassword123");
        User createdUser = userService.createUser(createRequest);

        // Act
        boolean passwordMatches = userService.verifyPassword(createdUser, "correctPassword123");

        // Assert
        assertTrue(passwordMatches);
    }

    @Test
    void verifyPassword_IncorrectPassword_ReturnsFalse() {
        // Arrange
        UserRequest createRequest = new UserRequest();
        createRequest.setName("Password Test");
        createRequest.setEmail("passwordtest2@example.com");
        createRequest.setPassword("correctPassword123");
        User createdUser = userService.createUser(createRequest);

        // Act
        boolean passwordMatches = userService.verifyPassword(createdUser, "wrongPassword123");

        // Assert
        assertFalse(passwordMatches);
    }
}
