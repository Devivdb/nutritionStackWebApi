package com.nutritionstack.nutritionstackwebapi.exception;

import com.nutritionstack.nutritionstackwebapi.constant.ErrorMessages;
import com.nutritionstack.nutritionstackwebapi.exception.BulkUploadValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
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
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Product Validation Error",
                "Product data validation failed",
                errors
        );
        
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
    }
    
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(ProductNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Product Not Found",
                ex.getMessage(),
                null
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    @ExceptionHandler(ProductAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleProductAlreadyExistsException(ProductAlreadyExistsException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Product Already Exists",
                ex.getMessage(),
                null
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
    
    @ExceptionHandler(ProductValidationException.class)
    public ResponseEntity<ErrorResponse> handleProductValidationException(ProductValidationException ex) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        
        String message = ex.getMessage();
        if (message != null) {
            if (message.contains("already exists")) {
                status = HttpStatus.CONFLICT;
            } else if (message.contains("EAN13 code") ||
                       message.contains("Product name") ||
                       message.contains("Amount") ||
                       message.contains("Unit") ||
                       message.contains("Calories") ||
                       message.contains("Protein") ||
                       message.contains("Carbs") ||
                       message.contains("Fat") ||
                       message.contains("Fiber") ||
                       message.contains("Sugar") ||
                       message.contains("Salt")) {
                status = HttpStatus.UNPROCESSABLE_ENTITY;
            }
        }
        
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                "Product Validation Error",
                ex.getMessage(),
                null
        );
        
        return ResponseEntity.status(status).body(errorResponse);
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                "Access Denied",
                "You do not have permission to perform this action",
                null
        );
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "Authentication Failed",
                ex.getMessage(),
                null
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
    
    @ExceptionHandler(BulkUploadValidationException.class)
    public ResponseEntity<ErrorResponse> handleBulkUploadValidationException(BulkUploadValidationException ex) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        
        String message = ex.getMessage();
        if (message != null) {
            if (message.contains("Duplicate EAN13 codes found within the upload file") ||
                message.contains("All") && message.contains("products in the file already exist")) {
                status = HttpStatus.CONFLICT;
            } else if (message.contains("File size exceeds maximum limit") ||
                       message.contains("Only JSON files are allowed") ||
                       message.contains("File must have .json extension") ||
                       message.contains("File cannot be null or empty")) {
                status = HttpStatus.UNPROCESSABLE_ENTITY;
            } else if (message.contains("Product validation failed") ||
                       message.contains("EAN13 code") ||
                       message.contains("Product name") ||
                       message.contains("Amount") ||
                       message.contains("Unit") ||
                       message.contains("Calories") ||
                       message.contains("Protein") ||
                       message.contains("Carbs") ||
                       message.contains("Fat") ||
                       message.contains("Fiber") ||
                       message.contains("Sugar") ||
                       message.contains("Salt")) {
                status = HttpStatus.UNPROCESSABLE_ENTITY;
            }
        }
        
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                "Bulk Upload Validation Error",
                ex.getMessage(),
                null
        );
        
        return ResponseEntity.status(status).body(errorResponse);
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
