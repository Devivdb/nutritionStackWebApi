package com.nutritionstack.nutritionstackwebapi.util;

import com.nutritionstack.nutritionstackwebapi.dto.auth.AuthResponseDTO;
import com.nutritionstack.nutritionstackwebapi.model.auth.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public final class AuthResponseBuilder {
    
    private AuthResponseBuilder() {}
    
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
