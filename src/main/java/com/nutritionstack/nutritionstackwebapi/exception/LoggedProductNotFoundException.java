package com.nutritionstack.nutritionstackwebapi.exception;

public class LoggedProductNotFoundException extends RuntimeException {
    
    public LoggedProductNotFoundException(String message) {
        super(message);
    }
    
    public LoggedProductNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
