package com.nutritionstack.nutritionstackwebapi.controller.nutrition;

import com.nutritionstack.nutritionstackwebapi.dto.nutrition.UserGoalCreateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.dto.nutrition.UserGoalUpdateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.dto.nutrition.UserGoalResponseDTO;
import com.nutritionstack.nutritionstackwebapi.exception.UserGoalNotFoundException;
import com.nutritionstack.nutritionstackwebapi.service.nutrition.UserGoalService;
import com.nutritionstack.nutritionstackwebapi.service.user.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-goals")
public class UserGoalController {
    
    private final UserGoalService userGoalService;
    private final UserGoalControllerHelper controllerHelper;
    
    public UserGoalController(UserGoalService userGoalService, UserGoalControllerHelper controllerHelper) {
        this.userGoalService = userGoalService;
        this.controllerHelper = controllerHelper;
    }
    
    @PostMapping
    public ResponseEntity<UserGoalResponseDTO> createGoal(
            @Valid @RequestBody UserGoalCreateRequestDTO request,
            Authentication authentication) {
        
        Long userId = controllerHelper.getUserIdFromAuthentication(authentication);
        UserGoalResponseDTO response = userGoalService.createGoal(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{goalId}")
    public ResponseEntity<UserGoalResponseDTO> updateGoal(
            @PathVariable Long goalId,
            @Valid @RequestBody UserGoalUpdateRequestDTO request,
            Authentication authentication) {
        
        Long userId = controllerHelper.getUserIdFromAuthentication(authentication);
        UserGoalResponseDTO response = userGoalService.updateGoal(goalId, request, userId);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(
            @PathVariable Long goalId,
            Authentication authentication) {
        
        Long userId = controllerHelper.getUserIdFromAuthentication(authentication);
        userGoalService.deleteGoal(goalId, userId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/active")
    public ResponseEntity<UserGoalResponseDTO> getActiveGoal(Authentication authentication) {
        Long userId = controllerHelper.getUserIdFromAuthentication(authentication);
        UserGoalResponseDTO response = userGoalService.getActiveGoal(userId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<UserGoalResponseDTO>> getAllUserGoals(Authentication authentication) {
        Long userId = controllerHelper.getUserIdFromAuthentication(authentication);
        List<UserGoalResponseDTO> response = userGoalService.getAllUserGoals(userId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{goalId}")
    public ResponseEntity<UserGoalResponseDTO> getGoalById(
            @PathVariable Long goalId,
            Authentication authentication) {
        
        Long userId = controllerHelper.getUserIdFromAuthentication(authentication);
        UserGoalResponseDTO response = userGoalService.getGoalById(goalId, userId);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{goalId}/reactivate")
    public ResponseEntity<UserGoalResponseDTO> reactivateGoal(
            @PathVariable Long goalId,
            Authentication authentication) {
        
        Long userId = controllerHelper.getUserIdFromAuthentication(authentication);
        UserGoalResponseDTO response = userGoalService.reactivateGoal(goalId, userId);
        return ResponseEntity.ok(response);
    }
}
