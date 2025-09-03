package com.nutritionstack.nutritionstackwebapi.service.nutrition;

import com.nutritionstack.nutritionstackwebapi.dto.nutrition.UserGoalCreateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.dto.nutrition.UserGoalUpdateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.util.UserGoalValidationUtil;
import org.springframework.stereotype.Component;

@Component
public class UserGoalValidator {
    
    public void validateCreateRequest(UserGoalCreateRequestDTO request) {
        UserGoalValidationUtil.validateCreateRequest(request);
    }
    
    public void validateUpdateRequest(UserGoalUpdateRequestDTO request) {
        if (!request.hasUpdates()) {
            throw new IllegalArgumentException("No updates provided");
        }
        UserGoalValidationUtil.validateUpdateRequest(request);
    }
}
