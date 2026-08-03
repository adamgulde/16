package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<UUID> findShortestPath(UUID startId, UUID targetId) {
        List<User> allUsers = userRepository.findAll();
        Map<UUID, User> userMap = allUsers.stream().collect(Collectors.toMap(User::getId, u -> u));

        Queue<UUID> queue = new LinkedList<>();
        queue.add(startId);
        Map<UUID, UUID> parentMap = new HashMap<>();
        parentMap.put(startId, null);

        while (!queue.isEmpty()) {
            UUID currentId = queue.poll();
            if (currentId.equals(targetId)) {
                return reconstructPath(parentMap, targetId);
            }

            User currentUser = userMap.get(currentId);
            if (currentUser != null) {
                for (String neighborIdStr : currentUser.getRelationships()) {
                    UUID neighborId = UUID.fromString(neighborIdStr);
                    if (!parentMap.containsKey(neighborId)) {
                        parentMap.put(neighborId, currentId);
                        queue.add(neighborId);
                    }
                }
            }
        }
        return Collections.emptyList();
    }

    private List<UUID> reconstructPath(Map<UUID, UUID> parentMap, UUID targetId) {
        List<UUID> path = new LinkedList<>();
        for (UUID at = targetId; at != null; at = parentMap.get(at)) {
            path.add(0, at);
        }
        return path;
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getPublicUserById(UUID targetId, UUID requestingUserId) {
        User targetUser = getUserById(targetId);
        User requestingUser = getUserById(requestingUserId);
        
        if (!targetUser.getRelationships().contains(requestingUserId.toString()) && !targetId.equals(requestingUserId)) {
            targetUser.setPhoneNumber(null);
        }
        return targetUser;
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(UUID id, User userDetails) {
        User user = getUserById(id);
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setPhoneNumber(userDetails.getPhoneNumber());
        user.setRelationships(userDetails.getRelationships());
        return userRepository.save(user);
    }

    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }
}
