package com.searchmiw.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.searchmiw.user.dto.UserRequest;
import com.searchmiw.user.dto.UserUpdateRequest;
import com.searchmiw.user.model.User;
import com.searchmiw.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    void getAllUsers_ReturnsEmptyList_WhenNoUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getAllUsers_ReturnsAllUsers_WhenUsersExist() throws Exception {
        // Create test users
        createTestUser("user1@example.com", "User One");
        createTestUser("user2@example.com", "User Two");

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].email", containsInAnyOrder("user1@example.com", "user2@example.com")))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("User One", "User Two")));
    }

    @Test
    void getUserById_ReturnsUser_WhenUserExists() throws Exception {
        User user = createTestUser("getbyid@example.com", "Get By ID User");

        mockMvc.perform(get("/api/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Get By ID User")))
                .andExpect(jsonPath("$.email", is("getbyid@example.com")));
    }

    @Test
    void getUserById_ReturnsNotFound_WhenUserDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/users/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUser_CreatesAndReturnsUser_WhenValidInput() throws Exception {
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail("newuser@example.com");
        userRequest.setName("New User");
        userRequest.setPassword("password123");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("New User")))
                .andExpect(jsonPath("$.email", is("newuser@example.com")))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    void createUser_ReturnsBadRequest_WhenEmailIsInvalid() throws Exception {
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail("invalid-email"); // Invalid email format
        userRequest.setName("Invalid Email User");
        userRequest.setPassword("password123");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_ReturnsBadRequest_WhenNameIsEmpty() throws Exception {
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail("valid@example.com");
        userRequest.setName(""); // Empty name
        userRequest.setPassword("password123");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_ReturnsConflict_WhenEmailAlreadyExists() throws Exception {
        // First create a user
        createTestUser("duplicate@example.com", "Duplicate User");

        // Try to create another with the same email
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail("duplicate@example.com");
        userRequest.setName("Second User");
        userRequest.setPassword("password123");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    void updateUser_UpdatesAndReturnsUser_WhenValidInput() throws Exception {
        User user = createTestUser("update@example.com", "Original Name");

        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setName("Updated Name");

        mockMvc.perform(put("/api/users/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Updated Name")))
                .andExpect(jsonPath("$.email", is("update@example.com")));
    }

    @Test
    void updateUser_ReturnsNotFound_WhenUserDoesNotExist() throws Exception {
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setName("Updated Name");

        mockMvc.perform(put("/api/users/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_ReturnsConflict_WhenEmailAlreadyExists() throws Exception {
        // Create first user
        createTestUser("existing@example.com", "Existing User");

        // Create second user to update
        User secondUser = createTestUser("toupdate@example.com", "Update User");

        // Try to update second user with first user's email
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setEmail("existing@example.com");

        mockMvc.perform(put("/api/users/{id}", secondUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteUser_DeletesUser_WhenUserExists() throws Exception {
        User user = createTestUser("delete@example.com", "Delete User");

        mockMvc.perform(delete("/api/users/{id}", user.getId()))
                .andExpect(status().isNoContent());

        // Verify user is deleted
        mockMvc.perform(get("/api/users/{id}", user.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_ReturnsNotFound_WhenUserDoesNotExist() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    void findUserByEmail_ReturnsUser_WhenUserExists() throws Exception {
        createTestUser("findbyemail@example.com", "Find By Email User");

        mockMvc.perform(get("/api/users/email/{email}", "findbyemail@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Find By Email User")))
                .andExpect(jsonPath("$.email", is("findbyemail@example.com")));
    }

    @Test
    void findUserByEmail_ReturnsNotFound_WhenUserDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/users/email/{email}", "nonexistent@example.com"))
                .andExpect(status().isNotFound());
    }

    // Helper method to create a test user
    private User createTestUser(String email, String name) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPassword("$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG"); // Encoded "password"
        return userRepository.save(user);
    }
}
