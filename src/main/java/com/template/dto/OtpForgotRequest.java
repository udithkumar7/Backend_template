package com.template.dto;

import lombok.Data;

@Data
public class OtpForgotRequest {
    private String email;
    private String otpCode;
    private String newPassword;
}