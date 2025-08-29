package com.nutritionstack.nutritionstackwebapi.util;

import com.nutritionstack.nutritionstackwebapi.dto.AuthResponseDTO;
import com.nutritionstack.nutritionstackwebapi.model.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public final class ResponseBuilder {
    
    private ResponseBuilder() {
        // Prevent instantiation
    }
    
    public static <T> ResponseEntity<T> success(T body) {
        return ResponseEntity.ok(body);
    }
    
    public static <T> ResponseEntity<T> created(T body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }
    
    public static ResponseEntity<AuthResponseDTO> authError(String message, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(new AuthResponseDTO(null, null, null, message));
    }
    
    public static ResponseEntity<AuthResponseDTO> authSuccess(String token, String username, 
                                                            UserRole role, String message) {
        return ResponseEntity.ok(new AuthResponseDTO(token, username, role, message));
    }
    
    public static ResponseEntity<AuthResponseDTO> authCreated(String token, String username, 
                                                            UserRole role, String message) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponseDTO(token, username, role, message));
    }
}
