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
public class UserCreateRequest {
    
    @NotBlank(message = ValidationConstants.NOT_BLANK_MESSAGE)
    @Size(min = 3, max = 50, message = ValidationConstants.USERNAME_SIZE_MESSAGE)
    @Pattern(regexp = ValidationConstants.USERNAME_PATTERN, message = ValidationConstants.USERNAME_MESSAGE)
    private String username;
    
    @NotBlank(message = ValidationConstants.NOT_BLANK_MESSAGE)
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Pattern(regexp = ValidationConstants.PASSWORD_PATTERN, message = ValidationConstants.PASSWORD_MESSAGE)
    private String password;
    
    @NotBlank(message = ValidationConstants.NOT_BLANK_MESSAGE)
    @Email(message = ValidationConstants.EMAIL_MESSAGE)
    @Size(min = 5, max = 100, message = ValidationConstants.EMAIL_SIZE_MESSAGE)
    private String email;
}