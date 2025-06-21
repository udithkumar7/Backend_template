package com.template.controller;

import com.template.dto.LoginRequest;
import com.template.dto.LoginResponse;
import com.template.entity.User;
import com.template.service.AuthenticationService;
import com.template.service.TokenBlacklistService;
import com.template.service.UserSessionService;
import com.template.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private final UserSessionService userSessionService;
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthenticationService.AuthenticationResult result = 
            authenticationService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
        
        if (result.isSuccess()) {
            User user = result.getUser();
            Set<String> authorities = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(java.util.stream.Collectors.toSet());
            
            String token = jwtUtil.generateToken(user.getUsername(), authorities);
            Instant expiry = jwtUtil.extractExpiration(token).toInstant();
            userSessionService.registerToken(user.getUsername(), token, expiry);
            
            return ResponseEntity.ok(new LoginResponse(token, user.getUsername(), "Login successful"));
        } else {
            return ResponseEntity.badRequest().body(new LoginResponse(null, null, result.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") @NotBlank(message = "Authorization header is required") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Instant expiry = jwtUtil.extractExpiration(token).toInstant();
        tokenBlacklistService.blacklistToken(token, expiry);
        // Optionally remove from session map
        String username = jwtUtil.extractUsername(token);
        userSessionService.removeToken(username, token);
        return ResponseEntity.ok("Logged out and token blacklisted");
    }
}