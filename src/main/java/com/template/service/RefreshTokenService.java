package com.template.service;

import com.template.entity.RefreshToken;
import com.template.entity.User;
import com.template.repository.RefreshTokenRepository;
import com.template.repository.UserRepository;
import com.template.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Value("${auth.max-refresh-tokens-per-user:5}")
    private int maxRefreshTokensPerUser;

    @Transactional
    public RefreshToken createRefreshToken(String username, String deviceInfo) {
        // Clean up old/expired tokens first
        cleanupExpiredTokens();
        
        // Check if user has too many active refresh tokens
        long activeTokenCount = refreshTokenRepository.countActiveTokensByUsername(username);
        if (activeTokenCount >= maxRefreshTokensPerUser) {
            // Revoke oldest tokens
            revokeOldestTokensForUser(username, (int) (activeTokenCount - maxRefreshTokensPerUser + 1));
        }

        String token = jwtUtil.generateRefreshToken(username);
        Instant expiryDate = jwtUtil.extractExpiration(token).toInstant();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .username(username)
                .expiryDate(expiryDate)
                .deviceInfo(deviceInfo != null ? deviceInfo : "Unknown")
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public boolean validateRefreshToken(String token) {
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByToken(token);
        
        if (refreshTokenOpt.isEmpty()) {
            log.warn("Refresh token not found in database");
            return false;
        }

        RefreshToken refreshToken = refreshTokenOpt.get();
        
        if (refreshToken.isRevoked()) {
            log.warn("Refresh token is revoked");
            return false;
        }

        if (refreshToken.isExpired()) {
            log.warn("Refresh token is expired");
            revokeToken(token);
            return false;
        }

        // Validate JWT token structure
        return jwtUtil.validateRefreshToken(token, refreshToken.getUsername());
    }

    @Transactional
    public TokenPair refreshTokens(String refreshToken, String deviceInfo) {
        if (!validateRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        Optional<RefreshToken> tokenEntity = refreshTokenRepository.findByToken(refreshToken);
        if (tokenEntity.isEmpty()) {
            throw new RuntimeException("Refresh token not found");
        }

        String username = tokenEntity.get().getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get user authorities
        Set<String> authorities = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(java.util.stream.Collectors.toSet());

        // Revoke the old refresh token (token rotation)
        revokeToken(refreshToken);

        // Generate new tokens
        String newAccessToken = jwtUtil.generateAccessToken(username, authorities);
        RefreshToken newRefreshToken = createRefreshToken(username, deviceInfo);

        return new TokenPair(newAccessToken, newRefreshToken.getToken());
    }

    @Transactional
    public void revokeToken(String token) {
        refreshTokenRepository.revokeByToken(token);
    }

    @Transactional
    public void revokeAllUserTokens(String username) {
        refreshTokenRepository.revokeAllByUsername(username);
    }

    @Transactional
    private void revokeOldestTokensForUser(String username, int count) {
        var tokens = refreshTokenRepository.findByUsernameAndRevokedFalse(username);
        tokens.stream()
                .limit(count)
                .forEach(token -> token.setRevoked(true));
        refreshTokenRepository.saveAll(tokens);
    }

    @Scheduled(fixedRate = 3600000) // Run every hour
    @Transactional
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteExpiredAndRevoked(Instant.now());
        log.info("Cleaned up expired and revoked refresh tokens");
    }

    public static class TokenPair {
        private final String accessToken;
        private final String refreshToken;

        public TokenPair(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }

        public String getAccessToken() { return accessToken; }
        public String getRefreshToken() { return refreshToken; }
    }
} 