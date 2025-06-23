package com.template.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenResponse {
    
    private String accessToken;
    private String refreshToken;
    private String username;
    private String message;
    private Long accessTokenExpiresIn;
    private Long refreshTokenExpiresIn;
} 