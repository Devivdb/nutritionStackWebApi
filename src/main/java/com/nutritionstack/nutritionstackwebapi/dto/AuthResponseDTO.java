package com.nutritionstack.nutritionstackwebapi.dto;

import com.nutritionstack.nutritionstackwebapi.model.UserRole;

public class AuthResponseDTO {
    private String token;
    private String username;
    private UserRole role;
    private String message;
    
    public AuthResponseDTO() {}
    
    public AuthResponseDTO(String token, String username, UserRole role, String message) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.message = message;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public UserRole getRole() {
        return role;
    }
    
    public void setRole(UserRole role) {
        this.role = role;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
