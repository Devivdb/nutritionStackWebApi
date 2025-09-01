package com.nutritionstack.nutritionstackwebapi.controller.user;

import com.nutritionstack.nutritionstackwebapi.dto.user.UserProfileDTO;
import com.nutritionstack.nutritionstackwebapi.dto.user.UserProfileUpdateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.service.user.UserProfileService;
import com.nutritionstack.nutritionstackwebapi.util.GenericResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/profile")
@RequiredArgsConstructor
public class UserProfileController {
    
    private final UserProfileService userProfileService;
    
    @GetMapping
    public ResponseEntity<UserProfileDTO> getUserProfile(Authentication authentication) {
        String username = authentication.getName();
        UserProfileDTO profile = userProfileService.getUserProfile(username);
        return GenericResponseBuilder.success(profile);
    }
    
    @PatchMapping
    public ResponseEntity<UserProfileDTO> updateUserProfile(
            Authentication authentication,
            @Valid @RequestBody UserProfileUpdateRequestDTO request) {
        String username = authentication.getName();
        UserProfileDTO updatedProfile = userProfileService.updateUserProfile(username, request);
        return GenericResponseBuilder.success(updatedProfile);
    }
    
    @DeleteMapping
    public ResponseEntity<Void> deleteUserAccount(Authentication authentication) {
        String username = authentication.getName();
        userProfileService.deleteUserAccount(username);
        return GenericResponseBuilder.noContent();
    }
}
