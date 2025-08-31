package com.nutritionstack.nutritionstackwebapi.controller;

import com.nutritionstack.nutritionstackwebapi.dto.MealCreateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.dto.MealProductDTO;
import com.nutritionstack.nutritionstackwebapi.dto.MealProductUpdateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.dto.MealResponseDTO;
import com.nutritionstack.nutritionstackwebapi.dto.MealLogResponseDTO;
import com.nutritionstack.nutritionstackwebapi.service.MealService;
import com.nutritionstack.nutritionstackwebapi.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/meals")
public class MealController {
    
    private final MealService mealService;
    private final UserProfileService userProfileService;
    
    public MealController(MealService mealService, UserProfileService userProfileService) {
        this.mealService = mealService;
        this.userProfileService = userProfileService;
    }
    
    @PostMapping
    public ResponseEntity<MealResponseDTO> createMeal(
            @Valid @RequestBody MealCreateRequestDTO request,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        MealResponseDTO response = mealService.createMeal(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{mealId}")
    public ResponseEntity<MealResponseDTO> getMealById(
            @PathVariable Long mealId,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        boolean isAdmin = isUserAdmin(authentication);
        MealResponseDTO response = mealService.getMealById(mealId, userId, isAdmin);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{mealId}/products")
    public ResponseEntity<MealResponseDTO> addProductToMeal(
            @PathVariable Long mealId,
            @Valid @RequestBody MealProductDTO productDTO,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        boolean isAdmin = isUserAdmin(authentication);
        MealResponseDTO response = mealService.addProductToMeal(mealId, productDTO, userId, isAdmin);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{mealId}/products/{ean13Code}")
    public ResponseEntity<MealResponseDTO> updateProductQuantity(
            @PathVariable Long mealId,
            @PathVariable String ean13Code,
            @Valid @RequestBody MealProductUpdateRequestDTO updateDTO,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        boolean isAdmin = isUserAdmin(authentication);
        MealResponseDTO response = mealService.updateProductQuantity(mealId, ean13Code, updateDTO, userId, isAdmin);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{mealId}/products/{ean13Code}")
    public ResponseEntity<MealResponseDTO> removeProductFromMeal(
            @PathVariable Long mealId,
            @PathVariable String ean13Code,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        boolean isAdmin = isUserAdmin(authentication);
        MealResponseDTO response = mealService.removeProductFromMeal(mealId, ean13Code, userId, isAdmin);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{mealId}")
    public ResponseEntity<Void> deleteMeal(
            @PathVariable Long mealId,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        boolean isAdmin = isUserAdmin(authentication);
        mealService.deleteMeal(mealId, userId, isAdmin);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{mealId}/log")
    public ResponseEntity<MealLogResponseDTO> logMeal(
            @PathVariable Long mealId,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        boolean isAdmin = isUserAdmin(authentication);
        MealLogResponseDTO response = mealService.logMeal(mealId, userId, isAdmin);
        return ResponseEntity.ok(response);
    }
    
    // Helper methods
    private Long getUserIdFromAuthentication(Authentication authentication) {
        String username = authentication.getName();
        return userProfileService.getUserIdByUsername(username);
    }
    
    private boolean isUserAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
