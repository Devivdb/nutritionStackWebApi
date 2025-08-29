package com.nutritionstack.nutritionstackwebapi.dto;

import com.nutritionstack.nutritionstackwebapi.constant.ErrorMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRegistrationRequestDTO {
    
        @NotBlank(message = ErrorMessages.USERNAME_REQUIRED)
    @Size(min = 3, max = 50, message = ErrorMessages.USERNAME_LENGTH)
    private String username;

    @NotBlank(message = ErrorMessages.PASSWORD_REQUIRED)
    @Size(min = 6, message = ErrorMessages.PASSWORD_LENGTH)
    private String password;
    
    public UserRegistrationRequestDTO() {}
    
    public UserRegistrationRequestDTO(String username, String password) {
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
