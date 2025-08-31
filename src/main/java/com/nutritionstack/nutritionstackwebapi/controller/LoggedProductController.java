package com.nutritionstack.nutritionstackwebapi.controller;

import com.nutritionstack.nutritionstackwebapi.dto.LoggedProductCreateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.dto.LoggedProductResponseDTO;
import com.nutritionstack.nutritionstackwebapi.dto.LoggedProductUpdateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.dto.LoggedProductSimpleResponseDTO;
import com.nutritionstack.nutritionstackwebapi.exception.LoggedProductNotFoundException;
import com.nutritionstack.nutritionstackwebapi.exception.ProductNotFoundException;
import com.nutritionstack.nutritionstackwebapi.service.LoggedProductService;
import com.nutritionstack.nutritionstackwebapi.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/logged-products")
public class LoggedProductController {
    
    private final LoggedProductService loggedProductService;
    private final UserProfileService userProfileService;
    
    public LoggedProductController(LoggedProductService loggedProductService, UserProfileService userProfileService) {
        this.loggedProductService = loggedProductService;
        this.userProfileService = userProfileService;
    }
    
    private Long getUserIdFromAuthentication(Authentication authentication) {
        String username = authentication.getName();
        return userProfileService.getUserIdByUsername(username);
    }
    
    @PostMapping
    public ResponseEntity<LoggedProductSimpleResponseDTO> logProduct(
            @Valid @RequestBody LoggedProductCreateRequestDTO request,
            Authentication authentication) {
        
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            LoggedProductSimpleResponseDTO response = loggedProductService.logProductSimple(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (ProductNotFoundException e) {
            throw e; // Let ProductNotFoundException pass through to return 404
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to log product: " + e.getMessage());
        }
    }
    
    @GetMapping
    public ResponseEntity<List<LoggedProductSimpleResponseDTO>> getUserLoggedProducts(Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            List<LoggedProductSimpleResponseDTO> response = loggedProductService.getUserLoggedProductsSimple(userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to retrieve logged products: " + e.getMessage());
        }
    }
    
    @GetMapping("/{logId}")
    public ResponseEntity<LoggedProductSimpleResponseDTO> getLoggedProduct(
            @PathVariable Long logId,
            Authentication authentication) {
        
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            LoggedProductSimpleResponseDTO response = loggedProductService.getLoggedProductSimple(logId, userId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (LoggedProductNotFoundException e) {
            throw e; // Let LoggedProductNotFoundException pass through to return 404
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to retrieve logged product: " + e.getMessage());
        }
    }
    
    @PutMapping("/{logId}")
    public ResponseEntity<LoggedProductSimpleResponseDTO> updateLoggedProduct(
            @PathVariable Long logId,
            @Valid @RequestBody LoggedProductUpdateRequestDTO request,
            Authentication authentication) {
        
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            LoggedProductSimpleResponseDTO response = loggedProductService.updateLoggedProductSimple(logId, request, userId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (LoggedProductNotFoundException e) {
            throw e; // Let LoggedProductNotFoundException pass through to return 404
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to update logged product: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{logId}")
    public ResponseEntity<Void> deleteLoggedProduct(
            @PathVariable Long logId,
            Authentication authentication) {
        
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            loggedProductService.deleteLoggedProduct(logId, userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (LoggedProductNotFoundException e) {
            throw e; // Let LoggedProductNotFoundException pass through to return 404
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to delete logged product: " + e.getMessage());
        }
    }
    
    @GetMapping("/by-date")
    public ResponseEntity<List<LoggedProductSimpleResponseDTO>> getUserLoggedProductsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication authentication) {
        
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            // Convert LocalDate to LocalDateTime at start of day for service layer
            LocalDateTime startOfDay = date.atStartOfDay();
            List<LoggedProductSimpleResponseDTO> response = loggedProductService.getUserLoggedProductsByDateSimple(userId, startOfDay);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to retrieve logged products by date: " + e.getMessage());
        }
    }
    
    @GetMapping("/by-meal-type")
    public ResponseEntity<List<LoggedProductSimpleResponseDTO>> getUserLoggedProductsByMealType(
            @RequestParam String mealType,
            Authentication authentication) {
        
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            List<LoggedProductSimpleResponseDTO> response = loggedProductService.getUserLoggedProductsByMealTypeSimple(userId, mealType);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to retrieve logged products by meal type: " + e.getMessage());
        }
    }
}
