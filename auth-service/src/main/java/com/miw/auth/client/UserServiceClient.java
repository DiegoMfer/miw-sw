package com.miw.auth.client;

import com.miw.auth.dto.UserCredentials;
import com.miw.auth.dto.UserDTO;
import com.miw.auth.dto.UserRegistrationDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class UserServiceClient {
    private final RestTemplate restTemplate;
    private final String userServiceBaseUrl;

    public UserServiceClient(RestTemplate restTemplate, 
                          @Value("${service.user.url:http://localhost:8086}") String userServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.userServiceBaseUrl = userServiceBaseUrl;
    }

    public UserDTO createUser(UserRegistrationDTO registrationDTO) {
        String url = userServiceBaseUrl + "/api/users";
        ResponseEntity<UserDTO> response = restTemplate.postForEntity(url, registrationDTO, UserDTO.class);
        return response.getBody();
    }

    public UserDTO getUserByEmail(String email) {
        try {
            String url = userServiceBaseUrl + "/api/users/email/" + email;
            ResponseEntity<UserDTO> response = restTemplate.getForEntity(url, UserDTO.class);
            return response.getBody();
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                return null;
            }
            throw ex;
        }
    }

    public boolean verifyCredentials(UserCredentials credentials) {
        try {
            String url = userServiceBaseUrl + "/api/users/verify";
            ResponseEntity<Boolean> response = restTemplate.postForEntity(url, credentials, Boolean.class);
            return Boolean.TRUE.equals(response.getBody());
        } catch (HttpClientErrorException ex) {
            return false;
        }
    }
}
