package com.nutritionstack.nutritionstackwebapi.exception;

public class AdminCannotDeleteSelfException extends RuntimeException {
    public AdminCannotDeleteSelfException(String message) {
        super(message);
    }
}
