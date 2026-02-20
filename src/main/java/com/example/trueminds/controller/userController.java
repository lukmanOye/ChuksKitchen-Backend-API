package com.example.trueminds.controller;

import com.example.trueminds.dtos.*;
import com.example.trueminds.service.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class userController {

    @Autowired
    private userService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@RequestBody SignupRequest request) {
        UserResponse response = userService.signup(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody LoginRequest request) {
        UserResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<UserResponse> verify(@RequestBody VerifyRequest request) {
        UserResponse response = userService.verify(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<String> resendOtp(@RequestParam String email) {
        String message = userService.resendOtp(email);
        return ResponseEntity.ok(message);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/{id}/topup")
    public ResponseEntity<String> topUpBalance(@PathVariable String id, @RequestBody TopUpRequest request) {
        userService.topUpBalance(id, request);
        return ResponseEntity.ok("Balance topped up successfully");
    }

    @GetMapping("/email")
    public ResponseEntity<UserResponse> getUserByEmail(@RequestParam String email) {
        UserResponse user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @RequestParam String requesterId,
            @PathVariable String id,
            @RequestBody UpdateUserRequest request) {
        UserResponse response = userService.updateUser(requesterId, id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @RequestParam String requesterId,
            @PathVariable String id) {
        userService.deleteUser(requesterId, id);
        return ResponseEntity.noContent().build();
    }
}