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

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductCreateRequestDTO request) {
        Long userId = getCurrentUserId();
        ProductResponseDTO response = productService.createProduct(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{ean13Code}")
    public ResponseEntity<ProductResponseDTO> getProductByEan13Code(@PathVariable String ean13Code) {
        ProductResponseDTO response = productService.getProductByEan13Code(ean13Code);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{ean13Code}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable String ean13Code,
            @Valid @RequestBody ProductUpdateRequestDTO request) {
        ProductResponseDTO response = productService.updateProduct(ean13Code, request);
        return ResponseEntity.ok(response);
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
