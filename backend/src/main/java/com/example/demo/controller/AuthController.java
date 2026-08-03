package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.model.UserCredentials;
import com.example.demo.repository.UserCredentialsRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final UserCredentialsRepository userCredentialsRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        String firstName = credentials.get("firstName");
        String lastName = credentials.get("lastName");
        String phoneNumber = credentials.get("phoneNumber");

        if (userCredentialsRepository.findAll().stream().anyMatch(c -> c.getUsername().equalsIgnoreCase(username))) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(phoneNumber)
                .relationships(new ArrayList<>())
                .build();
        user = userRepository.save(user);

        UserCredentials userCredentials = UserCredentials.builder()
                .username(username)
                .password(password)
                .user(user)
                .build();
        userCredentialsRepository.save(userCredentials);

        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");

        if (username == null) {
            return ResponseEntity.badRequest().body("Username is required");
        }

        // Try to find by credentials, fallback to searching user by name
        return userCredentialsRepository.findByUsername(username)
                .<ResponseEntity<Object>>map(c -> ResponseEntity.ok(c.getUser()))
                .orElseGet(() -> {
                    // Fallback: search by user name (split by space)
                    String[] parts = username.split(" ");
                    if (parts.length == 2) {
                        return userRepository.findByFirstNameAndLastName(parts[0], parts[1])
                                .<ResponseEntity<Object>>map(ResponseEntity::ok)
                                .orElseGet(() -> ResponseEntity.status(401).body("User not found: " + username));
                    }
                    return ResponseEntity.status(401).body("User not found: " + username);
                });
    }
}
