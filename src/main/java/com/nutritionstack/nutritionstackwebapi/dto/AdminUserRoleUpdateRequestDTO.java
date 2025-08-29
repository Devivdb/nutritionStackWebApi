package com.nutritionstack.nutritionstackwebapi.dto;

import com.nutritionstack.nutritionstackwebapi.model.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminUserRoleUpdateRequestDTO {
    @NotNull(message = "Role is required")
    private UserRole role;
}
