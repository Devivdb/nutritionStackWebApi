package com.nutritionstack.nutritionstackwebapi.exception;

public class AdminCannotDeleteLastAdminException extends RuntimeException {
    public AdminCannotDeleteLastAdminException(String message) {
        super(message);
    }
}
