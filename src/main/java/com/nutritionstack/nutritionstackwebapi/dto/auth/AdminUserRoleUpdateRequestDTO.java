package com.nutritionstack.nutritionstackwebapi.dto.auth;

import com.nutritionstack.nutritionstackwebapi.model.auth.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminUserRoleUpdateRequestDTO {
    @NotNull(message = "Role is required")
    private UserRole role;
}
