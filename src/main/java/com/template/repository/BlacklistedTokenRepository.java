package com.template.repository;

import com.template.entity.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;

public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {
    boolean existsByToken(String token);
    int deleteByExpiryBefore(Instant now);

}
