package com.nutritionstack.nutritionstackwebapi.exception;

public class UserGoalNotFoundException extends RuntimeException {
    
    public UserGoalNotFoundException(String message) {
        super(message);
    }
    
    public UserGoalNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
