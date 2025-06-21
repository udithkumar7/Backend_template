package com.template.util;

public class ValidationConstants {
    
    // Password validation
    public static final String PASSWORD_PATTERN = 
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
    public static final String PASSWORD_MESSAGE = 
        "Password must be at least 8 characters long and contain at least one digit, " +
        "one lowercase letter, one uppercase letter, and one special character";
    
    // OTP validation
    public static final String OTP_PATTERN = "^[0-9]{6}$";
    public static final String OTP_MESSAGE = "OTP must be exactly 6 digits";
    
    // Username validation
    public static final String USERNAME_PATTERN = "^[a-zA-Z0-9._-]{3,50}$";
    public static final String USERNAME_MESSAGE = 
        "Username must be 3-50 characters long and contain only letters, numbers, dots, underscores, and hyphens";
    
    // Email validation message
    public static final String EMAIL_MESSAGE = "Please provide a valid email address";
    
    // Keycode validation (for Code entity)
    public static final String KEYCODE_PATTERN = "^[A-Z0-9_-]{2,10}$";
    public static final String KEYCODE_MESSAGE = 
        "Keycode must be 2-10 characters long and contain only uppercase letters, numbers, underscores, and hyphens";
    
    // Common validation messages
    public static final String NOT_BLANK_MESSAGE = "This field is required";
    public static final String NOT_NULL_MESSAGE = "This field cannot be null";
    
    // Size validation messages
    public static final String USERNAME_SIZE_MESSAGE = "Username must be between 3 and 50 characters";
    public static final String PASSWORD_SIZE_MESSAGE = "Password must be at least 6 characters long";
    public static final String EMAIL_SIZE_MESSAGE = "Email must be between 5 and 100 characters";
    public static final String VALUEKEY_SIZE_MESSAGE = "Value key must be between 1 and 100 characters";
    public static final String CATEGORY_SIZE_MESSAGE = "Category must be between 1 and 50 characters";
    public static final String DESCRIPTION_SIZE_MESSAGE = "Description must not exceed 500 characters";
    
    private ValidationConstants() {
        // Utility class
    }
} 