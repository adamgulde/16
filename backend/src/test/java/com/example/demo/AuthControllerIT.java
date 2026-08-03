package com.example.demo;

import com.example.demo.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class AuthControllerIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldRegisterAndLoginWithoutPassword() {
        // Register
        Map<String, String> regCredentials = Map.of("username", "testuser", "password", "secret");
        ResponseEntity<User> regResponse = restTemplate.postForEntity("http://localhost:" + port + "/api/auth/register", regCredentials, User.class);
        
        assertThat(regResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(regResponse.getBody()).isNotNull();
        assertThat(regResponse.getBody().getName()).isEqualTo("testuser");

        // Login without password
        Map<String, String> loginCredentials = Map.of("username", "testuser");
        ResponseEntity<User> loginResponse = restTemplate.postForEntity("http://localhost:" + port + "/api/auth/login", loginCredentials, User.class);
        
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).isNotNull();
        assertThat(loginResponse.getBody().getName()).isEqualTo("testuser");
    }
}
