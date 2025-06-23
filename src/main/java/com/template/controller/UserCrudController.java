package com.template.controller;

import com.template.dto.UserCreateRequest;
import com.template.entity.User;
import com.template.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;
import java.util.HashSet;

@RestController
@RequestMapping("/api/users/crud")
@RequiredArgsConstructor
@Validated
public class UserCrudController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Value("${auth.account-expiry-years:1}")
    private int accountExpiryYears;

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody UserCreateRequest userRequest) {
        // Check if username already exists
        if (userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        
        // Check if email already exists
        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        
        // Create new user
        User user = User.builder()
                .username(userRequest.getUsername())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .email(userRequest.getEmail())
                .accountNonLocked(true)
                .failedLoginAttempts(0)
                .accountExpiryDate(LocalDateTime.now().plusYears(accountExpiryYears))
                .roles(new HashSet<>())
                .build();
        
        return ResponseEntity.ok(userRepository.save(user));
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable @Positive(message = "User ID must be positive") Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable @Positive(message = "User ID must be positive") Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}