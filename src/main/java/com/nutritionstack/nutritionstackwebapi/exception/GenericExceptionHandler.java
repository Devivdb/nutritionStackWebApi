package com.nutritionstack.nutritionstackwebapi.exception;

import com.nutritionstack.nutritionstackwebapi.constant.ErrorMessages;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GenericExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                ErrorMessages.VALIDATION_ERROR,
                ErrorMessages.INVALID_INPUT_DATA,
                errors
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                ErrorMessages.BAD_REQUEST,
                ex.getMessage(),
                null
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    @ExceptionHandler(AdminCannotDeleteSelfException.class)
    public ResponseEntity<ErrorResponse> handleAdminCannotDeleteSelfException(AdminCannotDeleteSelfException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Admin cannot delete self",
                ex.getMessage(),
                null
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    @ExceptionHandler(AdminCannotDeleteLastAdminException.class)
    public ResponseEntity<ErrorResponse> handleAdminCannotDeleteLastAdminException(AdminCannotDeleteLastAdminException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Cannot delete last admin",
                ex.getMessage(),
                null
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ErrorMessages.INTERNAL_SERVER_ERROR,
                ErrorMessages.UNEXPECTED_ERROR,
                null
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
