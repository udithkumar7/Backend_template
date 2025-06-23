package com.template.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import com.template.util.ValidationConstants;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    
    @NotBlank(message = ValidationConstants.NOT_BLANK_MESSAGE)
    @Size(min = 3, max = 50, message = ValidationConstants.USERNAME_SIZE_MESSAGE)
    @Pattern(regexp = ValidationConstants.USERNAME_PATTERN, message = ValidationConstants.USERNAME_MESSAGE)
    private String username;
    
    @NotBlank(message = ValidationConstants.NOT_BLANK_MESSAGE)
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;
}