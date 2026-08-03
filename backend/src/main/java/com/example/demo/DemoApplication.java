package com.example.demo;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                List<User> users = new java.util.ArrayList<>();
                for (int i = 1; i <= 20; i++) {
                    users.add(User.builder().firstName("User").lastName(String.valueOf(i)).phoneNumber(String.valueOf(100000000 + i)).relationships(new java.util.ArrayList<>()).build());
                }
                userRepository.saveAll(users);

                // Define connections (e.g., User 1 connects to 2, 3, 4)
                addConnection(users, "User 1", "User 2");
                addConnection(users, "User 1", "User 3");
                addConnection(users, "User 2", "User 5");
                // Removed connection: User 3 - User 6
                addConnection(users, "User 4", "User 7");
                addConnection(users, "User 5", "User 8");
                addConnection(users, "User 6", "User 9");
                addConnection(users, "User 7", "User 10");
                addConnection(users, "User 8", "User 11");
                addConnection(users, "User 9", "User 12");
                addConnection(users, "User 10", "User 13");
                addConnection(users, "User 11", "User 14");
                addConnection(users, "User 12", "User 15");
                addConnection(users, "User 13", "User 16");
                addConnection(users, "User 14", "User 17");
                // Removed connection: User 15 - User 18
                addConnection(users, "User 16", "User 19");
                addConnection(users, "User 17", "User 20");
                addConnection(users, "User 18", "User 1");
                addConnection(users, "User 19", "User 2");
                addConnection(users, "User 20", "User 3");
                
                userRepository.saveAll(users);
            }
        };
    }

    private void addConnection(List<User> users, String name1, String name2) {
        User u1 = users.stream().filter(u -> (u.getFirstName() + " " + u.getLastName()).equals(name1)).findFirst().orElseThrow();
        User u2 = users.stream().filter(u -> (u.getFirstName() + " " + u.getLastName()).equals(name2)).findFirst().orElseThrow();
        u1.getRelationships().add(u2.getId().toString());
        u2.getRelationships().add(u1.getId().toString()); // Bidirectional for graph consistency
    }
}
