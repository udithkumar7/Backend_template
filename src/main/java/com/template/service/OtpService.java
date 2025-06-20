package com.template.service;

import com.template.entity.Otp;
import com.template.entity.User;
import com.template.repository.OtpRepository;
import com.template.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {
    private final OtpRepository otpRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.otp.length:6}")
    private int otpLength;

    @Value("${app.otp.expiry-minutes:10}")
    private int otpExpiryMinutes;

    @Value("${auth.account-expiry-years:1}")
    private int accountExpiryYears;

    public String generateOtp() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < otpLength; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    public boolean sendOtp(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return false;

        String otpCode = generateOtp();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(otpExpiryMinutes);

        Otp otp = Otp.builder()
                .email(email)
                .otpCode(otpCode)
                .expiryTime(expiryTime)
                .used(false)
                .build();
        Otp savedOtp=otpRepository.save(otp);

        try {
            emailService.sendOtpEmail(email, otpCode);
            return true;
        } catch (Exception e) {
            otpRepository.delete(savedOtp);
            return false;
        }
    }

    public boolean verifyOtp(String email, String otpCode) {
        Optional<Otp> otpOpt = otpRepository.findByEmailAndOtpCodeAndUsedFalse(email, otpCode);
        if (otpOpt.isPresent()) {
            Otp otp = otpOpt.get();
            if (LocalDateTime.now().isAfter(otp.getExpiryTime())) {
                return false;
            }
            otp.setUsed(true);
            otpRepository.save(otp);
            return true;
        }
        return false;
    }

    // Unlock account
    public boolean unlockAccount(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setAccountNonLocked(true);
            user.setFailedLoginAttempts(0);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // Extend expiry
    public boolean extendExpiry(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setAccountExpiryDate(LocalDateTime.now().plusYears(accountExpiryYears));
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // Update password
    public boolean updatePassword(String email, String newPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Shift old passwords
            user.setPassword5(user.getPassword4());
            user.setPassword4(user.getPassword3());
            user.setPassword3(user.getPassword2());
            user.setPassword2(user.getPassword1());
            user.setPassword1(user.getPassword());
            // Set new password
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // Helper methods for controller
    public boolean isAccountLocked(String email) {
        return userRepository.findByEmail(email)
                .map(u -> !u.getAccountNonLocked())
                .orElse(false);
    }

    public boolean isAccountExpired(String email) {
        return userRepository.findByEmail(email)
                .map(u -> u.getAccountExpiryDate() != null && u.getAccountExpiryDate().isBefore(LocalDateTime.now()))
                .orElse(false);
    }
}