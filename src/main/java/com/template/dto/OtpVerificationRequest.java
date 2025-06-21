package com.template.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import com.template.util.ValidationConstants;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerificationRequest {
    
    @NotBlank(message = ValidationConstants.NOT_BLANK_MESSAGE)
    @Email(message = ValidationConstants.EMAIL_MESSAGE)
    @Size(min = 5, max = 100, message = ValidationConstants.EMAIL_SIZE_MESSAGE)
    private String email;
    
    @NotBlank(message = ValidationConstants.NOT_BLANK_MESSAGE)
    @Pattern(regexp = ValidationConstants.OTP_PATTERN, message = ValidationConstants.OTP_MESSAGE)
    private String otpCode;
}