package com.template.controller;

import com.template.dto.LoginRequest;
import com.template.dto.LoginResponse;
import com.template.dto.RefreshTokenRequest;
import com.template.entity.RefreshToken;
import com.template.entity.User;
import com.template.service.AuthenticationService;
import com.template.service.RefreshTokenService;
import com.template.service.TokenBlacklistService;
import com.template.service.UserSessionService;
import com.template.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private final UserSessionService userSessionService;
    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest, 
                                             HttpServletRequest request) {
        AuthenticationService.AuthenticationResult result = 
            authenticationService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
        
        if (result.isSuccess()) {
            User user = result.getUser();
            Set<String> authorities = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(java.util.stream.Collectors.toSet());
            
            // Generate access token
            String accessToken = jwtUtil.generateAccessToken(user.getUsername(), authorities);
            Instant accessTokenExpiry = jwtUtil.extractExpiration(accessToken).toInstant();
            
            // Generate refresh token
            String deviceInfo = getDeviceInfo(request);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUsername(), deviceInfo);
            
            // Register access token for session management
            userSessionService.registerToken(user.getUsername(), accessToken, accessTokenExpiry);
            
            // Calculate expires in seconds
            long expiresInSeconds = (accessTokenExpiry.toEpochMilli() - System.currentTimeMillis()) / 1000;
            
            return ResponseEntity.ok(new LoginResponse(
                accessToken, 
                refreshToken.getToken(), 
                user.getUsername(), 
                "Login successful",
                expiresInSeconds
            ));
        } else {
            return ResponseEntity.badRequest().body(new LoginResponse(null, null, null, result.getMessage(), 0));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestBody RefreshTokenRequest request, 
                                               HttpServletRequest httpRequest) {
        try {
            String deviceInfo = getDeviceInfo(httpRequest);
            RefreshTokenService.TokenPair tokenPair = refreshTokenService.refreshTokens(
                request.getRefreshToken(), 
                deviceInfo
            );
            
            // Extract username from new access token for response
            String username = jwtUtil.extractUsername(tokenPair.getAccessToken());
            Instant accessTokenExpiry = jwtUtil.extractExpiration(tokenPair.getAccessToken()).toInstant();
            
            // Register new access token for session management
            userSessionService.registerToken(username, tokenPair.getAccessToken(), accessTokenExpiry);
            
            // Calculate expires in seconds
            long expiresInSeconds = (accessTokenExpiry.toEpochMilli() - System.currentTimeMillis()) / 1000;
            
            return ResponseEntity.ok(new LoginResponse(
                tokenPair.getAccessToken(),
                tokenPair.getRefreshToken(),
                username,
                "Token refreshed successfully",
                expiresInSeconds
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new LoginResponse(null, null, null, e.getMessage(), 0));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader,
                                  @RequestBody(required = false) RefreshTokenRequest refreshRequest) {
        try {
            // Handle access token
            String accessToken = authHeader.replace("Bearer ", "");
            Instant expiry = jwtUtil.extractExpiration(accessToken).toInstant();
            tokenBlacklistService.blacklistToken(accessToken, expiry);
            
            // Remove from session map
            String username = jwtUtil.extractUsername(accessToken);
            userSessionService.removeToken(username, accessToken);
            
            // Handle refresh token if provided
            if (refreshRequest != null && refreshRequest.getRefreshToken() != null) {
                refreshTokenService.revokeToken(refreshRequest.getRefreshToken());
            }
            
            return ResponseEntity.ok("Logged out successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Logout failed: " + e.getMessage());
        }
    }

    @PostMapping("/logout-all")
    public ResponseEntity<?> logoutAll(@RequestHeader("Authorization") String authHeader) {
        try {
            String accessToken = authHeader.replace("Bearer ", "");
            String username = jwtUtil.extractUsername(accessToken);
            
            // Blacklist current access token
            Instant expiry = jwtUtil.extractExpiration(accessToken).toInstant();
            tokenBlacklistService.blacklistToken(accessToken, expiry);
            
            // Remove all sessions for user
            userSessionService.removeAllTokensForUser(username);
            
            // Revoke all refresh tokens for user
            refreshTokenService.revokeAllUserTokens(username);
            
            return ResponseEntity.ok("Logged out from all devices successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Logout failed: " + e.getMessage());
        }
    }

    private String getDeviceInfo(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String clientIp = getClientIpAddress(request);
        return String.format("IP: %s, User-Agent: %s", clientIp, userAgent != null ? userAgent : "Unknown");
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.getRemoteAddr();
        } else {
            return xForwardedForHeader.split(",")[0];
        }
    }
}