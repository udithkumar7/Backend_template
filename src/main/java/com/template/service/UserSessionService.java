package com.template.service;

import com.template.entity.BlacklistedToken;
import com.template.repository.BlacklistedTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserSessionService {
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    // In-memory map for demo; use DB or Redis for production
    private final Map<String, Deque<String>> userTokens = new HashMap<>();

    @Value("${auth.max-sessions-per-user:1}")
    private int maxSessionsPerUser;

    public synchronized void registerToken(String username, String token, Instant expiry) {
        userTokens.putIfAbsent(username, new LinkedList<>());
        Deque<String> tokens = userTokens.get(username);

        tokens.addLast(token);

        // If over the limit, blacklist and remove oldest tokens
        while (tokens.size() > maxSessionsPerUser) {
            String oldToken = tokens.removeFirst();
            blacklistedTokenRepository.save(
                BlacklistedToken.builder().token(oldToken).expiry(expiry).build()
            );
        }
    }

    public synchronized void removeToken(String username, String token) {
        Deque<String> tokens = userTokens.get(username);
        if (tokens != null) {
            tokens.remove(token);
        }
    }
}
