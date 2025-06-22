package com.template.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String username;
    private String message;
    private long accessTokenExpiresIn; // seconds until expiration
    
    // Backward compatibility constructor
    public LoginResponse(String token, String username, String message) {
        this.accessToken = token;
        this.username = username;
        this.message = message;
    }
}