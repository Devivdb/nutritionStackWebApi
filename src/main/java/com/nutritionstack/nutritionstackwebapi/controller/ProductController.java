package com.nutritionstack.nutritionstackwebapi.controller;

import com.nutritionstack.nutritionstackwebapi.dto.ProductCreateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.dto.ProductResponseDTO;
import com.nutritionstack.nutritionstackwebapi.dto.ProductUpdateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import com.nutritionstack.nutritionstackwebapi.security.CustomAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.nutritionstack.nutritionstackwebapi.exception.ProductValidationException;
import com.nutritionstack.nutritionstackwebapi.service.UserProfileService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final ProductService productService;
    private final UserProfileService userProfileService;
    
    public ProductController(ProductService productService, UserProfileService userProfileService) {
        this.productService = productService;
        this.userProfileService = userProfileService;
    }
    
    private Long getUserIdFromAuthentication(Authentication authentication) {
        String username = authentication.getName();
        return userProfileService.getUserIdByUsername(username);
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(
            @Valid @RequestBody ProductCreateRequestDTO request,
            Authentication authentication) {
        
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            ProductResponseDTO response = productService.createProduct(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ProductValidationException e) {
            // This will be handled by the global exception handler
            throw e;
        } catch (Exception e) {
            throw new ProductValidationException("Failed to create product: " + e.getMessage());
        }
    }
    
    @GetMapping("/{ean13Code}")
    public ResponseEntity<ProductResponseDTO> getProductByEan13Code(@PathVariable String ean13Code) {
        ProductResponseDTO response = productService.getProductByEan13Code(ean13Code);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{ean13Code}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable String ean13Code,
            @Valid @RequestBody ProductUpdateRequestDTO request,
            Authentication authentication) {
        
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            ProductResponseDTO response = productService.updateProduct(ean13Code, request, userId);
            return ResponseEntity.ok(response);
        } catch (ProductValidationException e) {
            // This will be handled by the global exception handler
            throw e;
        } catch (Exception e) {
            throw new ProductValidationException("Failed to update product: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{ean13Code}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable String ean13Code) {
        productService.deleteProduct(ean13Code);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<ProductResponseDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof CustomAuthenticationToken) {
            return ((CustomAuthenticationToken) authentication).getUserId();
        }
        throw new RuntimeException("Unable to extract user ID from authentication");
    }
}
