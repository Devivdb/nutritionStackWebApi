package com.nutritionstack.nutritionstackwebapi.dto;

import com.nutritionstack.nutritionstackwebapi.constant.ErrorMessages;
import jakarta.validation.constraints.NotBlank;

public class UserLoginRequestDTO {
    
        @NotBlank(message = ErrorMessages.USERNAME_REQUIRED)
    private String username;

    @NotBlank(message = ErrorMessages.PASSWORD_REQUIRED)
    private String password;
    
    public UserLoginRequestDTO() {}
    
    public UserLoginRequestDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
