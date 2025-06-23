package com.template.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.template.util.ValidationConstants;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SamplePdfData {
    
    @NotBlank(message = ValidationConstants.NOT_BLANK_MESSAGE)
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;
    
    @Size(max = 5000, message = "Content must not exceed 5000 characters")
    private String content;
    
    @Size(max = 100, message = "Author must not exceed 100 characters")
    private String author;
    
    @Size(max = 50, message = "Date must not exceed 50 characters")
    private String date;
    
    @Size(max = 200, message = "Footer must not exceed 200 characters")
    private String footer;
    
    @Size(max = 200, message = "Header must not exceed 200 characters")
    private String header;
    
    @Size(max = 500, message = "Logo URL must not exceed 500 characters")
    private String logoUrl;
    
    @Size(max = 100, message = "Company name must not exceed 100 characters")
    private String companyName;
    
    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;
    
    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;
    
    @Email(message = ValidationConstants.EMAIL_MESSAGE)
    @Size(max = 100, message = ValidationConstants.EMAIL_SIZE_MESSAGE)
    private String email;
} 