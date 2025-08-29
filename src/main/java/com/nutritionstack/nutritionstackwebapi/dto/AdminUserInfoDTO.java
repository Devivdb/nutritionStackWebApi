package com.nutritionstack.nutritionstackwebapi.dto;

import com.nutritionstack.nutritionstackwebapi.model.UserRole;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminUserInfoDTO {
    private Long id;
    private String username;
    private UserRole role;
    private LocalDateTime createdAt;
}
