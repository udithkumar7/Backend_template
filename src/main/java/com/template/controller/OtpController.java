package com.template.controller;

import com.template.dto.OtpRequest;
import com.template.dto.OtpVerificationRequest;
import com.template.dto.OtpForgotRequest;
import com.template.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/otp")
@RequiredArgsConstructor
public class OtpController {
    private final OtpService otpService;

    // Unlock account
    @PostMapping("/send")
    public ResponseEntity<?> sendOtp(@RequestBody OtpRequest req) {
        if (otpService.sendOtp(req.getEmail())) {
            return ResponseEntity.ok("OTP sent to email.");
        }
        return ResponseEntity.badRequest().body("Failed to send OTP.");
    }
    @PostMapping("/verify")
public ResponseEntity<?> verifyOtp(@RequestBody OtpVerificationRequest req) {
    if (otpService.verifyOtp(req.getEmail(), req.getOtpCode())) {
        return ResponseEntity.ok("OTP verified successfully.");
    }
    return ResponseEntity.badRequest().body("Invalid or expired OTP.");
}

    // 2. Send OTP only if account is locked
    @PostMapping("/send-if-locked")
    public ResponseEntity<?> sendOtpIfLocked(@RequestBody OtpRequest req) {
        if (!otpService.isAccountLocked(req.getEmail())) {
            return ResponseEntity.badRequest().body("Account is not locked.");
        }
        if (otpService.sendOtp(req.getEmail())) {
            return ResponseEntity.ok("OTP sent to email.");
        }
        return ResponseEntity.badRequest().body("Failed to send OTP.");
    }

    @PostMapping("/verify-unlock")
    public ResponseEntity<?> verifyUnlock(@RequestBody OtpVerificationRequest req) {
        if (otpService.verifyOtp(req.getEmail(), req.getOtpCode())) {
            otpService.unlockAccount(req.getEmail());
            return ResponseEntity.ok("Account unlocked.");
        }
        return ResponseEntity.badRequest().body("Invalid or expired OTP.");
    }

    // Activate/extend account
    @PostMapping("/send-activate")
    public ResponseEntity<?> sendActivateOtp(@RequestBody OtpRequest req) {
        if (!otpService.isAccountExpired(req.getEmail())) {
            return ResponseEntity.badRequest().body("Account is not expired.");
        }
        if (otpService.sendOtp(req.getEmail())) {
            return ResponseEntity.ok("OTP sent to email.");
        }
        return ResponseEntity.badRequest().body("Failed to send OTP.");
    }

    @PostMapping("/verify-activate")
    public ResponseEntity<?> verifyActivate(@RequestBody OtpVerificationRequest req) {
        if (otpService.verifyOtp(req.getEmail(), req.getOtpCode())) {
            otpService.extendExpiry(req.getEmail());
            return ResponseEntity.ok("Account activated.");
        }
        return ResponseEntity.badRequest().body("Invalid or expired OTP.");
    }

    // Forgot password
    @PostMapping("/send-forgot")
    public ResponseEntity<?> sendForgotOtp(@RequestBody OtpRequest req) {
        if (otpService.sendOtp(req.getEmail())) {
            return ResponseEntity.ok("OTP sent to email.");
        }
        return ResponseEntity.badRequest().body("Failed to send OTP.");
    }

    @PostMapping("/verify-forgot")
    public ResponseEntity<?> verifyForgot(@RequestBody OtpForgotRequest req) {
        if (otpService.verifyOtp(req.getEmail(), req.getOtpCode())) {
            otpService.updatePassword(req.getEmail(), req.getNewPassword());
            return ResponseEntity.ok("Password updated.");
        }
        return ResponseEntity.badRequest().body("Invalid or expired OTP.");
    }
}