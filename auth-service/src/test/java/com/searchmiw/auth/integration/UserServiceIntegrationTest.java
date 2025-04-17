package com.searchmiw.auth.integration;

import com.searchmiw.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private WebClient.Builder webClientBuilder;
    
    @MockBean
    private AuthService authService;
    
    @Test
    public void testRegisterEndpoint() throws Exception {
        String uuid = UUID.randomUUID().toString();
        String email = "test" + uuid + "@example.com";
        String json = "{\"email\":\"" + email + "\",\"password\":\"password123\",\"name\":\"Test User\"}";
        
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testLoginEndpoint() throws Exception {
        String json = "{\"email\":\"test@example.com\",\"password\":\"password123\"}";
        
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }
}
