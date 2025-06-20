package com.template.repository;

import com.template.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findByEmailAndOtpCodeAndUsedFalse(String email, String otpCode);
    Optional<Otp> findTopByEmailOrderByExpiryTimeDesc(String email);
}