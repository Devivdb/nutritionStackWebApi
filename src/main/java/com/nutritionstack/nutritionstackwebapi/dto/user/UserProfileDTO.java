package com.nutritionstack.nutritionstackwebapi.dto.user;

import com.nutritionstack.nutritionstackwebapi.model.auth.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private Long id;
    private String username;
    private UserRole role;
    private LocalDateTime createdAt;
}
