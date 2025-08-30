package com.nutritionstack.nutritionstackwebapi.exception;

public class BulkUploadValidationException extends RuntimeException {
    
    public BulkUploadValidationException(String message) {
        super(message);
    }
    
    public BulkUploadValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
