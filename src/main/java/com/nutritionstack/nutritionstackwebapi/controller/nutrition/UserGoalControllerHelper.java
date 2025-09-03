package com.nutritionstack.nutritionstackwebapi.controller.nutrition;

import com.nutritionstack.nutritionstackwebapi.service.user.UserProfileService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class UserGoalControllerHelper {
    
    private final UserProfileService userProfileService;
    
    public UserGoalControllerHelper(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }
    
    public Long getUserIdFromAuthentication(Authentication authentication) {
        String username = authentication.getName();
        return userProfileService.getUserIdByUsername(username);
    }
}
