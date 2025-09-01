package com.nutritionstack.nutritionstackwebapi.dto.auth;

import com.nutritionstack.nutritionstackwebapi.model.auth.UserRole;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminUserInfoDTO {
    private Long id;
    private String username;
    private UserRole role;
    private LocalDateTime createdAt;
}
