package com.nutritionstack.nutritionstackwebapi.dto;

import com.nutritionstack.nutritionstackwebapi.constant.ErrorMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileUpdateRequestDTO {
    
    @Size(min = 3, max = 50, message = ErrorMessages.USERNAME_LENGTH)
    private String username;
    
    @Size(min = 6, message = ErrorMessages.PASSWORD_LENGTH)
    private String password;
}
