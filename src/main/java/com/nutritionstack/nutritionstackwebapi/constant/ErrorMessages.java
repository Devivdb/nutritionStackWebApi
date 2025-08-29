package com.nutritionstack.nutritionstackwebapi.constant;

public final class ErrorMessages {
    
    private ErrorMessages() {
        // Prevent instantiation
    }
    
    // User-related error messages
    public static final String USER_ALREADY_EXISTS = "User with username '%s' already exists";
    public static final String INVALID_CREDENTIALS = "Invalid username or password";
    public static final String USER_NOT_FOUND = "User not found: %s";
    public static final String USERNAME_REQUIRED = "Username is required";
    public static final String PASSWORD_REQUIRED = "Password is required";
    public static final String USERNAME_LENGTH = "Username must be between 3 and 50 characters";
    public static final String PASSWORD_LENGTH = "Password must be at least 6 characters";
    public static final String ROLE_REQUIRED = "Role is required";
    
    // Success messages
    public static final String USER_REGISTERED_SUCCESSFULLY = "User registered successfully";
    public static final String LOGIN_SUCCESSFUL = "Login successful";
    
    // Generic error messages
    public static final String VALIDATION_ERROR = "Validation Error";
    public static final String INVALID_INPUT_DATA = "Invalid input data";
    public static final String BAD_REQUEST = "Bad Request";
    public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
    public static final String UNEXPECTED_ERROR = "An unexpected error occurred";
}
