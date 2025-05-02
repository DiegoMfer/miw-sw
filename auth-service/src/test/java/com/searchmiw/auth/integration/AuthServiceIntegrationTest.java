package com.searchmiw.auth.integration;

import com.searchmiw.auth.AuthServiceApplication;
import com.searchmiw.auth.config.JwtConfig;
import com.searchmiw.auth.model.AuthRequest;
import com.searchmiw.auth.model.RegisterRequest;
import com.searchmiw.auth.model.UserDto;
import com.searchmiw.auth.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = AuthServiceApplication.class)
@AutoConfigureWebTestClient
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class AuthServiceIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtConfig jwtConfig;

    // Test data
    private final String TEST_EMAIL = "test@example.com";
    private final String TEST_PASSWORD = "password123";
    private final String TEST_NAME = "Test User";
    private final Long TEST_USER_ID = 1L;
    private final String TEST_TOKEN = "test-jwt-token";
    private List<UserDto> testUsers;
    
    @BeforeEach
    public void setUp() {
        // Set up test data
        testUsers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            testUsers.add(new UserDto((long) i, "User " + i, "user" + i + "@example.com"));
        }
        
        UserDto mockUser = new UserDto(TEST_USER_ID, TEST_NAME, TEST_EMAIL);
        testUsers.add(mockUser);
        
        // Mock JWT token generation
        when(jwtConfig.generateToken(anyString(), any(Long.class))).thenReturn(TEST_TOKEN);
        when(jwtConfig.validateToken(TEST_TOKEN)).thenReturn(true);
        when(jwtConfig.validateToken(Mockito.argThat(token -> !token.equals(TEST_TOKEN)))).thenReturn(false);
        
        // Mock user service methods
        when(userService.verifyCredentials(TEST_EMAIL, TEST_PASSWORD)).thenReturn(Mono.just(true));
        when(userService.verifyCredentials(Mockito.argThat(email -> !email.equals(TEST_EMAIL)), anyString()))
            .thenReturn(Mono.just(false));
        
        when(userService.getUserByEmail(TEST_EMAIL)).thenReturn(Mono.just(mockUser));
        when(userService.getUserByEmail(Mockito.argThat(email -> !email.equals(TEST_EMAIL))))
            .thenReturn(Mono.empty());
        
        when(userService.createUser(any(RegisterRequest.class))).thenReturn(Mono.just(mockUser));
        
        // Mock for existing email
        when(userService.createUser(Mockito.argThat(req -> "existing@example.com".equals(req.getEmail()))))
                .thenReturn(Mono.error(new RuntimeException("Email already in use")));
    }

    @Test
    public void whenLoginWithValidCredentials_thenSuccess() {
        AuthRequest request = new AuthRequest(TEST_EMAIL, TEST_PASSWORD);

        EntityExchangeResult<Map> result = webTestClient.post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .returnResult();

        Map<String, Object> response = result.getResponseBody();
        assertThat(response).isNotNull();
        assertThat(response.get("token")).isEqualTo(TEST_TOKEN);
        // Fix the type comparison for userId
        assertThat(((Number)response.get("userId")).longValue()).isEqualTo(TEST_USER_ID);
        assertThat(response.get("name")).isEqualTo(TEST_NAME);
        assertThat(response.get("email")).isEqualTo(TEST_EMAIL);
        assertThat(response.get("message")).isEqualTo("Authentication successful");
    }

    @Test
    public void whenLoginWithInvalidCredentials_thenUnauthorized() {
        AuthRequest request = new AuthRequest("wrong@example.com", "wrongpassword");

        EntityExchangeResult<Map> result = webTestClient.post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(Map.class)
                .returnResult();

        Map<String, Object> response = result.getResponseBody();
        assertThat(response).isNotNull();
        assertThat(response.get("message")).isEqualTo("Invalid credentials");
    }

    @Test
    public void whenRegisterNewUser_thenSuccess() {
        RegisterRequest request = new RegisterRequest();
        request.setName(TEST_NAME);
        request.setEmail(TEST_EMAIL);
        request.setPassword(TEST_PASSWORD);

        EntityExchangeResult<Map> result = webTestClient.post()
                .uri("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Map.class)
                .returnResult();

        Map<String, Object> response = result.getResponseBody();
        assertThat(response).isNotNull();
        assertThat(response.get("token")).isEqualTo(TEST_TOKEN);
        // Fix the type comparison for userId
        assertThat(((Number)response.get("userId")).longValue()).isEqualTo(TEST_USER_ID);
        assertThat(response.get("name")).isEqualTo(TEST_NAME);
        assertThat(response.get("email")).isEqualTo(TEST_EMAIL);
        assertThat(response.get("message")).isEqualTo("Registration successful");
    }

    @Test
    public void whenRegisterExistingEmail_thenConflict() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Existing User");
        request.setEmail("existing@example.com");
        request.setPassword(TEST_PASSWORD);

        EntityExchangeResult<Map> result = webTestClient.post()
                .uri("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(Map.class)
                .returnResult();

        Map<String, Object> response = result.getResponseBody();
        assertThat(response).isNotNull();
        assertThat(response.get("message")).isEqualTo("Email already in use");
    }

    @Test
    public void whenValidateValidToken_thenSuccess() {
        EntityExchangeResult<Boolean> result = webTestClient.get()
                .uri("/api/auth/validate")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_TOKEN)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .returnResult();

        Boolean isValid = result.getResponseBody();
        assertThat(isValid).isTrue();
    }

    @Test
    public void whenValidateInvalidToken_thenReturnFalse() {
        EntityExchangeResult<Boolean> result = webTestClient.get()
                .uri("/api/auth/validate")
                .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .returnResult();

        Boolean isValid = result.getResponseBody();
        assertThat(isValid).isFalse();
    }

    @Test
    public void whenValidateMissingToken_thenBadRequest() {
        // Updated to expect 400 BAD_REQUEST instead of 200 OK
        EntityExchangeResult<Map> result = webTestClient.get()
                .uri("/api/auth/validate")
                .exchange()
                .expectStatus().isBadRequest() // Changed from isOk() to isBadRequest()
                .expectBody(Map.class)
                .returnResult();

        Map<String, Object> response = result.getResponseBody();
        assertThat(response).isNotNull();
        assertThat(response.get("status")).isEqualTo(400);
        assertThat(response.get("error")).isEqualTo("Bad Request");
        // We can also verify the path matches the endpoint
        assertThat(response.get("path")).isEqualTo("/api/auth/validate");
    }
}
