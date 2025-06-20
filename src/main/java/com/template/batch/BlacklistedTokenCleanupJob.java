package com.template.batch;

import com.template.repository.BlacklistedTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.Instant;

@Component
@RequiredArgsConstructor
@Profile("prod") // This ensures the bean is only active in prod
public class BlacklistedTokenCleanupJob {

    private final BlacklistedTokenRepository blacklistedTokenRepository;

    // Runs every day at 00:00 (midnight)
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanExpiredTokens() {
        // Delete tokens where expiry is before now
        int deleted = blacklistedTokenRepository.deleteByExpiryBefore(Instant.now());
        System.out.println("Deleted " + deleted + " expired blacklisted tokens.");
    }
}