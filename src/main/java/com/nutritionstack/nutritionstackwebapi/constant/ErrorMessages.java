package com.nutritionstack.nutritionstackwebapi.constant;

public final class ErrorMessages {
    
    private ErrorMessages() {
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
    public static final String PRODUCT_CREATED_SUCCESSFULLY = "Product created successfully";
    public static final String PRODUCT_UPDATED_SUCCESSFULLY = "Product updated successfully";
    public static final String PRODUCT_DELETED_SUCCESSFULLY = "Product deleted successfully";
    
    // Product-related error messages
    public static final String PRODUCT_NOT_FOUND = "Product not found with EAN13 code: %s";
    public static final String PRODUCT_ALREADY_EXISTS = "Product with EAN13 code '%s' already exists";
    public static final String EAN13_CODE_REQUIRED = "EAN13 code is required";
    public static final String PRODUCT_NAME_REQUIRED = "Product name is required";
    public static final String NEGATIVE_NUTRITION_VALUE = "Nutrition values must be non-negative";
    
    // Generic error messages
    public static final String VALIDATION_ERROR = "Validation Error";
    public static final String INVALID_INPUT_DATA = "Invalid input data";
    public static final String BAD_REQUEST = "Bad Request";
    public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
    public static final String UNEXPECTED_ERROR = "An unexpected error occurred";
}
