package com.example.demo.controller;

import com.example.demo.model.ConnectionRequest;
import com.example.demo.model.User;
import com.example.demo.repository.ConnectionRequestRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class RequestController {

    private final ConnectionRequestRepository requestRepository;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<ConnectionRequest> sendRequest(@RequestBody Map<String, String> request) {
        User sender = userRepository.findById(UUID.fromString(request.get("senderId"))).orElseThrow();
        User receiver = userRepository.findById(UUID.fromString(request.get("receiverId"))).orElseThrow();
        
        ConnectionRequest connRequest = ConnectionRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .status(ConnectionRequest.RequestStatus.PENDING)
                .build();
        return ResponseEntity.ok(requestRepository.save(connRequest));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<ConnectionRequest>> getRequests(@PathVariable UUID userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return ResponseEntity.ok(requestRepository.findBySenderOrReceiver(user, user));
    }

    @PutMapping("/{requestId}/accept")
    public ResponseEntity<ConnectionRequest> acceptRequest(@PathVariable UUID requestId) {
        ConnectionRequest request = requestRepository.findById(requestId).orElseThrow();
        request.setStatus(ConnectionRequest.RequestStatus.ACCEPTED);
        
        // Add connection
        User sender = request.getSender();
        User receiver = request.getReceiver();
        sender.getRelationships().add(receiver.getId().toString());
        receiver.getRelationships().add(sender.getId().toString());
        userRepository.save(sender);
        userRepository.save(receiver);

        return ResponseEntity.ok(requestRepository.save(request));
    }
}
